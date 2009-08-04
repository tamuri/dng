package bbk.dng.actions;

import bbk.dng.Constants;
import bbk.dng.graph.RenderingCountdown;
import bbk.dng.ui.panels.AppFrame;
import bbk.dng.Main;
import bbk.dng.graph.CustomizedForceDirectedLayout;
import bbk.dng.graph.PrintUtilities;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Rectangle;
import prefuse.activity.Activity;
import prefuse.data.tuple.TupleSet;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.util.PrefuseLib;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import java.util.Iterator;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import prefuse.util.ColorLib;

public class GraphStylePanelActions {
  /* RAL 3 Jul 09 --> */
  public static final String DOMAINS_ONLY = "domains_only";
  public static final String ADD_SEQUENCES = "add_sequences";
  public static final String ADD_STRUCTURES = "add_structures";
  public static final String ADD_ENZYMES = "add_enzymes";
  /* <-- RAL 3 Jul 09 */

  private static GraphStylePanelActions instance;
  /* RAL 3 Jul 09 --> */
  private String showNodesStatus = DOMAINS_ONLY;
  /* <-- RAL 3 Jul 09 */
  private String graphRenderingStatus = "running";
  static private String         currentDirectory = ".";
  static private File           currentFile = null;

  private GraphStylePanelActions() {
  }

  public static GraphStylePanelActions getInstance() {
    return instance == null ? instance = new GraphStylePanelActions() : instance;
  }

  private AppFrame appFrame;

  public void toggleGraphAction(AppFrame appFrame) {
    if (graphRenderingStatus.equals("running")) {

      // Stop the graph
      stopGraph(appFrame);
    } else {
// TEST
      appFrame.getGraphPanel().zoomToFit(appFrame.getGraphPanel().getVisualization());
      appFrame.getGraphPanel().getActionLayout().setDuration(Activity.INFINITY);
      appFrame.getGraphPanel().getActionLayout().run();
      graphRenderingStatus = "running";
      appFrame.getGraphStylePanel().getGraphRenderButton().
      /* RAL 3 Jul 09 -->
              setText("Stop Rendering Graph"); */
              setText("Freeze Graph");
      /* <-- RAL 3 Jul 09 */
    }
  }

  public void saveGraphImageAction() {
    /*// save image
    BufferedImage bi = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_ARGB);
    Graphics2D ig2 = bi.createGraphics();
    graphPanel.getVisualization().getDisplay(0).printAll(ig2);

    try {
    ImageIO.write(bi, "PNG", new File("/home/aut/tmp.png"));
    } catch (IOException e) {
    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }*/
  }

