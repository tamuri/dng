package bbk.dng.graph;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 * Date: 21-Aug-2008 10:17:07
 */
public class CustomizedForceDirectedLayout extends ForceDirectedLayout {

    /* RAL 3 Jul 09 --> */
    public static final int DEFAULT_FORCE_FACTOR = 20;
    public static final int SEQS_FORCE_FACTOR = 40;
    public static final int STRUCS_FORCE_FACTOR = 20;
    public static final int ENZYME_FORCE_FACTOR = 50;
    public static final int MAX_FORCE_FACTOR = 100;
    /* <-- RAL 3 Jul 09 */

    /* RAL 3 Jul 09 -->
    private int factor = 5; */
    private int factor = DEFAULT_FORCE_FACTOR;
    private int nParentDomains = 1;
    private double dFactor = 1;
    /* <-- RAL 3 Jul 09 */

    public CustomizedForceDirectedLayout(String group,
		ForceSimulator fsim, boolean enforceBounds) {
	    super(group, fsim, enforceBounds, false);
	}

  @Override
	protected float getSpringLength(EdgeItem e) {
	    /*NodeItem source = e.getSourceItem();
	    NodeItem target =  e.getTargetItem();

	    if (source.getInt("type") == target.getInt("type")) {
		return 140;
	    } else {
		return 200;
	    }*/
        float i;
        if (!e.getString("name").equals("sequence")) {
          // i = 100 - Math.round(Float.parseFloat(e.getString("name")));
          // i = 5 * Math.round(Float.parseFloat(e.getString("name")));
          //i = (i ^ 2) * factor;
          double dist = Double.parseDouble(e.getString("name"));
          i = (int) Math.round(Math.sqrt(dist) * factor * dFactor);
        } else {
//            i = (int) (nParentDomains * factor / 5);
            i = (int) (nParentDomains * factor / 2);
        }
        return i;
    }

	// two further possibilities to customize ....
  @Override
	protected float getMassValue(VisualItem n) {
	    return 1.0f;
	}

  @Override
	protected float getSpringCoefficient(EdgeItem e) {
	    return -1;
	}

  public void setFactor(int factor) {
    this.factor = factor;
  }

  public void setnParentDomains(int nParentDomains) {
    this.nParentDomains = nParentDomains;
    dFactor = Math.sqrt(nParentDomains);
  }
}
