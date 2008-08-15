package bbk.dng.graph;

import prefuse.data.Node;
import prefuse.data.Graph;
import prefuse.Visualization;
import prefuse.Display;
import prefuse.Constants;
import prefuse.controls.*;
import prefuse.util.ColorLib;
import prefuse.util.force.*;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.activity.Activity;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.AggregateTable;
import prefuse.visual.AggregateItem;
import prefuse.render.*;
import prefuse.render.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;

/**
 * Date: 13-Aug-2008 17:59:03
 */
public class GraphTestPanel extends JPanel {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
    public static final String AGGR = "aggregates";

    private Visualization m_vis;

    public GraphTestPanel() {
        super(new BorderLayout());

        // graph data
        Graph g = new Graph();
        for ( int i=0; i<3; ++i ) {
            Node n1 = g.addNode();
            Node n2 = g.addNode();
            Node n3 = g.addNode();
            g.addEdge(n1, n2);
            g.addEdge(n1, n3);
            g.addEdge(n2, n3);
        }
        g.addEdge(0, 3);
        g.addEdge(3, 6);
        g.addEdge(6, 0);

        // new empty visualisation
        m_vis = new Visualization();

        // add visual data groups
        VisualGraph vg = m_vis.addGraph(GRAPH, g);
        m_vis.setInteractive(EDGES, null, false);
        m_vis.setValue(NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);


//        LabelRenderer tr = new LabelRenderer("name");
//        tr.setRoundedCorner(8, 8);
//        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        Renderer nodeR = new ShapeRenderer(20);
        m_vis.setRendererFactory(new DefaultRendererFactory(nodeR));

        // set up the visual operators
        // first set up all the color actions
        ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));
        nStroke.add("_hover", ColorLib.gray(50));

        ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
        nFill.setDefaultColor(ColorLib.gray(255));
        nFill.add("_hover", ColorLib.gray(200));

        ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        nEdges.setDefaultColor(ColorLib.gray(100));
        
        ColorAction aStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR);
        aStroke.setDefaultColor(ColorLib.gray(200));
        aStroke.add("_hover", ColorLib.rgb(255,100,100));



        int[] palette = new int[] {
            ColorLib.rgba(255,200,200,150),
            ColorLib.rgba(200,255,200,150),
            ColorLib.rgba(200,200,255,150)
        };


        // bundle the color actions
        ActionList colors = new ActionList();
        colors.add(nStroke);
        colors.add(nFill);
        colors.add(nEdges);
        colors.add(aStroke);
        colors.add(new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.gray(0)));

        ForceSimulator fsim = new ForceSimulator(new RungeKuttaIntegrator());

        float gravConstant = -1f;
        float minDistance = -1f;
        float theta = 0.9f;

        float drag = 0.004f;
        float springCoeff = 1E-5f;
        float defaultLength = 0f;  //default: 50f

        fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
        fsim.addForce(new DragForce(drag));
        fsim.addForce(new SpringForce(springCoeff, defaultLength));

        ForceDirectedLayout fdl = new ForceDirectedLayout("graph", fsim, false);
        

        // now create the main layout routine
        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(colors);
        layout.add(fdl);
        //layout.add(new AggregateLayout(AGGR));
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);

        Display display = new Display(m_vis);
        display.setSize(500,500);
        display.pan(250, 250);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
        display.setHighQuality(true);
        display.addControlListener(new ZoomControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new DragControl());

        display.addControlListener(
                new ControlAdapter() {
                    public void itemClicked(VisualItem item, MouseEvent evt) {
                        System.out.printf("%s\n", item.getString("name"));
                    }
                }
        );

        // set things running
        m_vis.run("layout");

        
        add(display);
    }

    public Visualization getVisualization() {
        return m_vis;
    }

}
