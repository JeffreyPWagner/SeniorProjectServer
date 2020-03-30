package com.company;

import java.util.List;
import java.util.Scanner;

/**
 * The main class, which is responsible for collecting and responding to user inputs.
 */
public class Main {

    /**
     * Creates the server and monitors user inputs.
     */
    public static void main(String[] args) {
        try {

            // Create the server that will connect to clients
            Server server = new Server();

            // Obtain a reference to the server's clients to be passed on later
            List<Client> clients = server.getClients();

            // Start the server
            server.start();

            // Listen for user input
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();

                // Stop the server and terminate the program
                if ("stop".equals(input)) {
                    server.isRunning(false);
                    server.getServerSocket().close();
                    break;

                // Calculate d-scores for genes
                } else if ("dscore".equals(input)){
                    DScoreCalculator dScoreCalculator = new DScoreCalculator();
                    System.out.println("Please enter input file path:");
                    dScoreCalculator.calculateDScore(scanner.nextLine(), clients);

                // Alert user of an unknown command
                } else {
                    System.out.println("unknown command");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
