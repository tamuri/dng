/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bbk.dng.actions;

import bbk.dng.graph.GraphPanel;
import bbk.dng.ui.panels.AppFrame;
import prefuse.data.Tuple;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import prefuse.util.ColorLib;
import prefuse.Visualization;
import prefuse.data.Table;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

/**
 *
 * @author roman
 */
class WritePSFile {

  //
  // C O N S T A N T S
  //
  private final static int    X = 0;
  private final static int    Y = 1;


  //
  // F I E L D S
  //
  private boolean        ok = true;

  //
  // C O N S T R U C T O R S
  //

  public WritePSFile(String fileName, AppFrame appFrame, boolean useCATH,
          boolean landscape) throws IOException {
    Color bgColour = Color.white;
    int page = 0;
    int npages = 1;
    float offset[] = new float[2];
    PrintStream out = null;
    PostScript psFile = null;

    // Get the domain separator
    String domainSeparator;
    if (!useCATH) {
      domainSeparator = "\\.";
    } else {
      domainSeparator = "\\_";
    }

    // Get the graph visualization object
    Visualization vis = appFrame.getGraphPanel().getVisualization();

    // Get the extent of the graph
    Rectangle2D bounds = vis.getBounds("graph");

    // Initialise scale
    Scale scale = new Scale(this, bounds, landscape, PostScript.PLOT_MARGIN);

    // Get the scaling factor and plot offsets
    float scaleFactor = scale.getScaleFactor();
    for (int i = 0; i < 2; i++) {
      offset[i] = scale.getOffset(i);
    }

    // Open output .drw file
    try {
      // Open the file
      out = new PrintStream(fileName);

      // Get date and time
      Date today = new Date();

      // Initialise the PostScript-writing routines
      psFile = new PostScript(out);

      // Write out the header records
      String title = "PostScript output";
      psFile.writeMainHeaders(title, bbk.dng.Constants.APPLICATION_NAME,
              bbk.dng.Constants.VERSION, today.toString(), npages);

      // Increment page number
      page++;

      // Write the page header records
      psFile.writePageHeaders(page, npages, bgColour);

      // If plotting in Landscape mode, then issue page-rotation commands
      if (landscape) {
        psFile.writeLandscape();
      }

      // Pick up all the graph nodes
      Map<Integer, VisualItem> nodeMap
              = appFrame.getGraphPanel().getGraphNodesMap(vis);

      // Plot all the graph edges
      plotGraphEdges(psFile, vis, scale, nodeMap);

      // Plot only satellite nodes
      plotGraphNodes(psFile, vis, scale, GraphPanel.SATELLITE_NODE, useCATH,
              domainSeparator);

      // Plot the architecture nodes
      plotGraphNodes(psFile, vis, scale, GraphPanel.ARCH_NODE, useCATH,
              domainSeparator);

    // If error writing file, set flag
    } catch (IOException error) {
      throw error;
    } finally {
      if (out != null) {
        // Set the OK flag
        ok = true;

        // Write out the page end
        psFile.writeEndPage();

        // Write final lines to PostScript file
        psFile.writeClosingLines();

        // Close the file
        out.close();
      }
    }
  }

  //
  // P U B L I C   M E T H O D S
  //

  // Return whether write to disk completed successfully
  public boolean getOK() {
    return ok;
  }

  //
  // P R I V A T E   M E T H O D S
  //

