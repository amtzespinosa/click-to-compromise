package com.amtzespinosa.c2server;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class ConnectionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = "jdbc:mysql://localhost:3306/c2server";
        String user = "root";
        String password = "w0DSQGYPsvARiBVTiwKKH3otYZ4rxKGu"; // Make sure to handle credentials securely

        String sql = "SELECT * FROM agents ORDER BY timestamp DESC";  // SQL query to sort by timestamp

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html><body>");
        writer.println("<h1>Connected Machines</h1>");
        writer.println("<table border='1'><tr><th>ID</th><th>IP Address</th><th>OS</th><th>Timestamp</th></tr>");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String ip = rs.getString("ip");
                String os = rs.getString("os");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                writer.println("<tr><td>" + id + "</td><td>" + ip + "</td><td>" + os + "</td><td>" + timestamp + "</td></tr>");
            }

        } catch (SQLException e) {
            writer.println("<p>Error fetching data: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }

        writer.println("</table>");
        writer.println("</body></html>");
    }
}
