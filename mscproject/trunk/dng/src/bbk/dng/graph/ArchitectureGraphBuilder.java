package bbk.dng.graph;

import prefuse.data.Graph;
import prefuse.data.Table;

import java.util.*;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;

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

    public Graph addEdgesByMatrix(Graph g, Map<Pair<String,String>, Double> matrix, String parentArchitecture) {
        Table graphTable = g.getNodeTable();

        Set<String> connected = new HashSet<String>();
        Set<String> unconnected = new HashSet<String>();
        Map<String, Integer> architectureNodeId = new HashMap<String, Integer>();


        connected.add(parentArchitecture);

        int total =0;
        for (int i = 0; i < graphTable.getRowCount(); i++) {
            String architecture = graphTable.getString(i, "name");
            architectureNodeId.put(architecture, i);
            if (!architecture.equals(parentArchitecture)) {
                unconnected.add(architecture);
            }
            total++;
        }

        Table edgeTable = g.getEdgeTable();

        while (unconnected.size() > 0) {

            double maxscore = -999999;
            ArrayList<String> targetsToRemove = new ArrayList<String>();
            ArrayList<Pair<Integer,Integer>> toConnect = new ArrayList<Pair<Integer,Integer>>();

            for (String c: connected) {
                // find highest scoring pair for 'c' in connected architectures
                for (Pair<String,String> key: matrix.keySet()) {
                    if (Tuple.get1(key).equals(c) && !Tuple.get2(key).equals(c) && unconnected.contains(Tuple.get2(key))) {
                        if (matrix.get(key) > maxscore) {
                            maxscore = matrix.get(key);
                            toConnect.clear();
                            targetsToRemove.clear();
                            toConnect.add(Tuple.from(architectureNodeId.get(c), architectureNodeId.get(Tuple.get2(key))));
                            targetsToRemove.add(Tuple.get2(key));
                            //System.out.printf("adding clear (%s): %s -> %s\n", matrix.get(key), Tuple.get1(key), Tuple.get2(key));
                        } else if (matrix.get(key) == maxscore) {
                            toConnect.add(Tuple.from(architectureNodeId.get(c), architectureNodeId.get(Tuple.get2(key))));
                            targetsToRemove.add(Tuple.get2(key));
                            //System.out.printf("adding existing (%s): %s -> %s\n", matrix.get(key), Tuple.get1(key), Tuple.get2(key));
                        }
                    }
                }
            }

            if (targetsToRemove.size() > 0 && toConnect.size() > 0) {
                for(Pair<Integer,Integer> nodePair: toConnect) {
                    int row = edgeTable.addRow();
                    //System.out.printf("joining %s -> %s\n", Tuple.get1(nodePair), Tuple.get2(nodePair));
                    edgeTable.setInt(row, "source", Tuple.get1(nodePair));
                    edgeTable.setInt(row, "target", Tuple.get2(nodePair));
                }


                for(String target: targetsToRemove) {
                    ArrayList<String> tmp = new ArrayList<String>();
                    for (String u: unconnected) {
                        if (u.equals(target)) {
                            tmp.add(u);
                        }
                    }

                    for (String t: tmp) {
                        boolean b = unconnected.remove(t);
                        System.out.printf("removed %s - %s\n", b, t);
                    }
                    connected.add(target);
                }

            }
            System.out.printf("Connected:%s + %s = %s (=%s)",connected.size(), unconnected.size(), connected.size() + unconnected.size(),
                    total);
            System.out.printf("Total edges:%s\n", edgeTable.getRowCount());
        }

        return g;
    }
}
