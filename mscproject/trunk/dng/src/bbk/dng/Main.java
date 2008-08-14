package bbk.dng;

import bbk.dng.graph.GraphTestPanel;
import bbk.dng.data.index.SwissPfamSearcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import prefuse.data.Graph;
import prefuse.data.Node;

/**
 * Date: 13-Aug-2008 15:11:13
 */
public class Main {
    private JTextField textField1;
    private SwissPfamSearcher searcher;
    private GraphTestPanel graphPanel;

    Main() {
        try {
            searcher = new SwissPfamSearcher();
        } catch (Exception e) {
            System.out.printf("Exception instantiating SwissPfamSearcher:\n%s\n", e.getMessage());
        }
        graphPanel = new GraphTestPanel();
    }

    public void run() {
        System.out.printf("Starting dng...\n");

        // Main window
        JFrame frame = new JFrame("DNG");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // **** Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        ((GridBagLayout)inputPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
		((GridBagLayout)inputPanel.getLayout()).rowHeights = new int[] {0, 0};
		((GridBagLayout)inputPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
		((GridBagLayout)inputPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
        
        textField1 = new JTextField();
        inputPanel.add(textField1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

        JButton button1 = new JButton();
        button1.setText("Search");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.printf("You typed '%s'\n", textField1.getText());
                try {
                    ArrayList<String> domains = searcher.getDomainsBySequence(textField1.getText());
                    for (String d: domains) {
                        System.out.printf("%s\n", d);
                    }
                } catch (Exception ex) {
                    System.out.printf("Error searching with SwissPfamSearcher.\n");
                }

                graphPanel.getVisualization().removeGroup("graph");
                Graph g = new Graph();
                for ( int i=0; i<3; ++i ) {
                    Node n1 = g.addNode();
                    Node n2 = g.addNode();
                    Node n3 = g.addNode();
                    g.addEdge(n1, n2);
                    g.addEdge(n1, n3);
                    g.addEdge(n2, n3);
                }
//                g.addEdge(0, 3);
//                g.addEdge(3, 6);
//                g.addEdge(6, 0);
                graphPanel.getVisualization().addGraph("graph", g);
               // graphPanel.getVisualization().repaint();
                graphPanel.getVisualization().run("layout");

            }
        });
        inputPanel.add(button1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 1, 0), 0, 0));
        // **** End input panel

        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(graphPanel, BorderLayout.CENTER);

        // Pack and display window
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.run();
    }
}
