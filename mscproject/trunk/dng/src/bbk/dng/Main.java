package bbk.dng;

import bbk.dng.graph.GraphTestPanel;
import bbk.dng.graph.ArchitectureGraphBuilder;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.data.SimilarityCalculator;
import bbk.dng.ui.panels.SequenceSearchBoxPanel;
import bbk.dng.ui.panels.GraphCriteriaPanel;
import bbk.dng.utils.NameValue;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import prefuse.data.Graph;
import prefuse.visual.VisualItem;
import prefuse.Constants;
import prefuse.render.DefaultRendererFactory;
import prefuse.util.ColorLib;
import prefuse.activity.Activity;
import com.mallardsoft.tuple.Pair;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import org.jdesktop.application.*;
import org.jdesktop.application.Action;

/**
 * Date: 13-Aug-2008 15:11:13
 */
public class Main extends SingleFrameApplication {

    private SwissPfamSearcher searcher;
    private GraphTestPanel graphPanel;
    private SequenceSearchBoxPanel inputPanel;
    private String button2State = "running";
    private JButton graphStopRenderingButton;
    private GraphCriteriaPanel graphCriteriaPanel;

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    protected void startup() {

        System.out.printf("Starting dng...\n");

        // Try to set the JGoodies look
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception e) {
            System.out.printf("ERROR: Could not set Plastic L&F.\n");
        }

        // Application model components
        try {
            searcher = new SwissPfamSearcher();
        } catch (Exception e) {
            System.out.printf("Exception instantiating SwissPfamSearcher:\n%s\n", e.getMessage());
        }

