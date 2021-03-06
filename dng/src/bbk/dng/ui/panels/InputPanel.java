package bbk.dng.ui.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Date: 19-Aug-2008 14:38:38
 */
public class InputPanel extends JPanel {
    public JTextField textField1;
    public JButton button1;

    public InputPanel() {

        GridBagLayout g = new GridBagLayout();
        g.columnWidths = new int[] {0, 0, 0};
        g.rowHeights = new int[] {0, 0};
        g.columnWeights = new double[] {1.0, 0.0, 1.0E-4};
        g.rowWeights = new double[] {0.0, 1.0E-4};

        setLayout(g);

        textField1 = new JTextField();
        textField1.setText("A0EJ90"); //Q8GBW6
        add(textField1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        button1 = new JButton();

        add(button1, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 1, 0), 0, 0));
        

    }
}
