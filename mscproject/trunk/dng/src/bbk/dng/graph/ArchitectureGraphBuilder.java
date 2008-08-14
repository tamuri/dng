package bbk.dng.graph;

import prefuse.data.Graph;
import prefuse.data.Table;

import java.util.Map;
import java.util.ArrayList;

import bbk.dng.data.KeyPair;

/**
 * Date: 14-Aug-2008 14:33:23
 */
public class ArchitectureGraphBuilder {
    // Takes an architecture pairwise similarity matrix and builds a graph
    public ArchitectureGraphBuilder () {

    }

    public Graph initialiseGraph(ArrayList<String> architectures) {
        Graph g = new Graph();
        g.addColumn("name", String.class);

        Table t = g.getNodeTable();

        for (String a: architectures) {
            int nodeId = t.addRow();
            t.setString(nodeId, "name", a);
        }

        return g;
    }

    public Graph addEdgesByMatrix(Graph g, Map<KeyPair, Double> matrix) {
        Table t = g.getEdgeTable();

        

        return g;
    }
}
