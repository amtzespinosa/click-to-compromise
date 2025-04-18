package com.amtzespinosa.c2server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class C2Listener extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PORT = 4444;
    private static ServerSocket serverSocket;
    private static ExecutorService clientExecutor;

    // Thread-safe map to store client handlers by agent ID
    protected static final ConcurrentHashMap<String, ClientHandler> clientConnections = new ConcurrentHashMap<>();

    public static ClientHandler getAgent(String agentId) {
        if (agentId == null || agentId.trim().isEmpty()) {
            System.out.println("[-] getAgent: agentId is null or empty");
            return null;
        }
        return clientConnections.get(agentId);
    }


    @Override
    public void init() throws ServletException {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[+] Listening for incoming connections on port " + PORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("[+] Connection received from: " + socket.getInetAddress());

                    ClientHandler handler = new ClientHandler(socket);
                    clientConnections.put(handler.getAgentId(), handler);
                    new Thread(handler).start();

                }
            } catch (IOException e) {
                System.out.println("Error in server socket: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void destroy() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            clientExecutor.shutdown();
            System.out.println("[C2] Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