  /* RAL 2 Jul 09 -->
  public void addSequenceNodes(AppFrame appFrame) { */
  public void addSequenceNodes(final AppFrame appFrame, String nodeType) {

    String splitChar = ",";
    int node = Constants.SEQ_NODE;
//    int defaultColour = ColorLib.rgb(196,246,255);
    if (nodeType.equals("pdb_codes")) {
      splitChar = ":";
      node = Constants.STRUC_NODE;
//      defaultColour = ColorLib.rgb(255, 255, 179);
    }
    else if (nodeType.equals("enzymes")) {
      splitChar = ":";
      node = Constants.ENZYME_NODE;
    }

    // Set default colour
    int defaultColour =
            ColorLib.rgb(Constants.NODE_COLOUR[node][Constants.DEFAULT_COLOUR][0],
            Constants.NODE_COLOUR[node][Constants.DEFAULT_COLOUR][1],
            Constants.NODE_COLOUR[node][Constants.DEFAULT_COLOUR][2]);

    /* <-- RAL 2 Jul 09 */
    TupleSet ts = appFrame.getGraphPanel().getVisualization().
            getGroup("graph.nodes");
    Iterator iter = ts.tuples();
    while (iter.hasNext()) {
      NodeItem connectorNodeItem = (NodeItem) iter.next();
      Node connectorNode = (Node) connectorNodeItem.getSourceTuple();

      // Get this architecture node's total number of sequences
      int nSeqs = Integer.parseInt(connectorNode.getString("nseqs"));

      if (!connectorNode.getString("name").
              /* RAL 2 Jul 09 -->
              equals(connectorNode.getString("sequences"))) { */
              equals(connectorNode.getString(nodeType))) {
        /* <-- RAL 2 Jul 09 */
        double connectorX = connectorNodeItem.getX();
        double connectorY = connectorNodeItem.getY();
        synchronized (appFrame.getGraphPanel().getVisualization()) {

          /* RAL 2 Jul 09 -->
          for (String s : connectorNode.getString("sequences").split(",")) { */
          String nodeString = connectorNode.getString(nodeType);
          if (nodeString != null && nodeString.length() > 0) {

            /* RAL 14 Jul 09 --> */
            // Define colour type field
            int colour = defaultColour;

            /* <-- RAL 14 Jul 09 */

            // Initialise count of attached nodes
            int count = 0;
            String storedSeqs = "";
            for (String s : nodeString.split(splitChar)) {

              // Check that we haven't already got this sequence
              String sString = splitChar + s + splitChar;
              if (storedSeqs.indexOf(sString) == -1) {
                // Add current search sequence to the list of valid sequences
                storedSeqs = storedSeqs + sString;

                // Increment count of stored strings
                count++;
              }
            }

            // Get total number of nodes accfording to type
            int nTotal = nSeqs;
            if (node == Constants.STRUC_NODE) {
              nTotal = count;
            }

            // Get colour for this node according to node type and number of
            // sequences
            colour = getNodeColour(node, node, nTotal);

            // Re-initialise count
            count = 0;

            // Loop through the store string of sequence ids to create
            // a node for each
            for (String s : storedSeqs.split(splitChar)) {

              // If this is an enzyme node, set its default colour
              if (s.length() > 0 && node == Constants.ENZYME_NODE) {

                // Get the first number
                int dotPos = s.indexOf(".");

                // If dot found, assume this to be an E.C. number
                if (dotPos > -1) {

                  // Determine which E.C. class this node belongs to
                  int ecClass = Integer.parseInt(s.substring(0, 1));

                  if (ecClass > 0 && ecClass < 7) {
                    int iCol = ecClass + 1;

                    // Get the number of sequences that belong to this E.C. class
                    int eSeqs = 1;
                    int iPos = s.indexOf('(');
                    if (iPos > -1) {
                      String numberString = s.substring(iPos + 1);
                      iPos = numberString.indexOf("seqs");
                      if (iPos > -1) {

                        // Get number of sequences
                        numberString = numberString.substring(0, iPos);
                        eSeqs = Integer.parseInt(numberString);
                      }
                    }

                    // Get the colour for this node
                    colour = getNodeColour(node, iCol, eSeqs);
                  } else {
                    colour = ColorLib.rgb(255, 255, 255);
                  }
                }

                // Otherwise, take it to be an SSG class
                else {
                  // Get the class number
                  String numberString;
                  int spacePos = s.indexOf(" ");
                  if (spacePos > -1) {
                    numberString = s.substring(0, spacePos);
                  } else {
                    numberString = s;
                  }
                  int ssgClass = Integer.parseInt(numberString);

                  // If number within range, then assign colour
                  if (ssgClass > -1 && ssgClass < bbk.dng.Constants.NPFAM_COLOURS) {
                    // Get the corresponding colour
                    int iCol = ssgClass % (bbk.dng.Constants.NPFAM_COLOURS);

                    // Convert to rgb
                    String rgbString = bbk.dng.Constants.COLOUR_DEFN[iCol][2];
                    String[] rgbVals = rgbString.split("\\s");
                    int rgb[] = new int[3];
                    int i = 0;
                    for (String ival : rgbVals) {
                      rgb[i] = Integer.parseInt(ival);
                      i++;
                    }

                    // Store the Prefuse colour library's integer encoding of this RGB
                    // colour
                    colour = ColorLib.rgb(rgb[0], rgb[1], rgb[2]);

                  } else {
                    colour = ColorLib.rgb(255, 255, 255);
                  }
                }
              }

              // Add if not too many already attached to this architecture
              if (s.length() > 0 && count < bbk.dng.Constants.MAX_SEQSTR_NODES) {
                /* <-- RAL 2 Jul 09 */
                Node aNode = Main.graph.addNode();
                aNode.setString("name", s);
                aNode.setString("label", s);
                aNode.setString("type", nodeType);
                aNode.setString("sequences", s);
                aNode.setBoolean("parent", false);

                Edge e = Main.graph.addEdge(connectorNode, aNode);
                e.setString("name", "sequence");

                VisualItem aNodeItem = appFrame.getGraphPanel().getVisualization().
                        getVisualItem("graph", aNode);

                /* RAL 14 Jul 09 --> */
                aNodeItem.setFillColor(colour);
                /* <-- RAL 14 Jul 09 */

                PrefuseLib.setX(aNodeItem, null, connectorX);
                PrefuseLib.setY(aNodeItem, null, connectorY);

                /* RAL 10 Jul 09 --> */
                // Increment count
                count++;
                /* <-- RAL 10 Jul 09 */
              }
            }
          }
        }
      }
    }

    // If enzyme nodes switched on, redisplay initial data panel
    boolean enzymesOn = appFrame.getGraphStylePanel().getAddEnzymesRadioButton().isSelected();
    if (!enzymesOn) {
      appFrame.getDataPane().setText(SearchPanelActions.getInstance().getMasterText());
    } else {
      appFrame.getDataPane().setText(SearchPanelActions.getInstance().getEnzymesText());
    }

    EventQueue.invokeLater(new Runnable() {

      public void run() {
        appFrame.getDataScrollPane().getVerticalScrollBar().setValue(0);
      }
    }); 
  }

