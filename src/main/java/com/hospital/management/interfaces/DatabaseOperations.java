package com.hospital.management.interfaces;


import java.sql.Connection;

public interface DatabaseOperations {
    Connection getConnection();
    void closeConnection(Connection conn);
    // Other generic database utility methods if needed
}