  // Plot all the edges in the graph
  private void plotGraphEdges(PostScript psFile, Visualization vis, Scale scale,
          Map<Integer, VisualItem> nodeMap) {

    Color colour = Color.BLACK;
    float plotWidth;

    // Write remark record
    psFile.psComment("GRAPH EDGES");

    // Set the edge thickness
    plotWidth = scale.getScaleFactor() * bbk.dng.Constants.EDGE_THICKNESS;

    // Get the edges
    TupleSet ts = vis.getGroup(GraphPanel.EDGES);

    // Loop to plot all the edges
    Iterator iter = ts.tuples();
    while (iter.hasNext()) {

      // Get this edge
      Tuple tup = (Tuple) iter.next();
      VisualItem edge = vis.getVisualItem(GraphPanel.EDGES, tup);

      // Get the Table entry for this edge
      Table t = edge.getTable();
      int row = edge.getRow();

      // Get the two nodes that are joined by this edge
      VisualItem node1 = nodeMap.get(t.getInt(row, "source"));
      VisualItem node2 = nodeMap.get(t.getInt(row, "target"));

      // Plot edge only if both nodes are visible
      if (node1.isVisible() && node2.isVisible()) {
        // Get their coords
        float coords1[] = new float[2];
        float coords2[] = new float[2];
        coords1[X] = (float) node1.getX();
        coords1[Y] = (float) node1.getY();
        coords2[X] = (float) node2.getX();
        coords2[Y] = (float) node2.getY();

        // Calculate the PostScript coords of each point
        float psCoords1[] = calcPlotCoords(scale, coords1);
        float psCoords2[] = calcPlotCoords(scale, coords2);

        // Plot this edge
        psFile.psDrawLine(psCoords1[X], psCoords1[Y], psCoords2[X], psCoords2[Y],
                plotWidth, colour);
      }
    }
  }

  // Calculate the PostScript plot coordinates of the given point
  public float[] calcPlotCoords(Scale scale, float[] coords) {
    float psCoords[] = new float[2];

    // Convert to plot coordinates
    psCoords[X] = scale.getScaleFactor() * (scale.getOffset(X) + coords[X]);
    psCoords[Y] = scale.getScaleFactor() * (scale.getOffset(Y) + coords[Y]);

    // Flip the y-value
    psCoords[Y] = scale.getPlotHeight() - psCoords[Y];

    // Return the computed PS coords
    return psCoords;
  }

  // Plot the graph nodes
  private void plotGraphNodes(PostScript psFile, Visualization vis, Scale scale,
          int plotNodeType, boolean useCATH, String domainSeparator) {

    Color colour = Color.BLACK;
    float lineWidth;
    float textSize = scale.getScaleFactor() * bbk.dng.Constants.TEXT_SIZE;
// DEBUG
    int nVisible = 0;
    int nInVisible = 0;

    // Write remark record
    psFile.psComment("GRAPH NODES: " + GraphPanel.NODE_DESC[plotNodeType] + " nodes");

    // Set the edge thickness
    lineWidth = scale.getScaleFactor() * bbk.dng.Constants.LINE_THICKNESS;

    // Get the graph nodes
    TupleSet ts = vis.getGroup(GraphPanel.NODES);

    // Loop to get all the architecture nodes on the plot
    Iterator iter = ts.tuples();
    while (iter.hasNext()) {
      Tuple tup = (Tuple) iter.next();
      VisualItem node = vis.getVisualItem(GraphPanel.NODES, tup);
      boolean parent = false;

      // Get this node's x- and y coords
      double x = node.getX();
      double y = node.getY();

      // Get its width and height
      double width = node.getBounds().getWidth();
      double height = node.getBounds().getHeight();

      // Get the node type
      int nodeType = GraphPanel.ARCH_NODE;
      int aType = bbk.dng.Constants.NORMAL;
      if (!node.getSourceTuple().getString("name").
              equals(node.getSourceTuple().getString("sequences"))) {
        nodeType = GraphPanel.ARCH_NODE;

        // Determine if this is the parent architecture
        if (node.getSourceTuple().getBoolean("parent")) {
          aType = bbk.dng.Constants.PARENT;
          parent = true;
        }
      } else {
        nodeType = GraphPanel.SATELLITE_NODE;
      }
// DEBUG
      if (!node.isVisible()) {
        nInVisible++;
        System.out.println("Invisible node: " + node.getSourceTuple().getString("name"));
      }
      else {
        nVisible++;
      }

      // If this is the right node type, then plot
      if (nodeType == plotNodeType && node.isVisible()) {

        // Get the coords of the SW and NE corners
        float coords1[] = new float[2];
        float coords2[] = new float[2];
        coords1[X] = (float) (x - width / 2);
        coords1[Y] = (float) (y - height / 2);
        coords2[X] = (float) (x + width / 2);
        coords2[Y] = (float) (y + height / 2);

        // Get the fill colour
        int fillColour = node.getFillColor();
        Color fColour = ColorLib.getColor(fillColour);
        if (nodeType == GraphPanel.ARCH_NODE) {
          fColour = Color.white;
          if (aType == bbk.dng.Constants.PARENT) {
            fColour = bbk.dng.Constants.getColourFromIntRGB(bbk.dng.Constants.PARENT_COL[0],
              bbk.dng.Constants.PARENT_COL[1],
              bbk.dng.Constants.PARENT_COL[2]);
          }
        }

        // Calculate the PostScript coords of the two corners
        float psCoords1[] = calcPlotCoords(scale, coords1);
        float psCoords2[] = calcPlotCoords(scale, coords2);

        // Plot the coloured area
        psFile.psUnboundedBox(psCoords1[X], psCoords1[Y], psCoords1[X], psCoords2[Y],
                psCoords2[X], psCoords2[Y], psCoords2[X], psCoords1[Y], fColour);

        // Plot the outer box
        psFile.psDrawLine(psCoords1[X], psCoords1[Y], psCoords1[X], psCoords2[Y],
                lineWidth, colour);
        psFile.psDrawLine(psCoords1[X], psCoords2[Y], psCoords2[X], psCoords2[Y],
                lineWidth, colour);
        psFile.psDrawLine(psCoords2[X], psCoords2[Y], psCoords2[X], psCoords1[Y],
                lineWidth, colour);
        psFile.psDrawLine(psCoords2[X], psCoords1[Y], psCoords1[X], psCoords1[Y],
                lineWidth, colour);

        // If not an architecture node, write the text in the box
        if (nodeType == GraphPanel.SATELLITE_NODE) {
          
          // Calculate the text coords
          coords1[X] = (float) x;
          coords1[Y] = (float) y;
          psCoords1 = calcPlotCoords(scale, coords1);
          
          // Write the text
          psFile.psCentredText(psCoords1[X], psCoords1[Y], textSize, Color.black,
                  node.getSourceTuple().getString("name"));
        }

        // Otherwise, plot the sequence of domains
        else {
          plotDomains(psFile, scale, node, parent, aType, psCoords1, psCoords2,
                  useCATH, domainSeparator);
        }
      }
    }
// DEBUG
    System.out.println("Number of visible nodes    = " + nVisible);
    System.out.println("Number of invisible nodes  = " + nInVisible);
    System.out.println("TOTAL                      = " + (nVisible + nInVisible));
  }

