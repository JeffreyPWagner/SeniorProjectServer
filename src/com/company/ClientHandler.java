package com.company;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ClientHandler extends Thread {

    private List<Client> clients;
    private Client client;
    private double[] gene;
    private DScoreCalculator master;
    private int geneNumber;

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

    public void run() {
        try {
            System.out.println("Sending gene " + geneNumber);
            client.getObjectOutputStream().writeObject(gene);
            double dScore = client.getDataInputStream().readDouble();
            System.out.println("Score " + dScore + " received for gene " + geneNumber);
            master.getDScores().putIfAbsent(geneNumber, dScore);
            master.getGeneNumbers().remove(Integer.valueOf(geneNumber));
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
