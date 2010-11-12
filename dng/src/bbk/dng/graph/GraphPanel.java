package bbk.dng.graph;

import bbk.dng.utils.CollectionUtils;
import prefuse.action.*;
import prefuse.data.Tuple;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.expression.Predicate;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.Visualization;
import prefuse.Display;
import prefuse.Constants;
import prefuse.controls.*;
import prefuse.util.ColorLib;
import prefuse.util.force.*;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.assignment.ColorAction;
import prefuse.activity.Activity;
import prefuse.visual.*;
import prefuse.render.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import prefuse.data.Table;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.visual.sort.ItemSorter;



/**
 * Date: 13-Aug-2008 17:59:03
 */
public class GraphPanel extends JPanel {

  public static final String GRAPH = "graph";
  public static final String NODES = "graph.nodes";
  public static final String EDGES = "graph.edges";
  public static final String AGGR = "aggregates";
  /* RAL 9 Jul 09 --> */
  public static final int    GRAPH_NHOPS = 20;
  private static String       fontFamily = null;
  private boolean             startScreen = true;
  public static final String[] PREFERRED_FONT_NAME = {
    "Times New Roman", "Helvetica", "Georgia", "Bookman Old Style", "Arial"
  };
  public final static int    ARCH_NODE = 0;
  public final static int    SATELLITE_NODE = 1;
  public final static String[] NODE_DESC = {"Architecture", "Satellite"};
  /* <-- RAL 9 Jul 09 */

    
  public ActionList getActionLayout() {
    return layout;
  }
  private ActionList layout;
  private Visualization m_vis;
  private GraphDistanceFilter filter;

  public GraphDistanceFilter getFilter() {
    return filter;
  }

  public GraphPanel(boolean useCATH) {
    super(new BorderLayout());

    // new empty visualisation
    m_vis = new Visualization();

    // add visual data groups
    m_vis.setInteractive(EDGES, null, false);
    m_vis.setValue(NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);
    LabelRenderer tr = new LabelRenderer("name");
    //m_vis.setRendererFactory(new DefaultRendererFactory(tr));

    //DefaultRendererFactory rf = new DefaultRendererFactory(new ShapeRenderer(30));

    String domainSeparator = "\\.";
    DefaultRendererFactory rf
            = new DefaultRendererFactory(new ArchitectureImageRenderer(new
            HashMap<String, Integer>(),domainSeparator, useCATH));

    Predicate p0 = ExpressionParser.predicate("name==sequences");
    rf.add(p0, tr);

    m_vis.setRendererFactory(rf);

    // set up the visual operators
    // first set up all the color actions
    ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
    nStroke.setDefaultColor(ColorLib.gray(100));
    nStroke.add("_hover", ColorLib.gray(50));

    ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
    
    nFill.setDefaultColor(ColorLib.gray(255));
    nFill.add("_hover", ColorLib.gray(200));

    ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
    nEdges.setDefaultColor(ColorLib.gray(100));

    ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
    aStroke.setDefaultColor(ColorLib.gray(200));
    aStroke.add("_hover", ColorLib.rgb(255, 100, 100));

    TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
    focusGroup.addTupleSetListener(new TupleSetListener() {

      public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
        for (Tuple aRem : rem) {
          ((VisualItem) aRem).setFixed(false);
        }
        for (Tuple anAdd : add) {
          ((VisualItem) anAdd).setFixed(false);
          ((VisualItem) anAdd).setFixed(true);
        }
        if (ts.getTupleCount() == 0) {
          ts.addTuple(rem[0]);
          ((VisualItem) rem[0]).setFixed(false);
        }

        //System.out.printf("%s = %s\n", ts.getTupleCount(), ((VisualItem)add[0]).getSourceTuple().getString("name"));
        m_vis.runAfter("draw", "layout");
      }
    });

    int hops = GRAPH_NHOPS;
    filter = new GraphDistanceFilter(GRAPH, hops);

