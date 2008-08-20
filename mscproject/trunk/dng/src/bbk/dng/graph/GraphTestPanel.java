package bbk.dng.graph;

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
import prefuse.activity.Activity;
import prefuse.visual.*;
import prefuse.render.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * Date: 13-Aug-2008 17:59:03
 */
public class GraphTestPanel extends JPanel {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
    public static final String AGGR = "aggregates";

    public HashMap<String, Integer> domainColour;
    public ActionList getActionLayout() {
        return layout;
    }

    private ActionList layout;
    private Visualization m_vis;

    public GraphTestPanel() {
        super(new BorderLayout());

        // graph data
        Graph g = new Graph();

        // new empty visualisation
        m_vis = new Visualization();

        // add visual data groups
        m_vis.setInteractive(EDGES, null, false);
        m_vis.setValue(NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);


//        LabelRenderer tr = new LabelRenderer("name");
//        tr.setRoundedCorner(8, 8);
//        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        //DefaultRendererFactory rf = new DefaultRendererFactory(new ShapeRenderer(30));
        DefaultRendererFactory rf = new DefaultRendererFactory(new ArchitectureImageRenderer(new HashMap<String,Integer>()));

        //Predicate p0 = ExpressionParser.predicate("ISNODE() AND parent = true");
        //rf.add(p0, new CustomRenderer(40));
       

        m_vis.setRendererFactory(rf);


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


        



        // bundle the color actions
        ActionList colors = new ActionList();
        colors.add(nStroke);
        colors.add(nFill);
        colors.add(nEdges);
        colors.add(aStroke);
        colors.add(new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.gray(0)));

        ForceSimulator fsim = new ForceSimulator(new RungeKuttaIntegrator());

        /*float gravConstant = -1f;
        float minDistance = -1f;
        float theta = 0.9f;

        float drag = 0.004f;
        float springCoeff = 1E-5f;
        float defaultLength = 0f;  //default: 50f
        */
        float gravConstant = -1f;
        //float minDistance = 1000f;
        float minDistance = -1f;
        float theta = 0.9f;

        float drag = 0.004f;
        float springCoeff = 1E-4f;
        float defaultLength = 150f;  //default: 50f


        fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
        fsim.addForce(new DragForce(drag));
        fsim.addForce(new SpringForce(springCoeff, defaultLength));

        //ForceDirectedLayout fdl = new ForceDirectedLayout("graph", fsim, false);
        ForceDirectedLayout fdl = new CustomizedForceDirectedLayout("graph", fsim, false);