  /* RAL 2 Jul 09 -->
  public void removeSequenceNodes(AppFrame appFrame) { */
  public void removeSequenceNodes(AppFrame appFrame, String nodeType) {
    TupleSet ts = appFrame.getGraphPanel().getVisualization().
            getGroup("graph.nodes");
    Iterator iter = ts.tuples();
    while (iter.hasNext()) {
      NodeItem connectorNodeItem = (NodeItem) iter.next();
      Node connectorNode = (Node) connectorNodeItem.getSourceTuple();

      /* RAL 2 Jul 09 -->
      if (connectorNode.getString("name").equals(connectorNode.getString("sequences"))) { */
      if (connectorNode.getString("name").equals(connectorNode.getString(nodeType))) {
        synchronized (appFrame.getGraphPanel().getVisualization()) {
          Iterator edges = connectorNode.edges();
          while (edges.hasNext()) {
            Edge edge = (Edge) edges.next();
            Main.graph.removeEdge(edge);
          }
          Main.graph.removeNode(connectorNode);
        }
      }
    }
  }

  /* RAL 3 Jul 09 -->
  public SequenceCheckboxListener getSequenceCheckboxListener(AppFrame appFrame) {
    return new SequenceCheckboxListener(appFrame);
  }
  <-- RAL 3 Jul 09 */

  /* RAL 2 Jul 09 --> */
  // Radio button determining which nodes are to be displayed
  public DomainsOnlyRadioButtonListener getDomainsOnlyRadioButtonListener(AppFrame appFrame) {
    return new DomainsOnlyRadioButtonListener(appFrame);
  }

  public AddSequencesRadioButtonListener getAddSequencesRadioButtonListener(AppFrame appFrame) {
    return new AddSequencesRadioButtonListener(appFrame);
  }

  public AddStructuresRadioButtonListener getAddStructuresRadioButtonListener(AppFrame appFrame) {
    return new AddStructuresRadioButtonListener(appFrame);
  }

