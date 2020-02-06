package com.company;

import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private DataInputStream dataInputStream;
    private ObjectOutputStream objectOutputStream;
    private List<Double> gene;
    private DScoreCalculator master;
    private int geneNumber;
    private int socketNumber;

    public ClientHandler(Socket socket, List<Double> gene, DScoreCalculator master, int geneNumber, int socketNumber) {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
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
            objectOutputStream.writeObject(gene);
            master.getDScores().put(geneNumber,dataInputStream.readDouble());
            master.getAvailableClients().add(socketNumber);
            master.getGeneNumbers().remove(geneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
