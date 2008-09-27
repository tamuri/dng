package bbk.dng.ui.panels;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
/*
 * Created by JFormDesigner on Tue Aug 19 17:33:36 BST 2008
 */

public class GraphCriteriaPanel extends JPanel {
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JLabel sequenceTitle;
    private JList list1;
    private JComboBox comboBox1;
    private JButton button1;
    private JButton button2;
    private ButtonGroup radioButtonGroup;
    private JComboBox comboBox2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public GraphCriteriaPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		sequenceTitle = new JLabel();
        JPanel panel1 = new JPanel();
        JLabel label2 = new JLabel();
        JRadioButton radioButton1 = new JRadioButton("AND ", true);
        JRadioButton radioButton2 = new JRadioButton();
        JScrollPane scrollPane1 = new JScrollPane();
		list1 = new JList();
        JPanel panel2 = new JPanel();
        JLabel label3 = new JLabel();
		comboBox1 = new JComboBox();
        JLabel label4 = new JLabel();
        comboBox2 = new JComboBox();
		button1 = new JButton();
        button2 = new JButton();

		//======== this ========
		setBorder(Borders.TABBED_DIALOG_BORDER);
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {300, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 130, 0, 0, 0, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};

		//---- sequenceTitle ----
		sequenceTitle.setText("<html><br><br></html>");
		sequenceTitle.setFont(new Font("Dialog", Font.BOLD, 14));
		add(sequenceTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

			//---- label2 ----
			label2.setText("Select domains of interest:  ");
			panel1.add(label2);

            //---- radioButton1 ----
			radioButton1.setText("AND ");
			panel1.add(radioButton1);

            //---- radioButton2 ----
			radioButton2.setText("OR  ");
			panel1.add(radioButton2);

            radioButtonGroup = new ButtonGroup();
            radioButtonGroup.add(radioButton1);
            radioButtonGroup.add(radioButton2);
            radioButtonGroup.setSelected(radioButton2.getModel(), true);
        }
		add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== scrollPane1 ========
		{

			//---- list1 ----
			list1.setModel(new DefaultListModel());
            list1.setVisibleRowCount(7);
            scrollPane1.setViewportView(list1);
		}
		add(scrollPane1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== panel2 ========
		{
			panel2.setLayout(new GridBagLayout());
			((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0};
			((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0, 0};
			((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
			((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

			//---- label3 ----
			label3.setText("Organism:");
			panel2.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 5), 0, 0));
			panel2.add(comboBox1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 5, 0), 0, 0));

			//---- label4 ----
			label4.setText("Sequences:");
			panel2.add(label4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));

            comboBox2.addItem("ALL");
            comboBox2.addItem("In PDB only");
            panel2.add(comboBox2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		}
		add(panel2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- button1 ----
		button1.setText("Draw Graph");
		button1.setSelectedIcon(null);
		add(button1, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

        //---- button2 ----
		/*button2.setText("Reset");
		button2.setSelectedIcon(null);
		add(button2, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));*/

		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

    public JButton getDrawGraphButton() {
        return button1;
    }

    public ButtonGroup getDomainOperatorRadioButtonGroup() {
        return radioButtonGroup;
    }

    public JList getDomainList() {
        return list1;
    }

    public JComboBox getOrganismComboBox() {
        return comboBox1;
    }

    public JComboBox getPdbOptionComboBox() {
        return comboBox2;
    }

    public JLabel getSequenceInfoLabel() {
        return sequenceTitle;
    }

    public JButton getResetButton() {
        return button2;
    }
}