    // bundle the color actions
    ActionList colors = new ActionList();
    colors.add(filter);
    colors.add(nStroke);
    /* RAL 14 Jul 09 -->
    colors.add(nFill);
    <-- RAL 14 Jul 09 */
    colors.add(nEdges);
    colors.add(aStroke);
    colors.add(new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.gray(0)));

    ForceSimulator fsim = new ForceSimulator(new RungeKuttaIntegrator());

    /*float gravConstant = -1f;
    float minDistance = -1f;
    float theta = 0.9f;

    float drag = 0.004f;
    float springCoeff = 1E-5f;
    float defaultLength = 0f;  //default: 50f
     */
    float gravConstant = -1f;
    //float minDistance = 1000f;
    float minDistance = -1f;
    float theta = 0.9f;

    float drag = 0.004f;
    float springCoeff = 1E-4f;
    float defaultLength = 150f;  //default: 50f


    fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
    fsim.addForce(new DragForce(drag));
    fsim.addForce(new SpringForce(springCoeff, defaultLength));

    //ForceDirectedLayout fdl = new ForceDirectedLayout("graph", fsim, false);
    ForceDirectedLayout fdl = new CustomizedForceDirectedLayout("graph", fsim, false);

    // now create the main layout routine
    layout = new ActionList(Activity.INFINITY);
    layout.add(colors);
    layout.add(fdl);

    //layout.add(new AggregateLayout(AGGR));
    layout.add(new RepaintAction());
    m_vis.putAction("layout", layout);

    Display display = new Display(m_vis);
    display.setDamageRedraw(false);
    display.setSize(500, 500);
    display.pan(250, 250);
    display.setForeground(Color.GRAY);
    display.setBackground(Color.WHITE);
    display.setHighQuality(true);
    display.addControlListener(new ZoomControl());
    display.addControlListener(new ZoomToFitControl());
    display.addControlListener(new PanControl());
    //display.addControlListener(new FocusControl(1));
    display.addControlListener(new NeighborHighlightControl());
    display.addControlListener(new DragControl());
    //display.addControlListener(new RotationControl(Control.MIDDLE_MOUSE_BUTTON));
    //display.addControlListener(new ToolTipControl("name"));
    display.addControlListener(new ToolTipControl("label"));

    /*display.addControlListener(
    new ControlAdapter() {
    public void itemClicked(VisualItem item, MouseEvent evt) {
    System.out.printf("%s -> %s\n", item.getString("name"), item.getString("sequences"));
    }
    }
    );*/

    // Code to get the architecture nodes to come out on top when displaying
    // satellite nodes
    display.setItemSorter(new ItemSorter() {

      @Override
      public int score(VisualItem item) {
        String type = item.getString("type");
        if (type == null || !type.equals("architecture")) {
          return 10;
        } else {
          return 20;
        }
      }
    });

    // set things running
    m_vis.run("layout");

 

    add(display);
  }

  public Visualization getVisualization() {
    return m_vis;
  }

  public void panToParent(Visualization vis) {
    // .getSourceTuple().getBoolean("parent")

    TupleSet t = getVisualization().getGroup(NODES);
    Iterator i = t.tuples();
    while (i.hasNext()) {
      Tuple tup = (Tuple) i.next();
      if (tup.getBoolean("parent")) {
        VisualItem parent = vis.getVisualItem(NODES, tup);
        Display display = vis.getDisplay(0);
        display.panToAbs(new Point((int) parent.getX(),
                (int) parent.getY()));
        display.repaint();
      }
    }
  }

  /* RAL 3 Jul 09 --> */
  // Fit current graph to graph panel
  public void zoomToFit(Visualization vis) {

    // Get the bounds of the current graph
    Display display = vis.getDisplay(0);
    Rectangle2D bounds = display.getItemBounds();

    // Add margin to outside
    double margin = 50 + (int) (1 / display.getScale());
    GraphicsLib.expand(bounds, margin);
    DisplayLib.fitViewToBounds(display, bounds, 1000);
    display.repaint();
  }
  /* <-- RAL 3 Jul 09 --> */

  public void setDomainColours(Map<String, Integer> domainColour,
          String domainSeparator, boolean useCATH) {
    DefaultRendererFactory rf
            = new DefaultRendererFactory(new ArchitectureImageRenderer(domainColour,
            domainSeparator, useCATH));

    LabelRenderer tr = new LabelRenderer("name");
    tr.setRoundedCorner(5, 5);
    tr.setHorizontalPadding(4);

    Predicate p0 = ExpressionParser.predicate("name==sequences");
    rf.add(p0, tr);

    m_vis.setRendererFactory(rf);
  }

  public void clearGraph() {
    Visualization v = getVisualization();
    v.getGroup(Visualization.FOCUS_ITEMS).clear();
    v.removeGroup("graph");
    v.repaint();
  }

  // Remove all nodes and edges except those linking the clicked node to
  // the parent node. If parent node clicked, then reinstate whole network
  public synchronized void tracePathToParent(Visualization vis, VisualItem item,
          String architecture, boolean useCATH) {

    // Get the parent architecture
    String parentArchitecture = findParent(vis);

    // Determine whether this architecture is the parent architecture
    boolean isParent = checkIfParent(item);

    // If parent, then switch on all nodes and edges
    boolean allOn = false;

    // Pick up all the graph nodes
    Map<Integer, VisualItem> nodeMap = getGraphNodesMap(vis);

    // Identify which architecture nodes are in the path from the clicked
    // architecture to the parent node
    boolean[] selectedNode
            = getNodesInPath(vis, nodeMap, architecture, parentArchitecture, isParent);

    // Flag all satellite nodes connected to the switch-on nodes so that they
    // will be retained
    selectedNode = flagSatellites(vis, nodeMap, selectedNode);

    // Switch off all but the selected nodes
    switchNodesOnOff(vis, nodeMap, selectedNode);

    // Ditto for edges
    switchEdgesOnOff(vis, nodeMap, selectedNode);

    // Redisplay graph
    Display display = vis.getDisplay(0);
    display.repaint();
  }

  // Return a map of the visible nodes on the graph
  public Map<Integer, VisualItem> getGraphNodesMap(Visualization vis) {

    // Get the graph nodes
    TupleSet ts = vis.getGroup(NODES);

    // Create a map between the nodes and their visual items
    Map<Integer, VisualItem> nodeMap = CollectionUtils.newMap();

    // Loop to get all the architecture nodes on the plot
    Iterator iter = ts.tuples();
    while (iter.hasNext()) {
      Tuple tup = (Tuple) iter.next();
      VisualItem node = vis.getVisualItem(NODES, tup);

      // Get this node's row number
      int row = node.getRow();

      // Save the row number and link to corresponding visual item
      nodeMap.put(row, node);
    }

    // Return the node map
    return nodeMap;
  }

  // Switch off any unwanted architectures
  private void switchNodesOnOff(Visualization vis, Map<Integer, VisualItem> nodeMap,
          boolean[] selectedNode) {

    // Loop over the nodes to switch on and off
    for (int i = 0; i < nodeMap.size(); i++) {

      // Get this node
      VisualItem node = nodeMap.get(i);

      // If not selected, then switch off
      if (selectedNode[i]) {
        node.setVisible(true);
      } else {
        node.setVisible(false);
      }
    }
  }

  // Find the parent architecture
  private String findParent(Visualization vis) {

    boolean done = false;
    String parentArchitecture = "";

    // Get the graph nodes
    TupleSet ts = vis.getGroup(NODES);

    // Loop to find parent architecture
    Iterator iter = ts.tuples();
    while (iter.hasNext() && !done) {
      Tuple tup = (Tuple) iter.next();
      VisualItem node = vis.getVisualItem(NODES, tup);

      // Check if this is the parent node
      if (checkIfParent(node)) {
        parentArchitecture = node.getString("name");
        done = true;
      }
    }

    // Return the parent architecture
    return parentArchitecture;
  }

    // Determine whether current architecture is the parent architecture
  private boolean checkIfParent(VisualItem item) {
    boolean isParent = false;

    // Get architecture type: normal or parent
    if (item.getSourceTuple().getBoolean("parent")) {
      isParent = true;
    }

    // Return whether parent
    return isParent;
  }

  // Get all nodes in the path between the two given architectures
  private boolean[] getNodesInPath(Visualization vis, Map<Integer, VisualItem> nodeMap,
          String startArchitecture, String endArchitecture, boolean parent) {

    boolean done = false;

    // Create an array of node-flags
    int nNodes = nodeMap.size();
    boolean[] used = new boolean[nNodes];
    boolean[] nodeSelected = new boolean[nNodes];

    // Initialise selected nodes flags
    for (int i = 0; i < nodeMap.size(); i++) {
      nodeSelected[i] = false;
    }

    // If user has clicked in the parent node, then need to return all nodes
    if (parent) {
      for (int i = 0; i < nodeMap.size(); i++) {
        nodeSelected[i] = true;
      }

      // Return the full list of nodes
      return nodeSelected;
    }

    // Initialise paths stack
    Set<List<Integer>> paths = CollectionUtils.newSet();

    // Get the node numbers of the start- and end-nodes
    int startNode = getNodeNumber(nodeMap, startArchitecture);
    int endNode = getNodeNumber(nodeMap, endArchitecture);

    // Create path by placing the start node onto is
    List<Integer> nodeList = CollectionUtils.newList();
    nodeList.add(startNode);

    // Add this first path to the stack of paths to be processed
    paths.add(nodeList);

    // Loop until we have one or more paths to the end architecture
    while (!done) {

      // Initialise new paths added to stack
      Set<List<Integer>> addPaths = CollectionUtils.newSet();
      Set<List<Integer>> removePaths = CollectionUtils.newSet();

      // Get the next path off the stack
      Iterator iter = paths.iterator();
      while (iter.hasNext()) {
        // Pick up the path as a list of nodes
        List<Integer> pathNodes = (List<Integer>) iter.next();

        // Initialise the node-flags
        for (int i = 0; i < nNodes; i++) used[i] = false;

        // Initialise last node to which new nodes will be added
        int lastNode = -1;

        // Loop over the nodes in the current path to set the flags of the
        // used nodes
        Iterator jter = pathNodes.iterator();
        while (jter.hasNext()) {
          // Get the next node
          int iNode = (Integer) jter.next();
          
          // Set this node's used flag
          used[iNode] = true;

          // Save the last node
          lastNode = iNode;
        }

        // Get the edges
        TupleSet ts = vis.getGroup(EDGES);

        // Loop over the edges to create new paths with any connected
        // nodes added
        Iterator kter = ts.tuples();
        while (kter.hasNext()) {

          // Get this edge
          Tuple tup = (Tuple) kter.next();
          VisualItem edge = vis.getVisualItem(EDGES, tup);

          // Get the Table entry for this edge
          Table t = edge.getTable();
          int row = edge.getRow();

          // Get the two nodes that are joined by this edge
          int iNode1 = t.getInt(row, "source");
          int iNode2 = t.getInt(row, "target");

          // If one is the node we are currently processing, then see if we
          // already have the other one
          int otherNode = -1;
          if (iNode1 == lastNode) otherNode = iNode2;
          if (iNode2 == lastNode) otherNode = iNode1;

          // If have the other node, check that it hasn't already been joined to
          if (otherNode > -1) {

            // If node is free, then create a new path with this node added
            if (!used[otherNode]) {

              // Create a new path
              List<Integer> newPath = CollectionUtils.newList();

              // Copy across the existing nodes
              Iterator lter = pathNodes.iterator();
              while (lter.hasNext()) {
                // Get the next node
                int iNode = (Integer) lter.next();

                // Add to new path
                newPath.add(iNode);
              }

              // Add new node to this path
              newPath.add(otherNode);

              // If this latset node is the end node, then this path is
              // complete and we can add it to the output stack
              if (otherNode == endNode) {

                // Add nodes in this path to the output set
                Iterator mter = newPath.iterator();
                while (mter.hasNext()) {
                  // Get the next node
                  int iNode = (Integer) mter.next();

                  // Add to the output set of nodes
                  nodeSelected[iNode] = true;
                }
              } else {

                // Otherwise, add new path to the stack
                addPaths.add(newPath);
              }
            }
          }
        }

        // Remove the current path from the list
        removePaths.add(pathNodes);
      }

      // Remove any paths that need to be removed
      Iterator nter = removePaths.iterator();
      while (nter.hasNext()) {
        // Pick up this new path
        List<Integer> pathNodes = (List<Integer>) nter.next();

        // Add it to our current path stack
        paths.remove(pathNodes);
      }

      // If no new paths added to the stack, then we're done
      if (addPaths.size() == 0) {
        done = true;
      }

      // Otherwise, add new paths to current set
      else {
        nter = addPaths.iterator();
        while (nter.hasNext()) {
          // Pick up this new path
          List<Integer> pathNodes = (List<Integer>) nter.next();

          // Add it to our current path stack
          paths.add(pathNodes);
        }
      }
    }

    // Return the set of nodes in the path(s) between the start and end nodes
    return nodeSelected;
  }

  // Return the node number of the given architecture
  private int getNodeNumber(Map<Integer, VisualItem> nodeMap,
          String architecture) {

    int nodeNumber = -1;

    for (int i = 0; i < nodeMap.size() && nodeNumber == -1; i++) {
      VisualItem node = nodeMap.get(i);
      if (node.getString("name").equals(architecture)) {
        nodeNumber = i;
      }
    }

    // Return the node number
    return nodeNumber;
  }

  // Flag all satellite nodes connected to selected architecture nodes
  private boolean[] flagSatellites(Visualization vis, Map<Integer, VisualItem> nodeMap,
          boolean[] selectedNode) {

    // Get the edges
    TupleSet ts = vis.getGroup(EDGES);

    // Loop over the edges to create new paths with any connected
    // nodes added
    Iterator kter = ts.tuples();
    while (kter.hasNext()) {

      // Get this edge
      Tuple tup = (Tuple) kter.next();
      VisualItem edge = vis.getVisualItem(EDGES, tup);

      // Get the Table entry for this edge
      Table t = edge.getTable();
      int row = edge.getRow();

      // Get the two nodes that are joined by this edge
      int iNode1 = t.getInt(row, "source");
      int iNode2 = t.getInt(row, "target");
      int otherNode = -1;

      // If either node is selected get other one
      if (selectedNode[iNode1]) {
        otherNode = iNode2;
      } else if (selectedNode[iNode2]) {
        otherNode = iNode1;
      }

      // If have other node, then check to see if it is a satellite node
      if (otherNode > -1) {

        // Get this node
        VisualItem node = nodeMap.get(otherNode);

        // If node is a satellite node, then mark to keep it
        if (node.getSourceTuple().getString("name").
              equals(node.getSourceTuple().getString("sequences"))) {
          selectedNode[otherNode] = true;
        }
      }
    }

    // Return the new set of flags
    return selectedNode;
  }

  // Switch off edges between any unselected nodes
  private void switchEdgesOnOff(Visualization vis, Map<Integer, VisualItem> nodeMap,
          boolean[] selectedNode) {

    // Get the edges
    TupleSet ts = vis.getGroup(EDGES);

    // Loop over the edges to create new paths with any connected
    // nodes added
    Iterator kter = ts.tuples();
    while (kter.hasNext()) {

      // Get this edge
      Tuple tup = (Tuple) kter.next();
      VisualItem edge = vis.getVisualItem(EDGES, tup);

      // Get the Table entry for this edge
      Table t = edge.getTable();
      int row = edge.getRow();

      // Get the two nodes that are joined by this edge
      int iNode1 = t.getInt(row, "source");
      int iNode2 = t.getInt(row, "target");

      // If both nodes are switched on, display edge
      if (selectedNode[iNode1] && selectedNode[iNode2]) {
        edge.setVisible(true);
      } else {
        edge.setVisible(false);
      }
    }
  }
}