  public AddEnzymesRadioButtonListener getAddEnzymesRadioButtonListener(AppFrame appFrame) {
    return new AddEnzymesRadioButtonListener(appFrame);
  }
  /* <-- RAL 2 Jul 09 */

  public SpringLengthChangeListener getSpringLengthChangeListener(AppFrame appFrame) {
    return new SpringLengthChangeListener(appFrame);
  }

  public ConnectionFilterChangeListener getConnectionFilterChangeListener(AppFrame appFrame) {
    return new ConnectionFilterChangeListener(appFrame);
  }

  // Calculate the colour of the given node based on its type and number of sequences
  public int getNodeColour(int node, int iCol, int nSeqs) {

    int colour = ColorLib.rgb(255, 255, 255);

    // Calculate this node's RGB value
    int rgb[] = new int[3];
    if ((node == Constants.SEQ_NODE || node == Constants.STRUC_NODE)
            && nSeqs <= Constants.NSEQS_RANGE[node][0]) {

      // Set to default colour for this node type
      for (int irgb = 0; irgb < 3; irgb++) {
        rgb[irgb] = Constants.NODE_COLOUR[iCol][0][irgb];
      }
    } else {

      // Determine the scaling factor for computing the colour of
      // this node
      double factor = 0;
      if (nSeqs > Constants.NSEQS_RANGE[node][1]) {
        factor = 1;
      } else if (nSeqs > Constants.NSEQS_RANGE[node][0]) {
        factor = (double) (nSeqs - Constants.NSEQS_RANGE[node][0])
                / (double) (Constants.NSEQS_RANGE[node][1]
                - Constants.NSEQS_RANGE[node][0]);
      }

      // Adjust colour according to location in range
      for (int irgb = 0; irgb < 3; irgb++) {
        rgb[irgb] =
                Constants.NODE_COLOUR[iCol][1][irgb]
                + (int) (factor * (double) (Constants.NODE_COLOUR[iCol][2][irgb]
                - Constants.NODE_COLOUR[iCol][1][irgb]));
      }
    }

    // Set this colour
    colour = ColorLib.rgb(rgb[0], rgb[1], rgb[2]);

    // Return the colour
    return colour;
  }

  // Send current graph to printer
  public void printGraphAction(AppFrame appFrame) {

    // Stop graph rendering
    stopGraph(appFrame);

    // Print the current plot
    PrintUtilities.printComponent(appFrame.getGraphPanel());
  }

