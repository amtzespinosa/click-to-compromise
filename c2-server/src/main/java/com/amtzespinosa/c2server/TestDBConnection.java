package com.amtzespinosa.c2server;

import java.sql.*;

public class TestDBConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/c2server?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "w0DSQGYPsvARiBVTiwKKH3otYZ4rxKGu";

        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS agents (
                id INT AUTO_INCREMENT PRIMARY KEY,
                agent_id VARCHAR(64) NOT NULL,
                ip VARCHAR(45),
                os VARCHAR(255),
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL driver
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("[+] Successfully connected to the database!");

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableSQL);
                System.out.println("[+] 'agents' table has been created or already exists.");
            }

            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
