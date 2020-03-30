package com.company;

import java.io.EOFException;
import java.net.SocketException;
import java.util.List;

/**
 * The ClientHandler is the thread that is launched to communicate with a client when a piece of data is sent for
 * processing. The thread is only alive until the result is received from the client.
 */
public class ClientHandler extends Thread {

    // A list of available clients for the server
    private List<Client> clients;

    // The client that this thread is connected with
    private Client client;

    // The gene data to be processed
    private double[] gene;

    // The master that spawned this thread and is coordinating the worker clients
    private DScoreCalculator master;

    // The number of the gene being processed
    private int geneNumber;

    /**
     * The constructor for the thread.
     * @param clients A list of available clients for the server
     * @param client The client that this thread is connected with
     * @param gene The gene data to be processed
     * @param master The master that spawned this thread and is coordinating the worker clients
     * @param geneNumber The number of the gene being processed
     */
    public ClientHandler(List<Client> clients, Client client, double[] gene, DScoreCalculator master, int geneNumber) {
        try {
            this.clients = clients;
            this.client = client;
            this.gene = gene;
            this.geneNumber = geneNumber;
            this.master = master;

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Run the thread and communicate data and results with client.
     */
    public void run() {
        try {
            // Send the gene to the client for processing
            System.out.println("Sending gene " + geneNumber);
            client.getObjectOutputStream().writeObject(gene);

            // Wait for the result to come back and record it
            double dScore = client.getDataInputStream().readDouble();
            System.out.println("Score " + dScore + " received for gene " + geneNumber);

            // Add the score to the master's score list if another client did not already process it
            master.getDScores().putIfAbsent(geneNumber, dScore);

            // Mark the gene as processed so it is not processed again by another client
            master.getGeneNumbers().remove(Integer.valueOf(geneNumber));

            // Add the client back to the available list, signaling that it is ready for more work
            clients.add(client);
            System.out.println("Client is available");

        } catch (EOFException e) {
            System.out.println("Client disconnected");
        } catch (SocketException e) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
