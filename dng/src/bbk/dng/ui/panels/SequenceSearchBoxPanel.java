package bbk.dng.ui.panels;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
/*
 * Created by JFormDesigner on Tue Aug 19 17:33:06 BST 2008
 */

/**
 * @author SHOCKIE
 */
public class SequenceSearchBoxPanel extends JPanel {
	public SequenceSearchBoxPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
        JLabel label1 = new JLabel();
		textField1 = new JTextField();
		button1 = new JButton();

		//======== this ========
		setBorder(Borders.TABBED_DIALOG_BORDER);
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

		//---- label1 ----
		label1.setText("Sequence:");
		add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 5), 0, 0));

        //textField1.setText("A0AZ79");
        add(textField1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 5), 0, 0));

		//---- button1 ----
		button1.setText("Search");
		add(button1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

    private JTextField textField1;
	private JButton button1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

    public JButton getSearchButton() {
        return button1;
    }

    public JTextField getSequenceTextField() {
        return textField1;
    }

}
