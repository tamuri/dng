package bbk.dng.graph;

import prefuse.data.Graph;
import prefuse.data.Table;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

    public Graph addEdgesByMatrix(Graph g, Map<KeyPair, Double> matrix, String parentArchitecture) {
        Table graphTable = g.getNodeTable();

        ArrayList<String> connected = new ArrayList<String>();
        ArrayList<String> unconnected = new ArrayList<String>();
        Map<String, Integer> architectureNodeId = new HashMap<String, Integer>();


        connected.add(parentArchitecture);


        for (int i = 0; i < graphTable.getRowCount(); i++) {
            String architecture = graphTable.getString(i, "name");
            architectureNodeId.put(architecture, i);
            if (!architecture.equals(parentArchitecture)) {
                unconnected.add(architecture);
            }
        }

        Table edgeTable = g.getEdgeTable();

        while (unconnected.size() > 0) {
            System.out.printf("Connected:%s\n",connected.size());
            System.out.printf("Unconnected:%s\n",unconnected.size());
            double maxscore = -999999;
            ArrayList<String> targetsToRemove = new ArrayList<String>();
            ArrayList<KeyPair> toConnect = new ArrayList<KeyPair>();


            for (String c: connected) {
                // find highest scoring pair for 'c' in connected architectures
                for (KeyPair key: matrix.keySet()) {
                    if (key.keyOne.equals(c) && !key.keyTwo.equals(c) && unconnected.contains(key.keyTwo)) {
                        if (matrix.get(key) > maxscore) {
                            maxscore = matrix.get(key);
                            toConnect.clear();
                            targetsToRemove.clear();
                            toConnect.add(new KeyPair(architectureNodeId.get(c).toString(), architectureNodeId.get(key.keyTwo).toString()));
                            targetsToRemove.add(key.keyTwo);
                            System.out.printf("adding clear (%s): %s -> %s\n", matrix.get(key), key.keyOne, key.keyTwo);
                        } else if (matrix.get(key) == maxscore) {
                            toConnect.add(new KeyPair(architectureNodeId.get(c).toString(), architectureNodeId.get(key.keyTwo).toString()));
                            targetsToRemove.add(key.keyTwo);
                            System.out.printf("adding existing (%s): %s -> %s\n", matrix.get(key), key.keyOne, key.keyTwo);
                        }
                    }
                }
            }

            if (targetsToRemove.size() > 0 && toConnect.size() > 0) {
                for(KeyPair nodePair: toConnect) {
                    int row = edgeTable.addRow();
                    System.out.printf("joining %s -> %s\n", nodePair.keyOne, nodePair.keyTwo);
                    edgeTable.setInt(row, "source", Integer.parseInt(nodePair.keyOne));
                    edgeTable.setInt(row, "target", Integer.parseInt(nodePair.keyTwo));
                }

                for(String target: targetsToRemove) {
                    unconnected.remove(target);
                    connected.add(target);
                }

            }
        }

        return g;
    }
}
