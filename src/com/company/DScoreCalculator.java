package com.company;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Socket;
import java.util.*;

public class DScoreCalculator {

    private List<List<String>> fileData;
    private List<List<Double>> geneData;
    private List<String> geneLabels;
    private List<Integer> geneNumbers; //TODO concurrent access
    private List<Integer> availableClients; //TODO concurrent access
    private static Map<Integer, Double> dScores; //TODO concurrent access

    private void loadData(String filePath) {
        try {
            dScores = new TreeMap<>(); //TODO might not need a treemap now
            fileData = new ArrayList<>();
            geneData = new ArrayList<>();
            geneLabels = new ArrayList<>();

            // read in the raw file data
            String row;
            BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
            while ((row = csvReader.readLine()) != null) {
                fileData.add(Arrays.asList(row.split(",")));

            }
            csvReader.close();

            // replace missing genes at the end of rows with 0
            for (int i = 0; i < fileData.size(); i++) {
                while (fileData.get(i).size() < fileData.get(0).size()) {
                    fileData.get(i).add("0");
                }
            }

            // convert the gene data to floats and load into geneData, replace missing with 0
            for (int j = 1; j <  fileData.size(); j++) {
                geneLabels.add(fileData.get(j).get(0));
                List<Double> gene = new ArrayList<>();
                for (int k = 1; k < fileData.get(0).size(); k++) {
                    if (!fileData.get(j).get(k).isEmpty()) {
                        gene.add(Double.parseDouble(fileData.get(j).get(k)));
                    } else {
                        gene.add(0.0);
                    }
                }
                geneData.add(gene);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateDScore (String filePath, List<Socket> clients) {
        loadData(filePath);
        Random random = new Random();
        availableClients = new ArrayList<>();
        geneNumbers = new ArrayList<>();
        for (int i=0; i<geneData.size(); i++) {
            geneNumbers.add(i);
        }

        int clientNumber;

        for (clientNumber=0; clientNumber < geneData.size() && clientNumber < clients.size(); clientNumber++) {
            ClientHandler clientHandler = new ClientHandler(clients.get(clientNumber), geneData.get(clientNumber), this, clientNumber, clientNumber);
            clientHandler.start();
        }

        while (dScores.size() < geneData.size()) {
            if (availableClients.size() > 0) {
                ClientHandler clientHandler = new ClientHandler(clients.get(availableClients.get(0)), geneData.get(geneNumbers.get(random.nextInt(geneNumbers.size()))), this, clientNumber, clientNumber);
                clientHandler.start();
            }
        }


    }

    public Map<Integer, Double> getDScores() {
        return dScores;
    }

    public List<Integer> getGeneNumbers() {
        return geneNumbers;
    }

    public List<Integer> getAvailableClients() {
        return availableClients;
    }
}
