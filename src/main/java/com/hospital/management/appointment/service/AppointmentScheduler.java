package com.hospital.management.appointment.service;

import com.hospital.management.appointment.model.Appointment;
import com.hospital.management.appointment.model.AppointmentConflict;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AppointmentScheduler
 * --------------------
 * A lightweight async scheduler that queues appointment requests and processes them
 * in background worker threads with basic conflict checks and retry backoff.
 *
 * Typical usage:
 *   AppointmentScheduler scheduler = new AppointmentScheduler(new AppointmentServiceImpl());
 *   CompletableFuture<Long> idFuture =
 *       scheduler.submit(doctorId, patientId, startTime, "Initial consult");
 *
 *   // ... later, on app shutdown:
 *   scheduler.shutdown();
 */
public class AppointmentScheduler implements AutoCloseable {

    /** Represents a unit of work to schedule (or reschedule) an appointment. */
    private static final class AppointmentRequest {
        final Long doctorId;
        final Long patientId;
        final LocalDateTime start;
        final String notes;
        final int attempt;                 // retry count
        final CompletableFuture<Long> resultFuture;

        AppointmentRequest(Long doctorId, Long patientId, LocalDateTime start, String notes,
                           int attempt, CompletableFuture<Long> resultFuture) {
            this.doctorId = doctorId;
            this.patientId = patientId;
            this.start = start;
            this.notes = notes;
            this.attempt = attempt;
            this.resultFuture = resultFuture;
        }

        AppointmentRequest nextAttempt() {
            return new AppointmentRequest(doctorId, patientId, start, notes, attempt + 1, resultFuture);
        }

        @Override
        public String toString() {
            return "AppointmentRequest{" +
                    "doctorId=" + doctorId +
                    ", patientId=" + patientId +
                    ", start=" + start +
                    ", attempt=" + attempt +
                    '}';
        }
    }

    // --- Config knobs ---
    private static final int DEFAULT_WORKERS = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration INITIAL_BACKOFF = Duration.ofSeconds(2);
    private static final Duration SLOT_DURATION = Duration.ofMinutes(30); // align with DAO conflict checks

    // --- Collaborators & concurrency primitives ---
    private final AppointmentService appointmentService;
    private final BlockingQueue<AppointmentRequest> queue;
    private final ExecutorService workers;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AppointmentScheduler(AppointmentService appointmentService) {
        this(appointmentService,
                new LinkedBlockingQueue<>(),
                Executors.newFixedThreadPool(DEFAULT_WORKERS, namedThreadFactory("appt-worker")),
                Executors.newScheduledThreadPool(1, namedThreadFactory("appt-scheduler")));
    }

    public AppointmentScheduler(AppointmentService appointmentService,
                                BlockingQueue<AppointmentRequest> queue,
                                ExecutorService workers,
                                ScheduledExecutorService scheduler) {
        this.appointmentService = Objects.requireNonNull(appointmentService, "appointmentService");
        this.queue = Objects.requireNonNull(queue, "queue");
        this.workers = Objects.requireNonNull(workers, "workers");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        startWorkers();
    }

    // -------------------- PUBLIC API --------------------

    /**
     * Submit an appointment request to schedule ASAP.
     * Returns a CompletableFuture that completes with the new appointment ID.
     */
    public CompletableFuture<Long> submit(Long doctorId, Long patientId,
                                          LocalDateTime startTime, String notes) {
        validateInputs(doctorId, patientId, startTime);
        CompletableFuture<Long> future = new CompletableFuture<>();
        enqueue(new AppointmentRequest(doctorId, patientId, startTime, notes, 0, future));
        return future;
    }

    /**
     * Submit an appointment request to be attempted after a delay (e.g., future booking window).
     */
    public CompletableFuture<Long> submitWithDelay(Long doctorId, Long patientId,
                                                   LocalDateTime startTime, String notes,
                                                   Duration delay) {
        validateInputs(doctorId, patientId, startTime);
        CompletableFuture<Long> future = new CompletableFuture<>();
        scheduler.schedule(() -> enqueue(new AppointmentRequest(doctorId, patientId, startTime, notes, 0, future)),
                Math.max(0, delay.toMillis()), TimeUnit.MILLISECONDS);
        return future;
    }

    /** Gracefully stop all workers and scheduled tasks. */
    public void shutdown() {
        if (!running.getAndSet(false)) return;
        workers.shutdownNow();
        scheduler.shutdownNow();
    }

    @Override
    public void close() {
        shutdown();
    }

    // -------------------- INTERNALS --------------------

    private void startWorkers() {
        for (int i = 0; i < DEFAULT_WORKERS; i++) {
            workers.submit(this::workerLoop);
        }
    }

    private void workerLoop() {
        while (running.get()) {
            try {
                AppointmentRequest req = queue.poll(500, TimeUnit.MILLISECONDS);
                if (req == null) continue;
                processRequest(req);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable t) {
                // swallow & continue; individual request futures handle their own errors
            }
        }
    }

    private void processRequest(AppointmentRequest req) {
        // Short-circuit if already completed (e.g., cancelled upstream)
        if (req.resultFuture.isDone()) return;

        try {
            // Conflict check before attempting to create the appointment
            List<AppointmentConflict> conflicts = appointmentService.checkConflicts(
                    req.doctorId, req.start, req.start.plus(SLOT_DURATION));

            if (!conflicts.isEmpty()) {
                retryOrFail(req, new IllegalStateException("Conflict detected: " + conflicts));
                return;
            }

            Appointment appt = new Appointment(
                    null,
                    req.patientId,
                    req.doctorId,
                    req.start,
                    "SCHEDULED",
                    req.notes
            );

            Long id = appointmentService.schedule(appt);
            req.resultFuture.complete(id);

        } catch (IllegalStateException conflict) {
            // Business-level conflict thrown by service
            retryOrFail(req, conflict);
        } catch (Exception ex) {
            // Transient/system error (DB hiccup, connection reset, etc.)
            retryOrFail(req, ex);
        }
    }

    private void retryOrFail(AppointmentRequest req, Exception cause) {
        if (req.attempt + 1 >= MAX_ATTEMPTS) {
            req.resultFuture.completeExceptionally(
                    new RuntimeException("Failed to schedule after " + MAX_ATTEMPTS + " attempts", cause));
            return;
        }
        // Exponential backoff with jitter
        long base = INITIAL_BACKOFF.multipliedBy(1L << req.attempt).toMillis();
        long jitter = ThreadLocalRandom.current().nextLong(250, 750);
        long delayMs = Math.min(TimeUnit.MINUTES.toMillis(5), base + jitter);

        scheduler.schedule(() -> enqueue(req.nextAttempt()), delayMs, TimeUnit.MILLISECONDS);
    }

    private void enqueue(AppointmentRequest req) {
        if (!running.get()) {
            req.resultFuture.completeExceptionally(new RejectedExecutionException("Scheduler shutting down"));
            return;
        }
        queue.offer(req);
    }

    private static void validateInputs(Long doctorId, Long patientId, LocalDateTime startTime) {
        if (doctorId == null || patientId == null || startTime == null) {
            throw new IllegalArgumentException("doctorId, patientId, and startTime are required");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("startTime cannot be in the past");
        }
    }

    private static ThreadFactory namedThreadFactory(String base) {
        return new ThreadFactory() {
            private final ThreadFactory delegate = Executors.defaultThreadFactory();
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = delegate.newThread(r);
                t.setName(base + "-" + counter.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        };
    }

    // Optional: quick lookup by ID helper (not required, but handy in controllers)
    public Optional<Appointment> tryGet(Long id) {
        try {
            return appointmentService.getById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
