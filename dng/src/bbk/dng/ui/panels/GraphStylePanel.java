package bbk.dng.ui.panels;

import bbk.dng.Constants;
import bbk.dng.actions.SearchPanelActions;
import bbk.dng.graph.CustomizedForceDirectedLayout;
import bbk.dng.graph.GraphPanel;
import java.awt.*;
import javax.swing.*;

import com.jgoodies.forms.factories.*;
/*
 * Created by JFormDesigner on Thu Aug 21 09:03:12 BST 2008
 */

/**
 * @author SHOCKIE
 */
public class GraphStylePanel extends JPanel {

  public GraphStylePanel() {
    initComponents();
  }

  // Initialise all the action components
  private void initComponents() {
    // Form layout components
    graphRenderButton = new JButton();
    JLabel label1 = new JLabel();

    edgeLengthSlider = new JSlider(JSlider.HORIZONTAL, 1,
            CustomizedForceDirectedLayout.MAX_FORCE_FACTOR,
            CustomizedForceDirectedLayout.DEFAULT_FORCE_FACTOR);
    JLabel label2 = new JLabel();
    connectivityFilterSlider = new JSlider(JSlider.HORIZONTAL, 0,
            GraphPanel.GRAPH_NHOPS, GraphPanel.GRAPH_NHOPS);
    JPanel panel1 = new JPanel();
    JCheckBox checkBox1 = new JCheckBox();
    /* RAL 3 Jul 09 -->
    JSlider slider3 = new JSlider();
    <-- RAL 3 Jul 09 */
    JPanel panel2 = new JPanel();
    JPanel panel3 = new JPanel();
    JLabel label3 = new JLabel();
    JComboBox comboBox1 = new JComboBox();
    /* RAL 3 Jul 09 -->
    showSequenceCheckBox = new JCheckBox();
    <-- RAL 3 Jul 09 */

    // Node selection options
    /* RAL 2 Jul 09 --> */
    progNameLabel = new JLabel();
    plotOptionsLabel = new JLabel();
    nodeDisplayButtonGroup = new ButtonGroup();
    domainsOnlyRadioButton = new JRadioButton();
    addSequencesRadioButton = new JRadioButton();
    addStructuresRadioButton = new JRadioButton();
    addEnzymesRadioButton = new JRadioButton();
    navigationLabel = new JLabel();
    /* <-- RAL 2 Jul 09 */

    //======== this ========
    setBorder(Borders.TABBED_DIALOG_BORDER);
    setLayout(new GridBagLayout());
    ((GridBagLayout) getLayout()).columnWidths = new int[]{300, 0};
    ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    ((GridBagLayout) getLayout()).columnWeights = new double[]{1.0, 1.0E-4};
    ((GridBagLayout) getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

    /* RAL 3 Jul 09 --> */
    // Display program name
    progNameLabel.setName("progNameLabel");
    progNameLabel.setFont(new Font("Serif", Font.BOLD, 16));
    progNameLabel.setText(Constants.APPLICATION_NAME);

    add(progNameLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
            GridBagConstraints.PAGE_START, GridBagConstraints.NONE,
            new Insets(0, 0, 10, 0), 0, 0));

    // Display plot options label
    plotOptionsLabel.setName("plotOptionsLabel");
    plotOptionsLabel.setFont(new Font("Serif", Font.BOLD, 14));
    plotOptionsLabel.setText("Plot options");

    add(plotOptionsLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 0, 5, 0), 0, 0));
    /* <-- RAL 3 Jul 09 */

    /* RAL 2 Jul 09 --> */
    // Get the domain type we are dealing with
    int domainType = SearchPanelActions.getInstance().getDomainType();

    // Radio buttons for diaplay options - button group
    nodeDisplayButtonGroup.add(domainsOnlyRadioButton);
    nodeDisplayButtonGroup.add(addSequencesRadioButton);
    nodeDisplayButtonGroup.add(addStructuresRadioButton);
    nodeDisplayButtonGroup.add(addEnzymesRadioButton);

    // Domains only button
    domainsOnlyRadioButton.setName("domainsOnlyRadioButton");
    if (domainType == SearchPanelActions.PFAM) {
      domainsOnlyRadioButton.setText("Pfam domain architectures only");
    } else {
      domainsOnlyRadioButton.setText("CATH domain architectures only");
    }
    domainsOnlyRadioButton.setSelected(true);

    // Add sequences button
    addSequencesRadioButton.setName("addSequencesRadioButton");
    addSequencesRadioButton.setText("Add UniProt sequences");

    // Add PDB structures group
    addStructuresRadioButton.setName("addStructuresRadioButton");
    addStructuresRadioButton.setText("Add PDB structures");

    // Add Enzymes group
    addEnzymesRadioButton.setName("addEnzymesRadioButton");
    addEnzymesRadioButton.setText("Add enzyme classes");

    // Add label and radio buttons to the panel
    add(domainsOnlyRadioButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 5, 0, 0), 0, 0));
    add(addSequencesRadioButton, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 5, 0, 0), 0, 0));
    add(addStructuresRadioButton, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 5, 0, 0), 0, 0));
    add(addEnzymesRadioButton, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 5, 20, 0), 0, 0));
    /* <-- RAL 2 Jul 09 */

    // Edge-length slider
    label1.setText("Adjust lengths of edges between nodes:");
    label1.setLabelFor(edgeLengthSlider);
    edgeLengthSlider.setValue(CustomizedForceDirectedLayout.DEFAULT_FORCE_FACTOR);

    // Define size of edge-length slider
    Dimension d = edgeLengthSlider.getPreferredSize();
    d.width=280;
    edgeLengthSlider.setPreferredSize(d);
    edgeLengthSlider.setMinimumSize(new Dimension(280,d.height));

    add(label1, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 5, 0), 0, 0));
    add(edgeLengthSlider, new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 20, 0), 0, 0));

    // Connectivity filter
    label2.setText("Prune outer nodes in graph:");
    label2.setLabelFor(connectivityFilterSlider);
    connectivityFilterSlider.setValue(GraphPanel.GRAPH_NHOPS);

    // Define size of connectivity slider
    d = connectivityFilterSlider.getPreferredSize();
    d.width=280;
    connectivityFilterSlider.setPreferredSize(d);
    connectivityFilterSlider.setMinimumSize(new Dimension(280,d.height));

    add(label2, new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 0, 5, 0), 0, 0));
    add(connectivityFilterSlider, new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.NONE,
            new Insets(0, 0, 20, 0), 0, 0));

    // Plot/freeze graph button
    graphRenderButton.setText("Freeze Graph");
    add(graphRenderButton, new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 20, 0), 0, 0));

    // Navigation label
    //navigationLabel.setName("navigationLabel");
    //navigationLabel.setFont(new Font("Serif", Font.BOLD, 14));
    //navigationLabel.setText("Navigation");
    //add(navigationLabel, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0,
    //        GridBagConstraints.LINE_START, GridBagConstraints.NONE,
    //        new Insets(0, 0, 5, 0), 0, 0));

    // Add navigation instructions panel
    JLabel help = new JLabel();
    help.setText("<html><body>" +
            "<B>Navigation</B><p>" +
            "&middot; <B>Pan</B> around the plot by left-click-dragging on graph background.<BR>" +
            "&middot; <B>Zoom</B> in and out by right-click-dragging on graph background.<BR>" +
            "&middot; <B>Move nodes</B> by left-click-dragging them.<BR>" +
            "&middot; <B>Recentre graph</B> by single right-click on graph background.<BR>" +
            "&middot; <B>Recentre on parent sequence</B> by single middle-click on graph background.<BR>" +
            "&middot; <B>Show domain info</B> by single left-click on architecture node.<BR>" +
            "&middot; <B>Reset data panel</B> by double left-click on graph background.<BR>" +
            "</body></html>");
    add(help, new GridBagConstraints(0, 12, 2, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
            new Insets(0, 5, 20, 0), 0, 0));

    /*

    //======== panel1 ========
    {
    panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));

    //---- checkBox1 ----
    checkBox1.setText("Highlight edges by score");
    panel1.add(checkBox1);
    }
    add(panel1, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
    new Insets(0, 0, 5, 0), 0, 0));
    add(slider3, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
    new Insets(0, 0, 5, 0), 0, 0));
     */

    /*//======== panel2 ========
    {
    panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));

    //======== panel3 ========
    {
    panel3.setLayout(new GridBagLayout());
    ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0};
    ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
    ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
    ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

    //---- label3 ----
    label3.setText("Node style:");
    panel3.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
    new Insets(0, 0, 0, 0), 0, 0));
    panel3.add(comboBox1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
    new Insets(0, 0, 0, 0), 0, 0));
    }
    panel2.add(panel3);
    }
    add(panel2, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
    new Insets(0, 0, 5, 0), 0, 0));
     */

    //---- showSequenceCheckBox ----
    /* RAL 3 Jul 09 -->
    showSequenceCheckBox.setText("Show sequence nodes");
    add(showSequenceCheckBox, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
    <-- RAL 3 Jul 09 */


  // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  public JButton getGraphRenderButton() {
    return graphRenderButton;
  }

  public JSlider getEdgeLengthSlider() {
    return edgeLengthSlider;
  }

  public JSlider getConnectivityFilterSlider() {
    return connectivityFilterSlider;
  }

  /* RAL 3 Jul 09 -->
  public JCheckBox getShowSequenceCheckBox() {
    return showSequenceCheckBox;
  }
  <-- RAL 3 Jul 09 */

  public void setDomainsOnlyRadioButton(int domainType) {
    if (domainType == SearchPanelActions.PFAM) {
      domainsOnlyRadioButton.setText("Pfam domain architectures only");
    } else {
      domainsOnlyRadioButton.setText("CATH domain architectures only");
    }
  }

  /* RAL 2 Jul 09 --> */
  public JRadioButton getDomainsOnlyRadioButton() {
    return domainsOnlyRadioButton;
  }

  public JRadioButton getAddSequencesRadioButton() {
    return addSequencesRadioButton;
  }

  public JRadioButton getAddStructuresRadioButton() {
    return addStructuresRadioButton;
  }

  public JRadioButton getAddEnzymesRadioButton() {
    return addEnzymesRadioButton;
  }
  /* <-- RAL 2 Jul 09 */

  // Generated using JFormDesigner Open Source Project license - unknown
  private JButton graphRenderButton;
  private JSlider edgeLengthSlider;
  private JSlider connectivityFilterSlider;
  /* RAL 3 Jul 09 -->
  private JCheckBox showSequenceCheckBox;
  <-- RAL 3 Jul 09 */

  /* RAL 2 Jul 09 --> */
  private JLabel progNameLabel;
  private JLabel plotOptionsLabel;
  private ButtonGroup nodeDisplayButtonGroup;
  private JRadioButton domainsOnlyRadioButton;
  private JRadioButton addSequencesRadioButton;
  private JRadioButton addStructuresRadioButton;
  private JRadioButton addEnzymesRadioButton;
  private JLabel navigationLabel;
  /* <-- RAL 2 Jul 09 */
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}

