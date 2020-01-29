package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static BufferedReader bufferedReader;
    private static InputStreamReader inputStreamReader;
    private static String message = "";

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(5000);
            socket = serverSocket.accept();

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            message = bufferedReader.readLine();

            System.out.println(message);

            inputStreamReader.close();
            bufferedReader.close();
            serverSocket.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