  // Plot the sequence of domains for this architecture node
  private void plotDomains(PostScript psFile, Scale scale, VisualItem node,
          boolean parent, int aType, float[] psCorner1, float[] psCorner2,
          boolean useCATH, String domainSeparator) {

    Color colour = Color.BLACK;
    float coords1[] = new float[2];
    float coords2[] = new float[2];

    // Get the map of domain colours
    Map<String, Integer> domainColour
            = SearchPanelActions.getInstance().getDomainColour();

    // Get the coverage colour
    Color coverageColour
            = bbk.dng.Constants.getColourFromIntRGB(bbk.dng.Constants.COVERAGE_COL[0],
            bbk.dng.Constants.COVERAGE_COL[1],
            bbk.dng.Constants.COVERAGE_COL[2]);

    // Get the shadow colour
    Color shadeColour
            = bbk.dng.Constants.getColourFromIntRGB(bbk.dng.Constants.SHADOW_COL[0],
            bbk.dng.Constants.SHADOW_COL[1],
            bbk.dng.Constants.SHADOW_COL[2]);

    // Get the coordinates of the node's midpoint
    float psX = (psCorner1[X] + psCorner2[X]) / 2;
    float psY = (psCorner1[Y] + psCorner2[Y]) / 2;

    // Set the line thickness
    float lineWidth = scale.getScaleFactor() * bbk.dng.Constants.LINE_THICKNESS;

    // Get the width and height of this node
    float psWidth = psCorner2[X] - psCorner1[X];
    float psHeight = psCorner2[Y] - psCorner1[Y];

    // Extract the individual domains from the architecture string
    String[] arches = node.getSourceTuple().getString("name").split(domainSeparator);

    // Get the number of domains
    int archCount = arches.length;

    // Calculate the height of the sequence line
    coords1[X] = (float) (node.getX() - node.getBounds().getWidth() / 2);
    coords1[Y] = (float) node.getY();

    // Get the 3D coverage markers
    String coverage = node.getSourceTuple().getString("3D_coverage");
    if (!coverage.equals("NONE")) {
      // Adjust the mid-point of the sequence line
      coords1[Y] = (float) (coords1[Y] - bbk.dng.Constants.extraForCoverage[aType] / 2);
    }

    // Convert the midpoint to PostScript coords
    float psCoords1[] = calcPlotCoords(scale, coords1);

    // Draw sequence line
    psFile.psDrawLine(psCorner1[X], psCoords1[Y], psCorner2[X], psCoords1[Y],
            lineWidth, colour);

    // Initialise x-coords
    float x = coords1[X];
    float y = coords1[Y];

    // Loop over the CATH/Pfam domains to plot along sequence line
    for (int i = 0; i < archCount; i++) {

      // Get this domain
      Color domCol = new Color(domainColour.get(arches[i]));

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
        } // Adjust width if split CATH domain
        else if (arches[i].charAt(0) == 'p') {
          domWidth = bbk.dng.Constants.splitCATHWidth[aType];
          shift = (bbk.dng.Constants.recWidth[aType] - domWidth) / 2;
        }
      }

