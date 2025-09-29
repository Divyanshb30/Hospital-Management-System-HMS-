package com.hospital.management.inventory.service;

import com.hospital.management.inventory.model.StockAlert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * StockMonitor (no notifications)
 * -------------------------------
 * Scans inventory to:
 *  1) ensure low-stock alerts exist for items at/below threshold
 *  2) resolve alerts for items that recovered above threshold
 *
 * Use start()/stop() for periodic scans, or call runScanOnce() to scan synchronously.
 */
public class StockMonitor implements Runnable {

    private final InventoryService inventoryService;

    // Single background scheduler (daemon)
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "StockMonitor");
        t.setDaemon(true);
        return t;
    });

    // Config (mutable at runtime)
    private volatile int lowStockThreshold = 10;                    // default threshold
    private volatile Duration scanInterval = Duration.ofMinutes(5); // how often to scan

    private ScheduledFuture<?> future;

    public StockMonitor(InventoryService inventoryService) {
        this.inventoryService = Objects.requireNonNull(inventoryService, "inventoryService must not be null");
    }

    /** Start periodic scanning; idempotent. */
    public synchronized void start() {
        if (future != null && !future.isCancelled() && !future.isDone()) return;
        future = scheduler.scheduleAtFixedRate(this, 0, scanInterval.toMinutes(), TimeUnit.MINUTES);
    }

    /** Stop periodic scanning (keeps scheduler alive so you can start() again). */
    public synchronized void stop() {
        if (future != null) {
            future.cancel(false);
            future = null;
        }
    }

    /** Permanently shutdown underlying executor (call on app shutdown). */
    public synchronized void shutdown() {
        stop();
        scheduler.shutdownNow();
    }

    /** Update scan cadence at runtime; reschedules if already running. */
    public synchronized void setScanInterval(Duration newInterval) {
        if (newInterval == null || newInterval.isNegative() || newInterval.isZero())
            throw new IllegalArgumentException("scanInterval must be positive");
        this.scanInterval = newInterval;
        if (future != null) { stop(); start(); }
    }

    public void setLowStockThreshold(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException("threshold must be >= 0");
        this.lowStockThreshold = threshold;
    }

    /** Trigger a single scan synchronously (no background thread needed). */
    public void runScanOnce() {
        runInternal();
    }

    @Override
    public void run() {
        try {
            runInternal();
        } catch (Throwable t) {
            // Avoid crashing the scheduler; just log
            t.printStackTrace();
        }
    }

    private void runInternal() {
        // 1) Create/refresh alerts for low stock
        int created = inventoryService.ensureLowStockAlertsForAll(lowStockThreshold);

        // 2) Resolve alerts where stock has recovered
        int resolved = inventoryService.resolveRecoveredStockAlerts(lowStockThreshold);

        // Optional: simple log
        // System.out.printf("[StockMonitor] cycle: created=%d, resolved=%d @ %s%n",
        //         created, resolved, LocalDateTime.now());
    }

    // --------- (kept only if you ever need to iterate two lists; not used here) ---------
    @SuppressWarnings("unused")
    private static <T> Iterable<T> concat(List<T> a, List<T> b) {
        return () -> new Iterator<>() {
            int i = 0, j = 0;
            @Override public boolean hasNext() { return i < a.size() || j < b.size(); }
            @Override public T next() {
                if (i < a.size()) return a.get(i++);
                if (j < b.size()) return b.get(j++);
                throw new NoSuchElementException();
            }
        };
    }
}