        // now create the main layout routine
        layout = new ActionList(Activity.INFINITY);
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
        //display.addControlListener(new RotationControl(Control.MIDDLE_MOUSE_BUTTON));
        display.addControlListener(new ToolTipControl("name"));
        display.addControlListener(
                new ControlAdapter() {
                    public void itemClicked(VisualItem item, MouseEvent evt) {
                        System.out.printf("%s -> %s\n", item.getString("name"), item.getString("sequences"));
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

    public void setDomainColours(HashMap<String, Integer> domainColour) {
        m_vis.setRendererFactory(new DefaultRendererFactory(new ArchitectureImageRenderer(domainColour)));
    }
}

 class CustomizedForceDirectedLayout extends ForceDirectedLayout {

	public CustomizedForceDirectedLayout(String group,
		ForceSimulator fsim, boolean enforceBounds) {
	    super(group, fsim, enforceBounds, false);
	}

	protected float getSpringLength(EdgeItem e) {
	    /*NodeItem source = e.getSourceItem();
	    NodeItem target =  e.getTargetItem();

	    if (source.getInt("type") == target.getInt("type")) {
		return 140;
	    } else {
		return 200;
	    }*/
        int i = 100 - Math.round(Float.parseFloat(e.getString("name")));
        i = (i ^ 2) * 10;
        //System.out.printf("%s = %s\n",e.getString("name"), i);
        return i;
    }

	// two further possibilities to customize ....
	protected float getMassValue(VisualItem n) {
	    return 1.0f;
	}

	protected float getSpringCoefficient(EdgeItem e) {
	    return -1;
	}
    }

class CustomRenderer extends ShapeRenderer {

    public CustomRenderer()
    {
        m_baseSize = 10;
        super.setBaseSize(m_baseSize);
    }

    public CustomRenderer(int size)
    {
        m_baseSize = 10;
        super.setBaseSize(size);
        m_baseSize = size;
    }

    protected Shape getRawShape(VisualItem item)
    {
        //String sequences = item.getSourceTuple().getString("sequences");
        double x = item.getX();
        if(Double.isNaN(x) || Double.isInfinite(x))
            x = 0.0D;
        double y = item.getY();
        if(Double.isNaN(y) || Double.isInfinite(y))
            y = 0.0D;
        double width = (double)m_baseSize * item.getSize();
        if(width > 1.0D)
        {
            x -= width / 2D;
            y -= width / 2D;
        }

        return rectangle(x, y, width, width);
    }

    private int m_baseSize;
}

class ArchitectureImageRenderer extends ShapeRenderer {
    final static BasicStroke strokeThin = new BasicStroke (1.0F);
    final static BasicStroke strokeThick = new BasicStroke (2.0F);
    final static double recWidth = 12;
    final static double pfamAHeight = 24;
    final static double pfamBHeight = 12;
    final static double gap = 6;
    HashMap<String, Integer> domainColours;

    public ArchitectureImageRenderer(HashMap<String,Integer> domainColours) {
        this.domainColours = domainColours;
    }

    protected Shape getRawShape(VisualItem item) {
        String[] arches = item.getSourceTuple().getString("name").split("\\s");
        int archCount = arches.length;
        double boxWidth = 8 + (recWidth * archCount) + (gap * (archCount - 1));
        double x = item.getX() - boxWidth / 2;
        double y = item.getY();

        return new Rectangle((int)x - 4, (int)(y - pfamAHeight / 2 - 4),
                (int)boxWidth, (int)pfamAHeight + 8);
    }

    public void render(Graphics2D g, VisualItem item) {

        
        String[] arches = item.getSourceTuple().getString("name").split("\\s");
        int archCount = arches.length;

        // draw box around architecture
        g.setPaint(Color.black);
        double boxWidth = 8 + (recWidth * archCount) + (gap * (archCount - 1));

        double x = item.getX() - boxWidth / 2;
        double y = item.getY();

        item.setBounds(x - 4, y - pfamAHeight / 2 - 4, boxWidth, pfamAHeight + 8);



        if (item.getSourceTuple().getBoolean("parent")) {
            g.setPaint(Color.gray);
            g.fill(new Rectangle2D.Double(x - 4, y - pfamAHeight / 2 - 4,
                boxWidth, pfamAHeight + 8));
            //g.setPaint(Color.red);
            g.setStroke(strokeThick);
        } else {
            g.setPaint(Color.white);
            g.fill(new Rectangle2D.Double(x - 4, y - pfamAHeight / 2 - 4,
                boxWidth, pfamAHeight + 8));

        }

        g.setPaint(Color.black);
        g.draw(new Rectangle2D.Double(x - 4, y - pfamAHeight / 2 - 4,
                boxWidth, pfamAHeight + 8));
        g.setStroke(strokeThin);

        //g.setPaint(Color.black);
        g.draw(new Line2D.Double(x - 4, y, x + 4
                + (recWidth * archCount) + (gap * (archCount - 1)), y));

        //int[] colors = ColorLib.getCategoryPalette(archCount);

        for (int i=0; i<archCount; i++) {
            Color c = new Color(this.domainColours.get(arches[i]));
            if (arches[i].substring(0, 2).equals("PF")) {
                g.setPaint(c);
                g.fill(new Rectangle2D.Double(x, y - pfamAHeight / 2, recWidth, pfamAHeight));
                g.setPaint(Color.black);
                g.draw(new Rectangle2D.Double(x, y - pfamAHeight / 2, recWidth, pfamAHeight));
            } else {
                g.setPaint(c);
                g.fill(new Rectangle2D.Double(x, y - pfamBHeight / 2, recWidth, pfamBHeight));
                g.setPaint(Color.black);
                g.draw(new Rectangle2D.Double(x, y - pfamBHeight / 2, recWidth, pfamBHeight));
            }

            x += recWidth + gap;
        }


    }
}