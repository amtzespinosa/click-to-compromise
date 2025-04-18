<%@ page import="java.sql.*, java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>C2 Agents</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    table { width: 100%; border-collapse: collapse; }
    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    th { background-color: #4CAF50; color: white; }
    tr:hover { background-color: #f5f5f5; }
    .command-input { width: 100%; padding: 5px; }
  </style>
</head>
<body>
<h2>Connected Agents</h2>

<table>
  <tr>
    <th>Agent ID</th>
    <th>IP Address</th>
    <th>OS</th>
    <th>Actions</th>
  </tr>

  <%
    // Fetch agents from database
    String url = "jdbc:mysql://localhost:3306/c2server";
    String user = "root";
    String password = "w0DSQGYPsvARiBVTiwKKH3otYZ4rxKGu"; // Secure this!

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection conn = DriverManager.getConnection(url, user, password);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT agent_id, ip, os FROM agents");

      while (rs.next()) {
        String agentId = rs.getString("agent_id");
        String ip = rs.getString("ip");
        String os = rs.getString("os");
  %>
  <tr>
    <td><a href="agent.jsp?id=<%= agentId %>"><%= agentId %></a></td>
    <td><%= ip %></td>
    <td><%= os %></td>
    <td>
      <p>Extra info</p>
    </td>
  </tr>
  <%
      }
      conn.close();
    } catch (Exception e) {
      out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
    }
  %>
</table>
</body>
</html>
