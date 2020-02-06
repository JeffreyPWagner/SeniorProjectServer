package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private  List<Socket> clients;
    private boolean running;
    private ServerSocket serverSocket;

    public Server() {
        try {
            clients = new ArrayList<>();
            running = true;
            serverSocket = new ServerSocket(5000);
            System.out.println("The server is running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (running) {
                Socket socket = serverSocket.accept();
                if (running) {
                    clients.add(socket);
                }
            }
        } catch (IOException e) {
            System.out.println("Server Stopping");
        }
    }

    public void isRunning(boolean running) {
        this.running = running;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<Socket> getClients() {
        return clients;
    }
}
