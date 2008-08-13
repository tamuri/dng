package bbk.dng.graph;

import prefuse.data.Node;
import prefuse.data.Graph;
import prefuse.Visualization;
import prefuse.Display;
import prefuse.Constants;
import prefuse.controls.*;
import prefuse.util.ColorLib;
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
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * Date: 13-Aug-2008 17:59:03
 */
public class GraphTestPanel extends JPanel {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
    public static final String AGGR = "aggregates";
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
        Visualization m_vis = new Visualization();

        // add visual data groups
        VisualGraph vg = m_vis.addGraph(GRAPH, g);
        m_vis.setInteractive(EDGES, null, false);
        m_vis.setValue(NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);

        AggregateTable at = m_vis.addAggregates(AGGR);
        at.addColumn(VisualItem.POLYGON, float[].class);
        at.addColumn("id", int.class);

        // add nodes to aggregates
        // create an aggregate for each 3-clique of nodes
        Iterator nodes = vg.nodes();
        for ( int i=0; i<3; ++i ) {
            AggregateItem aitem = (AggregateItem)at.addItem();
            aitem.setInt("id", i);
            for ( int j=0; j<3; ++j ) {
                aitem.addItem((VisualItem)nodes.next());
            }
        }



        // set up the renderers
        // draw the nodes as basic shapes
        Renderer nodeR = new ShapeRenderer(20);
        // draw aggregates as polygons with curved edges
        Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
        ((PolygonRenderer)polyR).setCurveSlack(0.15f);

        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeR);
        drf.add("ingroup('aggregates')", polyR);
        m_vis.setRendererFactory(drf);

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
        ColorAction aFill = new DataColorAction(AGGR, "id",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

        // bundle the color actions
        ActionList colors = new ActionList();
        colors.add(nStroke);
        colors.add(nFill);
        colors.add(nEdges);
        colors.add(aStroke);
        colors.add(aFill);

        // now create the main layout routine
        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(colors);
        layout.add(new ForceDirectedLayout(GRAPH, true));
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
        // set things running
        m_vis.run("layout");

        
        add(display);
    }

}
