<%@ page import="java.util.*, com.amtzespinosa.c2server.C2Listener, com.amtzespinosa.c2server.ClientHandler" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Agent Console</title>
  <style>
    .command-entry {
      border: 1px solid #ccc;
      padding: 10px;
      margin-bottom: 10px;
      background: #f8f8f8;
    }
  </style>
</head>
<body>

<h2>Agent Console: <%= request.getParameter("id") %></h2>

<form action="sendCommand" method="post">
  <input type="hidden" name="agentId" value="<%= request.getParameter("id") %>">
  <input type="text" name="command" placeholder="Enter command">
  <button type="submit">Send</button>
</form>

<div id="console">
  <h3>Command History:</h3>
  <div id="command-history">
    <%
      String agentId = request.getParameter("id");
      ClientHandler handler = C2Listener.getAgent(agentId);
      if (handler != null) {
        for (String entry : handler.getCommandHistory()) {
          out.println("<div class='command-entry'><pre>" + entry + "</pre></div>");
        }
      } else {
        out.println("<p>No history available. Agent may be disconnected.</p>");
      }
    %>
  </div>
</div>

</body>
</html>
