package com.company;

import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private Client client;
    private double[] gene;
    private DScoreCalculator master;
    private int geneNumber;
    private int socketNumber;

    public ClientHandler(Client client, double[] gene, DScoreCalculator master, int geneNumber, int socketNumber) {
        try {
            this.client = client;
            this.gene = gene;
            this.geneNumber = geneNumber;
            this.master = master;
            this.socketNumber = socketNumber;

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() {
        try {
            System.out.println("Sending gene " + geneNumber + " to client " + socketNumber);
            client.getObjectOutputStream().writeObject(gene);
            double dScore = client.getDataInputStream().readDouble();
            System.out.println("Score " + dScore + " received for gene " + geneNumber + " from client " + socketNumber);
            master.getDScores().putIfAbsent(geneNumber, dScore);
            master.getGeneNumbers().remove(Integer.valueOf(geneNumber));
            master.getAvailableClients().add(socketNumber);
            System.out.println("Client " + socketNumber + " is available");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
