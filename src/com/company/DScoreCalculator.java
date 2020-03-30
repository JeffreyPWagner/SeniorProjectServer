package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * The DScoreCalculator represents the dedicated gene processing functionality of the server. It takes in input data,
 * distributes work, and collects results into a final output.
 */
public class DScoreCalculator {

    // All gene data to be processed
    private double[][] geneData;

    // The gene identifiers
    private List<String> geneLabels;

    // Numerical identifiers for the genes to assist in processing
    private List<Integer> geneNumbers;

    // The list of gene scores that will become the final output
    private static Map<Integer, Double> dScores;

    /**
     * Helper method to load the gene data from a CSV file
     * @param filePath the path to the CSV file
     */
    private void loadData(String filePath) {
        try {
            // Instantiate objects to hold gene data
            dScores = Collections.synchronizedMap(new TreeMap<>());
            geneLabels = new ArrayList<>();
            List<List<String>> fileData = new ArrayList<>();

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

    /**
     * Helper method to write the final scores to an output CSV file
     * @param dScores the complete score map
     */
    private void writeData(Map<Integer, Double> dScores) {
        try {

            // Create new stringbuilder and printwriter to assemble the result CSV
            StringBuilder stringBuilder = new StringBuilder();
            PrintWriter printWriter = new PrintWriter(new File("DScoreOutput.csv"));

            // Write the scores and corresponding genes to the stringbuilder, one per line
            for (Map.Entry<Integer, Double> dScore : dScores.entrySet()) {
                stringBuilder.append(geneLabels.get(dScore.getKey())).append(", ").append(dScore.getValue()).append('\n');
            }

            // Write the contents of the stringbuilder to file
            printWriter.write(stringBuilder.toString());
            printWriter.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates d-scores for the genes contained in an input file.
     * @param filePath path to the input file
     * @param clients List of clients available to work
     */
    public void calculateDScore (String filePath, List<Client> clients) {

        // Load the input data
        loadData(filePath);

        // Instantiate a random object to be used in gene assignment
        Random random = new Random();

        // Generate the list of numerical gene identifiers to assist in processing
        for (int z=0; z<3; z++) {
            geneNumbers = Collections.synchronizedList(new ArrayList<>());
            for (int i=0; i<geneData.length; i++) {
                geneNumbers.add(i);
            }

            // Begin gene processing and start timing
            System.out.println("Processing genes");
            long startTime = System.currentTimeMillis();
            int geneNumber = 0;

            // While the number of scores calculated is less than the number of input genes...
            while (dScores.size() < geneData.length) {

                // If there is at least 1 available client...
                if (clients.size() > 0) {

                    // If there is more than one gene left to process, send a random gene that needs to be processed
                    if (geneNumbers.size() > 1) {
                        geneNumber = geneNumbers.get(random.nextInt(geneNumbers.size()));

                    // Otherwise send the last gene
                    } else {
                        geneNumber = geneNumbers.get(0);
                    }

                    // While there is at least one gene left to process, create and start a new handler thread
                    if (!geneNumbers.isEmpty()) {
                        ClientHandler clientHandler = new ClientHandler(clients, clients.remove(0), geneData[geneNumber], this, geneNumber);
                        clientHandler.start();
                    }
                }
            }

            // Once all genes have been scored, output the elapsed wall clock processing time
            System.out.println("Calculation complete, elapsed time " + (System.currentTimeMillis() - startTime) + " milliseconds");
        }

        // Write results to file
        System.out.println("Writing D-Score results to file");
        writeData(dScores);
        System.out.println("D-Score calculation complete");
    }

    /**
     * Getter for the score list
     * @return the score list
     */
    public Map<Integer, Double> getDScores() {
        return dScores;
    }

    /**
     * Getter for the numerical gene identifiers
     * @return the numerical gene identifiers
     */
    public List<Integer> getGeneNumbers() {
        return geneNumbers;
    }
}