        // Application GUI components
        JFrame frame1 = new JFrame("DNG");
        frame1.setSize(800,600);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        {
            // Main JFrame container
            Container frame1ContentPane = frame1.getContentPane();
			frame1ContentPane.setLayout(new GridBagLayout());
			((GridBagLayout)frame1ContentPane.getLayout()).columnWidths = new int[] {0, 0};
			((GridBagLayout)frame1ContentPane.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)frame1ContentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
			((GridBagLayout)frame1ContentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            // Tabbed panel
            JTabbedPane tabbedPane1 = new JTabbedPane();

            // Search tab panel
            inputPanel = new SequenceSearchBoxPanel();
            //inputPanel.button1.setAction(getAction("button1ActionPerformed"));
            inputPanel.button1.setAction(getAction("sequenceSubmitted"));

            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridBagLayout());
			((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0};
			((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0};
			((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
			((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            panel2.add(inputPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 0), 0, 0));

            graphCriteriaPanel = new GraphCriteriaPanel();
            graphCriteriaPanel.button1.setEnabled(false);
            graphCriteriaPanel.button1.setAction(getAction("button1ActionPerformed"));

            panel2.add(graphCriteriaPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));




            tabbedPane1.addTab("Search", panel2);

            // Graph options tab panel
            JPanel graphTab = new JPanel();
            graphStopRenderingButton = new JButton();
            graphTab.add(graphStopRenderingButton, BorderLayout.CENTER);
            tabbedPane1.addTab("Graph", graphTab);
            graphStopRenderingButton.setAction(getAction("button2ActionPerformed"));

            // Main network graph drawing area
            graphPanel = new GraphTestPanel();

            // Split pane for main frame
            JSplitPane splitPane1 = new JSplitPane();
            splitPane1.setOneTouchExpandable(true);

            // Add tabbed panel and graph display panel to split pane
            splitPane1.setLeftComponent(tabbedPane1);
            splitPane1.setRightComponent(graphPanel);

            splitPane1.setDividerLocation(300);
            

            // Add split pane to main frame
            frame1ContentPane.add(splitPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

        }

        // Pack and display window
        frame1.pack();
        frame1.setLocationRelativeTo(frame1.getOwner());
        frame1.setSize(1024, 700);
        frame1.setVisible(true);

        try {
            button1ActionPerformed();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Action
    public void button2ActionPerformed() {
        if (button2State.equals("running")){
            graphPanel.getActionLayout().setDuration(1000);
            graphStopRenderingButton.setText("Run");
            button2State = "stopped";
        } else {
            graphPanel.getActionLayout().setDuration(Activity.INFINITY);
            graphPanel.getActionLayout().run();
            button2State = "running";
            graphStopRenderingButton.setText("Stop");
        }

    }

    @Action
    public void button1ActionPerformed() throws Exception {
        String sequenceIdentifier = inputPanel.textField1.getText();
        String pfamDomainOperator = getSelection(graphCriteriaPanel.radioButtonGroup).getText().trim();
        ArrayList<String> pfamDomainsSelected = new ArrayList<String>();

        for (Object t: graphCriteriaPanel.list1.getSelectedValues()) {
            pfamDomainsSelected.add(((NameValue) t).getValue());
        }

        String organism = ((NameValue) graphCriteriaPanel.comboBox1.getSelectedItem()).getValue();
        String sequenceSelectionOperator = graphCriteriaPanel.comboBox2.getSelectedItem().toString();

        Map<String, ArrayList<String>> architectures;
        ArrayList<String> domains;

        SimilarityCalculator calculator = new SimilarityCalculator();

        try {
            // find domains for this sequence
            domains = searcher.getDomainsBySequence(sequenceIdentifier);
            for (String d : domains) {
                System.out.printf("%s\n", d);
            }

            // find architectures with these domains
            architectures = searcher.getArchitecturesByDomains(pfamDomainsSelected, pfamDomainOperator, organism);

            for (String a : architectures.keySet()) {
                System.out.printf("%s\n", a);
            }
            System.out.printf("%s architectures total.\n", architectures.size());

            if (architectures.size() > 100) {
                Map<String, Double> matrix = calculator.getSimilarityScoresForSingleArchitecture(joinDomainsForArchitecture(domains), architectures.keySet());
                ArrayList<String> mostSimilarArchitectures = calculator.getMostSimilarArchitectures(matrix, 100);

                ArrayList<String> toRemove = new ArrayList<String>();
                for (String a : architectures.keySet()) {
                    if (!mostSimilarArchitectures.contains(a)) {
                        toRemove.add(a);
                    }
                }

                for (String a : toRemove) {
                    architectures.remove(a);
                }

            }

        } catch (Exception ex) {
            System.out.printf("Error searching with SwissPfamSearcher.\n");
            throw (ex);
        }

        Map<Pair<String, String>, Double> similarityMatrix = calculator.getArchitectureSimilarityMatrix(architectures.keySet());

        ArchitectureGraphBuilder graphBuilder = new ArchitectureGraphBuilder();
        Graph g = graphBuilder.initialiseGraph(architectures, joinDomainsForArchitecture(domains));
        graphBuilder.addEdgesByMatrix(g, similarityMatrix, joinDomainsForArchitecture(domains));

        // set domain colours
        HashMap<String,Integer> domainColour = new HashMap<String,Integer>();
        // all distinct domains
        Set<String> d = new HashSet<String>();
        for (String a: architectures.keySet()) {
            for (String thisDomain: a.split("\\s"))
                d.add(thisDomain);
        }

        int[] colors = ColorLib.getCategoryPalette(d.size());
        int thisColour = 0;
        for (String thisDomain: d) {
            domainColour.put(thisDomain, colors[thisColour]);
            thisColour++;
        }

        /*System.out.print( "Paused..." );
        (new BufferedReader( new InputStreamReader( System.in ))).readLine();
*/

        graphPanel.setDomainColours(domainColour);

        graphPanel.getVisualization().removeGroup("graph");
        graphPanel.getVisualization().addGraph("graph", g);
        graphPanel.getVisualization().setValue("graph.nodes", null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);

        // graphPanel.getVisualization().repaint();
        graphPanel.getVisualization().run("layout");
    }

    @Action
    public void sequenceSubmitted() {
        // get the search string
        String sequenceIdentifier = inputPanel.textField1.getText();

        // if it's empty, do nothing
        if (sequenceIdentifier.length() == 0) {
            return;
        }

        // determine whether it's an entry or accession number
        Map<String, String> sequence = searcher.getSequenceByEntryName(sequenceIdentifier);

        // if nothing found, display error
        if (sequence == null) {
            return;
        }

        // otherwise, populate the graph criteria panel
        graphCriteriaPanel.sequenceTitle.setText("<html>" + sequence.get("entry_name") + "<br>" +
                sequence.get("protein_name") + "</html>");

        String[] domains = sequence.get("architecture").split("\\s");

        // get unique list of domains
        Set<String> tmp = new HashSet<String>(Arrays.asList(domains));
        domains = tmp.toArray(new String[tmp.size()]);

        // get domain description
        HashMap<String, HashMap<String, String>> domainDetails = searcher.getDomainDetails(domains);


        DefaultListModel model = (DefaultListModel) graphCriteriaPanel.list1.getModel();
        model.clear();

        Arrays.sort(domains);
        for (String d: domains) {
            model.add(model.size(),
                    new NameValue(domainDetails.get(d).get("accession") + " " + domainDetails.get(d).get("description")
                            ,d));
        }
        
        // set all domains to selected
        graphCriteriaPanel.list1.setSelectionInterval(0, model.size() - 1);

        Set<String> o2=null;
        // get all organisms with these domains
        try {
            o2 = searcher.getOrganismsByDomains(new ArrayList<String>(Arrays.asList(domains)));

        } catch (Exception e) {
            System.out.printf("Error invoking searcher.getOrganismsByDomains()\n");
            e.printStackTrace();
        }

        if (o2==null) return;

        if (graphCriteriaPanel.comboBox1.getItemCount() > 0) {
            graphCriteriaPanel.comboBox1.removeAllItems();
        }
        
        String[] o = new String[o2.size()];
        o2.toArray(o);
        Arrays.sort(o);

        graphCriteriaPanel.comboBox1.addItem(new NameValue("ALL","ALL"));
        for (String organism: o) {
            NameValue o3;
            if (organism.length() > 30) {
                o3 = new NameValue(organism.substring(0, 25) + "...", organism);
            } else {
                o3 = new NameValue(organism, organism);
            }
            graphCriteriaPanel.comboBox1.addItem(o3);
        }

        graphCriteriaPanel.button1.setEnabled(true);



    }

    private String joinDomainsForArchitecture(ArrayList<String> domains) {
        Iterator<String> iter = domains.iterator();
        StringBuffer architecture = new StringBuffer(iter.next());
        // we're using whitespace analyser to store architectures - separate by space
        while (iter.hasNext()) architecture.append(" ").append(iter.next());

        return architecture.toString();
    }

    private javax.swing.Action getAction(String actionName) {
        return getContext().getActionMap().get(actionName);
    }

    public static JRadioButton getSelection(ButtonGroup group) {
            for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
                JRadioButton b = (JRadioButton)e.nextElement();
                if (b.getModel() == group.getSelection()) {
                    return b;
                }
            }
            return null;
        }

}

