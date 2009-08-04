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

  private int parentNodeId = 0;

  // Takes an architecture pairwise similarity matrix and builds a graph
  public ArchitectureGraphBuilder() {
  }

  public Graph initialiseGraph(Map<String, List<String>> architectures,
          String parentArchitecture,
          Map<String, List<String>> archPDBListPair,
          Map<String, String> archCoveragePair,
          Map<String, List<String>> archEnzymeListPair,
          Set<Map<String, String>> archSet) {
    Graph g = new Graph();
    g.addColumn("name", String.class);
    g.addColumn("label", String.class);
    g.addColumn("type", String.class);
    g.addColumn("sequences", String.class);
    g.addColumn("parent", boolean.class);
    /* RAL 1 Jul 09 --> */
    g.addColumn("pdb_codes", String.class);
    g.addColumn("3D_coverage", String.class);
    g.addColumn("enzymes", String.class);
    g.addColumn("nseqs", int.class);
    g.addColumn("arch_id", int.class);
    /* <-- RAL 1 Jul 09 */

    Table t = g.getNodeTable();
    //Table e = g.getEdgeTable();

    for (String a : architectures.keySet()) {
      int nodeId = t.addRow();
      t.setString(nodeId, "name", a);
      t.setString(nodeId, "label", a);
      t.setString(nodeId, "type", "architecture");
      t.setString(nodeId, "sequences",
              CollectionUtils.join(architectures.get(a), ','));
      if (a.equals(parentArchitecture)) {
        t.setBoolean(nodeId, "parent", true);
        parentNodeId = nodeId;
      } else {
        t.setBoolean(nodeId, "parent", false);
      }
//            t.setString(nodeId, "nSeqs", architectures.get(a));

      /* RAL 1 Jul 09 --> */
      // Add the list of PDB codes
      t.setString(nodeId, "pdb_codes",
              CollectionUtils.join(archPDBListPair.get(a), ':'));

      // Add the 3D coverage
      t.setString(nodeId, "3D_coverage", archCoveragePair.get(a));

      // Add the enzyme classes, if present
      if (archEnzymeListPair.get(a) != null) {
        t.setString(nodeId, "enzymes",
                CollectionUtils.join(archEnzymeListPair.get(a), ':'));
      }

      // Get the total number of sequences that this architecture node has
      boolean done = false;
      Iterator iArch = archSet.iterator();
      while (iArch.hasNext() && !done) {
        // Get the next architecture record
        Map<String, String> archDetails = (Map<String, String>) iArch.next();

        // Get this architecture
        String architecture = archDetails.get("architecture");

        // If this is our architecture, then save the node number and number of
        // sequences
        if (architecture.equals(a)) {
          // Add to current node
          t.setString(nodeId, "arch_id", archDetails.get("arch_id"));
          t.setString(nodeId, "nseqs", archDetails.get("nseqs"));

          // Set flag that we're done
          done = true;
        }
      }
      /* <-- RAL 1 Jul 09 */
    }
    return g;
  }

  // Add the edges read in from the search results file (replaces the
  // addEdgesByMatrix routine)
  public Graph addEdges(Graph g,
          Map<Pair<String, String>, Integer> connectionsList,
          String parentArchitecture) {
    Table graphTable = g.getNodeTable();
    Table edgeTable = g.getEdgeTable();

    Map<String, Integer> architectureNodeId = CollectionUtils.newMap();

    for (int i = 0; i < graphTable.getRowCount(); i++) {
      String architecture = graphTable.getString(i, "name");
      architectureNodeId.put(architecture, i);
    }

    // Loop through the connections list and add to graph's edges
    for (Pair<String, String> nodePair : connectionsList.keySet()) {
      int row = edgeTable.addRow();

      String architecture1 = Tuple.get1(nodePair);
      String architecture2 = Tuple.get2(nodePair);
      int dist = connectionsList.get(nodePair);

      Pair<Integer, Integer> toConnect = Tuple.from(architectureNodeId.get(architecture1),
              architectureNodeId.get(architecture2));

      edgeTable.setInt(row, "source", Tuple.get1(toConnect));
      edgeTable.setInt(row, "target", Tuple.get2(toConnect));
      edgeTable.setString(row, "name", Integer.toString(dist));
    }

    return g;
  }

  // Get the list of species details
  public int getParentNodeId() {
    return parentNodeId;
  }
}
