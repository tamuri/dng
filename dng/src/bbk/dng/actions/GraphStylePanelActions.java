package bbk.dng.actions;

import bbk.dng.ui.panels.AppFrame;
import bbk.dng.Main;
import bbk.dng.graph.CustomizedForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.data.tuple.TupleSet;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.util.PrefuseLib;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import java.util.Iterator;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class GraphStylePanelActions {
    private static GraphStylePanelActions instance;
    private String graphRenderingStatus = "running";

    private GraphStylePanelActions() { }

    public static GraphStylePanelActions getInstance() {
        return instance == null ? instance = new GraphStylePanelActions() : instance;
    }

    public void toggleGraphAction(AppFrame appFrame) {
        if (graphRenderingStatus.equals("running")){
            appFrame.getGraphPanel().getActionLayout().setDuration(1000);
            appFrame.getGraphStylePanel().getGraphRenderButton().setText("Start Rendering Graph");
            graphRenderingStatus = "stopped";
        } else {
            appFrame.getGraphPanel().getActionLayout().setDuration(Activity.INFINITY);
            appFrame.getGraphPanel().getActionLayout().run();
            graphRenderingStatus = "running";
            appFrame.getGraphStylePanel().getGraphRenderButton().setText("Stop Rendering Graph");
        }
    }

    public void saveGraphImageAction() {
         /*// save image
        BufferedImage bi = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
        graphPanel.getVisualization().getDisplay(0).printAll(ig2);

        try {
            ImageIO.write(bi, "PNG", new File("/home/aut/tmp.png"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/
    }

    public void addSequenceNodes(AppFrame appFrame) {
        TupleSet ts = appFrame.getGraphPanel().getVisualization().getGroup("graph.nodes");
        Iterator iter = ts.tuples();
        while (iter.hasNext()) {
            NodeItem connectorNodeItem = (NodeItem)iter.next();
            Node connectorNode = (Node) connectorNodeItem.getSourceTuple();

            if (!connectorNode.getString("name").equals(connectorNode.getString("sequences"))) {
                double connectorX = connectorNodeItem.getX();
                double connectorY = connectorNodeItem.getY();
                synchronized (appFrame.getGraphPanel().getVisualization()) {
                    for (String s : connectorNode.getString("sequences").split(",")) {
                        Node aNode = Main.graph.addNode();
                        aNode.setString("name", s);
                        aNode.setString("sequences", s);
                        aNode.setBoolean("parent", false);

                        Edge e = Main.graph.addEdge(connectorNode, aNode);
                        e.setString("name", "sequence");

                        VisualItem aNodeItem = appFrame.getGraphPanel().getVisualization().getVisualItem("graph", aNode);
                        PrefuseLib.setX(aNodeItem,null,connectorX);
                        PrefuseLib.setY(aNodeItem,null,connectorY);
                    }
                }
            }
        }

    }

    public void removeSequenceNodes(AppFrame appFrame) {
        TupleSet ts = appFrame.getGraphPanel().getVisualization().getGroup("graph.nodes");
        Iterator iter = ts.tuples();
        while (iter.hasNext()) {
            NodeItem connectorNodeItem = (NodeItem)iter.next();
            Node connectorNode = (Node) connectorNodeItem.getSourceTuple();

            if (connectorNode.getString("name").equals(connectorNode.getString("sequences"))) {
                synchronized (appFrame.getGraphPanel().getVisualization()) {
                    Iterator edges = connectorNode.edges();
                    while (edges.hasNext()) {
                        Edge edge = (Edge) edges.next();
                        Main.graph.removeEdge(edge);
                    }
                    Main.graph.removeNode(connectorNode);
                }
            }
        }
    }

    public SequenceCheckboxListener getSequenceCheckboxListener(AppFrame appFrame) {
        return new SequenceCheckboxListener(appFrame);
    }

    public SpringLengthChangeListener getSpringLengthChangeListener(AppFrame appFrame) {
        return new SpringLengthChangeListener(appFrame);
    }

    public ConnectionFilterChangeListener getConnectionFilterChangeListener(AppFrame appFrame) {
        return new ConnectionFilterChangeListener(appFrame);
    }

    private class SequenceCheckboxListener implements ItemListener {
        private AppFrame appFrame;
        SequenceCheckboxListener(AppFrame appFrame) {
            this.appFrame = appFrame;
        }
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                GraphStylePanelActions.getInstance().addSequenceNodes(this.appFrame);
            } else {
                GraphStylePanelActions.getInstance().removeSequenceNodes(this.appFrame);
            }
        }
    }

    private class SpringLengthChangeListener implements ChangeListener {
        private AppFrame appFrame;
        SpringLengthChangeListener(AppFrame appFrame) {
            this.appFrame = appFrame;
        }
        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();
            CustomizedForceDirectedLayout force = ((CustomizedForceDirectedLayout) appFrame.getGraphPanel().getActionLayout().get(1));
            force.setFactor(s.getValue());
        }
    }

    private class ConnectionFilterChangeListener implements ChangeListener {
        private AppFrame appFrame;
        ConnectionFilterChangeListener(AppFrame appFrame) {
            this.appFrame = appFrame;
        }
        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();
            appFrame.getGraphPanel().getFilter().setDistance(s.getValue());
            appFrame.getGraphPanel().getVisualization().runAfter("draw", "layout");
        }
    }

    public String getGraphRenderingStatus() {
        return graphRenderingStatus;
    }
}