class CustomRenderer extends ShapeRenderer {

  public CustomRenderer() {
    m_baseSize = 10;
    super.setBaseSize(m_baseSize);
  }

  public CustomRenderer(int size) {
    m_baseSize = 10;
    super.setBaseSize(size);
    m_baseSize = size;
  }

  @Override
  protected Shape getRawShape(VisualItem item) {
    //String sequences = item.getSourceTuple().getString("sequences");
    double x = item.getX();
    if (Double.isNaN(x) || Double.isInfinite(x)) {
      x = 0.0D;
    }
    double y = item.getY();
    if (Double.isNaN(y) || Double.isInfinite(y)) {
      y = 0.0D;
    }
    double w = (double) m_baseSize * item.getSize();
    if (w > 1.0D) {
      x -= w / 2D;
      y -= w / 2D;
    }

    return rectangle(x, y, w, w);
  }
  private int m_baseSize;
}

class ArchitectureImageRenderer extends ShapeRenderer {

  Map<String, Integer> domainColours;
  String domainSeparator;
  private boolean             useCATH = false;

  public ArchitectureImageRenderer(Map<String, Integer> domainColours,
          String domainSeparator, boolean useCATH) {
    this.domainColours = domainColours;
    this.domainSeparator = domainSeparator;
    this.useCATH = useCATH;
  }