      // Calculate the coords of the SW and NE corners of this domain box
      coords1[X] = (float) (x + bbk.dng.Constants.xMargin[aType] + shift);
      coords1[Y] = (float) (y - domHeight / 2);
      coords2[X] = (float) (coords1[X] + domWidth);
      coords2[Y] = (float) (coords1[Y] + domHeight);

      // Calculate the PostScript coords of the two corners
      psCoords1 = calcPlotCoords(scale, coords1);
      float psCoords2[] = calcPlotCoords(scale, coords2);

      // Plot the coloured area
      psFile.psUnboundedBox(psCoords1[X], psCoords1[Y], psCoords1[X], psCoords2[Y],
              psCoords2[X], psCoords2[Y], psCoords2[X], psCoords1[Y], domCol);

      // Plot the outer box
      psFile.psDrawLine(psCoords1[X], psCoords1[Y], psCoords1[X], psCoords2[Y],
              lineWidth, colour);
      psFile.psDrawLine(psCoords1[X], psCoords2[Y], psCoords2[X], psCoords2[Y],
              lineWidth, colour);
      psFile.psDrawLine(psCoords2[X], psCoords2[Y], psCoords2[X], psCoords1[Y],
              lineWidth, colour);
      psFile.psDrawLine(psCoords2[X], psCoords1[Y], psCoords1[X], psCoords1[Y],
              lineWidth, colour);

      // Plot structural coverage, if any
      if (!coverage.equals("NONE")) {

        // Get the starting position
        double yPos = y + bbk.dng.Constants.pfamAHeight[aType] / 2
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
              xStart = x + bbk.dng.Constants.xMargin[aType]
                      - bbk.dng.Constants.gap[aType];
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
            
            // Define corner coords
            coords1[X] = (float) (xStart + 1);
            coords1[Y] = (float) (yPos + 1);
            coords2[X] = (float) (xEnd + 1);
            coords2[Y] = (float) (yPos + 3);

            // Calculate the PostScript coords of the two corners
            psCoords1 = calcPlotCoords(scale, coords1);
            psCoords2 = calcPlotCoords(scale, coords2);

            // Plot the coloured area
            psFile.psUnboundedBox(psCoords1[X], psCoords1[Y], psCoords1[X], psCoords2[Y],
                    psCoords2[X], psCoords2[Y], psCoords2[X], psCoords1[Y],
                    shadeColour);

            // Define corner coords
            coords1[X] = (float) (xStart);
            coords1[Y] = (float) (yPos);
            coords2[X] = (float) (xEnd);
            coords2[Y] = (float) (yPos + 2);

            // Calculate the PostScript coords of the two corners
            psCoords1 = calcPlotCoords(scale, coords1);
            psCoords2 = calcPlotCoords(scale, coords2);

            // Plot the coloured area
            psFile.psUnboundedBox(psCoords1[X], psCoords1[Y], psCoords1[X], psCoords2[Y],
                    psCoords2[X], psCoords2[Y], psCoords2[X], psCoords1[Y],
                    coverageColour);
          }
        }
      }

      // Shift x-position along
      x += bbk.dng.Constants.recWidth[aType] + bbk.dng.Constants.gap[aType];
    }
  }
}
