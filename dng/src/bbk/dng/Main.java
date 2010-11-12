package bbk.dng;

import bbk.dng.actions.DataPaneActions;
import bbk.dng.actions.GraphActions;
import bbk.dng.actions.GraphStylePanelActions;
import bbk.dng.actions.SearchPanelActions;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.ui.panels.AppFrame;
import edu.stanford.ejalbert.BrowserLauncher;
import java.awt.*;
import java.net.URL;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import prefuse.data.Graph;

/**
 * @author Asif Tamuri and Roman Laskowski
 * @email asif@tamuri.com
 * @date 13-Aug-2008
 *
 * Graph nodes defined in ArchitectureGraphBuilder
 * Node rendering in GraphPanel
 * Sequence/structure/E.C. nodes added in GraphStylePanelActions
 * Data panel information in SearchPanelActions
 * Assign colours to domains in SearchPanelActions
 *
 */
public class Main extends SingleFrameApplication {

  private SwissPfamSearcher searcher;
  private AppFrame appFrame;
  /* RAL 17 Jun 10 --> */
  public static boolean useCATH = false;
  public static boolean useSSG = false;
  /* <-- RAL 17 Jun 10 */
// RAL 22 Oct 10 -->
  public static boolean offLine = false;
  public static boolean addEnzymes = false;
// <-- RAL 22 Oct 10
  public static Graph graph;
  /* RAL 1 Jul 09 --> */
  public static String pfamId = null;
  public static String seqId = null;
  public static String userId = null;
  public static URL imgURL;
  /* <-- RAL 1 Jul 09 */
  /* RAL 9 Jul 09 --> */
  public static int nDomains;
  public static int nSeqs;
  public static int domainType;
  public static final int START_SCREEN = 0;
  public static final int SEARCH_SCREEN = 1;
  public static final int MANY_SEQUENCES = 2;
  public static final int SELECT_DOMAINS = 3;
  /* <-- RAL 9 Jul 09 */

  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  @Override
  protected void initialize(String[] args) {
    /* RAL 1 Jul 09 --> */
    boolean haveIndexDir = false;
    boolean remoteSearch = false;
    String indexDir = null;

    // Get number of command-line parameters entered
    int num = args.length;

    // Initialise variables
    seqId = null;
    pfamId = null;
    userId = null;
  /* RAL 17 Jun 10 --> */
    useCATH = false;
    useSSG = false;
  /* <-- RAL 17 Jun 10 */
// RAL 22 Oct 10 -->
    offLine = false;
    addEnzymes = false;
// <-- RAL 22 Oct 10

    // Loop through command arguments
    for (int i = 0; i < num; i++) {

      // Check for the -u flag indicating that UniProt code follows
      if (args[i].equals("-u")) {

        // Get the UniProt code from the next argument
        if (i < num - 1) {
          seqId = args[i + 1];
          i++;
        }
      }

      // Check for the -p flag indicating that Pfam id follows
      else if (args[i].equals("-p")) {

        // Get the Pfam id from the next argument
        if (i < num - 1) {
          pfamId = args[i + 1];
          i++;
        }
      }

/* RAL 17 Jun 10 --> */
      // Check for the -cath flag indicating that we are using CATH, rather
      // than Pfam domains
      else if (args[i].equals("-cath")) {

        // Set flag
        useCATH = true;
      }

// RAL 22 Oct 10 -->
      // Check for the -offline flag indicating that PostScript file of plot
      // to be generated off-line
      else if (args[i].equals("-offline")) {

        // Set flag
        offLine = true;
      }

      // Check for the -ec flag indicating that E.C. satellite nodes to be
      // added to PostScript plot
      else if (args[i].equals("-ec")) {

        // Set flag
        addEnzymes = true;
      }
// <-- RAL 22 Oct 10

      // Check for the -ssg flag indicating that we are using SSG annotations,
      // rather than E.C. numbers
      else if (args[i].equals("-ssg")) {

        // Set flag
        useSSG = true;
      }
/* <-- RAL 17 Jun 10 */

      // Check for the -uid flag giving the user-id
      else if (args[i].equals("-uid")) {

        // Get the user id from the next argument
        if (i < num - 1) {
          userId = args[i + 1];
          i++;
        }
      }

      // Check for the -i flag indicating that path of ArchSchema
      // index follows
      else if (args[i].equals("-i")) {

        // Get the index directory from the next argument
        if (i < num - 1) {
          indexDir = args[i + 1];
          haveIndexDir = true;
          i++;
        }
      }

      // Check for the -r flag indicating remote database search
      else if (args[i].equals("-r")) {

        // Get the index directory from the next parameter
        if (i < num - 1) {
          indexDir = args[i + 1];
          remoteSearch = true;
          i++;
        }

      } // Otherwise, take this to be the name of the data index directory
      else {
        indexDir = args[i];
      }
    }

    /* Define remote search */
    remoteSearch = true;
    if (indexDir == null) {
      indexDir = Constants.URL_ARCHSEARCH;
    }
// DEBUG
//    indexDir = "/Users/atamuri/Documents/MSc/project/submission/archindex_Q3JWH7.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_P00519.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_BRCA1.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_Q76RF1.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_A0A1F2.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_P0AGE9.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_A5VNG2.txt";
//    indexDir = "C:\\roman\\talks\\argonne10\\archschema\\archindex_pfam.out";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex.out";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_BRCA2.txt";
//    indexDir = "C:\\roman\\pdbsum\\data\\archindex_C3Y4H7.txt";
//      indexDir = "C:\\roman\\pdbsum\\data\\archindex_Q9RPT1.txt";
//      indexDir = "C:\\roman\\pdbsum\\data\\archindex_3_90_226_10.out";
//      indexDir = "C:\\roman\\pdbsum\\data\\archindex_3_40_50_1000.out";
//      indexDir = "C:\\roman\\pdbsum\\data\\archindex_1_10_600_10.out";
// DEBUG
//      indexDir = "D:\\archindex.txt";
//      useCATH = true;
// DEBUG
      
    // Load ArchSchema searcher
    try {
      // Define new searcher using archindex
      searcher = new SwissPfamSearcher(indexDir);

    } catch (Exception e) {
      System.out.printf("*** Unable to locate ArchSchema database:\n%s\n",
              e.getMessage());
      e.printStackTrace();
      exit();
    }
// DEBUG
//    seqId = "Q3JWH7";
//    seqId = "A7SNU5";
//    seqId = "P00519";
//    userId = "5452";
//    seqId = "Q76RF1";
//    seqId = "A0A1F2";
//    seqId = "P0AGE9";
//    seqId = "A5VNG2";
//    seqId = "P0AEK4";
//    seqId = "Q6IM78";

//    seqId = "A2TC87_SPHYA";
//    seqId = "P38398";
//    pfamId = "PF01335";
//    seqId = "Q9W3H4";
//    seqId = "BRCA2_HUMAN";
//    seqId = "C3Y4H7";
//    seqId = "Q9RPT1";
//      pfamId = "3.90.226.10";
//      pfamId = "3.40.50.1000";
//      pfamId = "1.10.600.10";

// DEBUG
//    useCATH = true;
//    offLine = true;
//    addEnzymes = true;
//    useSSG = true;

  }

