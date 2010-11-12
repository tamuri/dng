package bbk.dng.actions;

import bbk.dng.graph.RenderingCountdown;
import bbk.dng.utils.NameValue;
import bbk.dng.utils.CollectionUtils;
import bbk.dng.ui.panels.AppFrame;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.graph.ArchitectureGraphBuilder;
import bbk.dng.Main;

import bbk.dng.graph.CustomizedForceDirectedLayout;
import bbk.dng.utils.SwingUtils;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import com.mallardsoft.tuple.Pair;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.Visualization;
import prefuse.Constants;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class SearchPanelActions {

  public static final int PFAM = 0;
  public static final int CATH = 1;

  private static SearchPanelActions instance;
  private int domainType;
  private boolean useCATH;
  private boolean useSSG;
// RAL 22 Oct 10 -->
  private boolean offLine;
  private boolean addEnzymes;
// <<- RAL 22 Oct 10
  private String domainSeparator = "\\.";
  private String parentArchitecture;
  private String parentSequence = null;
  private String parentName = null;
  private int nParentDomains = 0;
  /* RAL 10 Jul 09 --> */
  private String parentPDBCodes;
  private String parentCoverage;
  /* <-- RAL 10 Jul 09 */
  /* RAL 20 Jul 09 -->
  private Map<String, Map<String, String>> domainDetails;
  <-- RAL 20 Jul 09 */
  private String masterText;
  private String enzymesText;
  /* RAL 2 Jul 09 --> */
  private Map<String, List<String>> archSeqListPair = null;
  private Map<String, List<String>> archPDBListPair = null;
  private Map<String, List<String>> archEnzymeListPair = null;
  private Map<String, String> archCoveragePair = null;
  private Map<Integer, Map<String, String>> archIndex =null;
  // private Map<Pair<String,String>, Double> similarityMatrix = null;
  private Map<Pair<String,String>, Integer> connectionsList = null;
  private Set<Map<String, String>> architectures = null;
  private Set<Map<String, String>> enzSet = null;
  public static URL imgURL;
  /* <-- RAL 2 Jul 09 */
  /* RAL 8 Jul 09 --> */
  Map<String, Integer> domainColour = null;
  /* <-- RAL 8 Jul 09 */
  /* RAL 20 Jul 09 --> */
  private static boolean newSearch = true;
  public static Vector seqSearchResults = null;
  public static String[] domainCatDesc = new String [2];
  /* <-- RAL 20 Jul 09 */
  /* RAL 1 Jul 09 --> */
  public static final String[] specialOrganisms = {
    "HUMAN", "MOUSE", "DROME", "CAEEL", "YEAST"
  };
  public static final String[] domainTypeDesc = { "Pfam", "CATH" };
  public static final String[] pfamCatDesc = { "PfamA", "PfamB" };
  public static final String[] cathCatDesc = { "CATH", "split-CATH" };

  public static final String[] enzymeClass = {
    "Oxidoreductases", "Transferases", "Hydrolases", "Lyases", "Isomerases",
    "Ligases"
  };
  /* <-- RAL 1 Jul 09 */

  private SearchPanelActions() {
  }

    public static SearchPanelActions getInstance() {
    return instance == null ? instance = new SearchPanelActions() : instance;
  }

  /* RAL 1 Jul 09 -->
  public void sequenceSearchAction(AppFrame appFrame, SwissPfamSearcher searcher) {
  // Get the sequence identifier to search for
  String seqId = appFrame.getInputPanel().getSequenceTextField().getText().toUpperCase(); */
  public boolean sequenceSearchAction(AppFrame appFrame,
          SwissPfamSearcher searcher, String seqId, String pfamId,
  /* RAL 17 Jun 10 --> */
  //        boolean newSearch,String userId)
// RAL 22 Oct 10 -->
//          boolean newSearch,String userId,boolean useCATH,boolean useSSG)
          boolean newSearch,String userId,boolean useCATH,boolean useSSG,
          boolean offLine, boolean addEnzymes)
// <-- RAL 22 Oct 10
  /* <-- RAL 17 Jun 10 */
          throws Exception {

    // Initialise variables
    int nDomains = 0;
    seqSearchResults = null;
    this.useCATH = useCATH;
    this.useSSG = useSSG;
// RAL 22 Oct 10 -->
    this.offLine = offLine;
    this.addEnzymes = addEnzymes;
// <-- RAL 22 Oct 10

    // Get the sequence identifier from the text field
    if (seqId == null && pfamId == null) {

      // See if text field contains a sequence id
      seqId = appFrame.getInputPanel().getSequenceTextField().getText().
              toUpperCase();

      // If not, then check pfam text field */
      if (seqId == null || seqId.length() == 0) {
        pfamId = appFrame.getInputPanel().getPfamIdTextField().getText().
              toUpperCase();
      }
    }
    /* <-- RAL 1 Jul 09 */

    // Get the maximum number of architectures
    int maxArchitectures = appFrame.getInputPanel().getMaxArchitectures();

    // Get which button of the reviewed/unreviewed pair is selected
    boolean reviewedOnly
            = appFrame.getInputPanel().getReviewedUniProtOnlyRadioButton().isSelected();

    // Place sequence id in text field
    if (seqId != null) {
      appFrame.getInputPanel().getSequenceTextField().setText(seqId);
    }

    // Repeat for pfam id
    if (pfamId != null) {
      appFrame.getInputPanel().getPfamIdTextField().setText(pfamId);
    }

    // If it's empty, do nothing
    if ((seqId == null || seqId.length() == 0) &&
            (pfamId == null || pfamId.length() == 0)) {
      /* RAL 1 Jul 09 -->
      return; */
      return false;
    /* <-- RAL 1 Jul 09 */
    }

    // Initialise user-defined filters
    Vector<Object> filterCriteria = new Vector<Object>();
    filterCriteria = null;

    // If the sequence-selection criteria panel is open, retrieve the
    // user-defined filters
    if (!newSearch && appFrame.getGraphCriteriaPanel().isVisible()) {
      // Get the package of user-defined filtering criteria to be applied
      filterCriteria = getUserFilterCriteria(appFrame);
    }

    // Show waiting cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // Initiate a search for this sequence/domain and get back the package of
    // results for processing
    String searchMade = "NONE";
    if (pfamId != null && pfamId.length() > 0) {
      seqSearchResults
/* RAL 17 Jun 10 --> */
//              = searcher.pfamDomainSearch(userId, pfamId, true, filterCriteria);
              = searcher.pfamDomainSearch(userId, pfamId, true, filterCriteria,
              useCATH, maxArchitectures, reviewedOnly, useSSG);
/* <-- RAL 17 Jun 10 */
      searchMade = "Pfam";
    } else {
      seqSearchResults
              = searcher.uniprotSequenceSearch(userId, seqId, null, true,
/* RAL 17 Jun 10 --> */
//              filterCriteria);
              filterCriteria, useCATH, maxArchitectures, reviewedOnly, useSSG);
/* <-- RAL 17 Jun 10 */
      searchMade = "UniProt";
    }
    
    /* RAL 22 Jul 09 --> */
    // Reset default cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    /* <-- RAL 22 Jul 09 */

    // Determine what kind of results package we have
    //int resultsPackage = getPackageType();
    /* RAL 9 Dec 09 -->
    int resultsPackage = -9;
     <-- RAL 9 Dec 09 */

    // If sequence search failed, show error message
    /* RAL 9 Dec 09 --> */
    if (seqSearchResults == null) {

      // Show error dialogue
      JOptionPane.showMessageDialog(appFrame,
              "<html>" + "No sequences match the selection criteria!",
              "No match", JOptionPane.ERROR_MESSAGE);

      // Return
      return false;

    } // If have an error message, display it
    else if (getRunStatus() == SwissPfamSearcher.ERROR_MESSAGE) {

      // Extract the error type and message from the next two
      // vector items
      String errorType = getErrorType();
      String errorMessage = getErrorMessage();

      // Show error dialogue
      JOptionPane.showMessageDialog(appFrame, "<html>" + errorMessage,
              errorType, JOptionPane.ERROR_MESSAGE);

      // Return
      return false;
    }
    /* <-- RAL 9 Dec 09 */

    // Retrieve the numbers of hits, Pfam domains and species
    int nSequences = getNSequences();
    int nUniqueArchitectures = getNArch();
    int nFinal = getNFinal();
    int nSpecies = getNSpecies();

    // Show waiting cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // Get the parent sequence
    parentSequence = getParentSequence();

    // Retrieve the sequence data: architecture, accession, uniprot_id,
    // organism, protein_name, status, 3D coverage
    architectures = getArchitectures();

    // Identify the architecture corresponding to the parent sequence
    Map<String, String> parentMap = null;
    for (Map<String, String> map : architectures) {
      // If this is the parent sequence, then store map
      if (map.get("uniprot_id").equals(parentSequence)) {
        parentMap = map;
      }
    }

    // If not found, then show error message
    if (parentMap == null) {

      // Show error dialogue
      JOptionPane.showMessageDialog(appFrame,
              "<html>" + "Search sequence not found in hits!",
              "Search sequence lost", JOptionPane.ERROR_MESSAGE);

      // Return
      return false;
    }

    // Get the parent architecture and its number of domains
    parentArchitecture = parentMap.get("architecture");

    // Determine whether we are dealing with Pfam or CATH domains
    if (!useCATH) {
      domainType = PFAM;
      domainSeparator = "\\.";
      domainCatDesc[0] = pfamCatDesc[0];
      domainCatDesc[1] = pfamCatDesc[1];
    } else {
      domainType = CATH;
      domainSeparator = "\\_";
      domainCatDesc[0] = cathCatDesc[0];
      domainCatDesc[1] = cathCatDesc[1];
    }

    String[] nodeDomains = parentArchitecture.split(domainSeparator);
    nParentDomains = nodeDomains.length;
    if (nParentDomains == 0)
      nParentDomains = 1;

    // Get the architecture-enzyme mappings
    enzSet = getEnzymes();

    /* RAL 14 Jul 09 --> */
    // Switch on the graph criteria panel if it is not visible
    if (!appFrame.getGraphCriteriaPanel().isVisible()) {
      appFrame.getGraphCriteriaPanel().setVisible(true);
    }
    /* <-- RAL 14 Jul 09 */

    // Get the protein's name
    String proteinName = parentMap.get("protein_name");
    parentName = proteinName;

    // Display sequence information in the panel
    appFrame.getGraphCriteriaPanel().getSequenceInfoLabel().
            setText("<html>" + parentMap.get("uniprot_id") + "<br>" +
            proteinName + "</html>");

    // Get the unique list of domains in the sequence architecture
    List<String> domains = getDomainList(parentMap.get("architecture"));
    
    // Clear the domain list panel
    DefaultListModel model = (DefaultListModel) appFrame.
            getGraphCriteriaPanel().getDomainList().getModel();
    model.clear();

    // Retrieve the Pfam domain description and number of sequences
    Map<String, Map<String, String>> domainDetails = getDomainDetails();

    // Repeat for the species definitions
    Map<String, Map<String, String>> speciesDetails = getSpeciesDetails();

    // Get the architectures index containing distances from the parent
    // architecture
    archIndex = getArchIndex();

    // Get the architecture-node connectivities
    connectionsList = getConnectionsList();

    // Get maximum architectures
    maxArchitectures = getmaxArchitectures();

    // Put into text box
    appFrame.getInputPanel().setMaxArchitectures(maxArchitectures);
    
    // Update the Pfam domain list in the panel
    for (String d : domains) {

      // Get the Pfam id and description of this domain
      String pfId = domainDetails.get(d).get("id");
      String description = domainDetails.get(d).get("description");
      int nSeqs = Integer.parseInt(domainDetails.get(d).get("nseqs"));

      // Add to the combo box
      String endString = "seqs]";
      if (nSeqs == 1)
        endString = "seq]";
      model.add(model.size(), new NameValue(pfId + " " + description
              + " [" + nSeqs + endString, d));
    }

    // Default to all domains selected
    appFrame.getGraphCriteriaPanel().getDomainList().
            setSelectionInterval(0, model.size() - 1);

    // Add the "All species" item to the species combo box
    appFrame.getGraphCriteriaPanel().getOrganismComboBox()
            .addItem(new NameValue("ALL", "ALL"));

    // Pull out just the species identifiers
    Set<String> extensions = (Set<String>) speciesDetails.keySet();

    // Loop over the special organisms to see which ones we have
    for (String special : specialOrganisms) {
      Map<String, String> speciesEntry = speciesDetails.get(special);

      // Id present, add it to the species combo box
      if (speciesEntry != null) {
        int nSeqs = Integer.parseInt(speciesEntry.get("nseqs"));
        String endString = "seqs]";
        if (nSeqs == 1) {
          endString = "seq]";
        }
        String sp = speciesEntry.get("organism") + " [" + nSeqs + endString;
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().
                addItem(new NameValue(sp, special));

        // Remove from the set of species extensions
        extensions.remove(special);
      }
    }

    // Add dashed line prior to remainder of the species
    appFrame.getGraphCriteriaPanel().getOrganismComboBox().
            addItem(new NameValue("------------------------------",
            "ALL"));

    // Sort the remaining extensions
    List<String> spList = CollectionUtils.newList();
    for (String e : extensions) {
      Map<String, String> speciesEntry = speciesDetails.get(e);
      spList.add(speciesEntry.get("organism") + "\t" + e);
    }
    Collections.sort(spList);

    // Loop over the sorted list of organism to add to combo box
    for (String org : spList) {

      // Separate the organism name from its extension
      String[] namePlusExtension = org.split("\\t");
      String name = namePlusExtension[0];
      String extension = namePlusExtension[1];

      // Truncate species name if too long
      if (name.length() > 30) {
        name = name.substring(0, 30) + "...";
      }

      // Find this species entry
      Map<String, String> speciesEntry = speciesDetails.get(extension);

      // Retrieve its number of sequences
      int nSeqs = 0;
      if (speciesEntry != null) {
        nSeqs = Integer.parseInt(speciesEntry.get("nseqs"));
      }

      // Form the sequence count
      if (nSeqs == 1) {
        name = name + " [" + nSeqs + "seq]";
      } else if (nSeqs > 1) {
        name = name + " [" + nSeqs + "seqs]";
      }

      // Truncate species name if too long
      NameValue nameValue;
      nameValue = new NameValue(name, extension);

      // Add to species combo box
      appFrame.getGraphCriteriaPanel().getOrganismComboBox()
              .addItem(nameValue);
    }

    // Enable the 'Draw Graph' button
    appFrame.getGraphCriteriaPanel().getDrawGraphButton().setEnabled(true);

    // Reset default cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    return true;
  }

  // Perform the search to get data for the graph
  public void drawGraphAction(AppFrame appFrame,
          SwissPfamSearcher searcher) throws Exception {

    // Show waiting cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    // Get the various counts returned by the search
    int nSeqs = getNSequences();
    int nArch = getNArch();
    int nFinal = getNFinal();
    int nCollapsed = getNCollapsed();
    int nPfam = getNPfam();
    int nSpecies = getNSpecies();

    // Get the sequence identifier of the parent sequence
    // parentSequence = getParentSequence();

    /* RAL 9 Jul 09 --> */
    // Re-enable the split-screen
    appFrame.getSplitPane2().setOneTouchExpandable(true);
    appFrame.getSplitPane2().getTopComponent().setVisible(true);
    appFrame.getSplitPane2().setDividerLocation(500);
    /* <-- RAL 9 Jul 09 */

    // Calculate similarity scores between the architectures
    // SimilarityCalculator calculator = new SimilarityCalculator();

    // Retrieve the architecture records
    /* RAL 22 Jul 09 --> */
    // Set<Map<String, String>> archSet = getArchitectures();

    // Extract the required architecture -> sequence, PDB codes, and
    // 3D coverage mappings
    getArchitectureMappings(architectures);

    // Get the architecture to enzyme mappings
    getEnzymeMappings(enzSet);
    /* <-- RAL 22 Jul 09 */

    // Get number of architectures
    // int originalArchitectureCount = archSeqListPair.size();
    
    // If too many architectures returned, pare the number down to
    // something more reasonable - No longer required
    /*
    if (archSeqListPair.size() > bbk.dng.Constants.MAX_ARCHITECTURES) {
      Map<String, Double> matrix
              = calculator.
              getSimilarityScoresForSingleArchitecture(parentArchitecture,
              archSeqListPair.keySet());
      List<String> mostSimilarArchitectures
              = calculator.getMostSimilarArchitectures(matrix,
              bbk.dng.Constants.MAX_ARCHITECTURES);

      // Retrieve the required number of architectures closest to the
      // parent architecture using the score read in from the search
      List<String> closestArchitectures =
              getClosestArchitectures(archIndex,
              bbk.dng.Constants.MAX_ARCHITECTURES);
      // Identify which architectures are to be removed
      List<String> toRemove = CollectionUtils.newList();
      for (String a : archSeqListPair.keySet()) {
        if (!closestArchitectures.contains(a)) {
          toRemove.add(a);
        }

        // Set final number of architectures on screen
        nFinal = bbk.dng.Constants.MAX_ARCHITECTURES;
      }

      // Remove the most distant architectures
      for (String a : toRemove) {
        // Remove, providing this isn't the parent architecture
        if (!a.equals(parentArchitecture)) {

          archSeqListPair.remove(a);
          archPDBListPair.remove(a);
          archCoveragePair.remove(a);
        }
      }
    }
*/

    // Add the original sequence + architecture to the set, if it doesn't
    // exist
    /* RAL 10 Jul 09 --> */
    archSeqListPair = replaceParents(archSeqListPair, searcher);
    /* <-- RAL 10 Jul 09 */

    // Calculate the similarity scores between the returned architectures
    /* similarityMatrix
            = calculator
            .getArchitectureSimilarityMatrix(archSeqListPair.keySet()); */
    ArchitectureGraphBuilder graphBuilder = new ArchitectureGraphBuilder();
    Main.graph = graphBuilder.initialiseGraph(archSeqListPair,
            parentArchitecture, archPDBListPair, archCoveragePair,
            archEnzymeListPair, architectures);

    // Add all the edges to the graph
    graphBuilder.addEdges(Main.graph, connectionsList, parentArchitecture);
    /* graphBuilder.addEdgesByMatrix(Main.graph, similarityMatrix,
           parentArchitecture); */
    /* RAL 22 Jul 09 -->
            CollectionUtils.join(domains, ' '), archPDBListPair,
            archCoveragePair);
    graphBuilder.addEdgesByMatrix(Main.graph, similarityMatrix,
            CollectionUtils.join(domains, ' ')); */
    /* RAL 22 Jul 09 --> */

    // set domain colours
    /* RAL 8 Jul 09 -->
    Map<String, Integer> domainColour = CollectionUtils.newMap(); */
    domainColour = CollectionUtils.newMap();
    /* <-- RAL 8 Jul 09 */

    // all distinct domains
    Set<String> allDistinctDomains = CollectionUtils.newSet();
// Test
    Collections.addAll(allDistinctDomains, parentArchitecture.split(domainSeparator));
    for (String arch : archSeqListPair.keySet()) {
      /* RAL 22 Jul 09 -->
      Collections.addAll(allDistinctDomains, arch.split("\\s")); */
      Collections.addAll(allDistinctDomains, arch.split(domainSeparator));
      /* <-- RAL 22 Jul 09 */
    }

    // get domain data
    Set<String> distinctPfamADomains = CollectionUtils.newSet();
    Set<String> distinctPfamBDomains = CollectionUtils.newSet();

    // Separate the Pfam-A and Pfam-B domains
    for (String domain : allDistinctDomains) {
      if (domain.substring(0, 2).equals("PF") ||
              (domain.charAt(0) != 'P' && domain.charAt(0) != 'p')) {
        distinctPfamADomains.add(domain);
      } else {
        distinctPfamBDomains.add(domain);
      }
    }

    // Define the colours for the Pfam-A domains
    /* RAL 7 Jul 09 -->
    int[] colors = ColorLib.getCategoryPalette(distinctPfamADomains.size(),
            1.f, 0.2f, 1.f, 1.0f); */
    int colourIndex = 0;

    // Create a colour table for the PfamA domains
    int[] colourTableA = new int[distinctPfamADomains.size()];

    // Get the Prefuse colour palette for use if there are so many
    // domains that the PDBsum colour list is exhausted
    int[] colorsA = ColorLib.getCategoryPalette(distinctPfamADomains.size(),
             1.f, 0.2f, 1.f, 1.0f);

    // Get start of the PDBsum domain colour list
    int startColour = bbk.dng.Constants.PFAMA_STARTCOL;

    // Create the colour table
    colourTableA = copyToColourTable(colourTableA, colorsA, startColour);

    /* 12 Aug 09 --> */
    // Loop over any Pfam-A domains in the parent sequence to assign
    // colours to them
    String[] domains = parentArchitecture.split(domainSeparator);
    for (String domain : domains) {
      // If a Pfam-A domains, and not already assigned, assign next colour
      // to this domain
      if ((domain.substring(0, 2).equals("PF") ||
              (domain.charAt(0) != 'P' && domain.charAt(0) != 'p')) &&
              domainColour.get(domain) == null) {
        domainColour.put(domain, colourTableA[colourIndex]);
        colourIndex++;
      }
    }
    /* <-- 12 Aug 09 */

    // Get the most distinct colours for the main domains of the chain - Pfam A
    for (String domain : distinctPfamADomains) {
      /* RAL 7 Jul 09 -->
      domainColour.put(domain, colors[colourIndex]); */
      /* 12 Aug 09 --> */
      if (domainColour.get(domain) == null) {
        domainColour.put(domain, colourTableA[colourIndex]);
      /* <-- 12 Aug 09 */
      /* <-- RAL 7 Jul 09 */
        colourIndex++;
      }
    }

    // Repeat for the Pfam-B domains
    colourIndex = 0;
    int[] colourTableB = new int[distinctPfamBDomains.size()];

    // Get the Prefuse colour palette for use if there are so many
    // domains that the PDBsum colour list is exhausted
    int[] colorsB = ColorLib.getCategoryPalette(distinctPfamBDomains.size(),
             1.f, 0.2f, 1.f, 1.0f);

    // Get start of the PDBsum domain colour list
    startColour = bbk.dng.Constants.PFAMB_STARTCOL;

    // Create the colour table
    colourTableB = copyToColourTable(colourTableB, colorsB, startColour);

    // Get the most distinct colours for the main domains of the chain - Pfam B
    for (String thisDomain : distinctPfamBDomains) {
      /* RAL 7 Jul 09 -->
      domainColour.put(thisDomain, colors[colourIndex]); */
      domainColour.put(thisDomain, colourTableB[colourIndex]);
      colourIndex++;
      /* <-- RAL 7 Jul 09 */
    }

    // Define number of parent nodes for use by graph layout manager
    CustomizedForceDirectedLayout force
            = ((CustomizedForceDirectedLayout) appFrame.getGraphPanel().
            getActionLayout().get(1));
    force.setnParentDomains(nParentDomains);

    // Set the domain colours
    appFrame.getGraphPanel().setDomainColours(domainColour, domainSeparator, useCATH);

    appFrame.getGraphPanel().getVisualization().
            getGroup(Visualization.FOCUS_ITEMS).clear();

    appFrame.getGraphPanel().getVisualization().removeGroup("graph");

    // if the graph rendering is currently off, turn it on
    if (GraphStylePanelActions.getInstance().getGraphRenderingStatus().equals("stopped")) {
      GraphStylePanelActions.getInstance().toggleGraphAction(appFrame);
    }

    VisualGraph vg
            = appFrame.getGraphPanel().getVisualization().addGraph("graph",
            Main.graph);

    VisualItem f = (VisualItem) vg.getNode(graphBuilder.getParentNodeId());
    appFrame.getGraphPanel().getVisualization().
            getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
    f.setFixed(false);

    appFrame.getGraphPanel().getVisualization().setValue("graph.nodes",
            null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);
    appFrame.getGraphPanel().getVisualization().setValue("graph.edges",
            null, VisualItem.INTERACTIVE, Boolean.FALSE);

    appFrame.getGraphPanel().getVisualization().repaint();


    //graphPanel.getVisualization().runAfter("draw","layout");

    // show sequence nodes
    /* RAL 2 Jul 09 -->
    if (appFrame.getGraphStylePanel().getShowSequenceCheckBox().isSelected()) {
      GraphStylePanelActions.getInstance().addSequenceNodes(appFrame);
    }
    <-- RAL 2 Jul 09 */

    // Show sequence, structure or enzyme nodes if selected
    if (appFrame.getGraphStylePanel().getAddSequencesRadioButton().isSelected()) {
      GraphStylePanelActions.getInstance().
              AddRemoveSelectedNodes(appFrame,
              GraphStylePanelActions.ADD_SEQUENCES, offLine);
    }
    else if (appFrame.getGraphStylePanel().getAddStructuresRadioButton().isSelected()) {
      GraphStylePanelActions.getInstance().
              AddRemoveSelectedNodes(appFrame,
              GraphStylePanelActions.ADD_STRUCTURES, offLine);
    }
// RAL 1 Nov 10 -->
//    else if (appFrame.getGraphStylePanel().getAddEnzymesRadioButton().isSelected()) {
    else if (appFrame.getGraphStylePanel().getAddEnzymesRadioButton().isSelected() ||
            addEnzymes) {
// <-- RAL 1 Nov 10
      GraphStylePanelActions.getInstance().
              AddRemoveSelectedNodes(appFrame,
              GraphStylePanelActions.ADD_ENZYMES, offLine);
    }

    // fit the graph
    Rectangle2D bounds = appFrame.getGraphPanel().getVisualization().
            getBounds("graph");
    GraphicsLib.expand(bounds, 300 + (int) (1 / appFrame.getGraphPanel().
            getVisualization().getDisplay(0).getScale()));
    DisplayLib.fitViewToBounds(appFrame.getGraphPanel().getVisualization().
            getDisplay(0), bounds, 0);


    /* <-- AUG 24 Feb 10 */
      Thread t = new Thread(new RenderingCountdown(appFrame));
      t.start();
      /* AUT 24 Feb 10 --> */

    // Initialise sequences count
    int sequenceCount = 0;

    // Count domain occurrences
    Map<String, Integer> domainOccurrences = CollectionUtils.newMap();
    Map<String, Integer> domainArchitectureOccurrences
            = CollectionUtils.newMap();
    for (String d : allDistinctDomains) {
      domainOccurrences.put(d, 0);
      domainArchitectureOccurrences.put(d, 0);
    }

    // Count total numbers of sequences
    for (String a : archSeqListPair.keySet()) {
      sequenceCount += archSeqListPair.get(a).size();
      Set<String> tmp = CollectionUtils.newSet();
      /* RAL 22 Jul 09 -->
      for (String d : a.split("\\s")) { */
      for (String d : a.split(domainSeparator)) {
      /* <-- RAL 22 Jul 09 */
        int i = domainOccurrences.get(d) + 1;
        domainOccurrences.put(d, i);

        if (!tmp.contains(d)) {
          int j = domainArchitectureOccurrences.get(d) + 1;
          domainArchitectureOccurrences.put(d, j);
          tmp.add(d);
        }
      }
    }
    
    // Generate HTML for lower panel
    StringBuilder sb = new StringBuilder();

    /* RAL 3 Jul 09 --> */
    // Get the domains making up the parent architecture
    /* RAL 22 Jul 09 -->
    String[] parentDomains = parentArchitecture.split("\\s"); */
    String[] parentDomains = parentArchitecture.split(domainSeparator);
    /* <-- RAL 22 Jul 09 */

    // Get a list of domains in this architecture
    List<String> pDomains = CollectionUtils.newList();
    for (String d : parentDomains) {
      pDomains.add(d);
    }

    // Get the sequence id and protein name
//    String proteinName = sequence.get("protein_name");

    // Get the 3D coverage
    String coverage = archCoveragePair.get(parentArchitecture);

    // Add spacer at top of frame
    sb = addSpacer(sb, 1, 6);

    // Show heading
    sb.append("<center>");
    sb.append("<b>Graph of related " + domainTypeDesc[domainType] +
            " domain architectures</b>");
    sb.append("</center>");

    // Add extra spacer
    sb = addSpacer(sb, 1, 6);

    // Start outer table
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td valign=top>");

    // Write out parent sequence's domain architecture
    sb = nodeDomainsTable(sb, parentDomains, coverage, domainColour,
            "parent");

    // Add name of selected sequence
    sb.append("</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td valign=top><b>Parent sequence:</b></td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td valign=top><font color=blue>" + parentSequence + "</font></td>");
    sb.append("<td><font color=blue>&nbsp;-&nbsp;</font></td>");
    sb.append("<td valign=top><font color=blue>" + parentName + "</font></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Set the title of the frame
    appFrame.setTitle(parentSequence + " - " + parentName);

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // Start table surrounding the key and the stats
    sb.append("<table cellpadding=0 cellspacing=0>");

    // Show the key
    sb.append("<tr><td valign=top>");
    sb = showKey(sb);
    sb.append("</td>");

    // Show the stats
    sb.append("<td align=right valign=top>");
    sb = showStats(sb, nArch, nFinal, nCollapsed, nPfam, nSeqs);
    sb.append("</td></tr>");
    sb.append("</table>");

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // List the domains in this architecture
    sb = showDomainList(sb, pDomains, null, null, domainColour);

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // Get a list of domain on the plot, sorted in decreasing order of
    // occurrence on the plot
    List<String> entries
            = CollectionUtils.getKeysSortedByValue(domainOccurrences, true);

    // Show the ordered list of domains in decreasing order of occurrence
    // on the plot
    sb = showDomainList(sb, entries, domainOccurrences,
            domainArchitectureOccurrences, domainColour);
    /* <-- RAL 3 Jul 09 */

    // Save the master text for the data panel
    masterText = "<html>" + bbk.dng.Constants.HEAD_HTML
            + "<body font=\"sans-serif\">" + sb + "</body></html>";

    // Create a new string the for enzyme table
    sb = new StringBuilder();

    // Show the enzyme colour-ranges table
    sb = showEnzymeColoursTable(sb);

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // Get the list of enzyme codes appearing on the plot
    Map<Integer, Map<String, String>> enzyme = getEnzymeDetails();

    // Add the list of enzymes, their names and architecture and sequence
    // counts
    sb = showEnzymeList(sb, enzyme);

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // Save as the enzyme master text
    enzymesText = "<html>" + bbk.dng.Constants.HEAD_HTML
            + "<body font=\"sans-serif\">" + sb + "</body></html>";

    // Apply to data panel
    appFrame.getDataPane().setText(masterText);

    // Reset default cursor
    appFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

// RAL 22 Oct 10 -->
    // If in off-line mode, wait for the plot to stop before generating the
    // PostScript plot
    if (offLine) {
      for (int i = 0; i < 50 && t.isAlive(); i++) {
        Thread.sleep(1000);
      }

      // Open output PostScript file
      int errorCode = 0;
      boolean landscape = false;
      boolean writeError = false;
      String psName = "ArchSchema.ps";
      try {
        // Write out the PostScript file
        WritePSFile writePS = new WritePSFile(psName, appFrame,
                useCATH, landscape);

        // If file written OK, then show message
        if (!writePS.getOK()) {
          writeError = true;
        }

       // If error writing file, set flag
      } catch (IOException error) {
        writeError = true;
      }

      // If write failed, show warning message
      if (writeError) {
        System.out.println("*** ERROR. Failed to write output PostScript file "
                + psName);
        errorCode = -1;
      }

      // Loop twice to write out the domains and enzymes tables
      for (int loop = 0; loop < 2; loop++) {

        String outName;
        String htmlPage;

        // On first loop, write out the list of domains
        if (loop == 0) {
          outName = "domains.html";
          htmlPage = masterText;
        }

        // On second loop, write out the list of enzymes
        else {
          outName = "enzymes.html";
          htmlPage = enzymesText;
        }

        // Open output html file
        PrintStream out = new PrintStream(outName);

        // Write out the table
        out.println(htmlPage);

        // Close the file
        out.close();
      }

      // Terminate the program
      System.exit(errorCode);
    }
// <-- RAL 22 Oct 10
  }

  /* RAL 23 Jul 09 --> */
  // Pick up the selection criteria entered by the user in the sequence
  // criteria panel
  private Vector<Object> getUserFilterCriteria(AppFrame appFrame) {
    Vector<Object> filterCriteria = new Vector<Object>();

    // Get the Pfam domain logic operator (and/or)
    String pfamDomainOperator
            = SwingUtils.getSelection(appFrame.getGraphCriteriaPanel().
            getDomainOperatorRadioButtonGroup()).getText().trim();

    // Get the selected Pfam domains
    List<String> pfamDomainsSelected = CollectionUtils.newList();
    for (Object t : appFrame.getGraphCriteriaPanel().getDomainList().
            getSelectedValues()) {
      pfamDomainsSelected.add(((NameValue) t).getValue());
    }

    // Get selected organism
    String organism = ((NameValue) appFrame.getGraphCriteriaPanel().
            getOrganismComboBox().getSelectedItem()).getValue();

    // Get PDB selection option
    boolean pdbOnly = !appFrame.getGraphCriteriaPanel().getPdbOptionComboBox().
            getSelectedItem().toString().equals("ALL");

    // Package the filters into a vector
    filterCriteria.add(pfamDomainOperator);
    filterCriteria.add(pfamDomainsSelected);
    filterCriteria.add(organism);
    filterCriteria.add(pdbOnly);

    // Return the filter criteria
    return filterCriteria;
  }
  /* <-- RAL 23 Jul 09 */

  // Return the given number of architectures that are the most similar to
  // the parent
  public ArrayList<String> getClosestArchitectures(Map<Integer, Map<String, String>> index,
          int itemsToReturn) {
    Map<String, Double> distances = CollectionUtils.newMap();

    // Loop over the architectures
    for (Integer key: index.keySet()) {

      // Get the architecture and its distance from the parent architecture
      Map<String, String> archData = index.get(key);
      String architecture = archData.get("architecture");
      double dist = 1000 - (double) Double.parseDouble(archData.get("dist"));

      // Store in the distances array
      distances.put(architecture, dist);
    }

    // Sort the distances and return the number required
    List<String> sortedArchitectures
            = CollectionUtils.getKeysSortedByValue(distances, true);
    return new ArrayList<String>(sortedArchitectures.subList(0, itemsToReturn));
  }

  // Get the architecture-PDB code mappings and architecture-coverage
  // mappings
  private void getArchitectureMappings(Set<Map<String, String>> archSet) {

    // Initialise the mappings
    archSeqListPair = CollectionUtils.newMap();
    archPDBListPair = CollectionUtils.newMap();
    archCoveragePair = CollectionUtils.newMap();

    // Loop over the architectures to pick up the relevant pairings
    Iterator iArch = archSet.iterator();
    while (iArch.hasNext()) {
      // Get the next architecture record
      Map<String, String> archDetails = (Map<String, String>) iArch.next();
      
      // Get this architecture and its identifier
      String architecture = archDetails.get("architecture");

      // If we already have this architecture, add the UniProt sequence
      // to its list of sequences and any PDB codes to its list of
      // structures
      if (archSeqListPair.containsKey(architecture)) {

        // Add current sequence to list
        archSeqListPair.get(architecture).add(archDetails.get("uniprot_id"));

        // Add any PDB codes to list
        String pdbCodes = archDetails.get("pdb_codes");
        if (pdbCodes != null && pdbCodes.length() != 0 &&
                !pdbCodes.equals("NONE")) {

          // Split list into individual PDB codes
          for (String code : pdbCodes.split(" ")) {

            // If code too long (ie has too long a chain list)
            // truncate and add ellipsis
            if (code.length() > 8) {
              // Truncate
              code = code.substring(0, 8) + "...";
            }

            // Add to PDB list
            archPDBListPair.get(architecture).add(code);
          }
        }

        // Get the 3D coverage of this architecture
        String coverage = archDetails.get("3D_coverage");
        
        // If not empty, and current architecture doesn't already
        // have coverage, pair up with the current architecture
        if (!coverage.equals("NONE")) {

          // Get the currently stored coverage
          String oldCoverage = archCoveragePair.get(architecture);

          // If current coverage is empty, replace it with this one
          if (oldCoverage.equals("NONE")) {
            archCoveragePair.put(architecture, coverage);
          } // Otherwise, check to see if this coverage is fuller than the
          // one we already have, or whether the two complement each other
          else if (coverage.length() == oldCoverage.length()) {

            // Initialise new string
            String newCoverage = "";

            // Loop over the characters in the coverage string
            for (int iPos = 0; iPos < coverage.length(); iPos++) {

              // Compare the two characters at this position
              char ch = coverage.charAt(iPos);
              char oldCh = oldCoverage.charAt(iPos);

              // If new character represents a higher coverage than the
              // old, then replace
              if ((ch == 'A' && (oldCh == 'P' || oldCh == 'F' ||
                      oldCh == '.')) ||
                      (ch == 'P' && (oldCh == 'F' || oldCh == '.')) ||
                      (ch == 'F' && oldCh == '.') ||
                      (ch == '-' && oldCh == ' ')) {
                // Copy the new coverage char
                newCoverage = newCoverage + ch;
              }

              // Otherwise, use old character
              else {
                newCoverage = newCoverage + oldCh;
              }
            }

            // If the new string is not the same as the old, replace it
            if (!newCoverage.equals(oldCoverage)) {
              archCoveragePair.put(architecture, newCoverage);
            }
          }
        }

      // Otherwise, create a new architecture record, create a new
      // sequences list and store the current UniProt seq id. Also,
      // store list of PDB codes and 3D coverage
      } else {
        // Create a new sequence list for this architecture
        List<String> seqList = CollectionUtils.newList();

        // Add current sequence to sequence list
        seqList.add(archDetails.get("uniprot_id"));

        // Join the sequence list to the current architecture
        archSeqListPair.put(architecture,seqList);

        // Repeat for PDB codes attached to this node
        String pdbCodes = archDetails.get("pdb_codes");
        List<String> pdbList = getPDBList(pdbCodes);

        // Join the PDB codes list to the current architecture
        archPDBListPair.put(architecture,pdbList);

        // Get the 3D coverage of this architecture
        String coverage = archDetails.get("3D_coverage");

        // Pair up with the current architecture
        archCoveragePair.put(architecture, coverage);
      }
    }
  }

  // Get the architecture-enzyme mappings
  private void getEnzymeMappings(Set<Map<String, String>> enzSet) {

    // Initialise the mappings
    archEnzymeListPair = CollectionUtils.newMap();

    // Loop over the architectures to pick up the relevant pairings
    Iterator iEnz = enzSet.iterator();
    while (iEnz.hasNext()) {
      // Get the next architecture record
      Map<String, String> enzDetails = (Map<String, String>) iEnz.next();

      // Get this architecture
      String architecture = enzDetails.get("architecture");

      // Form label containing EC code and number of seqs
      int nSeqs = Integer.parseInt(enzDetails.get("nseqs"));
      String endString = "seqs)";
      if (nSeqs == 1) {
        endString = "seq)";
      }
      String enzyme = enzDetails.get("id") + " (" + nSeqs + endString;

      // If we already have this architecture, add the enzyme code to it
      if (archEnzymeListPair.containsKey(architecture)) {

        // Add current enzyme and number of sequences to list
        archEnzymeListPair.get(architecture).add(enzyme);

      // Otherwise, create a new architecture record, create a new
      // enzymes list and store the current enzyme
      } else {
        // Create a new sequence list for this architecture
        List<String> enzList = CollectionUtils.newList();

        // Add current sequence to sequence list
        enzList.add(enzyme);

        // Join the sequence list to the current architecture
        archEnzymeListPair.put(architecture,enzList);
      }
    }
  }

  // Convert list of PDB codes in string into a list of strings
  public List<String> getPDBList(String pdbCodes) {

    List<String> pdbList = CollectionUtils.newList();

    // Split the PDB codes corresponding to the current
    // UniProt sequence
    if (pdbCodes != null && pdbCodes.length() != 0 &&
            !pdbCodes.equals("NONE")) {
      // Split list into individual PDB codes
      for (String code : pdbCodes.split(" ")) {

        // If code too long (ie has too long a chain list)
        // truncate and add ellipsis
        if (code.length() > 8) {
          // Truncate
          code = code.substring(0, 8) + "...";
        }

        // Add to PDB list
        pdbList.add(code);
      }
    }

    // Return the list
    return pdbList;
  }
  /* <-- RAL 22 Jul 09 */

  // Retrieve the error type
  public String getErrorType() {
    String errorType = "Unknown error";
    if (seqSearchResults != null) {
      errorType = (String) seqSearchResults.get(SwissPfamSearcher.ERR_TYPE);
    }
    return errorType;
  }

  // Retrieve the error message
  public String getErrorMessage() {
    String errorMessage = "No error message";
    if (seqSearchResults != null) {
      errorMessage
              = (String) seqSearchResults.get(SwissPfamSearcher.ERR_MESSAGE);
    }
    return errorMessage;
  }

  // Retrieve number of domains in the parent sequence
  public int getNDomains() {
    int nDomains = 0;
    if (seqSearchResults != null) {
      nDomains = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_NDOMAINS);
    }
    return nDomains;
  }

  // Retrieve number of Pfam domains from last sequence search
  public int getNPfam() {
    int nPfam = 0;
    if (seqSearchResults != null) {
      nPfam = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_NPFAM);
    }
    return nPfam;
  }

  // Retrieve number of sequences from last sequence search 
  public int getNSequences() {
    int nSequences = 0;
    if (seqSearchResults != null) {
      nSequences = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_NSEQS);
    }
    return nSequences;
  }
  
  // Retrieve number of unique architectures found
  public int getNArch() {
    int nUnique = 0;
    if (seqSearchResults != null) {
      nUnique = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_ARCHITECTURES);
    }
    return nUnique;
  }

  // Retrieve filtered number of architectures returned by search
  public int getNFinal() {
    int nFinal = 0;
    if (seqSearchResults != null) {
      nFinal = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_NFILTERED);
    }
    return nFinal;
  }

  // Retrieve number of architectures after collapse (if any) of Pfam-B domains
  public int getNCollapsed() {
    int nCollapsed = 0;
    if (seqSearchResults != null) {
      nCollapsed = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_NCOLLAPSED);
    }
    return nCollapsed;
  }

  // Retrieve number of species from last sequence search 
  public int getNSpecies() {
    int nSpecies = 0;
    if (seqSearchResults != null) {
      nSpecies = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RESULT_NSPECIES);
    }
    return nSpecies;
  }

  // Retrieve the parent sequence
  public String getParentSequence() {
    String seqId = "";
    if (seqSearchResults != null) {
      seqId = (String) seqSearchResults
              .get(SwissPfamSearcher.RESULT_PARENT_SEQUENCE);
    }
    return seqId;
  /* <-- RAL 22 Jul 09 */
  }

  // Retrieve the run status - ie successful search or error
  public int getRunStatus() {
    int status = SwissPfamSearcher.UNKNOWN;
    if (seqSearchResults != null) {
      status = (Integer) seqSearchResults
              .get(SwissPfamSearcher.RUN_STATUS);
    }
    return status;
  }

  // Get the full details of the search sequence
  public Set<Map<String, String>> getArchitectures() {
    Set<Map<String, String>> archSet = CollectionUtils.newSet();
    
    if (seqSearchResults != null) {

      // Retrieve the architecture data
      archSet = (Set<Map<String, String>>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_HITS);
    } else {
      return null;
    }
    return archSet;
  }

  // Get the enzyme classes associated with each node
  public Set<Map<String, String>> getEnzymes() {
    Set<Map<String, String>> enzSet = CollectionUtils.newSet();

    if (seqSearchResults != null) {

      // Retrieve the architecture data
      enzSet = (Set<Map<String, String>>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_EC_NUMBERS);
    } else {
      return null;
    }
    return enzSet;
  }

  // Get the list of unique domains in the search sequence
  public List<String> getDomainList(String architecture) {
    List<String> domains = CollectionUtils.newList();

    // Split the architecture into its Pfam domains
    for (String pfam : architecture.split(domainSeparator)) {

      // If not already in the list, then add
      if (!domains.contains(pfam)) {
        domains.add(pfam);
      }
    }
    return domains;
  }

  // Get the list of domain details for domains in the search sequence
  public Map<String, Map<String, String>> getDomainDetails() {
    Map<String, Map<String, String>> domainMap = CollectionUtils.newMap();

    if (seqSearchResults != null) {

      // Retrieve the architecture data
      domainMap =  (Map<String, Map<String, String>>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_PFAM);
    } else {
      return null;
    }
    return domainMap;
  }

  // Get the list of species details
  public Map<String, Map<String, String>> getSpeciesDetails() {
    Map<String, Map<String, String>> speciesMap = CollectionUtils.newMap();

    if (seqSearchResults != null) {

      // Retrieve the sequence hits vector
      speciesMap = (Map<String, Map<String, String>>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_SPECIES);
    } else {
      return null;
    }
    return speciesMap;
  }

  // Get the enzyme details
  public Map<Integer, Map<String, String>> getEnzymeDetails() {
    Map<Integer, Map<String, String>> enzymeMap = CollectionUtils.newMap();

    if (seqSearchResults != null) {

      // Retrieve the sequence hits vector
      enzymeMap = (Map<Integer, Map<String, String>>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_ENZYME_NAMES);
    } else {
      return null;
    }
    return enzymeMap;
  }

  // Get the architectures index containing distances from the parent
  // architecture
  private Map<Integer, Map<String, String>> getArchIndex() {
    Map<Integer, Map<String, String>> archIndex = CollectionUtils.newMap();

    if (seqSearchResults != null) {

      // Retrieve the architectures index
      archIndex = (Map<Integer, Map<String, String>>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_ARCHINDEX);
    } else {
      return null;
    }
    return archIndex;
  }

  // Get the architecture-node connectivities
  private Map<Pair<String, String>, Integer> getConnectionsList() {
    Map<Pair<String, String>, Integer> connectionsList = CollectionUtils.newMap();

    if (seqSearchResults != null) {

      // Retrieve the architectures index
      connectionsList = (Map<Pair<String, String>, Integer>) seqSearchResults
              .get(SwissPfamSearcher.RESULT_CONNECTIONS_MATRIX);
    } else {
      return null;
    }
    return connectionsList;
  }

  // Retrieve maximum number of architectures
  public int getmaxArchitectures() {
    int maxArchitectures = 0;
    if (seqSearchResults != null) {
      try {
        maxArchitectures
                = (Integer) seqSearchResults.get(SwissPfamSearcher.RESULT_MAX_ARCHITECTURES);
      } catch (Exception e) {
        maxArchitectures = 0;
      }
    }
    return maxArchitectures;
  }

  public void resetAction(AppFrame appFrame) {
    // clear graph
    appFrame.getGraphPanel().clearGraph();

    // clear data pane
    // appFrame.getDataPane().setAutoscrolls(false);
    appFrame.getDataPane().setText("");

    // reset graph style panel

    // set search panel
    appFrame.getInputPanel().getSequenceTextField().setText("");
    appFrame.getGraphCriteriaPanel().getSequenceInfoLabel().setText("");

  }

  public String getDomainSeparator() {
    return domainSeparator;
  }

  public String getParentArchitecture() {
    return parentArchitecture;
  }

  /* RAL 20 Jul 09 -->
  public Map<String, Map<String, String>> getDomainDetails() {
    return domainDetails;
  }
  <-- RAL 20 Jul 09 */

  public String getEnzymesText() {
    return enzymesText;
  }

  public String getMasterText() {
    return masterText;
  }

  public void setImgURL(URL imgURL) {
    this.imgURL = imgURL;
  }

  public int getDomainType() {
    return this.domainType;
  }

  public String getCoverage(String architecture) {
    String coverage = " ";

    // Get the 3D coverage
    if (archCoveragePair != null) {
      coverage = archCoveragePair.get(architecture);
    }

    // Return this architecture's 3D coverage
    return coverage;
  }

  public Map<String, Integer> getDomainColour() {
    return domainColour;
  }

  // Add a spacer table
  public StringBuilder addSpacer(StringBuilder sb, int width, int height) {

    // Create a table holding a single 1x1.gif of the given dimensions
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td><img width=" + width + " height=" + height + " src=\""
            + imgURL + "1x1.gif\"></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Return the string with spacer table added
    return sb;
  }

  // Create a table listing the sequences belonging to the given architecture
  public StringBuilder showSequencesList(StringBuilder sb,
          String architecture) {

    // Initialise names of images
    String tickName = imgURL + "greentick.gif";
    String tickName1 = imgURL + "greentick1.gif";
    String tickImage = "<img border=0 src=\"" + imgURL + "greentick.gif"
            + "\">";
    String tickImage1 = "<img border=0 src=\"" + imgURL + "greentick1.gif"
            + "\">";

    // Show the heading
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><b>List of UniProt sequences:</b></td>");
    sb.append("</tr>");
    sb.append("</table>");

    // Start the table
    sb.append("<table cellpadding=0 cellspacing=0>");

    // Show column headings
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=left><u>UniProt</u></td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=left><u>Code</u></td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td colspan=3 align=left><u>PDB*</u></td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=left><u>Protein name</u></td>");

    sb.append("</tr>");

    // Initialise a string of already-processed sequences
    String doneSeqs = "";

    // Initialise the number of sequences listed
    int nSeqs = 0;
    int nListed = 0;
    int nTotal = 0;

    // Retrieve the architecture records
    //Set<Map<String, String>> archSet = getArchitectures();

    // Outer loop: 3 times to pick up (1) sequences with full PDB coverage,
    // (2) sequences with partial PDB coverage, and (3) the rest
    for (int loop = 0; loop < 3; loop++) {

      // Re-initialise count of sequences
      nSeqs = 0;

      // Loop over the architectures to pick up the relevant pairings
      Iterator iArch = architectures.iterator();
      while (iArch.hasNext()) {

        // Get the next architecture record
        Map<String, String> archDetails = (Map<String, String>) iArch.next();

        // If this is the right architecture, write out the sequence details
        if (architecture.equals(archDetails.get("architecture"))) {

          // Add to table providing we haven't already hit the limit
          if (nListed < bbk.dng.Constants.MAX_SEQS_IN_LIST) {
            
            // Get this sequence's 3D coverage
            String coverage = archDetails.get("3D_coverage");
            
            // If don't yet have the total number of sequences for this
            // architecture, get it now
            if (nTotal == 0) {
              nTotal = (int) Integer.parseInt(archDetails.get("nseqs"));
            }

            // Check whether this sequence is wanted for current loop
            boolean wanted = false;
            if (loop == 0) {

              // If any part of the structure is missing, then don't want
              // this sequence for this loop
              if (coverage != null && !coverage.equals("NONE")) {
                if (coverage.indexOf(' ') > -1 ||
                        coverage.indexOf('.') > -1 ||
                        coverage.indexOf('P') > -1 ||
                        coverage.indexOf('F') > -1) {
                  wanted = false;
                } else {
                  wanted = true;
                }
              }
            } else if (loop == 1) {

              // For second loop, want incomplete structures only
              if (coverage != null && !coverage.equals("NONE")) {
                if (coverage.indexOf(' ') > -1 ||
                        coverage.indexOf('.') > -1 ||
                        coverage.indexOf('P') > -1 ||
                        coverage.indexOf('F') > -1) {
                  wanted = true;
                } else {
                  wanted = false;
                }
              }
            } else if (loop == 2) {
              if (coverage == null || coverage.equals("NONE")) {
                wanted = true;
              } else {
                wanted = false;
              }
            }

            // If wanted, add to table
            if (wanted) {
              // Show sequence details
              sb.append("<tr>");

              // Get the UniProt accession code and is
              String code = archDetails.get("uniprot_acc");
              String id = archDetails.get("uniprot_id");

              // UniProt accession
              sb.append("<td>&nbsp;&nbsp;</td>");
              sb.append("<td valign=top align=left><a href=\""
                      + bbk.dng.Constants.URL_UNIPROT + code + "\">"
                      + code + "</a></td>");

              // UniProt id
              sb.append("<td>&nbsp;&nbsp;</td>");
              sb.append("<td valign=top align=left>" + id + "</td>");

              // Get the protein name, species and 3D coverage
              String proteinName = archDetails.get("protein_name");
              String organism = archDetails.get("organism");

              // Show whether this sequence has any PDB structures
              sb.append("<td>&nbsp;&nbsp;</td>");
              if (coverage != null && !coverage.equals("NONE")) {

                // Assume coverage is of the complete sequence
                String showImage = tickName;

                // Check whether any missing parts of the sequence
                if (coverage.indexOf(' ') > -1 ||
                        coverage.indexOf('P') > -1 ||
                        coverage.indexOf('F') > -1) {
                  showImage = tickName1;
                }

                // Give link to PDBsum uniplot page
                sb.append("<td valign=top align=center><a href=\""
                        + bbk.dng.Constants.URL_PDBSUM_UNIPLOT + code
                        + "&uniprot_code=" + id + "\"><img border=0 src=\""
                        + showImage + "\"></a></td>");

                // Show number of associated PDB entries
                sb.append("<td>&nbsp;&nbsp;</td>");
                String numPDB = "-";
                String pdbCodes = archDetails.get("pdb_codes");
                if (pdbCodes != null && pdbCodes.length() != 0
                        && !pdbCodes.equals("NONE")) {
                  // Initialise count
                  int nPDB = 0;

                  // Split list into individual PDB codes
                  for (String c : pdbCodes.split(" ")) {

                    // Increment count
                    nPDB++;
                    numPDB = "" + nPDB;
                  }
                }
                sb.append("<td>" + numPDB + "</td>");

              } else {
                sb.append("<td colspan=3 valign=top align=center>-</td>");
              }

              // Show protein name
              sb.append("<td>&nbsp;&nbsp;</td>");
              sb.append("<td valign=top align=left>" + proteinName);
              if (organism != null && organism.length() > 1) {
                sb.append(" <font color=purple><i>" + organism + "</i></font>");
              }
              sb.append("</td>");

              sb.append("</tr>");

              // Increment count of listed sequences
              nListed++;
            }
          }

          // Increment count of sequences
          nSeqs++;
        }
      }
    }

    // Close off the table
    sb.append("</table>");

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // Show table giving number of sequences
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><b>Number of sequences listed: " + nSeqs);
    if (nTotal > nSeqs) {
      sb.append("<font color=red> (from " + nTotal +
              " total sequences for this architecture)</font>");
    }
    sb.append("</b>");
    sb.append("</td></tr>");
    sb.append("</table>");

    // Add spacer
    sb = addSpacer(sb, 1, 10);

    // Show explanation of ticks in PDB column
    // Start the footnote table
    sb.append("<table cellpadding=0 cellspacing=0>");

    // Show explanation of ticks in PDB column
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td colspan=13>* <i>PDB column indicates whether the ");
    sb.append("sequence has structural information in the PDB, as follows:");
    sb.append("</i></td></tr>");

    // Show ticks and what they mean
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>");
    sb.append("<td>" + tickImage + "</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><i>Structure(s) of complete sequence;</i></td>");
    sb.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>");
    sb.append("<td>" + tickImage1 + "</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><i>Structure(s) of part of sequence;</i></td>");
    sb.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>");
    sb.append("<td>-</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><i>No structural data.</i></td>");
    sb.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>");
    sb.append("</tr>");

    // Explain the number
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td colspan=13>The number to the right of the tick indicates ");
    sb.append("the number of PDB entries.");
    sb.append("</i></td></tr>");

    // Mention link to PDBsum
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td colspan=13><i>Click on tick to go to list of PDB codes ");
    sb.append("for the sequence in PDBsum.");
    sb.append("</i></td></tr>");

    // Close off the footnote table
    sb.append("</table>");

    // Return the string with the sequences table appended
    return sb;
  }

  // Create a colour table for the current domain set
  private int[] copyToColourTable(int[] colourTable, int[] prefuseColors,
          int startColour) {

    // Initialise variables
    int nextPrefuseColour = 0;
    int dummyColourCode = 99999999;

    // Loop over all the colours to be generated
    for (int iColour = 0; iColour < colourTable.length; iColour++) {

      // Get the next colour in the PDBsum list, if there is one
      if (startColour + iColour < bbk.dng.Constants.NPFAM_COLOURS) {

        // Use this colour
        int colour = (startColour + iColour) % (bbk.dng.Constants.NPFAM_COLOURS);

        // Convert to rgb
        String rgbString = bbk.dng.Constants.COLOUR_DEFN[colour][2];
        String[] rgbVals = rgbString.split("\\s");
        int rgb[] = new int[3];
        int i = 0;
        for (String ival : rgbVals) {
          rgb[i] = Integer.parseInt(ival);
          i++;
        }

        // Store the Prefuse colour library's integer encoding of this RGB
        // colour
        colourTable[iColour] = ColorLib.rgb(rgb[0], rgb[1], rgb[2]);

        // Initialise flag
        int closestColour = dummyColourCode;
        int minDist = 0;

        // Loop over the Prefuse palette to knock out the one that is
        // closest to the colour we've just used
        for (i = 0; i < prefuseColors.length; i++) {

          // Save the prefuse integer code for this colour
          int colourCode = prefuseColors[i];

          // If not knocked out, then calculate colour-to-colour distance
          if (colourCode != dummyColourCode) {

            // Get this colour and its RGB components
            Color c = ColorLib.getColor(colourCode);
            int red = c.getRed();
            int green = c.getGreen();
            int blue = c.getBlue();

            // Get the squared "distance" between these two colours
            int dist = (red - rgb[0]) * (red - rgb[0])
                    + (green - rgb[1]) * (green - rgb[1])
                    + (blue - rgb[2]) * (blue - rgb[2]);

            // If this is the closest distance so far, then store
            if (i == 0 || dist < minDist) {
              minDist = dist;
              closestColour = i;
            }
          }
        }

        // If have a closest colour, then knock it out
        if (closestColour != dummyColourCode) {
          prefuseColors[closestColour] = dummyColourCode;
        }
      } // Otherwise, need to pick the next suitable colour from the
      // Prefuse palette
      else {

        // Initialise flag
        boolean ok = false;
        int colourCode = dummyColourCode;

        // Get the next available Prefuse colour
        while (!ok && nextPrefuseColour < prefuseColors.length) {
          // Get this Prefuse palette colour
          colourCode = prefuseColors[nextPrefuseColour];

          // Increment Pefuse colour counter
          nextPrefuseColour++;

          // If colour hasn't been knocked out, then we can use it
          if (colourCode != dummyColourCode) {
            ok = true;
          }
        }

        // If have a colour, store it
        if (ok) {

          // Store the colour
          colourTable[iColour] = colourCode;

          // Otherwise, store black
        } else {
          // Store black
          colourTable[iColour] = ColorLib.rgb(0, 0, 0);
        }
      }
    }

    // Return the colour table
    return colourTable;
  }

  /* RAL 7 Jul 09 --> */
  // Get the next domain colour from the domain colours list
  private int getNextColour(int startColour, int iColour) {

    // Get the next colour in the list
    int colour = (startColour + iColour)
            % (bbk.dng.Constants.NPFAM_COLOURS);

    // Convert to rgb
    String rgbString = bbk.dng.Constants.COLOUR_DEFN[colour][2];
    String[] rgbVals = rgbString.split("\\s");
    int rgb[] = new int[3];
    int i = 0;
    for (String ival : rgbVals) {
      rgb[i] = Integer.parseInt(ival);
      i++;
    }

    // Get the Prefuse colour library's integer encoding of this RGB
    // colour
    int colourCode = ColorLib.rgb(rgb[0], rgb[1], rgb[2]);

    // Return the colour code
    return colourCode;
  }
  /* <-- RAL 7 Jul 09 */

  /* RAL 6 Jul 09 --> */
  // Form the HTML table showing given node's domain architecture
  public StringBuilder newSearchIcon(StringBuilder sb, String architecture,
          boolean useCATH) {

    // Get the first UniProt sequence for this architecture
    String seqId = null;
    Iterator iArch = architectures.iterator();
    while (iArch.hasNext() && seqId == null) {

      // Get the first architecture record
      Map<String, String> archDetails = (Map<String, String>) iArch.next();

      // If this is the right architecture3, save the UniProt code
      if (archDetails.get("architecture").equalsIgnoreCase(architecture)) {
        // Get the UniProt accession code
        seqId = archDetails.get("uniprot_acc");
      }
    }

    // If have no UniProt sequence, then return empty-handed
    if (seqId == null) return sb;

    // Form name of search image
    String iconURL = "<img border=0 src=\"" + imgURL + "new_search.jpg" + "\">";

    // Form URL for new search
    String url = bbk.dng.Constants.URL_ARCHINDEX + "seqId=" + seqId
            + "&source=New";
    if (useCATH) {
      url = url + "&cath=TRUE";
    }

    // Add URL and image
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td valign=top><a href=\"" + url + "\">" + iconURL
              + "</a></td>");
    sb.append("<td><i>Run ArchSchema with this architecture as parent</i></td>");

    return sb;
  }

  /* RAL 6 Jul 09 --> */
  // Form the HTML table showing given node's domain architecture
  public StringBuilder nodeDomainsTable(StringBuilder sb, String[] domains,
          String coverage, Map<String, Integer> domainColour,
          String nodeType) {

    // Initialise start- and end of HTML table cells for images
    String imgSrc = "<td><img src=\"" + imgURL;
    String imgEnd = "\"></td>";

    // Initialise names of images
    String borderBottom = imgSrc + "border_bottom.gif" + imgEnd;
    String borderMiddle = imgSrc + "border_middle.gif" + imgEnd;
    String borderTop = imgSrc + "border_top.gif" + imgEnd;
    String bottom = imgSrc + "bottom.gif" + imgEnd;
    String bottomGap1 = imgSrc + "bottom_gap1.gif" + imgEnd;
    String bottomGap2 = imgSrc + "bottom_gap2.gif" + imgEnd;
    String covA = imgSrc + "covA.gif" + imgEnd;
    String covAA = imgSrc + "covAA.gif" + imgEnd;
    String covP = imgSrc + "covP.gif" + imgEnd;
    String covGap = imgSrc + "covgap.gif" + imgEnd;
    String gap1 = imgSrc + "gap1.gif" + imgEnd;
    String gap2 = imgSrc + "gap2.gif" + imgEnd;
    String middle = imgSrc + "middle.gif" + imgEnd;
    String middleGap1 = imgSrc + "middle_gap1.gif" + imgEnd;
    String middleGap2 = imgSrc + "middle_gap2.gif" + imgEnd;
    String splitCATHgif = "split_cath.gif";
    String pFamACATHgif = "pfamA_CATH.gif";
    String pFamAgif = "pfamA.gif";
    String pFamBgif = "pfamB.gif";

    // If this is the parent node, replace all image names by their
    // parent equivalents
    if (nodeType.equals("parent")) {
      bottom = imgSrc + "bottom_parent.gif" + imgEnd;
      bottomGap1 = imgSrc + "bottom_gap1_parent.gif" + imgEnd;
      bottomGap2 = imgSrc + "bottom_gap2_parent.gif" + imgEnd;
      covA = imgSrc + "covA_parent.gif" + imgEnd;
      covAA = imgSrc + "covAA_parent.gif" + imgEnd;
      covP = imgSrc + "covP_parent.gif" + imgEnd;
      covGap = imgSrc + "covgap_parent.gif" + imgEnd;
      gap1 = imgSrc + "gap1_parent.gif" + imgEnd;
      gap2 = imgSrc + "gap2_parent.gif" + imgEnd;
      middle = imgSrc + "middle_parent.gif" + imgEnd;
      middleGap1 = imgSrc + "middle_gap1_parent.gif" + imgEnd;
      middleGap2 = imgSrc + "middle_gap2_parent.gif" + imgEnd;
      splitCATHgif = "split_cath_parent.gif";
      pFamACATHgif = "pfamA_CATH_parent.gif";
      pFamAgif = "pfamA_parent.gif";
      pFamBgif = "pfamB_parent.gif";
    }

    // Determine whether we have any 3D coverage
    boolean haveCoverage = false;
    int lenCoverage = 0;
    if (coverage.indexOf('A') > -1 || coverage.indexOf('P') > -1) {
      haveCoverage = true;
      lenCoverage = coverage.length();
    }

    // Start the table
    sb.append("<table cellpadding=0 cellspacing=0>");

    // Left-hand edge
    sb.append("<tr>");
    sb.append(borderTop);

    // Initialise gap counter
    int gap = 0;

    // Loop over the domains to plot the representation of each one
    for (String d : domains) {

      // If first gap, then want narrow gap, otherwise show wide gap
      if (gap == 0) {
        sb.append(gap1);
      } else {
        sb.append(gap2);
      }

      // Increment gap number
      gap++;

      // Get this domain's colour
      sb.append("<td bgcolor=\"");
      Color c = ColorLib.getColor(domainColour.get(d));
      sb.append(String.format("#%02X%02X%02X", c.getRed(), c.getGreen(),
              c.getBlue()));
      sb.append("\"><img src=\"").append(imgURL);

      // Determine whether PfamA or PfamB domain
      String gifName;
      if (d.substring(0, 2).equals("PB")) {
        gifName = pFamBgif;
      } else {
        gifName = pFamAgif;
      }
      if (useCATH) {
        if (d.substring(0, 2).equals("PF")) {
          gifName = pFamACATHgif;
        } else if (d.charAt(0) == 'p') {
          gifName = splitCATHgif;
        }
      }

      // Append name of gif image
      sb.append(gifName).append("\"></td>");
    }

    // Final gap, right-hand edge and end of current line
    sb.append(gap1);
    sb.append(borderTop);
    sb.append("</tr>");

    // If have any structural coverage, show the middle row containing
    // this info
    if (haveCoverage) {
      // Start lower row with left-hand edge
      sb.append("<tr>");
      sb.append(borderMiddle);

      // Re-initialise gap counter
      gap = 0;
      int iPos = 0;

      // Loop over the domains a second time to plot the 3D coverage of each
      for (String d : domains) {

        // If first gap, then want narrow gap, otherwise show wide gap
        if (gap == 0) {
          sb.append(middleGap1);
        } else {

          // Check whether have 3D coverage spanning this gap
          if (iPos < lenCoverage && coverage.charAt(iPos) == '-') {
            sb.append(covGap);
          } else {
            sb.append(middleGap2);
          }

          // Increment position in 3D coverage
          iPos++;
        }

        // Increment gap number
        gap++;

        // Get this domain's 3D coverage
        char ch = ' ';
        if (iPos < lenCoverage) {
          ch = coverage.charAt(iPos);
        }

        // If no coverage, just print blank
        if (ch == ' ') {
          sb.append(middle);
        } // If full coverage, check whether have "double"
        else if (ch == 'A') {
          // If next character is a hyphen, the print "double" A image,
          // otherwise, print standard
          if (iPos + 1 < coverage.length() &&
                  coverage.charAt(iPos + 1) == '-') {
            sb.append(covAA);
          } else {
            sb.append(covA);
          }
        } // If have partial coverage, show marker
        else if (ch == 'P') {
          sb.append(covP);
        } // Otherwise, print blank
        else {
          sb.append(middle);
        }

        // Increment coverage position
        iPos++;
      }

      // Final gap, right-hand edge and end of current line
      sb.append(middleGap1);
      sb.append(borderMiddle);
      sb.append("</tr>");
    }

    // Start bottom row with left-hand edge
    sb.append("<tr>");
    sb.append(borderBottom);

    // Re-initialise gap counter
    gap = 0;

    // Loop over the domains a third time to plot the bottom row
    for (String d : domains) {

      // If first gap, then want narrow gap, otherwise show wide gap
      if (gap == 0) {
        sb.append(bottomGap1);
      } else {
        sb.append(bottomGap2);
      }

      // Increment gap number
      gap++;

      // Print bottom row under a domain
      sb.append(bottom);
    }

    // Final gap, right-hand edge and end of current line
    sb.append(bottomGap1);
    sb.append(borderBottom);
    sb.append("</tr>");

    // Close off table
    sb.append("</table>");

    // Return the input string with table appended
    return sb;
  }

  /* RAL 24 Jul 09 --> */
  private Map<String, List<String>>
          replaceParents(Map<String, List<String>> architectures,
          SwissPfamSearcher searcher) {

    // If parent architecture absent, then add back in
    if (!architectures.containsKey(parentArchitecture)) {

    // add the parent architecture
      List<String> tmp = CollectionUtils.newList();
      tmp.add(parentSequence);
      architectures.put(parentArchitecture, tmp);

      // Add back this sequence's PDB codes and coverage
      List<String> pdbList = searcher.getPDBList(parentPDBCodes);
      archPDBListPair.put(parentArchitecture, pdbList);
      archCoveragePair.put(parentArchitecture, parentCoverage);

      // If parent architecture present, but parent sequence isn't, then
      // add it back in
    } else if (!architectures.get(parentArchitecture).contains(parentSequence)) {
      // parent architecture exists, doesn't have parent sequence
      //architectures.get(parentArchitecture).add(parentSequence);

      // Add back this sequence's PDB codes and coverage
      List<String> pdbList = searcher.getPDBList(parentPDBCodes);
      archPDBListPair.put(parentArchitecture, pdbList);
      archCoveragePair.put(parentArchitecture, parentCoverage);
    }

    // Return new architectures map
    return architectures;
  }
  /* <-- RAL 24 Jul 09 */

  /* RAL 7 Jul 09 --> */
  // Show the key explaining architecture nodes and domain symbols
  private StringBuilder showKey(StringBuilder sb) {
    // Initialise names of images
    String imgSrc = "<td align=center valign=center><img src=\"" + imgURL;
    String imgEnd = "\"></td>";

    // Show the key
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><b>Key:</b></td>");
    sb.append("<td>&nbsp;&nbsp;</td>");

    // Get colour of very first PfamA domain
    int colourCode = getNextColour(bbk.dng.Constants.PFAMA_STARTCOL, 0);
    Color cCode = ColorLib.getColor(colourCode);
    String colourString = String.format("#%02X%02X%02X", cCode.getRed(),
            cCode.getGreen(), cCode.getBlue());

    // Show PfamA domain
    sb.append("<td align=center valign=center>");
    String dummyDomain = "PF00000";
    if (useCATH) dummyDomain = "1.10.20.300";
    sb = singleDomainTable(sb, dummyDomain, colourString);
    sb.append("</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td>" + domainCatDesc[0] + " domain</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");

    // Get colour of very first PfamB domain
    colourCode = getNextColour(bbk.dng.Constants.PFAMB_STARTCOL, 0);
    cCode = ColorLib.getColor(colourCode);
    colourString = String.format("#%02X%02X%02X", cCode.getRed(),
            cCode.getGreen(), cCode.getBlue());

    // Show PfamB domain
    sb.append("<td align=center valign=center>");
    dummyDomain = "PB00000";
    if (useCATH) dummyDomain = "p1.10.20.300";
    sb = singleDomainTable(sb, dummyDomain, colourString);
    sb.append("</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td>" + domainCatDesc[1] + " domain</td>");
    sb.append("<td><img width=100 height=1 src=\"" + imgURL
            + "1x1.gif" + imgEnd);
    sb.append("</tr>");

    // Second row
    sb.append("<tr>");
    sb.append("<td colspan=3>&nbsp;</td>");
    sb.append(imgSrc + "key_covA.gif" + imgEnd);
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td colspan=7>3D structure of whole domain is known</td>");
    sb.append("</tr>");

    // Third row
    sb.append("<tr>");
    sb.append("<td colspan=3>&nbsp;</td>");
    sb.append(imgSrc + "key_covP.gif" + imgEnd);
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td colspan=7>Only part of domain has a known 3D structure</td>");
    sb.append("</tr>");

    // Table end
    sb.append("</table>");

    // Return string with key table added
    return sb;
  }

  // Show the stats for the current plot
  private StringBuilder showStats(StringBuilder sb, int nArch,
          int nFinal, int nCollapsed, int nPfam, int nSeqs) {

    // Start the table
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><b>Stats:</b></td>");
    sb.append("<td>&nbsp;&nbsp;</td>");

    // Show number of Pfam domain architectures
    sb.append("<td>No. of domain architectures:</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=right><b>" + nArch + "</b></td>");
    sb.append("</tr>");

    // If number of domains shown on screen is fewer than the total found,
    // show number
    if (nFinal < nArch) {
      sb.append("<tr>");
      sb.append("<td colspan=3>&nbsp;</td>");
      sb.append("<td><font color=red>&nbsp;&nbsp;- of which number shown in plot:</td>");
      sb.append("<td>&nbsp;&nbsp;</td>");
      sb.append("<td align=right><b>" + nFinal + "</b></font></td>");
      sb.append("</tr>");
    }

    // Show number of unique domains
    sb.append("<tr>");
    sb.append("<td colspan=3>&nbsp;</td>");
    sb.append("<td>No. of distinct domains:</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=right><b>" + nPfam + "</b></td>");
    sb.append("</tr>");

    // If Pfam-B domains have been collapsed into one (PB000000), then say so
    if (nCollapsed < nArch) {
      sb.append("<tr>");
      sb.append("<td colspan=3>&nbsp;</td>");
      sb.append("<td><font color=red>Note: All Pfam-B domains (excluding any "
              + "in the parent architecture) have been " +
              "replaced by a single dummy domain, PB000000</font></td>");
      sb.append("<td>&nbsp;&nbsp;</td>");
      sb.append("<td align=right>&nbsp;</td>");
      sb.append("</tr>");
    }

    // Show number of protein sequences
    sb.append("<tr>");
    sb.append("<td colspan=3>&nbsp;</td>");
    sb.append("<td>No. of protein seqs:</td>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=right><b>" + nSeqs + "</b></td>");
    sb.append("</tr>");

    // Table end
    sb.append("</table>");

    // Return string with key table added
    return sb;
  }

  // Show the sorted list of Pfam domains
  public StringBuilder showDomainList(StringBuilder sb, List<String> entries,
          Map<String, Integer> domainOccurrences,
          Map<String, Integer> domainArchitectureOccurrences,
          Map<String, Integer> domainColour) {

    // Initialise names of images
    String imgSrc = "<td align=center valign=center><img src=\"" + imgURL;
    String imgEnd = "\"></td>";

    // Show the heading
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    if (domainOccurrences != null && domainArchitectureOccurrences != null) {
      sb.append("<td><b>Sorted list of all " + domainTypeDesc[domainType] +
              " domains in graph:</b></td>");
    } else {
      sb.append("<td><b>List of " + domainTypeDesc[domainType] +
              " domains:</b></td>");
    }
    sb.append("</tr>");
    sb.append("</table>");

    // Show the headings row
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=center valign=bottom><u>Domain</u></td>");
    sb.append("<td>&nbsp;</td>");
    if (domainType == PFAM) {
      sb.append("<td align=left valign=bottom><u>Pfam id</u></td>");
    } else {
      sb.append("<td align=left valign=bottom><u>CATH/Pfam code</u></td>");
    }
    sb.append("<td>&nbsp;</td>");
    if (domainType == PFAM) {
      sb.append("<td align=left valign=bottom><u>Name</u></td>");
    } else {
      sb.append("<td align=left valign=bottom><u>Fold</u></td>");
    }
    sb.append("<td>&nbsp;</td>");
    sb.append("<td align=left valign=bottom><u>Description</u></td>");
    if (domainOccurrences != null && domainArchitectureOccurrences != null) {
      sb.append("<td>&nbsp;</td>");
      sb.append("<td align=right valign=bottom><u>Count</u></td>");
      sb.append("<td>&nbsp;</td>");
      sb.append("<td align=right valign=bottom><u>No.</u><br><u>arch.</u></td>");
    }
    sb.append("</tr>");

    // Get the domain details from the results package
    Map<String, Map<String, String>> domainDetails = getDomainDetails();
// DEBUG
//    int colourIndex = 0;

    // Loop over the sorted domains list to show details
    for (String d : entries) {

      // Start line for current domain
      sb.append("<tr>");
      sb.append("<td>&nbsp;&nbsp;</td>");

      // Get this domain's colour
      Color c = ColorLib.getColor(domainColour.get(d));
      String colourString
              = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(),
              c.getBlue());
// DEBUG
//      colourIndex++;
//      System.out.println("DOMCOLOUR" + colourIndex + " " + colourString);
// DEBUG

      // Show schematic diagram of this domain
      sb.append("<td align=center valign=top>");
      sb = singleDomainTable(sb, d, colourString);
      sb.append("</td>");
      sb.append("<td>&nbsp;</td>");

      // Domain id, hyperlinked to Pfam/CATH
      String url = bbk.dng.Constants.URL_PFAM;
      String domName = d;
      if (d.charAt(0) == 'p') {
        domName = d.substring(1);
      }
      if (d.charAt(0) != 'P')
        url = bbk.dng.Constants.URL_CATH;
      sb.append("<td valign=top><a href=\"" + url + domName + "\">" + d
              + "</a></td>");
      sb.append("<td>&nbsp;</td>");

      // Get this domain's name and description
      String name = "&nbsp;&nbsp;-";
      String description = "&nbsp;&nbsp;-";
      if (domainDetails.get(d) != null) {
        name = domainDetails.get(d).get("id");
        if (!d.substring(0, 2).equals("PB")) {
          name = domainDetails.get(d).get("short_name");
          description = domainDetails.get(d).get("description");
        }
      }

      // Short Pfam name
      sb.append("<td valign=top>" + name + "</td>");
      sb.append("<td>&nbsp;</td>");

      // Full Pfam description
      sb.append("<td valign=top>" + description + "</td>");

      // Show occurrence counts, if available
      if (domainOccurrences != null && domainArchitectureOccurrences != null) {
        sb.append("<td>&nbsp;</td>");

        // Count of domains on plot
        int count = 0;
        if (domainOccurrences.get(d) != null) {
          count = domainOccurrences.get(d);
        }
        sb.append("<td align=right valign=top>" + count + "</td>");
        sb.append("<td>&nbsp;</td>");

        // Count of architectures in which domain is present
        count = 0;
        if (domainArchitectureOccurrences.get(d) != null) {
          count = domainArchitectureOccurrences.get(d);
        }
        sb.append("<td align=right valign=top>" + count + "</td>");
        sb.append("<td>&nbsp;</td>");
      }

      // End current line
      sb.append("</tr>");
    }

    // Close the domains list table
    sb.append("</table>");

    // Return string with domain table added
    return sb;
  }

  // Form HTML table for a single PfamA or PfamB domain
  private StringBuilder singleDomainTable(StringBuilder sb, String domain,
          String domainColour) {

    // Initialise start- and end of HTML table cells for images
    String imgSrc = "<img src=\"" + imgURL;
    String imgEnd = "\">";

    // Initialise names of images
    String domainImage = imgSrc + "key_pfamA.gif" + imgEnd;
    if (domain.substring(0, 2).equals("PB")) {
      domainImage = imgSrc + "key_pfamB.gif" + imgEnd;
    }

    // If showing CATH domains, adjust accordingly
    if (useCATH && domain.charAt(0) != 'P') {
      // Check for split CATH domain
      if (domain.charAt(0) == 'p') {
        domainImage = imgSrc + "key_splitCATH.gif" + imgEnd;
      } else if (domain.substring(0, 2).equals("PF")) {
        domainImage = imgSrc + "key_pfamA_CATH.gif" + imgEnd;
      }
    }

    // Start the table
    sb.append("<table cellpadding=0 cellspacing=0>");

    // Add the domain image
    sb.append("<tr><td bgcolor =\"" + domainColour + "\">" + domainImage
            + "</td></tr>");

    // Close off table
    sb.append("</table>");

    // Return the input string with table appended
    return sb;
  }

  // Show a table of the enzyme node colou7rs
  private StringBuilder showEnzymeColoursTable(StringBuilder sb) {

    // Initialise start- and end of HTML table cells for images
    String imgSrc = "<img src=\"" + imgURL;
    String imgEnd = "\">";
    int cellSize = 10;

    // Initialise names of images
    String blankImage = "src=\"" + imgURL + "1x1.gif\">";

    // Show heading
    sb.append("<table border=0 cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><b>Enzyme node colours</b></td>");
    sb.append("</tr>");
    sb.append("<tr><td colspan=2>&nbsp;&nbsp;</td></tr>");
    sb.append("</table>");

    // Start the outer table
    sb.append("<table border=0 cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");

    // Start table for enzyme colours
//    sb.append("<table border=0 cellpadding=0 cellspacing=0>");

    // Loop over the 6 E.C. classes to show their colour ranges
    for (int i = 0; i < 6; i++) {

      // Put this enzyme class' definition in its own table
      sb.append("<td>");
      sb.append("<table border=0 cellpadding=0 cellspacing=0>");

      // Start row
      sb.append("<tr>");
      sb.append("<td bgcolor=\"white\"><img width=10 height=1 "
              + blankImage + "</td>");
      sb.append("<td colspan=3 bgcolor=\"black\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("<td bgcolor=\"white\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("<td colspan=3 bgcolor=\"black\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("<td colspan=5 bgcolor=\"white\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("</tr>");

      // Get the colour row to be used
      int iRow = bbk.dng.Constants.ENZYME_NODE + i;
      
      // Start current HTML row for E.C. code and description
      sb.append("<tr>");
      sb.append("<td bgcolor=\"white\"><img width=1 height=1 " + blankImage + "</td>");

      // Loop over the two colour for this enzyme class
      for (int j = 0; j < 2; j++) {

        // Start box
        sb.append("<td bgcolor=\"black\"><img width=1 height=1 " + blankImage + "</td>");

        // Loop to pick up the RGB components of the colour
        int rgb[] = new int[3];
        for (int k = 0; k < 3; k++) {
          rgb[k] = bbk.dng.Constants.NODE_COLOUR[iRow][j + 1][k];
        }

        // Form the HTML version of this colour
        String cellColour
                = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);

        // Draw this cell
        sb.append("<td bgcolor=" + cellColour + "><img width=" + cellSize
                + " height=" + cellSize + " " + blankImage + "</td>");
        sb.append("<td bgcolor=\"black\"><img width=1 height=1 " + blankImage + "</td>");

        // On first loop, add the "to "
        if (j == 0) {
          sb.append("<td>&nbsp;to&nbsp;</td>");
        }
      }
      // Write out E.C. class and description
      sb.append("<td>&nbsp;&nbsp;</td>");
      sb.append("<td>E.C." + (i + 1) + "</td>");
      sb.append("<td>&nbsp;&nbsp;</td>");
      sb.append("<td>" + enzymeClass[i] + "</td>");
      sb.append("<td>&nbsp;&nbsp;</td>");
      sb.append("</tr>");

      // Close off the boxes
      sb.append("<tr>");
      sb.append("<td bgcolor=\"white\"><img width=10 height=1 "
              + blankImage + "</td>");
      sb.append("<td colspan=3 bgcolor=\"black\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("<td bgcolor=\"white\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("<td colspan=3 bgcolor=\"black\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("<td colspan=5 bgcolor=\"white\"><img width=1 height=1 "
              + blankImage + "</td>");
      sb.append("</tr>");

      // Add blank line inbetween
      sb.append("<tr>");
      sb.append("<td colspan=10 bgcolor=\"white\"><img width=2 height=2 "
              + blankImage + "</td>");
      sb.append("</tr>");

      // Closed off this enzyme entry's table
      sb.append("</table>");
      sb.append("</td>");

      // If odd number, then throw new row
      if (((i + 1) % 3) == 0) {
        sb.append("</tr>");
        if (i != 5) {
          sb.append("<tr>");
          sb.append("<td>&nbsp;&nbsp;</td>");
        }
      }
    }

    // Close the outer table
    sb.append("</tr>");
    sb.append("</table>");

    // Show explanatory note at the bottom
    sb.append("<table border=0 cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><i>The darker colours for each E.C. class "
            + "signify a higher number of sequences in that class</i></td>");
    sb.append("</tr>");

    // Explain white nodes
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td><i>Any white E.C. class nodes "
            + "signify mulitple E.C. numbers from different E.C. classes</i></td>");
    sb.append("</tr>");

    // Close off table
    sb.append("</table>");

    // Return the input string with table appended
    return sb;
  }

  // Show the list of enzymes on the plot
  private StringBuilder showEnzymeList(StringBuilder sb,
          Map<Integer, Map<String, String>> enzyme) {

    // Show the heading
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    if (enzyme != null) {
      sb.append("<td><b>Sorted list of all enzymes in the graph:</b></td>");
    } else {
      sb.append("<td><b>No enzymes in graph</b></td>");
    }
    sb.append("</tr>");
    sb.append("</table>");

    // If no enzymes, then return
    if (enzyme == null) return sb;

    // Show the headings row
    sb.append("<table cellpadding=0 cellspacing=0>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;&nbsp;</td>");
    sb.append("<td align=left valign=bottom><u>E.C. number</u></td>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td align=left valign=bottom><u>Description</u></td>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td align=right valign=bottom><u>No.</u><br><u>arch.</u></td>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td align=right valign=bottom><u>Seqs</u></td>");
    sb.append("</tr>");

    // Loop over the enzymes list to show details
    for (int i = 0; i < enzyme.size(); i++) {

      // Start line for current E.C. number
      sb.append("<tr>");
      sb.append("<td>&nbsp;&nbsp;</td>");

      // Get this E.C. number and counts of occurrences
      String ecNumber = enzyme.get(i).get("id");
      String description = enzyme.get(i).get("description");
      int nArch = Integer.parseInt(enzyme.get(i).get("narch"));
      int nSeqs = Integer.parseInt(enzyme.get(i).get("nseqs"));

      // Determine which E.C. class this node belongs to
      String fontStart = "";
      String fontEnd = "";
      int colourCode = 0;
      boolean haveColour = false;

      // Get the first number
      int dotPos = ecNumber.indexOf(".");

      // If dot found, assume this to be an E.C. number
      if (dotPos > -1) {

        // Get the E.C. class
        int ecClass = Integer.parseInt(ecNumber.substring(0, 1));
        if (ecClass > 0 && ecClass < 7) {
          int iCol = ecClass + 1;
          int node = bbk.dng.Constants.ENZYME_NODE;
          int eSeqs = 1000;

          // Get colour for this enzyme class
          colourCode = GraphStylePanelActions.getInstance().getNodeColour(node, iCol, eSeqs);
          haveColour = true;
        }
      }

      // Otherwise, take to be SSG class and use domain-colouring to assign
      // colour
      else {
        // Get the class number
        String numberString;
        int spacePos = ecNumber.indexOf(" ");
        if (spacePos > -1) {
          numberString = ecNumber.substring(0, spacePos);
        } else {
          numberString = ecNumber;
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
          int j = 0;
          for (String ival : rgbVals) {
            rgb[j] = Integer.parseInt(ival);
            j++;
          }

          // Store the Prefuse colour library's integer encoding of this RGB
          // colour
          colourCode = ColorLib.rgb(rgb[0], rgb[1], rgb[2]);

          // Set flag that we have the colour
          haveColour = true;
        }
      }

      // If have a colour, form the font commands to display it
      if (haveColour) {
        Color cCode = ColorLib.getColor(colourCode);
        String colourString = String.format("#%02X%02X%02X", cCode.getRed(),
                cCode.getGreen(), cCode.getBlue());

        // Form the start and end for this E.C. number
        fontStart = "<font color=\"" + colourString + "\">";
        fontEnd = "</font>";
      }

      // E.C. number, hyperlinked to EC->PDB
      String url = bbk.dng.Constants.URL_EC_PDB;
      sb.append("<td valign=top><a href=\"" + url + ecNumber + "\">" + ecNumber
              + "</a></td>");
      sb.append("<td>&nbsp;</td>");

      // Enzyme name
      sb.append("<td valign=top>" + fontStart + description + fontEnd + "</td>");
      sb.append("<td>&nbsp;</td>");

      // Count of architectures containing this E.C. number
      sb.append("<td align=right valign=top>" + nArch + "</td>");
      sb.append("<td>&nbsp;</td>");

      // Count of sequences belonging to this E.C. number
      sb.append("<td align=right valign=top>" + nSeqs + "</td>");
      sb.append("<td>&nbsp;</td>");

      // End current line
      sb.append("</tr>");
    }

    // Close the enzymes list table
    sb.append("</table>");

    // Return string with enzymes table added
    return sb;
  }
  /* <-- RAL 7 Jul 09 */
}
