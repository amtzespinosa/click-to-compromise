package com.amtzespinosa.c2server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgentServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/c2server";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "w0DSQGYPsvARiBVTiwKKH3otYZ4rxKGu";

    @Override
    public void init() throws ServletException {
        try {
            // Ensure JDBC driver is loaded (Optional for Java 11+, but good for Java 8)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL JDBC Driver not found!", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Agent> agents = new ArrayList<>();
        String query = "SELECT agent_id, ip, os, timestamp FROM agents";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                agents.add(new Agent(
                        rs.getString("agent_id"),
                        rs.getString("ip"),
                        rs.getString("os"),
                        rs.getTimestamp("timestamp")
                ));
            }

            System.out.println("[AgentServlet] Successfully fetched " + agents.size() + " agents from database.");
        } catch (SQLException e) {
            System.err.println("[AgentServlet] Database error: " + e.getMessage());
            e.printStackTrace();
        }

        request.setAttribute("agents", agents);
        request.getRequestDispatcher("agents.jsp").forward(request, response);
    }
}