  protected void startup() {

// RAL 23 Oct 10 -->
    if (!offLine) {
      // Set JGoodies Swing Look & Feel - http://www.jgoodies.com/freeware/looks/index.html
//@@      try {
//@@        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
//@@      } catch (Exception e) {
//@@        System.out.printf("ERROR: Could not set Plastic L&F.\n");
//@@      }// <-- RAL 23 Oct 10

// RAL 23 Oct 10 -->
    }
// <-- RAL 23 Oct 10

    /* RAL 9 Jul 09 --> */
    // Get the location of the image files
    imgURL = getClass().getResource("images/");
    SearchPanelActions.getInstance().setImgURL(imgURL);
    /* <-- RAL 9 Jul 09 */

    // Create the ArchSchema window
    setAppFrame(new AppFrame(Constants.APPLICATION_NAME, useCATH));

    // LOAD ACTIONS & LISTENERS FOR WINDOW COMPONENTS

    // Set the action for the sequence search button
    getAppFrame().getInputPanel().getSearchButton().
            setAction(getAction("sequenceSubmitAction"));
    /* RAL 13 Jul 09 --> */
    getAppFrame().getInputPanel().getSearchButton().setText("Search");
    getAppFrame().getInputPanel().getPfamSearchButton().
            setAction(getAction("pfamSubmitAction"));
    getAppFrame().getInputPanel().getPfamSearchButton().setText("Search");
    /* <-- RAL 13 Jul 09 */

    // Set the action for the draw graph button
    getAppFrame().getGraphCriteriaPanel().getDrawGraphButton().
//            setAction(getAction("drawGraphAction"));
            setAction(getAction("refineSearchAction"));
    /* RAL 15 Jul 09 --> */
    getAppFrame().getGraphCriteriaPanel().getDrawGraphButton().
            setText("Refine search");
    /* <-- RAL 15 Jul 09 */

    /* RAL 1 Jul 09 --> */
    // Disable the 'Draw Graph' button
    getAppFrame().getGraphCriteriaPanel().getDrawGraphButton().setEnabled(false);
    /* <-- RAL 1 Jul 09 */

    getAppFrame().getGraphCriteriaPanel().getResetButton()
            .setAction(getAction("resetAction"));
    // Set actions and listeners on the graph style panel
    getAppFrame().getGraphStylePanel().getGraphRenderButton().
            setAction(getAction("toggleGraphRenderingAction"));
    /* RAL 15 Jul 09 --> */
    getAppFrame().getGraphStylePanel().getGraphRenderButton().
            setText("Freeze Graph");

    // Print menu item
    getAppFrame().getPrintMenuItem().setAction(getAction("printGraphAction"));
    getAppFrame().getPrintMenuItem().setText("Print graph");
    getAppFrame().getPrintMenuItem().setEnabled(false);

    // PostScript menu item
    getAppFrame().getPSMenuItem().setAction(getAction("postScriptAction"));
    getAppFrame().getPSMenuItem().setText("PostScript file");
    getAppFrame().getPSMenuItem().setEnabled(false);

    // Documentation menu item
    getAppFrame().getDocumentationMenuItem().setAction(getAction("documentationAction"));
    getAppFrame().getDocumentationMenuItem().setText("Documentation");

    /* <-- RAL 15 Jul 09 */
    if (!offLine) {
      getAppFrame().getGraphStylePanel().getEdgeLengthSlider().
              addChangeListener(GraphStylePanelActions.getInstance().
              getSpringLengthChangeListener(getAppFrame()));
      getAppFrame().getGraphStylePanel().getConnectivityFilterSlider()
              .addChangeListener(GraphStylePanelActions.getInstance().
              getConnectionFilterChangeListener(getAppFrame()));
    }
    /* RAL 3 Jul 09 -->
    getAppFrame().getGraphStylePanel().getShowSequenceCheckBox().addItemListener(GraphStylePanelActions.getInstance().getSequenceCheckboxListener(getAppFrame()));
    <-- RAL 2 Jul 09 */
    /* RAL 2 Jul 09 --> */
    getAppFrame().getGraphStylePanel().getDomainsOnlyRadioButton()
            .addItemListener(GraphStylePanelActions.getInstance()
            .getDomainsOnlyRadioButtonListener(getAppFrame()));
    getAppFrame().getGraphStylePanel().getAddSequencesRadioButton()
            .addItemListener(GraphStylePanelActions.getInstance()
            .getAddSequencesRadioButtonListener(getAppFrame()));
    getAppFrame().getGraphStylePanel().getAddStructuresRadioButton()
            .addItemListener(GraphStylePanelActions.getInstance()
            .getAddStructuresRadioButtonListener(getAppFrame()));
    getAppFrame().getGraphStylePanel().getAddEnzymesRadioButton()
            .addItemListener(GraphStylePanelActions.getInstance()
            .getAddEnzymesRadioButtonListener(getAppFrame()));
    /* <-- RAL 2 Jul 09 */

    // Set listeners for the main graph panel (clicking on nodes and background)
    getAppFrame().getGraphPanel().getVisualization().getDisplay(0).addControlListener(GraphActions.getInstance().getGraphNodeClickControlAdapter(getAppFrame(), getSearcher(), useCATH));
    getAppFrame().getGraphPanel().getVisualization().getDisplay(0).addControlListener(GraphActions.getInstance().getGraphBackgroundClickFocus(getAppFrame()));

    /* RAL 9 Jul 09 --> */
    // Close the graph panel so that logo and description fill the
    // right-hand panel
    getAppFrame().getSplitPane2().getTopComponent().setVisible(false);
    getAppFrame().getSplitPane2().setOneTouchExpandable(false);
    /* <-- RAL 9 Jul 09 */

    // Set the intial text for the data pane and add a listener that opens browser when clicking on links
    /* RAL 9 Jul 09 -->
    getAppFrame().getDataPane().setText("<html>" + Constants.HEAD_HTML + "<body><h1>ArchSchema</h1></body></html>"); */
    StringBuilder panelText = null;
    if (seqId == null) {
      panelText = setDataPanelText(START_SCREEN, true);
    } else {
      panelText = setDataPanelText(SEARCH_SCREEN, true);
    }
    String masterText = "<html>" + bbk.dng.Constants.HEAD_HTML +
            "<body font=\"sans-serif\">" + panelText + "</body></html>";
    getAppFrame().getDataPane().setText(masterText);
    /* <-- RAL 9 Jul 09 */
    getAppFrame().getDataPane()
            .addHyperlinkListener(DataPaneActions.getInstance()
            .getDataPaneHyperlinkListener());

    // If running for CATH domains, change all relevant text
    if (useCATH) {
      getAppFrame().getInputPanel().setLabel2(SearchPanelActions.CATH);
      getAppFrame().getGraphStylePanel().setDomainsOnlyRadioButton(SearchPanelActions.CATH);
    }

    // Pack and draw the ArchSchema window
    getAppFrame().setSize(1000, 700);
    getAppFrame().setVisible(true);

    /* RAL 13 Jul 09 --> */
    // If already have a Pfam id, then identify the most representative
    // sequence for it
    if (pfamId != null) {
      appFrame.getInputPanel().getPfamIdTextField().setText(pfamId);
      pfamSubmitAction();
    }
    /* <-- RAL 13 Jul 09 */

    /* RAL 1 Jul 09 --> */
    // If already have a UniProt code from the command-line, call the
    // sequence search
    else if (seqId != null) {
      appFrame.getInputPanel().getSequenceTextField().setText(seqId);
      sequenceSubmitAction();
    }
    /* <-- RAL 1 Jul 09 */
  }

