package bbk.dng.graph;

import prefuse.data.Graph;
import prefuse.data.Table;

import java.util.*;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;
import bbk.dng.utils.CollectionUtils;

/**
 * Date: 14-Aug-2008 14:33:23
 */
public class ArchitectureGraphBuilder {
    // Takes an architecture pairwise similarity matrix and builds a graph
    public ArchitectureGraphBuilder () {

    }

    public Graph initialiseGraph(Map<String, List<String>> architectures, String parentArchitecture) {
        Graph g = new Graph();
        g.addColumn("name", String.class);
        g.addColumn("sequences", String.class);
        g.addColumn("parent", boolean.class);

        Table t = g.getNodeTable();
        //Table e = g.getEdgeTable();

        for (String a: architectures.keySet()) {
            int nodeId = t.addRow();
            t.setString(nodeId, "name", a);
            t.setString(nodeId, "sequences", CollectionUtils.join(architectures.get(a), ','));
            if (a.equals(parentArchitecture)) {
                t.setBoolean(nodeId, "parent", true);
            } else {
                t.setBoolean(nodeId, "parent", false);
            }
        }
        return g;
    }

    public Graph addEdgesByMatrix(Graph g, Map<Pair<String,String>, Double> matrix, String parentArchitecture) {
        Table graphTable = g.getNodeTable();

        Set<String> connected = CollectionUtils.newSet();
        Set<String> unconnected = CollectionUtils.newSet();
        Map<String, Integer> architectureNodeId = CollectionUtils.newMap();

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

            double maxscore = -999999;
            List<String> targetsToRemove = CollectionUtils.newList();
            List<Pair<Integer,Integer>> toConnect = CollectionUtils.newList();

            for (String c: connected) {
                // find highest scoring pair for 'c' in connected architectures
                for (Pair<String,String> key: matrix.keySet()) {
                    // TODO: cleanup this code to look for high-scoring score in the 'opposite' direction
                    if (Tuple.get1(key).equals(c) && !Tuple.get2(key).equals(c) && unconnected.contains(Tuple.get2(key))) {
                        if (matrix.get(key) > maxscore) {
                            maxscore = matrix.get(key);
                            toConnect.clear();
                            targetsToRemove.clear();
                            toConnect.add(Tuple.from(architectureNodeId.get(c), architectureNodeId.get(Tuple.get2(key))));
                            targetsToRemove.add(Tuple.get2(key));
                        } else if (matrix.get(key) == maxscore && c.equals(parentArchitecture)) {
                            toConnect.clear();
                            targetsToRemove.clear();
                            toConnect.add(Tuple.from(architectureNodeId.get(c), architectureNodeId.get(Tuple.get2(key))));
                            targetsToRemove.add(Tuple.get2(key));
                        }
                    } else if (Tuple.get2(key).equals(c) && !Tuple.get1(key).equals(c) && unconnected.contains(Tuple.get1(key))) {
                        if (matrix.get(key) > maxscore) {
                            maxscore = matrix.get(key);
                            toConnect.clear();
                            targetsToRemove.clear();
                            toConnect.add(Tuple.from(architectureNodeId.get(c), architectureNodeId.get(Tuple.get1(key))));
                            targetsToRemove.add(Tuple.get1(key));
                        } else if (matrix.get(key) == maxscore && c.equals(parentArchitecture)) {
                            toConnect.clear();
                            targetsToRemove.clear();
                            toConnect.add(Tuple.from(architectureNodeId.get(c), architectureNodeId.get(Tuple.get1(key))));
                            targetsToRemove.add(Tuple.get1(key));
                        }
                    }
                }
            }

            if (targetsToRemove.size() > 0 && toConnect.size() > 0) {
                for(Pair<Integer,Integer> nodePair: toConnect) {
                    int row = edgeTable.addRow();
                    edgeTable.setInt(row, "source", Tuple.get1(nodePair));
                    edgeTable.setInt(row, "target", Tuple.get2(nodePair));
                    edgeTable.setString(row, "name", Double.toString(maxscore));
                }

                for(String target: targetsToRemove) {
                    List<String> tmp = CollectionUtils.newList();
                    for (String u: unconnected) {
                        if (u.equals(target)) {
                            tmp.add(u);
                        }
                    }

                    for (String t: targetsToRemove) {
                        unconnected.remove(t);
                    }
                    connected.add(target);
                }

            }
        }
        return g;
    }
}
