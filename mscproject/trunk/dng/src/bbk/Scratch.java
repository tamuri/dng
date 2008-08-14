package bbk;

import bbk.dng.data.SimilarityCalculator;
import bbk.dng.data.KeyPair;
import bbk.dng.graph.ArchitectureGraphBuilder;

import java.util.ArrayList;
import java.util.Map;

/**
 * Date: 13-Aug-2008 01:03:53
 */
public class Scratch {
    public static void main(String[] args) {
        SimilarityCalculator sc = new SimilarityCalculator();

        // test alignment of keyTwo architectures
        ArrayList<String> architectures = new ArrayList<String>();

        architectures.add("PF00190 PB000466 PF00190");
        architectures.add("PB074724 PF00190 PB000466 PF00190");
        architectures.add("PF00190 PB124409 PB000466 PF00190");
        architectures.add("PB009710 PF00190 PB000466 PF00190");


        Map<KeyPair, Double> matrix = sc.getArchitectureSimilarityMatrix(architectures);

        for(KeyPair architecturePair: matrix.keySet()) {
            System.out.printf("%s\ncompared to\n%s\n=%s\n\n", architecturePair.keyOne, architecturePair.keyTwo, matrix.get(architecturePair));
        }

        ArchitectureGraphBuilder graphBuilder = new ArchitectureGraphBuilder();

        graphBuilder.initialiseGraph(architectures);


    }
}
