package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private  List<ClientHandler> clients;
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
                    ClientHandler handler = new ClientHandler(socket);
                    handler.start();
                    clients.add(handler);
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
}
