package bbk.dng.graph;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 * Date: 21-Aug-2008 10:17:07
 */
public class CustomizedForceDirectedLayout extends ForceDirectedLayout {

    private int factor = 5;

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
        int i;
        if (!e.getString("name").equals("sequence")) {
            i = 100 - Math.round(Float.parseFloat(e.getString("name")));
            i = (i ^ 2) * factor;
        } else {
            i = 40;
        }
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

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }
}
