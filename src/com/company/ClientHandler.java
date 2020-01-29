package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler extends Thread {

    private static BufferedReader bufferedReader;
    private static InputStreamReader inputStreamReader;

    public ClientHandler(Socket socket) {
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() {
        try {
            boolean running = true;
            while (running) {
                String message = bufferedReader.readLine();
                System.out.println(message);
                running = false;
            }
            inputStreamReader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
