package com.company;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class DScoreCalculator {

    private List<List<String>> fileData;
    private double[][] geneData;
    private List<String> geneLabels;
    private List<Integer> geneNumbers;
    private List<Integer> availableClients;
    private static Map<Integer, Double> dScores;

    private void loadData(String filePath) {
        try {
            dScores = Collections.synchronizedMap(new TreeMap<>());
            fileData = new ArrayList<>();
            geneLabels = new ArrayList<>();

            // read in the raw file data
            String row;
            BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
            while ((row = csvReader.readLine()) != null) {
                fileData.add(new ArrayList<>(Arrays.asList(row.split(","))));

            }
            csvReader.close();

            // replace missing genes at the end of rows with 0
            for (int i = 0; i < fileData.size(); i++) {
                while (fileData.get(i).size() < fileData.get(0).size()) {
                    fileData.get(i).add("0");
                }
            }

            // create geneData array now that we know the size of the input
            geneData = new double[fileData.size() - 1][fileData.get(0).size() - 1];

            // convert the gene data to floats and load into geneData, replace missing with 0
            for (int j = 1; j < fileData.size(); j++) {
                geneLabels.add(fileData.get(j).get(0));
                for (int k = 1; k < fileData.get(0).size(); k++) {
                    if (!fileData.get(j).get(k).isEmpty()) {
                        geneData[j - 1][k - 1] = Double.parseDouble(fileData.get(j).get(k));
                    } else {
                        geneData[j - 1][k - 1] = 0.0;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeData(Map<Integer, Double> dScores) {
        try {
            PrintWriter printWriter = new PrintWriter(new File("DScoreOutput.csv"));
            StringBuilder stringBuilder = new StringBuilder();

            for (Map.Entry<Integer, Double> dScore : dScores.entrySet()) {
                stringBuilder.append(geneLabels.get(dScore.getKey())).append(", ").append(dScore.getValue()).append('\n');
            }

            printWriter.write(stringBuilder.toString());
            printWriter.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateDScore (String filePath, List<Client> clients) {
        loadData(filePath);
        Random random = new Random();
        availableClients = Collections.synchronizedList(new ArrayList<>());
        geneNumbers = Collections.synchronizedList(new ArrayList<>());
        for (int i=0; i<geneData.length; i++) {
            geneNumbers.add(i);
        }

        System.out.println("Sending first batch of genes to clients");

        int geneNumber = 0;

        for (int clientNumber=0; geneNumber < geneData.length && clientNumber < clients.size(); clientNumber++, geneNumber++) {
            ClientHandler clientHandler = new ClientHandler(clients.get(clientNumber), geneData[geneNumber], this, geneNumber, clientNumber);
            clientHandler.start();
        }

        System.out.println("Processing remaining genes");

        while (dScores.size() < geneData.length ) {
            if (availableClients.size() > 0) {
                if (geneNumbers.size() > 1) {
                    geneNumber = geneNumbers.get(random.nextInt(geneNumbers.size()));
                } else {
                    geneNumber = geneNumbers.get(0);
                }
                if (!geneNumbers.isEmpty()) {
                    ClientHandler clientHandler = new ClientHandler(clients.get(availableClients.get(0)), geneData[geneNumber], this, geneNumber, availableClients.get(0));
                    availableClients.remove(0);
                    clientHandler.start();
                }
            }
        }

        System.out.println("Writing D-Score results to file");

        writeData(dScores);

        System.out.println("D-Score calculation complete");
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