  @Action
  public void refineSearchAction() {

    boolean newSearch = false;

    // Initialise user id
    userId = null;

    // Get the sequence id
    seqId = appFrame.getInputPanel().getSequenceTextField().getText().toUpperCase();
    if (seqId != null && !seqId.equals("")) {
      pfamId = null;
      runSearch(newSearch);
    } else {
      // Get the pfamId
      pfamId = appFrame.getInputPanel().getPfamIdTextField().getText().toUpperCase();
      if (pfamId != null && !pfamId.equals("")) {
        seqId = null;
        runSearch(newSearch);
      }
    }
  }

  @Action
  public void pfamSubmitAction() {

    boolean newSearch = true;

    // Get the pfamId
    pfamId = appFrame.getInputPanel().getPfamIdTextField().getText().toUpperCase();

    // Blank out the UniProt id
    seqId = null;
    appFrame.getInputPanel().getSequenceTextField().setText("");

    // Run the search
    runSearch(newSearch);
  }

  @Action
  public void sequenceSubmitAction() {

    boolean newSearch = true;

    // If not starting up, blank out sequence id
    seqId = appFrame.getInputPanel().getSequenceTextField().getText().toUpperCase();

    // Blank out the pfam id
    pfamId = null;
    appFrame.getInputPanel().getPfamIdTextField().setText("");

    // Run the search
    runSearch(newSearch);
  }