  // Generate PostScript plot
  public void writePostScriptAction(AppFrame appFrame, boolean useCATH) {
    
    boolean landscape = false;

    // Stop graph rendering
    stopGraph(appFrame);

    // Get file name for output PostScript file
    File file = getPostScriptOutputFile(appFrame);

    // If aborted, then return
    if (file == null) {
      return;
    }

    // Create size options dialogue box
    PSOptions dialogue = new PSOptions(appFrame, true, landscape);
    dialogue.setLocationRelativeTo(appFrame);

    // Show the dialogue
    dialogue.setVisible(true);

    // If not cancelled, get options and plot
    if (!dialogue.cancelled()) {

      // Retrieve the PostScript options entered by the user
      landscape = dialogue.isLandscape();

      // Write the PostScript file
      System.out.println("Writing PostScript file to: " + file.getPath());
      boolean writeError = false;

      // Write out just the required residues from the current PDB file
      try {
        // Get the domain colours map
        Map<String, Integer> domainColour
                = SearchPanelActions.getInstance().getDomainColour();

        // Write out the PostScript file
        WritePSFile writePS = new WritePSFile(file.getPath(), appFrame,
                useCATH, landscape);

        // If file written OK, then show message
        if (writePS.getOK()) {
          // Show message that plot done
          JOptionPane.showMessageDialog(appFrame,
                  "PostScript file written to disk",
                  "File written", JOptionPane.INFORMATION_MESSAGE);
        } else {
          writeError = true;
        }

      } catch (IOException error) {
          writeError = true;
      }

      // If write failed, show warning message
      if (writeError) {
          // Show error message
          JOptionPane.showMessageDialog(appFrame,
                  "<html>Error writing PostScript file<p>" +
                  file.getPath(), "Write error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private Rectangle getNodeBounds(VisualItem item, String domainSeparator) {

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
// DEBUG
    System.out.println("archCount = " + archCount + "  Box width = "
            + boxWidth);
    /* RAL 3 Jul 09 --> */
    // Initialise node height and sequence mid-point
    double height = bbk.dng.Constants.pfamAHeight[aType];

    // Check whether the architecture has any 3D coverage
    String coverage = item.getSourceTuple().getString("3D_coverage");
    if (coverage != null && !coverage.equals("NONE")) {
      // Allow extra height for showing 3D coverage
      height = height + bbk.dng.Constants.extraForCoverage[aType];
    }

    // Return the bounding recvtangle
    return new Rectangle((int) x,
            (int) (y - height / 2 - bbk.dng.Constants.yMargin[aType]),
            (int) boxWidth,
            (int) (height + 2 * bbk.dng.Constants.yMargin[aType]));
  }

  // Get the name of the output PostScript file
  private File getPostScriptOutputFile(AppFrame appFrame) {
    boolean done = false;
    boolean writeFile = false;
    File file = null;

    // Loop until file saved or save operation aborted
    while (done == false) {

      // Get the file from the user
      file = getFileName(appFrame);
      if (file == null) {
        return null;
      }
      String fileName = file.getPath();

      // If filename doesn't have the .ps extension, add one
      if (!fileName.endsWith(".ps")) {
        fileName = fileName + ".ps";
        file = new File(fileName);
      }

      // If filename entered, and exists already, confirm overwrite
      if (file != null && file.exists() == true) {
        // Show dialogue to confirm overwrite
        Object[] options = {"Yes", "No", "Cancel"};
        int n = JOptionPane.showOptionDialog(appFrame,
                "File already exists. Do you want to overwrite it?",
                "File exists",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (n == JOptionPane.YES_OPTION) {
          // Overwrite existing file
          writeFile = done = true;
        } else if (n == JOptionPane.NO_OPTION) {
          // Request new file name
          writeFile = done = false;
        } else if (n == JOptionPane.CANCEL_OPTION) {
          // Cancel save operation altogether
          done = true;
          writeFile = false;
        } else {
          // Cancel save operation altogether
          done = true;
          writeFile = false;
        }
      } else if (file != null) {
        done = true;
        writeFile = true;
      } else {
        done = true;
      }
    }

    // If not OK to write, then return null
    if (!writeFile) {
      file = null;
    }

    // Return the file
    return file;
  }

  // Get filename from user
  private File getFileName(AppFrame appFrame) {
    File file = null;
    String label;

    // Set "waiting" cursor
    Cursor cursor = new Cursor(Cursor.WAIT_CURSOR);
    appFrame.setCursor(cursor);

    // Get the file system view
    FileSystemView fsv = FileSystemView.getFileSystemView();

    // File open dialogue
    JFileChooser fc = new JFileChooser(currentDirectory, fsv);

    // Show files and directories
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    // Define visible file name extensions
    FileNameExtensionFilter drwFilter
            = new FileNameExtensionFilter("PostScript files (*.ps)", "ps");
    fc.addChoosableFileFilter(drwFilter);

    // Get name of output file
    fc.setDialogType(JFileChooser.SAVE_DIALOG);
    label = "Save";

    if (currentFile != null) {
      fc.setSelectedFile(currentFile);
    }

    int returnVal = fc.showDialog(appFrame, label);

    // Unset "waiting" cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    // Get the filename that was selected
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();

      // Get the file name
      String fileName = file.getName();
      String fullName = file.getPath();

      // Save the current directory and file name for future
      // file-selection/saves
      currentDirectory = file.getParent();
      currentFile = file;

      // Open the file
      System.out.println("Opening: " + fullName);
      System.out.println("File name: " + fileName);
      System.out.println("Directory: " + currentDirectory);

    } else {
      System.out.println("Open command cancelled by user.");
    }

    // Return the chosen file, null if nothing chosen
    return file;
  }

  // Stoip plotting of graph and zoom to fill screen
  private void stopGraph(AppFrame appFrame) {
    appFrame.getGraphPanel().getActionLayout().setDuration(1000);
    appFrame.getGraphStylePanel().getGraphRenderButton().
            setText("Plot Graph");
    graphRenderingStatus = "stopped";

    // Zoom graph so that it fills the graph panel
    appFrame.getGraphPanel().zoomToFit(appFrame.getGraphPanel().getVisualization());
  }

  /* RAL 3 Jul 09 -->
  private class SequenceCheckboxListener implements ItemListener {

    private AppFrame appFrame;

    SequenceCheckboxListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        GraphStylePanelActions.getInstance().addSequenceNodes(this.appFrame);
      } else {
        GraphStylePanelActions.getInstance().removeSequenceNodes(this.appFrame);
      }
    }
  } */

  // User clicked on domains-only radio button
  private class DomainsOnlyRadioButtonListener implements ItemListener {

    private AppFrame appFrame;

    DomainsOnlyRadioButtonListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void itemStateChanged(ItemEvent e) {

      // If selected, switch off sequence or structure nodes
      if (e.getStateChange() == ItemEvent.SELECTED) {
        AddRemoveSelectedNodes(this.appFrame,DOMAINS_ONLY);
      }
    }
  }

  // User clicked on add UniProt sequences radio button
  private class AddSequencesRadioButtonListener implements ItemListener {

    private AppFrame appFrame;

    AddSequencesRadioButtonListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void itemStateChanged(ItemEvent e) {

      // If selected, switch off sequence or structure nodes
      if (e.getStateChange() == ItemEvent.SELECTED) {
        AddRemoveSelectedNodes(this.appFrame,ADD_SEQUENCES);
      }
    }
  }

  // Click on add PDB structures radio button
  private class AddStructuresRadioButtonListener implements ItemListener {

    private AppFrame appFrame;

    AddStructuresRadioButtonListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void itemStateChanged(ItemEvent e) {

      // If selected, switch off sequence or structure nodes
      if (e.getStateChange() == ItemEvent.SELECTED) {
        AddRemoveSelectedNodes(this.appFrame,ADD_STRUCTURES);
      }
    }
  }

  // Click on add enzymes radio button
  private class AddEnzymesRadioButtonListener implements ItemListener {

    private AppFrame appFrame;

    AddEnzymesRadioButtonListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void itemStateChanged(ItemEvent e) {

      // If selected, switch off sequence or structure nodes
      if (e.getStateChange() == ItemEvent.SELECTED) {
        AddRemoveSelectedNodes(this.appFrame,ADD_ENZYMES);
      }
    }
  }

  // Add/remove appropriate nodes
  public void AddRemoveSelectedNodes(AppFrame appFrame, String selected) {

    // If any satellite nodes displayed, remove these first
    if (showNodesStatus.equals(ADD_SEQUENCES) ||
            showNodesStatus.equals(ADD_STRUCTURES) ||
            showNodesStatus.equals(ADD_ENZYMES)) {

      // Remove sequence nodes
      GraphStylePanelActions.getInstance().
              removeSequenceNodes(appFrame, "sequences");
    }

    // Add selected nodes and set force factor between nodes
    if (selected.equals(ADD_SEQUENCES)) {
      GraphStylePanelActions.getInstance().
              addSequenceNodes(appFrame, "sequences");
      resetEdgeLengthSlider(appFrame,
              CustomizedForceDirectedLayout.SEQS_FORCE_FACTOR);
    }
    else if (selected.equals(ADD_STRUCTURES)) {
      GraphStylePanelActions.getInstance().
              addSequenceNodes(appFrame, "pdb_codes");
      resetEdgeLengthSlider(appFrame,
              CustomizedForceDirectedLayout.STRUCS_FORCE_FACTOR);
    }
    else if (selected.equals(ADD_ENZYMES)) {
      GraphStylePanelActions.getInstance().
              addSequenceNodes(appFrame, "enzymes");
      resetEdgeLengthSlider(appFrame,
              CustomizedForceDirectedLayout.ENZYME_FORCE_FACTOR);
    } else {
      resetEdgeLengthSlider(appFrame,
              CustomizedForceDirectedLayout.DEFAULT_FORCE_FACTOR);
    }

    // Set status
    showNodesStatus = selected;

    // If graph rendering is stopped, then restart
    if (graphRenderingStatus.equals("stopped")) {
      toggleGraphAction(appFrame);
    }
    /* <-- AUT 24 Feb 10 */
      Thread t = new Thread(new RenderingCountdown(appFrame));
      t.start();
      /* AUT 24 Feb 10 --> */
  }
  /* <-- RAL 3 Jul 09 */

  private class SpringLengthChangeListener implements ChangeListener {

    private AppFrame appFrame;

    SpringLengthChangeListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void stateChanged(ChangeEvent e) {
      JSlider s = (JSlider) e.getSource();
      /* RAL 3 Jul 09 -->
      CustomizedForceDirectedLayout force
              = ((CustomizedForceDirectedLayout) appFrame.getGraphPanel().getActionLayout().get(1));
      force.setFactor(s.getValue()); */
      changeForceFactor(appFrame, s.getValue());

      /* RAL 3 Jul 09 --> */
      // If graph rendering is stopped, then restart
      if (graphRenderingStatus.equals("stopped")) {
        toggleGraphAction(appFrame);
      }
      /* <-- RAL 3 Jul 09 */

      /* <-- AUT 24 Feb 10 */
      Thread t = new Thread(new RenderingCountdown(appFrame));
      t.start();
      /* AUT 24 Feb 10 --> */
    }
  }

  /* RAL 3 Jul 09 --> */
  public void changeForceFactor(AppFrame appFrame, int factor) {
    CustomizedForceDirectedLayout force
            = ((CustomizedForceDirectedLayout) appFrame.getGraphPanel().
            getActionLayout().get(1));
    force.setFactor(factor);
  }
  /* <-- RAL 3 Jul 09 */

  private class ConnectionFilterChangeListener implements ChangeListener {

    private AppFrame appFrame;

    ConnectionFilterChangeListener(AppFrame appFrame) {
      this.appFrame = appFrame;
    }

    public void stateChanged(ChangeEvent e) {
      JSlider s = (JSlider) e.getSource();
      appFrame.getGraphPanel().getFilter().setDistance(s.getValue());
      appFrame.getGraphPanel().getVisualization().runAfter("draw", "layout");

      /* RAL 3 Jul 09 --> */
      // If graph rendering is stopped, then restart
      if (graphRenderingStatus.equals("stopped")) {
        toggleGraphAction(appFrame);
      }
      /* <-- RAL 3 Jul 09 */

      /* <-- AUT 24 Feb 10 */
      Thread t = new Thread(new RenderingCountdown(appFrame));
      t.start();
      /* AUT 24 Feb 10 --> */
    }
  }

  public String getGraphRenderingStatus() {
    return graphRenderingStatus;
  }

  /* RAL 3 Jul 09 --> */
  // Reset the edge length slider
  private void resetEdgeLengthSlider(AppFrame appFrame, int factor) {
    JSlider edgeLengthSlider
            = appFrame.getGraphStylePanel().getEdgeLengthSlider();
    edgeLengthSlider.setValue(factor);
  }
  /* <-- RAL 3 Jul 09 */

}