  @Override
  protected Shape getRawShape(VisualItem item) {

    // Get architecture type: normal or parent
    int aType = bbk.dng.Constants.NORMAL;
    if (item.getSourceTuple().getBoolean("parent")) {
      aType = bbk.dng.Constants.PARENT;
    }

    String[] arches = item.getSourceTuple().getString("name")
            .split(domainSeparator);

    int archCount = arches.length;
    double boxWidth = 2 * bbk.dng.Constants.xMargin[aType]
            + (bbk.dng.Constants.recWidth[aType] * archCount)
            + (bbk.dng.Constants.gap[aType] * (archCount - 1));
    double x = item.getX() - boxWidth / 2;
    double y = item.getY();

    /* RAL 3 Jul 09 --> */
    // Initialise node height and sequence mid-point
    double height = bbk.dng.Constants.pfamAHeight[aType];

    // Check whether the architecture has any 3D coverage
    String coverage = item.getSourceTuple().getString("3D_coverage");
    if (coverage != null && !coverage.equals("NONE")) {
      // Allow extra height for showing 3D coverage
      height = height + bbk.dng.Constants.extraForCoverage[aType];
    }
    /* <-- RAL 3 Jul 09 */

    /* RAL 3 Jul 09 -->
    return new Rectangle((int) x - 2, (int) (y - pfamAHeight / 2 - 2),
    (int) boxWidth, (int) pfamAHeight + 4); */
    return new Rectangle((int) x,
            (int) (y - height / 2 - bbk.dng.Constants.yMargin[aType]),
            (int) boxWidth,
            (int) (height + 2 * bbk.dng.Constants.yMargin[aType]));
  /* <-- RAL 3 Jul 09 */
  }