  @Action
  public void drawGraphAction() {

    try {
      // Plot the graph, performing new search if necessary
      SearchPanelActions.getInstance().drawGraphAction(getAppFrame(),
              getSearcher());

      // Enable the menu items
      getAppFrame().getPrintMenuItem().setEnabled(true);
      getAppFrame().getPSMenuItem().setEnabled(true);

    } catch (Exception e) {
      System.out.printf("Error executing Main.drawGraphAction().\n");
      e.printStackTrace();
    }

    // Move data pane scrollbar to the top and switch to graph panel
    EventQueue.invokeLater(new Runnable() {

      public void run() {
        getAppFrame().getDataScrollPane().getVerticalScrollBar().
                setValue(0);
        getAppFrame().selectTab("Graph");
      }
    });
  }

  @Action
  public void resetAction() {
    SearchPanelActions.getInstance().resetAction(getAppFrame());
  }

  @Action
  public void toggleGraphRenderingAction() {
    GraphStylePanelActions.getInstance().toggleGraphAction(getAppFrame());
  }

  @Action
  public void printGraphAction() {
    GraphStylePanelActions.getInstance().printGraphAction(getAppFrame());
  }

  @Action
  public void postScriptAction() {
    GraphStylePanelActions.getInstance().writePostScriptAction(getAppFrame(), useCATH);
  }

