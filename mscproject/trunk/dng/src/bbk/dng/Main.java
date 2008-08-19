package bbk.dng;

import bbk.dng.graph.GraphTestPanel;
import bbk.dng.graph.ArchitectureGraphBuilder;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.data.SimilarityCalculator;
import bbk.dng.ui.panels.InputPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import prefuse.data.Graph;
import prefuse.visual.VisualItem;
import prefuse.Constants;
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
    private InputPanel inputPanel;
    private String button2State = "running";

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    protected void startup() {

        System.out.printf("Starting dng...\n");

        // Try to set the JGoodies look
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception e) {}

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
            Container frame1ContentPane = frame1.getContentPane();
			frame1ContentPane.setLayout(new GridBagLayout());
			((GridBagLayout)frame1ContentPane.getLayout()).columnWidths = new int[] {0, 0};
			((GridBagLayout)frame1ContentPane.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)frame1ContentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
			((GridBagLayout)frame1ContentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            JSplitPane splitPane1 = new JSplitPane();
            splitPane1.setOneTouchExpandable(true);

            frame1ContentPane.add(splitPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

            graphPanel = new GraphTestPanel();
            inputPanel = new InputPanel();

            inputPanel.button1.setAction(getAction("button1ActionPerformed"));
            inputPanel.button2.setAction(getAction("button2ActionPerformed"));


            splitPane1.setLeftComponent(inputPanel);
            splitPane1.setRightComponent(graphPanel);

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
            button2State = "stopped";
        } else {
            graphPanel.getActionLayout().setDuration(Activity.INFINITY);
            graphPanel.getActionLayout().run();
            button2State = "running";
        }

    }

    @Action
    public void button1ActionPerformed() throws Exception {
        System.out.printf("You typed '%s'\n", inputPanel.textField1.getText());
        Map<String, ArrayList<String>> architectures = null;
        ArrayList<String> domains = null;

        SimilarityCalculator calculator = new SimilarityCalculator();

        try {
            // find domains for this sequence
            domains = searcher.getDomainsBySequence(inputPanel.textField1.getText());
            for (String d: domains) {
                System.out.printf("%s\n", d);
            }

            // find architectures with these domains
            architectures = searcher.getArchitecturesByDomains(domains);

            for (String a:architectures.keySet()) {
                System.out.printf("%s\n", a);
            }
            System.out.printf("%s architectures total.\n", architectures.size());

            if (architectures.size() > 100) {
                Map<String, Double> matrix = calculator.getSimilarityScoresForSingleArchitecture(joinDomainsForArchitecture(domains), architectures.keySet());
                ArrayList<String> mostSimilarArchitectures = calculator.getMostSimilarArchitectures(matrix, 100);

                ArrayList<String> toRemove = new ArrayList<String>();
                for (String a: architectures.keySet()) {
                    if (!mostSimilarArchitectures.contains(a)) {
                        toRemove.add(a);
                    }
                }

                for (String a: toRemove) {
                    architectures.remove(a);
                }

            }
            
        } catch (Exception ex) {
            System.out.printf("Error searching with SwissPfamSearcher.\n");
            throw (ex);
        }

        if (architectures != null) {


            Map<Pair<String,String>, Double> similarityMatrix = calculator.getArchitectureSimilarityMatrix(architectures.keySet());

            ArchitectureGraphBuilder graphBuilder = new ArchitectureGraphBuilder();
            Graph g = graphBuilder.initialiseGraph(architectures, joinDomainsForArchitecture(domains));
            graphBuilder.addEdgesByMatrix(g, similarityMatrix,  joinDomainsForArchitecture(domains));

            graphPanel.getVisualization().removeGroup("graph");
            graphPanel.getVisualization().addGraph("graph", g);
            graphPanel.getVisualization().setValue("graph.nodes", null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);
        }

        // graphPanel.getVisualization().repaint();
        graphPanel.getVisualization().run("layout");
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
}