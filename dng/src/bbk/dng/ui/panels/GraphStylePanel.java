package bbk.dng.ui.panels;

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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		graphRenderButton = new JButton();
        JLabel label1 = new JLabel();
		edgeLengthSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
        JLabel label2 = new JLabel();
		connectivityFilterSlider = new JSlider(JSlider.HORIZONTAL, 0, 30, 30);
        JPanel panel1 = new JPanel();
        JCheckBox checkBox1 = new JCheckBox();
        JSlider slider3 = new JSlider();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JLabel label3 = new JLabel();
        JComboBox comboBox1 = new JComboBox();
		showSequenceCheckBox = new JCheckBox();

		//======== this ========
		setBorder(Borders.TABBED_DIALOG_BORDER);
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {300, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

		//---- graphRenderButton ----
		graphRenderButton.setText("Stop Rendering Graph");
		add(graphRenderButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- label1 ----
		label1.setText("Edge length:");
		label1.setLabelFor(edgeLengthSlider);
        edgeLengthSlider.setValue(5); //TODO get this value from user preferences
        add(label1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));




        add(edgeLengthSlider, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- label2 ----
		label2.setText("Connectivity filter:");
		label2.setLabelFor(connectivityFilterSlider);
		add(label2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));
		add(connectivityFilterSlider, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));
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
		showSequenceCheckBox.setText("Show sequence nodes");
		add(showSequenceCheckBox, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));


        JLabel help = new JLabel();
        help.setText("<html><body><ul><li>Graph can be panned by click-hold on the background of the graph." +
                "<li>Nodes can be moved by click-hold." +
                "<li>Single-click on architecture updates data pane." +
                "<li>Double-click on graph background returns to main data pane numbers." +
                "<li>Middle-click on graph background focuses on parent sequence" + 
                "<li>Single right-click on graph background zooms and pans to fit graph." +
                "<li>Right-click and hold zooms graph in and out." +
                "<li>Double-click on architecture or sequence node perform search for that architecture/sequence as the 'parent' and the same domain options as previously selected." +
                "</ul></body></html>");
        add(help, new GridBagConstraints(0,9, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
        
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

    public JCheckBox getShowSequenceCheckBox() {
        return showSequenceCheckBox;
    }

    // Generated using JFormDesigner Open Source Project license - unknown
	private JButton graphRenderButton;
    private JSlider edgeLengthSlider;
    private JSlider connectivityFilterSlider;
    private JCheckBox showSequenceCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}

