package com.company;

import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            Server server = new Server();
            List<Client> clients = server.getClients();
            server.start();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("stop".equals(input)) {
                    server.isRunning(false);
                    server.getServerSocket().close();
                    break;
                } else if ("dscore".equals(input)){
                    DScoreCalculator dScoreCalculator = new DScoreCalculator();
                    System.out.println("Please enter input file path:");
                    dScoreCalculator.calculateDScore(scanner.nextLine(), clients);
                } else {
                    System.out.println("unknown command");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
