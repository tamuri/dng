package bbk;

import bbk.dng.data.SimilarityCalculator;

import java.util.ArrayList;
import java.util.Map;

/**
 * Date: 13-Aug-2008 01:03:53
 */
public class Scratch {
    public static void main(String[] args) {
        SimilarityCalculator sc = new SimilarityCalculator();

        // test alignment of two architectures
        ArrayList<String> architectures = new ArrayList<String>();

        architectures.add("PF00001 PF00002 PF00003 PF00001");
        //architectures.add("PF00001 PF00002 PF00003 PF00001");
        architectures.add("PF00001 PF00002 PF00003");
        architectures.add("PF00002");

        Map<String[], Double> matrix = sc.getArchitectureSimilarityMatrix(architectures);

        for(String[] architecturePair: matrix.keySet()) {
            System.out.printf("%s\ncompared to\n%s\n=%s\n\n", architecturePair[0], architecturePair[1], matrix.get(architecturePair));
        }
    }
}
