package com.hospital.management.common.migration;

import com.hospital.management.common.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Database Migration Runner for Hospital Management System
 * Executes SQL migration scripts in version order
 */
public class DatabaseMigrationRunner {

    private static final String MIGRATION_PATH = "src/main/resources/database/migrations/";

    public static void bootstrapAndMigrate() {
        System.out.println("🏥 Hospital Management System - Database Bootstrap & Migration");
        System.out.println("=============================================================");

        try {
            if (!DatabaseConfig.testRootConnection()) {
                System.err.println("❌ Cannot connect to MySQL server. Please check your credentials.");
                return;
            }

            DatabaseConfig.createDatabaseIfNotExists();

            if (!DatabaseConfig.testConnection()) {
                System.err.println("❌ Cannot connect to hospital management database.");
                return;
            }

            runMigrations();
            System.out.println("🎉 Bootstrap and migration completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Bootstrap/Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runMigrations() {
        System.out.println("🔧 Starting database migrations...");

        try {
            createMigrationTrackingTable();

            List<String> migrationFiles = getMigrationFiles();

            if (migrationFiles.isEmpty()) {
                System.out.println("⚠️  No migration files found in: " + MIGRATION_PATH);
                return;
            }

            System.out.println("📁 Found migration files: " + migrationFiles);

            for (String migrationFile : migrationFiles) {
                String version = extractVersionFromFilename(migrationFile);

                if (!isMigrationExecuted(version)) {
                    executeMigration(migrationFile, version);
                } else {
                    System.out.println("⏭️  Skipping already executed migration: " + version);
                }
            }

            System.out.println("✅ All migrations completed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Migration failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Migration process failed", e);
        }
    }

    private static void createMigrationTrackingTable() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            String createTableSql = """
                CREATE TABLE IF NOT EXISTS schema_migrations (
                    version VARCHAR(50) PRIMARY KEY,
                    description VARCHAR(255),
                    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    execution_time_ms INT,
                    checksum VARCHAR(64)
                )
            """;

            stmt.execute(createTableSql);
            System.out.println("✅ Migration tracking table ready");

        } catch (SQLException e) {
            System.err.println("❌ Failed to create migration tracking table: " + e.getMessage());
            throw new RuntimeException("Migration setup failed", e);
        }
    }

    private static List<String> getMigrationFiles() {
        try {
            java.io.File migrationDir = new java.io.File(MIGRATION_PATH);
            if (!migrationDir.exists()) {
                System.out.println("📁 Creating migration directory: " + MIGRATION_PATH);
                migrationDir.mkdirs();
                return Arrays.asList();
            }

            return Files.list(Paths.get(MIGRATION_PATH))
                    .filter(path -> path.toString().endsWith(".sql"))
                    .map(path -> path.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error reading migration files: " + e.getMessage());
            return Arrays.asList();
        }
    }

    private static String extractVersionFromFilename(String filename) {
        if (filename.startsWith("V") && filename.contains("__")) {
            return filename.substring(1, filename.indexOf("__")).replace("_", ".");
        }
        return filename.replace(".sql", "");
    }

    private static boolean isMigrationExecuted(String version) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) as count FROM schema_migrations WHERE version = '" + version + "'"
            );
            rs.next();
            return rs.getInt("count") > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Execute a single migration file with proper SQL parsing
     */
    private static void executeMigration(String migrationFile, String version) {
        System.out.println("🚀 Executing migration: " + migrationFile);

        long startTime = System.currentTimeMillis();

        try {
            String filePath = MIGRATION_PATH + migrationFile;
            System.out.println("📄 Reading migration file: " + filePath);

            String sqlContent = Files.readString(Paths.get(filePath));
            System.out.println("📖 SQL Content length: " + sqlContent.length() + " characters");

            if (sqlContent.trim().isEmpty()) {
                System.out.println("⚠️  Migration file is empty: " + migrationFile);
                return;
            }

            // Debug: Print the actual SQL content
            System.out.println("🔍 Raw SQL content:");
            System.out.println("===================");
            System.out.println(sqlContent);
            System.out.println("===================");

            try (Connection conn = DatabaseConfig.getConnection()) {

                // Clean up the SQL content
                sqlContent = cleanSqlContent(sqlContent);

                // Parse SQL statements properly
                List<String> statements = parseSqlStatements(sqlContent);
                System.out.println("📝 Found " + statements.size() + " SQL statements to execute");

                int executedCount = 0;

                for (int i = 0; i < statements.size(); i++) {
                    String sql = statements.get(i).trim();
                    if (!sql.isEmpty() && !isCommentLine(sql)) {
                        System.out.println("🔧 [" + (i+1) + "/" + statements.size() + "] Executing SQL: " +
                                sql.substring(0, Math.min(sql.length(), 100)) + "...");

                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                            executedCount++;
                            System.out.println("✅ SQL statement executed successfully");
                        } catch (SQLException e) {
                            System.err.println("❌ SQL execution failed: " + e.getMessage());
                            System.err.println("🔍 Failed SQL: " + sql);
                            throw e;
                        }
                    }
                }

                System.out.println("📊 Successfully executed " + executedCount + " SQL statements");

                // Record migration execution
                long executionTime = System.currentTimeMillis() - startTime;
                String description = extractDescriptionFromFilename(migrationFile);

                String recordSql = String.format(
                        "INSERT INTO schema_migrations (version, description, execution_time_ms) VALUES ('%s', '%s', %d)",
                        version, description, executionTime
                );

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(recordSql);
                    System.out.println("📝 Migration recorded in schema_migrations table");
                }

                System.out.println("✅ Migration completed: " + migrationFile + " (" + executionTime + "ms)");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to execute migration " + migrationFile + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Migration failed: " + migrationFile, e);
        }
    }

    /**
     * Parse SQL statements correctly while preserving order
     */
    private static List<String> parseSqlStatements(String sqlContent) {
        List<String> statements = new ArrayList<>();

        // Split by semicolon but be smart about it
        String[] parts = sqlContent.split(";");

        for (String part : parts) {
            String trimmed = part.trim();

            // Skip empty parts and comments
            if (trimmed.isEmpty() || isCommentLine(trimmed)) {
                continue;
            }

            // If the part doesn't end with a complete statement, it might be part of a multi-line statement
            // For now, let's add each non-empty part as a statement
            statements.add(trimmed);
        }

        return statements;
    }

    private static String cleanSqlContent(String sqlContent) {
        // Remove CREATE DATABASE statements
        sqlContent = sqlContent.replaceAll("(?i)CREATE\\s+DATABASE\\s+IF\\s+NOT\\s+EXISTS[^;]*;", "");
        sqlContent = sqlContent.replaceAll("(?i)CREATE\\s+DATABASE[^;]*;", "");

        // Remove USE statements
        sqlContent = sqlContent.replaceAll("(?i)USE\\s+[^;]*;", "");

        return sqlContent;
    }

    private static boolean isCommentLine(String line) {
        line = line.trim();
        return line.startsWith("--") || line.startsWith("/*") || line.startsWith("*");
    }

    private static String extractDescriptionFromFilename(String filename) {
        if (filename.contains("__")) {
            return filename.substring(filename.indexOf("__") + 2).replace(".sql", "").replace("_", " ");
        }
        return filename.replace(".sql", "");
    }

    public static void main(String[] args) {
        bootstrapAndMigrate();
    }
}
