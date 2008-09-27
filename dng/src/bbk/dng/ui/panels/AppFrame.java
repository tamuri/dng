package bbk.dng.ui.panels;

import bbk.dng.graph.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    public AppFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        {
            Container frame1ContentPane = this.getContentPane();
            frame1ContentPane.setLayout(new GridBagLayout());
            ((GridBagLayout)frame1ContentPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)frame1ContentPane.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)frame1ContentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)frame1ContentPane.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            // Tabbed panel
            tabbedPane1 = new JTabbedPane();

            // Search tab panel
            inputPanel = new SequenceSearchBoxPanel();

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
            graphCriteriaPanel.getDrawGraphButton().setEnabled(false);

            panel2.add(graphCriteriaPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

            tabbedPane1.addTab("Search", panel2);

            // Graph options tab panel
            graphStylePanel = new GraphStylePanel();
            tabbedPane1.addTab("Graph", graphStylePanel);

            // Main network graph drawing area
            graphPanel = new GraphPanel();

            // Two parts - GraphPanel (top) and DataPanel
            JPanel rightPanel = new JPanel(new GridLayout(1, 1));

            JSplitPane splitPane2 = new JSplitPane();
            dataScrollPane = new JScrollPane();
            dataPane = new JEditorPane();

            splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);

            //---- button1 ----
            splitPane2.setTopComponent(graphPanel);

            //======== dataScrollPane ========
            {

                //---- dataPane ----
                dataPane.setEditable(false);
                dataPane.setContentType("text/html");

                dataScrollPane.setViewportView(dataPane);
            }
            splitPane2.setBottomComponent(dataScrollPane);
            splitPane2.setDividerLocation(500);
            splitPane2.setOneTouchExpandable(true);

            rightPanel.add(splitPane2);

            // Split pane for main frame
            JSplitPane splitPane1 = new JSplitPane();
            splitPane1.setOneTouchExpandable(true);

            // Add tabbed panel and graph display panel to split pane
            splitPane1.setLeftComponent(tabbedPane1);
            splitPane1.setRightComponent(rightPanel);

            splitPane1.setDividerLocation(300);

            // Add split pane to main frame
            frame1ContentPane.add(splitPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));

        }

        this.pack();

    }

    public SequenceSearchBoxPanel getInputPanel() {
        return inputPanel;
    }

    public GraphCriteriaPanel getGraphCriteriaPanel() {
        return graphCriteriaPanel;
    }

    public GraphStylePanel getGraphStylePanel() {
        return graphStylePanel;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public JEditorPane getDataPane() {
        return dataPane;
    }

    public JScrollPane getDataScrollPane() {
        return dataScrollPane;
    }

    public void selectTab(String name) {
        if (name.equals("Graph")) {
            tabbedPane1.setSelectedComponent(graphStylePanel);
        } else {
            tabbedPane1.setSelectedComponent(graphCriteriaPanel);
        }
    }

    private SequenceSearchBoxPanel inputPanel;
    private GraphCriteriaPanel graphCriteriaPanel;
    private GraphStylePanel graphStylePanel;
    private GraphPanel graphPanel;
    private JEditorPane dataPane;
    private JScrollPane dataScrollPane;
    private JTabbedPane tabbedPane1;
}
