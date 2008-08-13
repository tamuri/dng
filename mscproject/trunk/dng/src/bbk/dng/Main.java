package bbk.dng;

import bbk.dng.graph.GraphTestPanel;

import javax.swing.*;
import java.awt.*;
/**
 * Date: 13-Aug-2008 15:11:13
 */
public class Main {
    public static void main(String[] args) {
        System.out.printf("Starting dng...\n");

        // Main window
        JFrame frame = new JFrame("DNG");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        ((GridBagLayout)inputPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
		((GridBagLayout)inputPanel.getLayout()).rowHeights = new int[] {0, 0};
		((GridBagLayout)inputPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
		((GridBagLayout)inputPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
        
        JTextField textField1 = new JTextField();
        inputPanel.add(textField1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

        JButton button1 = new JButton();
        button1.setText("Search");
        inputPanel.add(button1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
        
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(new GraphTestPanel(), BorderLayout.CENTER);

        // Pack and display window
        frame.pack();
        frame.setVisible(true);
    }
}