  @Action
  public void documentationAction() {
    // Form URL of ArchSchema documentation page
    String URL = bbk.dng.Constants.URL_ARCHSCHEMA_HELP;

    // Call the URL
    try {
      BrowserLauncher bl = new BrowserLauncher();
      bl.openURLinBrowser(URL);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private AppFrame getAppFrame() {
    return appFrame;
  }

  // Run the search for the given Pfam id or sequence id
  private void runSearch(boolean newSearch) {

    boolean haveGraphData = false;

    /* RAL 1 Jul 09 --> */
    // Initialise domain and sequence counts
    nDomains = 0;
    nSeqs = 0;

    // Switch off the sequence filters
    // getAppFrame().getGraphCriteriaPanel().setVisible(false);

    // Disable the 'Draw Graph' button
    //getAppFrame().getGraphCriteriaPanel().getDrawGraphButton().
    //        setEnabled(false);
    /* <-- RAL 1 Jul 09 */

    /* RAL 10 Jul 09 --> */
    // Show "searching" message
    StringBuilder panelText = setDataPanelText(SEARCH_SCREEN, true);
    String masterText = "<html>" + bbk.dng.Constants.HEAD_HTML
            + "<body font=\"sans-serif\">" + panelText + "</body></html>";
    getAppFrame().getDataPane().setText(masterText);
    getAppFrame().getDataPane().repaint();

    /* <-- RAL 10 Jul 09 */
      // Perform the sequence search
      /* RAL 1 Jul 09 -->
      SearchPanelActions.getInstance().sequenceSearchAction(getAppFrame(),
      getSearcher()); */
    try {
      // Perform the sequence search
      haveGraphData = SearchPanelActions.getInstance().
              sequenceSearchAction(getAppFrame(), getSearcher(), seqId, pfamId,
    /* RAL 17 Jun 10 --> */
//              newSearch,userId);
// RAL 22 Oct 10 -->
//              newSearch, userId, useCATH, useSSG);
              newSearch, userId, useCATH, useSSG, offLine, addEnzymes);
// <-- RAL 22 Oct 10
    /* <-- RAL 17 Jun 10 */

      // Get the numbers of sequences and domains matched
      nSeqs = SearchPanelActions.getInstance().getNSequences();
      nDomains = SearchPanelActions.getInstance().getNDomains();

      // Get domain types
      domainType = SearchPanelActions.getInstance().getDomainType();

    } catch (Exception ex) {
      nDomains = 0;
    }

    // Unset "searching" message
    panelText = setDataPanelText(START_SCREEN, true);
    masterText = "<html>" + bbk.dng.Constants.HEAD_HTML
            + "<body font=\"sans-serif\">" + panelText + "</body></html>";
    getAppFrame().getDataPane().setText(masterText);
    getAppFrame().getDataPane().repaint();

    // If the top-right panel is open, close it
    if (getAppFrame().getSplitPane2().getTopComponent().isVisible()) {

      // Close the panel
      getAppFrame().getSplitPane2().getTopComponent().setVisible(false);
      getAppFrame().getSplitPane2().setOneTouchExpandable(false);
    }

    // Change text in various panels according to type of domain data
    getAppFrame().getGraphStylePanel().setDomainsOnlyRadioButton(domainType);
    getAppFrame().getInputPanel().setLabel2(domainType);

    /* RAL 1 Jul 09 --> */
    // If we have the graph data already, go straight to the plot
    if (haveGraphData) {
      // Plot the graph right away
      drawGraphAction();
    }

    // Re-initialise flags
    newSearch = false;
    userId = null;
    /* <-- RAL 1 Jul 09 */
  }

  private void setAppFrame(AppFrame appFrame) {
    this.appFrame = appFrame;
  }

  private SwissPfamSearcher getSearcher() {
    return searcher;
  }

  private javax.swing.Action getAction(String actionName) {
    return getContext().getActionMap().get(actionName);
  }

  /* RAL 9 Jul 09 --> */
  // Show the ArchSchema logo and description or other message on
  // the expanded data panel
  private StringBuilder setDataPanelText(int displayMode, boolean showLogo) {

    // Generate HTML for data panel
    StringBuilder sb = new StringBuilder();

    // Initialise html to indent all the text
    sb.append("<blockquote>");

    // If data panel is expanded, display the logo
    if (showLogo) {
      sb = showLogo(sb);
    }

    // If showing start-screen, display description
    if (displayMode == START_SCREEN) {
      sb = showDescription(sb);
    }

    // If showing search-screen, display waiting message
    else if (displayMode == SEARCH_SCREEN) {
      sb = showWaitMessage(sb);
    }

    // If a huge number of sequences hit by initial search, show warning
    else if (displayMode == MANY_SEQUENCES) {
      sb = showManySequencesMessage(sb);
    }

    // If sequence returned > 2 domains, show message about domain
    // selection
    else if (displayMode == SELECT_DOMAINS) {
      sb = showDomainSelectMessage(sb);
    }

    // Unset indent command
    sb.append("</blockquote>");

    // Return the HTML string
    return sb;
  }

  // Show the ArchSchema logo
  private StringBuilder showLogo(StringBuilder sb) {

    // Initialise start- and end of HTML table cells for images
    String imgSrc = "<img src=\"" + imgURL;
    String imgEnd = "\">";

    // Initialise name of logo image
    String logoImage = imgSrc + "ArchSchema_logo_start.gif" + imgEnd;

    // Add spacer at top of frame
    sb = SearchPanelActions.getInstance().addSpacer(sb, 1, 20);

    // Show the ArchSchema logo
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td colspan=2>" + logoImage + "</td>");
    sb.append("</tr>");

    // Gap
    sb.append("<tr>");
    sb.append("<td colspan=2><img width=1 height=10 src=\"" + imgURL + "1x1.gif\"></td>");
    sb.append("</tr>");

    // Version number
    sb.append("<tr>");
    sb.append("<td><b>Version " + Constants.VERSION + "</b></td>");

    // Authors
    sb.append("<td align=right><b>Written by:</b> Asif Tamuri</td>");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td align=right><b>Modified by:</b> Roman Laskowski</td>");
    sb.append("</tr>");

    // Gap
    sb.append("<tr>");
    sb.append("<td colspan=2><img width=1 height=15 src=\"" + imgURL
            + "1x1.gif\"></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Return the string
    return sb;
  }

  // Show the program description on the start screen
  private StringBuilder showDescription(StringBuilder sb) {

    // Initialise start- and end of HTML table cells for images
    String imgSrc = "<img src=\"" + imgURL;
    String imgEnd = "\">";

    // Initialise name of logo image
    String nodeImage1 = imgSrc + "eg_node1.gif" + imgEnd;
    String nodeImage2 = imgSrc + "eg_node2.gif" + imgEnd;
    String nodeImage3 = imgSrc + "eg_node3.gif" + imgEnd;
    String nodeSeqsImage = imgSrc + "eg_node_seqs.gif" + imgEnd;
    String nodeStrucsImage = imgSrc + "eg_node_strucs.gif" + imgEnd;
    String nodeManySeqsImage = imgSrc + "eg_node_manyseqs.gif" + imgEnd;

    // Description
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td><b>Description</b></td>");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td>");
    sb.append(
            "ArchSchema plots the relationships between Pfam domain "
            + "architectures. An architecture is defined as the "
            + "sequence of Pfam domains common to a family of protein "
            + "sequences. On the plot each architecture is represented "
            + "by a node with the domains colour-coded, as shown in the "
            + "examples below.");
    sb.append("</td>");
    sb.append("</tr>");

    // Node examples
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td align=center>");
    sb.append("  <table cellpadding=0 cellspacing=0>");
    sb.append("  <tr>");
    sb.append("  <td align=center valign=top>" + nodeImage1 + "</td>");
    sb.append("  <td><img width=25 height=1 src=\"" + imgURL
            + "1x1.gif\"></td>");
    sb.append("  <td align=center valign=top>" + nodeImage2 + "</td>");
    sb.append("  <td><img width=25 height=1 src=\"" + imgURL
            + "1x1.gif\"></td>");
    sb.append("  <td align=center valign=top>" + nodeImage3 + "</td>");
    sb.append("  </tr>");
    sb.append("  <tr>");
    sb.append("  <td align=center><i>a.</i></td>");
    sb.append("  <td>&nbsp;</td>");
    sb.append("  <td align=center><i>b.</i></td>");
    sb.append("  <td>&nbsp;</td>");
    sb.append("  <td align=center><i>c.</i></td>");
    sb.append("  </td>");
    sb.append("  </tr>");
    sb.append("  </table>");
    sb.append("</td>");
    sb.append("</tr>");

    // Structural coverage text
    sb.append("<tr>");
    sb.append("<td>");
    sb.append(
            "The red underlines in (c) indicate the extent to which "
            + "3D structures of the domains and architectures are "
            + "available in the PDB. Left-clicking on a node shows "
            + "a panel containing information about the constituent "
            + "domains, the protein sequences having the given "
            + "architecture, and any sequences that have whole or "
            + "partial structures in the PDB.");
    sb.append("</td>");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("</tr>");

    // Sequence and structure nodes
    sb.append("<tr>");
    sb.append("<td>");
    sb.append(
            "You can display UniProt identifiers (or, alternatively, PDB "
            + "codes) associated with each architecture, as in the examples "
            + "below. Where there are too many sequence "
            + "or structure nodes to show, a selection are shown and are "
            + "coloured pink (as in the third example). You can also display "
            + "associated enzyme classes (not shown here).");
    sb.append("</td>");
    sb.append("</tr>");

    // Sequence-structure examples
    sb.append("<tr>");
    sb.append("<td align=center>");
    sb.append("  <table cellpadding=0 cellspacing=0>");
    sb.append("  <tr>");
    sb.append("  <td align=center valign=top>" + nodeSeqsImage + "</td>");
    sb.append("  <td><img width=25 height=1 src=\"" + imgURL + "1x1.gif\"></td>");
    sb.append("  <td align=center valign=top>" + nodeStrucsImage + "</td>");
    sb.append("  <td><img width=25 height=1 src=\"" + imgURL + "1x1.gif\"></td>");
    sb.append("  <td align=center valign=top>" + nodeManySeqsImage + "</td>");
    sb.append("  </tr>");
    sb.append("  <tr>");
    sb.append("  <td align=center><i>UniProt sequences</i></td>");
    sb.append("  <td>&nbsp;</td>");
    sb.append("  <td align=center><i>PDB structures</i></td>");
    sb.append("  <td>&nbsp;</td>");
    sb.append("  <td align=center><i>Many satellite nodes</i></td>");
    sb.append("  </td>");
    sb.append("  </tr>");
    sb.append("  </table>");
    sb.append("</td>");
    sb.append("</tr>");

    // Getting started text
    sb.append("<tr>");
    sb.append("<td><b>Getting started</b></td>");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td>");
    sb.append(
            "To start, use the text boxes at the top of the left-hand panel "
            + "to enter either the UniProt id of your protein sequence, or "
            + "the Pfam id of your protein domain of interest. Then press the "
            + "corresponding Search button.");
    sb.append("</td>");
    sb.append("</tr>");

    // Close off the table
    sb.append("</table>");
    sb.append("</blockquote>");

    // Return the html string
    return sb;
  }
  /* <-- RAL 9 Jul 09 */

  /* RAL 10 Jul 09 --> */
  // Show the waiting message
  private StringBuilder showWaitMessage(StringBuilder sb) {

    // Form the waiting message
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td><font color=blue>");
    sb.append("Searching for your sequence ...");
    sb.append("</font></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Return the string
    return sb;
  }

  // Show message explaining domain selection
  private StringBuilder showDomainSelectMessage(StringBuilder sb) {

    // Form the domain-selection message
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td><font color=blue><b>Select domains</b></font></td?");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td><font color=blue>");
    sb.append(
            "Your search sequence contains <b>" + nDomains
            + "</b> different Pfam domains (listed in box in "
            + "left-hand panel). Select the domains you are interested "
            + "in (currently all are selected), by left-click and "
            + "shift-left-click. "
            + "You can then select which organisms to limit the selection "
            + "to and whether to include only sequences for which there "
            + "is structural information in the PDB. Finally, "
            + "press the Plot Graph button to obtain the ArchSchema "
            + "plot for the architectures containing the selected domains."
            + "<p><p>"
            + "Note that, for reference, your \"parent\" search sequence "
            + "will be retained whether or not it passes the selection "
            + "criteria.");
    sb.append("</font></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Return the string
    return sb;
  }
  /* <-- RAL 10 Jul 09 */

  /* RAL 20 Jul 09 --> */
  private StringBuilder showManySequencesMessage(StringBuilder sb) {

    // Form the message
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td><font color=red><b>Very many sequences matched!</b></font></td?");
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td><font color=red>");
    sb.append(
            "The domains in your search sequence are found (individually "
            + "or together) in <b>" + nSeqs
            + "</b> different UniProt sequences. The plotting of the graph "
            + "may thus be very slow. You can reduce the number of sequences "
            + "by using the filtering parameters in the panel on the left. "
            + "These allow you to: ");
    if (nDomains > 1) {
      sb.append("select specific domains, ");
    }
    sb.append(
            "select a particular organism, and/or include only sequences for "
            + "which there is structural information in the PDB. After making "
            + "your selections, press "
            + "the Plot Graph button to obtain the ArchSchema plot."
            + "</font><p><p><font color=blue>"
            + "Note that, for reference, your \"parent\" search sequence "
            + "will be retained whether or not it passes the selection "
            + "criteria.");
    sb.append("</font></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Return the string
    return sb;
  }
  /* <-- RAL 20 Jul 09 */
}

