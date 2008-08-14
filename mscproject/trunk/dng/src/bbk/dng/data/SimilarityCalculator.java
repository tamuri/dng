package bbk.dng.data;

import java.util.*;

/**
 * Date: 14-Aug-2008 11:08:16
 */
public class SimilarityCalculator {
    final static Double GAP = -5.0;

    SimilarityCalculator() {

    }

    public Map<String[], Double> getArchitectureSimilarityMatrix(ArrayList<String> architectures) {
        // Given an array list of distinct architectures, returns a Map (with key of two
        // architectures) of similarity scores

        Map<String[], Double> similarityMatrix = initialiseArchitectureSimilarityMatrix(architectures);
        Set<String[]> archKeys = similarityMatrix.keySet();

        // Loop over each key
        for (String[] archKey: archKeys) {
            // If the two architectures are identical
            if (archKey[0].equals(archKey[1])) {
                similarityMatrix.put(archKey, 100.0);
            } else {
                Double similarity = getArchitectureSimilarity(archKey[0], archKey[1]);
            }
        }

        return similarityMatrix;
    }

    private Double getArchitectureSimilarity(String archOne, String archTwo) {
        // convert each architecture into lists of domains
        ArrayList<String> domainsOne = new ArrayList<String>(Arrays.asList(archOne.split("\\s")));
        ArrayList<String> domainsTwo = new ArrayList<String>(Arrays.asList(archTwo.split("\\s")));

        // get domain similarities
        Map<String[], Double> domainSimilarities = getDomainSimilarities(domainsOne, domainsTwo);

        // get similarity matrix
        Double[][] similarityMatrix = buildDomainSimilarityMatrix(domainsOne, domainsTwo, domainSimilarities);

        // Perform the Needleman-Wunsch alignment on these two architecture given the similarity matrix


        return null;
    }

    private Double[][] buildDomainSimilarityMatrix(ArrayList<String> domainsY,
                                                   ArrayList<String> domainsX,
                                                   Map<String[], Double> domainSimilarities) {

        // Build matrix for these alignment
        Double[][] matrix = new Double[domainsX.size() + 1][domainsY.size() + 1];

        // Fill the first row and first column with gap values
        for (int x = 0; x < domainsX.size(); x++) matrix[x][0] = GAP * x;

        for (int y = 0; y < domainsY.size(); y++) matrix[0][y] = GAP * y;

        // Fill the rest of the array
        for (int x = 1; x < domainsX.size() + 1; x++) {
            for (int y = 1; y < domainsY.size() + 1; y++) {
                String domainX = domainsX.get(x - 1);
                String domainY = domainsY.get(y - 1);

                double k = matrix[x - 1][y - 1] + domainSimilarities.get(new String[]{domainX, domainY});
                double l = matrix[x - 1][y] + GAP;
                double m = matrix[x][y - 1] + GAP;

                matrix[x][y] = Math.max(k, Math.max(l, m));
            }
        }

        return matrix;
    }


    private Map<String[], Double> getDomainSimilarities(ArrayList<String> domainsOne, ArrayList<String> domainsTwo) {
        Map<String[], Double> domainSimilarities = new HashMap<String[], Double>();

        // for each domain in architecture 1
        for (String domain1: domainsOne) {
            // for each domain in architecture 2
            for (String domain2: domainsTwo) {
                String[] key = new String[]{domain1, domain2};
                // if this pair of domains is equivalent
                if (domain1.equals(domain2)) {
                    domainSimilarities.put(key, 10.0);
                } else if (domain1.substring(0,2).equals("PF") && domain2.substring(0,2).equals("PF")) {
                    // if both are Pfam-A
                    domainSimilarities.put(key, 3.0);
                } else if (domain2.substring(0,2).equals("PB") && domain2.substring(0,2).equals("PB")) {
                    // if both are Pfam-B
                    domainSimilarities.put(key, 1.0);
                } else {
                    // domains don't match
                    domainSimilarities.put(key, 0.0);
                }
            }
        }

        return domainSimilarities;
    }

    private Map<String[], Double> initialiseArchitectureSimilarityMatrix(ArrayList<String> architectures) {
        Map<String[], Double> matrix = new HashMap<String[], Double>();

        for (String archRow: architectures) {
            for (String archColumn: architectures) {
                String[] key = {archRow, archColumn};
                matrix.put(key, 0.0);
            }
        }

        return matrix;
    }
}