  @Override
  public void render(Graphics2D g, VisualItem item) {

    /* RAL 22 Jul 09 -->
    String[] arches = item.getSourceTuple().getString("name").split("\\s"); */
    String[] arches = item.getSourceTuple().getString("name").split(domainSeparator);
    /* <-- RAL 22 Jul 09 */
    int archCount = arches.length;

    // Get architecture type: normal or parent
    int aType = bbk.dng.Constants.NORMAL;
    if (item.getSourceTuple().getBoolean("parent")) {
      aType = bbk.dng.Constants.PARENT;
    }

    // draw box around architecture
    g.setPaint(Color.black);
    double boxWidth = 2 * bbk.dng.Constants.xMargin[aType]
            + (bbk.dng.Constants.recWidth[aType] * archCount)
            + (bbk.dng.Constants.gap[aType] * (archCount - 1));

    double x = item.getX() - boxWidth / 2;
    double y = item.getY();
    double height = bbk.dng.Constants.pfamAHeight[aType];
    double yMid = y;

    // Get the 3D coverage markers
    String coverage = item.getSourceTuple().getString("3D_coverage");
    if (!coverage.equals("NONE")) {
      // Adjust the mid-point of the sequence line
      yMid = y - bbk.dng.Constants.extraForCoverage[aType] / 2;

      // Allow extra height for showing 3D coverage
      height = height + bbk.dng.Constants.extraForCoverage[aType];
    }
    /* <-- RAL 3 Jul 09 */

    /* RAL 3 Jul 09 -->
    item.setBounds(x - 2, y - pfamAHeight / 2 - 2, boxWidth, pfamAHeight + 4); */
    item.setBounds(x, y - height / 2 - bbk.dng.Constants.yMargin[aType], boxWidth,
            height + 2 * bbk.dng.Constants.yMargin[aType]);
    /* <-- RAL 3 Jul 09 */

    // Colour the parent node grey and all others white
    if (aType == bbk.dng.Constants.PARENT) {
    /* RAL 6 Jul 09 -->
      g.setPaint(Color.gray); */
      Color colour = bbk.dng.Constants.getColourFromIntRGB(bbk.dng.Constants.PARENT_COL[0],
              bbk.dng.Constants.PARENT_COL[1],
              bbk.dng.Constants.PARENT_COL[2]);
      g.setPaint(colour);
    /* <-- RAL 6 Jul 09 */
    } else {
      g.setPaint(Color.white);
    }

    /* RAL 3 Jul 09 -->
    g.fill(new Rectangle2D.Double(x - 2, y - pfamAHeight / 2 - 2,
    boxWidth, pfamAHeight + 4));
    g.setPaint(Color.black);
    g.draw(new Rectangle2D.Double(x - 2, y - pfamAHeight / 2 - 2,
    boxWidth, pfamAHeight + 4)); */

    // Draw and fill the node's borders
    g.fill(new Rectangle2D.Double(x, y - height / 2 - bbk.dng.Constants.yMargin[aType],
            boxWidth, height + 2 * bbk.dng.Constants.yMargin[aType]));
    g.setPaint(Color.black);
    g.draw(new Rectangle2D.Double(x, y - height / 2 - bbk.dng.Constants.yMargin[aType],
            boxWidth, height + 2 * bbk.dng.Constants.yMargin[aType]));
    /* <-- RAL 3 Jul 09 */

    // Draw sequence line
    //g.setPaint(Color.black);
    /* RAL 3 Jul 09 -->
    g.draw(new Line2D.Double(x - 2, y, x + 2 + (recWidth * archCount)
            + (gap * (archCount - 1)), y)); */
    g.draw(new Line2D.Double(x, yMid, x + boxWidth, yMid));
    /* <-- RAL 3 Jul 09 */

    //int[] colors = ColorLib.getCategoryPalette(archCount);

    // Loop over the CATH/Pfam domains to plot along sequence line
    for (int i = 0; i < archCount; i++) {

      // Get this domain
      Color c = new Color(this.domainColours.get(arches[i]));

      // Set this domain's height and width to default values
      double domHeight = bbk.dng.Constants.pfamAHeight[aType];
      double domWidth = bbk.dng.Constants.recWidth[aType];
      double shift = 0;

      // For PfamB domain, adjust height
      if (arches[i].substring(0, 2).equals("PB")) {
        domHeight = bbk.dng.Constants.pfamBHeight[aType];
      }

      // If plotting CATH domain, adjust height of any PfamA domains and width
      // of any split-domains
      if (useCATH) {
        // Adjust height if PfamA domain
        if (arches[i].substring(0, 2).equals("PF")) {
          domHeight = bbk.dng.Constants.pfamAHeight[aType + 2];
        }

        // Adjust width if split CATH domain
        else if (arches[i].charAt(0) == 'p') {
          domWidth = bbk.dng.Constants.splitCATHWidth[aType];
          shift = (bbk.dng.Constants.recWidth[aType] - domWidth) / 2;
        }
      }

      // Draw this domain as a coloured rectangle
        g.setPaint(c);
        g.fill(new Rectangle2D.Double(x + bbk.dng.Constants.xMargin[aType] + shift,
                yMid - domHeight / 2, domWidth, domHeight));

      // Draw black border round it
        g.setPaint(Color.black);
        g.draw(new Rectangle2D.Double(x + bbk.dng.Constants.xMargin[aType] + shift,
                yMid - domHeight / 2, domWidth, domHeight));


/*
      if (arches[i].substring(0, 2).equals("PF") ||
              (arches[i].charAt(0) != 'P' && arches[i].charAt(0) != 'p')) {
        g.setPaint(c);
        g.fill(new Rectangle2D.Double(x + xMargin[aType],
                yMid - pfamAHeight[aType] / 2, recWidth[aType], pfamAHeight[aType]));
        g.setPaint(Color.black);
        g.draw(new Rectangle2D.Double(x + xMargin[aType],
                yMid - pfamAHeight[aType] / 2, recWidth[aType], pfamAHeight[aType]));
      } else {
        g.setPaint(c);
        g.fill(new Rectangle2D.Double(x + xMargin[aType],
                yMid - pfamBHeight[aType] / 2, recWidth[aType], pfamBHeight[aType]));
        g.setPaint(Color.black);
        g.draw(new Rectangle2D.Double(x + xMargin[aType],
                yMid - pfamBHeight[aType] / 2, recWidth[aType], pfamBHeight[aType]));
      }
 */

      /* RAL 3 Jul 09 --> */
      // If have 3D coverage for this node, add the appropriate markers
      if (!coverage.equals("NONE")) {

        // Get the starting position
        double yPos = yMid + bbk.dng.Constants.pfamAHeight[aType] / 2
                + bbk.dng.Constants.extraForCoverage[aType] / 2;
        double xStart = x + bbk.dng.Constants.xMargin[aType];
        double xEnd = x + bbk.dng.Constants.xMargin[aType]
                + bbk.dng.Constants.recWidth[aType];

        // Get the character position of the corresponding coverage symbol
        int spos = 2 * i;
        if (spos < coverage.length()) {
          // Get this character
          char ch = coverage.charAt(spos);

          // For full coverage, check for hyphen to previous domain
          if (ch == 'A' && i > 0) {
            // If previous character a hyphen, extend line backwards
            if (coverage.charAt(spos - 1) == '-') {
              xStart = x + bbk.dng.Constants.xMargin[aType] - bbk.dng.Constants.gap[aType];
            }
          }
          
          // If a partial coverage, show short bar
          if (ch == 'P') {
            xStart = x + bbk.dng.Constants.xMargin[aType]
                    + bbk.dng.Constants.recWidth[aType] / 3;
            xEnd = x + bbk.dng.Constants.xMargin[aType]
                    + 2 * bbk.dng.Constants.recWidth[aType] / 3;
          }

          // Draw the coverage bar
          if (ch != '.' && ch != 'F') {
            Color colour
                    = bbk.dng.Constants.getColourFromIntRGB(bbk.dng.Constants.SHADOW_COL[0],
                    bbk.dng.Constants.SHADOW_COL[1],
                    bbk.dng.Constants.SHADOW_COL[2]);
            g.setPaint(colour);
            g.fill(new Rectangle2D.Double(xStart + 1, yPos + 2, xEnd - xStart, 1));
            g.fill(new Rectangle2D.Double(xEnd, yPos + 1, 1, 2));
            colour = bbk.dng.Constants.getColourFromIntRGB(bbk.dng.Constants.COVERAGE_COL[0],
                    bbk.dng.Constants.COVERAGE_COL[1],
                    bbk.dng.Constants.COVERAGE_COL[2]);
            g.setPaint(colour);
            g.fill(new Rectangle2D.Double(xStart, yPos, xEnd - xStart, 2));
          }
        }
      }
      /* <-- RAL 3 Jul 09 */

      x += bbk.dng.Constants.recWidth[aType] + bbk.dng.Constants.gap[aType];
    }
  }

}

