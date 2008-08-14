package bbk.dng.data;

import java.util.*;

/**
 * Date: 14-Aug-2008 11:08:16
 */
public class SimilarityCalculator {
    final static Double GAP_SCORE = -5.0;
    final static Double MATCH_SCORE = 20.0;
    final static Double PF_SIMILAR_SCORE = 5.0;
    final static Double PB_SIMILAR_SCORE = 2.0;
    final static Double MISMATCH_SCORE = 0.0;

    public SimilarityCalculator() {

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
                similarityMatrix.put(archKey, similarity);
            }
        }

        return similarityMatrix;
    }

    private Double getArchitectureSimilarity(String archOne, String archTwo) {
        // convert each architecture into lists of domains
        ArrayList<String> domainsOne = new ArrayList<String>(Arrays.asList(archOne.split("\\s")));
        ArrayList<String> domainsTwo = new ArrayList<String>(Arrays.asList(archTwo.split("\\s")));

        // get similarity matrix
        Map<DomainKey, Double> domainSimilarities = getDomainSimilarities(domainsOne, domainsTwo);

        double[][] similarityMatrix = buildDomainSimilarityMatrix(domainsOne, domainsTwo, domainSimilarities);

        // print the domain matrix
        printDomainSimilarityMatrix(domainsOne, domainsTwo, similarityMatrix);




        return getArchitectureAlignmentScore(domainsOne, domainsTwo, similarityMatrix, domainSimilarities);
    }

    private double getArchitectureAlignmentScore(ArrayList<String> domainsOne, ArrayList<String> domainsTwo,
                                                 double[][] similarityMatrix, Map<DomainKey, Double> domainSimilarities) {
        // Perform the Needleman-Wunsch alignment on these two architectures given the domain similarity matrix
        int x = domainsOne.size();
        int y = domainsTwo.size();

        
        List<String> alignedDomainsX = new ArrayList<String>();
        List<String> alignedDomainsY = new ArrayList<String>();

        while (x > 0 && y > 0) {
            String domainX = domainsOne.get(x - 1);
            String domainY = domainsTwo.get(y - 1);

            double score = similarityMatrix[x][y];
            double scoreDiagonal = similarityMatrix[x -1][y-1];
            double scoreUp = similarityMatrix[x][y-1];
            double scoreLeft = similarityMatrix[x-1][y];

            if (score == scoreDiagonal + domainSimilarities.get(new DomainKey(domainX, domainY))) {
                alignedDomainsX.add(domainX);
                alignedDomainsY.add(domainY);
                x--;
                y--;
            } else if (score == scoreLeft + GAP_SCORE) {
                alignedDomainsX.add(domainX);
                alignedDomainsY.add("GAP");
                x--;
            } else if (score == scoreUp + GAP_SCORE) {
                alignedDomainsX.add("GAP");
                alignedDomainsY.add(domainY);
                y--;
            }
        }

        while (x > 0) {
            alignedDomainsX.add(domainsOne.get(x - 1));
            alignedDomainsY.add("GAP");
            x--;
        }

        while (y > 0) {
            alignedDomainsX.add("GAP");
            alignedDomainsY.add(domainsTwo.get(y - 1));
            y--;
        }

        // Now calculate the score
        Collections.reverse(alignedDomainsX);
        Collections.reverse(alignedDomainsY);

        double matches = 0;
        double mismatches = 0;
        double similar = 0;
        double gaps = 0;

        for (int i = 0; i < alignedDomainsX.size(); i++) {
            if (alignedDomainsX.get(i).equals("GAP") | alignedDomainsY.get(i).equals("GAP")) {
                gaps++;
            } else if (alignedDomainsX.get(i).equals(alignedDomainsY.get(i))) {
                matches++;
            } else if (alignedDomainsX.get(i).substring(0,2).equals(alignedDomainsY.get(i).substring(0,2))){
                similar++; // PF or PB aligned
            } else {
                mismatches++;
            }
        }


        double score = 2.0 * (matches + similar / 10.0 - gaps / Math.abs(GAP_SCORE)) / (domainsOne.size() + domainsTwo.size());

        // print the alignment
        System.out.printf("\n\n");
        for (String s : alignedDomainsX) System.out.printf("%s\t\t", s);
        System.out.printf("\n");
        for (String s : alignedDomainsY) System.out.printf("%s\t\t", s);
        System.out.printf("\n\n");

        System.out.printf("%s matches, %s similar, %s gaps, %s mismatches = score %s\n\n",
                matches, similar, gaps, mismatches, score);

        return score * 100.0;
    }


    private double[][] buildDomainSimilarityMatrix(ArrayList<String> domainsX,
                                                   ArrayList<String> domainsY,
                                                   Map<DomainKey, Double> domainSimilarities) {
        // Build matrix for these alignment
        double[][] matrix = new double[domainsX.size() + 1][domainsY.size() + 1];

        // Fill the first row and first column with gap values
        for (int x = 0; x < domainsX.size() + 1; x++) matrix[x][0] = GAP_SCORE * x;

        for (int y = 0; y < domainsY.size() + 1 ; y++) matrix[0][y] = GAP_SCORE * y;

        // Fill the rest of the array
        for (int x = 1; x < domainsX.size() + 1; x++) {
            for (int y = 1; y < domainsY.size() + 1; y++) {
                String domainX = domainsX.get(x - 1);
                String domainY = domainsY.get(y - 1);

                double k = matrix[x - 1][y - 1] + domainSimilarities.get(new DomainKey(domainX, domainY));
                double l = matrix[x - 1][y] + GAP_SCORE;
                double m = matrix[x][y - 1] + GAP_SCORE;

                matrix[x][y] = Math.max(k, Math.max(l, m));
            }
        }
        return matrix;
    }

    private Map<DomainKey, Double> getDomainSimilarities(ArrayList<String> domainsOne, ArrayList<String> domainsTwo) {
        Map<DomainKey, Double> domainSimilarities = new HashMap<DomainKey, Double>();

        // for each domain in architecture 1
        for (String domain1: domainsOne) {
            // for each domain in architecture 2
            for (String domain2: domainsTwo) {
                //String[] key = new String[]{domain1, domain2};
                DomainKey key = new DomainKey(domain1, domain2);
                // if this pair of domains is equivalent
                if (domain1.equals(domain2)) {
                    domainSimilarities.put(key, MATCH_SCORE);
                } else if (domain1.substring(0,2).equals("PF") && domain2.substring(0,2).equals("PF")) {
                    // if both are Pfam-A
                    domainSimilarities.put(key, PF_SIMILAR_SCORE);
                } else if (domain2.substring(0,2).equals("PB") && domain2.substring(0,2).equals("PB")) {
                    // if both are Pfam-B
                    domainSimilarities.put(key, PB_SIMILAR_SCORE);
                } else {
                    // domains don't match
                    domainSimilarities.put(key, MISMATCH_SCORE);
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

    private void printDomainSimilarityMatrix(ArrayList<String> domainsOne, ArrayList<String> domainsTwo, double[][] similarityMatrix) {
        System.out.printf("\t\t");
        for (String s: domainsTwo) System.out.printf("%s\t", s);
        System.out.printf("\n");

        for (int x = 0; x < similarityMatrix.length; x++) {
            if (x == 0) {
                System.out.printf("\t");
            } else {
                System.out.printf("%s\t", domainsOne.get(x - 1));
            }
            for (int y = 0; y < similarityMatrix[x].length; y++) {
                System.out.printf("%s\t", similarityMatrix[x][y]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n\n");
    }
}

class DomainKey {
    public String one;
    public String two;

    DomainKey(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainKey domainKey = (DomainKey) o;

        if (one != null ? !one.equals(domainKey.one) : domainKey.one != null) return false;
        if (two != null ? !two.equals(domainKey.two) : domainKey.two != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (one != null ? one.hashCode() : 0);
        result = 31 * result + (two != null ? two.hashCode() : 0);
        return result;
    }
}