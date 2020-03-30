package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Server that listens for new client connection requests and provides them with sockets.
 */
public class Server extends Thread {

    // The list of clients available for work
    private List<Client> clients;

    // The server is running
    private boolean running;

    // Server Socket to receive incoming connections
    private ServerSocket serverSocket;

    /**
     * The constructor creates the client list and server socket.
     */
    public Server() {
        try {
            clients = Collections.synchronizedList(new ArrayList<>());
            running = true;
            serverSocket = new ServerSocket(5000);
            System.out.println("The server is running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the server and listens for new clients connecting.
     */
    public void run() {
        try {
            while (running) {

                // Listen for new clients and accept their connections, then add them to the client list
                Socket socket = serverSocket.accept();
                if (running) {
                    clients.add(new Client(socket));
                    System.out.println("New client added");
                }
            }
        } catch (IOException e) {
            System.out.println("Server Stopping");
        }
    }

    /**
     * Setter for the running boolean
     * @param running should the server be running
     */
    public void isRunning(boolean running) {
        this.running = running;
    }

    /**
     * Getter for the server socket
     * @return the server socket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Getter for the available client list
     * @return the available client list
     */
    public List<Client> getClients() {
        return clients;
    }
}
