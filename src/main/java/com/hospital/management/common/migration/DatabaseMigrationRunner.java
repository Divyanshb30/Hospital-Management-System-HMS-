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
import java.util.regex.Pattern;

/**
 * Database Migration Runner for Hospital Management System
 * Executes SQL migration scripts in version order with improved parsing
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
     * Execute a single migration file with improved SQL parsing
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

            try (Connection conn = DatabaseConfig.getConnection()) {
                // Disable auto-commit for transaction control
                conn.setAutoCommit(false);

                // Clean up the SQL content
                sqlContent = cleanSqlContent(sqlContent);

                // Parse SQL statements with improved logic
                List<String> statements = parseStatements(sqlContent);
                System.out.println("📝 Found " + statements.size() + " SQL statements to execute");

                int executedCount = 0;

                for (int i = 0; i < statements.size(); i++) {
                    String sql = statements.get(i).trim();
                    if (!sql.isEmpty()) {
                        System.out.println("🔧 [" + (i+1) + "/" + statements.size() + "] Executing SQL: " +
                                getStatementType(sql) + "...");

                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                            executedCount++;
                            System.out.println("✅ SQL statement executed successfully");
                        } catch (SQLException e) {
                            System.err.println("❌ SQL execution failed: " + e.getMessage());
                            System.err.println("🔍 Failed SQL: " + sql.substring(0, Math.min(sql.length(), 200)) + "...");
                            // Rollback transaction on error
                            conn.rollback();
                            throw e;
                        }
                    }
                }

                // Commit all changes
                conn.commit();
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
     * Improved SQL statement parsing
     */
    private static List<String> parseStatements(String sqlContent) {
        List<String> statements = new ArrayList<>();

        // Remove comments and split by semicolon
        String cleanedContent = removeComments(sqlContent);

        // Split by semicolon, but be careful about semicolons inside strings
        String[] parts = smartSplit(cleanedContent, ';');

        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                statements.add(trimmed);
            }
        }

        return statements;
    }

    /**
     * Remove SQL comments from content
     */
    private static String removeComments(String sql) {
        // Remove single line comments (-- comment)
        sql = sql.replaceAll("(?m)^\\s*--.*$", "");

        // Remove multi-line comments (/* comment */)
        sql = sql.replaceAll("(?s)/\\*.*?\\*/", "");

        return sql;
    }

    /**
     * Smart split that handles semicolons inside strings properly
     */
    private static String[] smartSplit(String text, char delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        boolean inEscape = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (inEscape) {
                current.append(c);
                inEscape = false;
                continue;
            }

            if (c == '\\') {
                current.append(c);
                inEscape = true;
                continue;
            }

            if (c == '\'' || c == '"') {
                current.append(c);
                inString = !inString;
                continue;
            }

            if (c == delimiter && !inString) {
                result.add(current.toString());
                current = new StringBuilder();
                continue;
            }

            current.append(c);
        }

        if (current.length() > 0) {
            result.add(current.toString());
        }

        return result.toArray(new String[0]);
    }

    /**
     * Get statement type for better logging
     */
    private static String getStatementType(String sql) {
        String upperSql = sql.toUpperCase().trim();
        if (upperSql.startsWith("CREATE TABLE")) return "CREATE TABLE";
        if (upperSql.startsWith("CREATE INDEX")) return "CREATE INDEX";
        if (upperSql.startsWith("ALTER TABLE")) return "ALTER TABLE";
        if (upperSql.startsWith("INSERT")) return "INSERT";
        if (upperSql.startsWith("UPDATE")) return "UPDATE";
        if (upperSql.startsWith("DELETE")) return "DELETE";
        if (upperSql.startsWith("DROP")) return "DROP";
        return "SQL STATEMENT";
    }

    private static String cleanSqlContent(String sqlContent) {
        // Remove CREATE DATABASE statements
        sqlContent = sqlContent.replaceAll("(?i)CREATE\\s+DATABASE\\s+IF\\s+NOT\\s+EXISTS[^;]*;", "");
        sqlContent = sqlContent.replaceAll("(?i)CREATE\\s+DATABASE[^;]*;", "");

        // Remove USE statements
        sqlContent = sqlContent.replaceAll("(?i)USE\\s+[^;]*;", "");

        return sqlContent;
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
