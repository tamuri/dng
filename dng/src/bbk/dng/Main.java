package bbk.dng;

import bbk.dng.actions.DataPaneActions;
import bbk.dng.actions.GraphActions;
import bbk.dng.actions.GraphStylePanelActions;
import bbk.dng.actions.SearchPanelActions;
import bbk.dng.data.index.RemoteSwissPfamSearcher;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.data.index.SwissPfamIndexer;
import bbk.dng.ui.panels.AppFrame;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import prefuse.data.Graph;

import javax.swing.*;
import java.awt.*;

/**
 * @author Asif Tamuri
 * @email asif@tamuri.com
 * @date 13-Aug-2008
 */
public class Main extends SingleFrameApplication {

    private SwissPfamSearcher searcher;
    private AppFrame appFrame;
    public static Graph graph;

    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    protected void initialize(String[] args) {
        // Load ArchSchema searcher
        try {
            if (args.length == 2 && Boolean.parseBoolean(args[0])) {
                searcher = new RemoteSwissPfamSearcher(args[1]);
            } else if (args.length == 1) {
                searcher = new SwissPfamSearcher(args[0]);
            } else {
                searcher = new SwissPfamSearcher(SwissPfamIndexer.INDEX_DIR);
            }
        } catch (Exception e) {
            System.out.printf("Exception instantiating searcher:\n%s\n", e.getMessage());
            e.printStackTrace();
        }
    }

    protected void startup() {
        // Set JGoodies Swing Look & Feel - http://www.jgoodies.com/freeware/looks/index.html
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception e) {
            System.out.printf("ERROR: Could not set Plastic L&F.\n");
        }



        // Create the ArchSchema window
        setAppFrame(new AppFrame(Constants.APPLICATION_NAME));

        // LOAD ACTIONS & LISTENERS FOR WINDOW COMPONENTS

        // Set the action for the sequence search button
        getAppFrame().getInputPanel().getSearchButton().setAction(getAction("sequenceSubmitAction"));

        // Set the action for the draw graph button
        getAppFrame().getGraphCriteriaPanel().getDrawGraphButton().setAction(getAction("drawGraphAction"));

        getAppFrame().getGraphCriteriaPanel().getResetButton().setAction(getAction("resetAction"));
        // Set actions and listeners on the graph style panel
        getAppFrame().getGraphStylePanel().getGraphRenderButton().setAction(getAction("toggleGraphRenderingAction"));
        getAppFrame().getGraphStylePanel().getEdgeLengthSlider().addChangeListener(GraphStylePanelActions.getInstance().getSpringLengthChangeListener(getAppFrame()));
        getAppFrame().getGraphStylePanel().getConnectivityFilterSlider().addChangeListener(GraphStylePanelActions.getInstance().getConnectionFilterChangeListener(getAppFrame()));
        getAppFrame().getGraphStylePanel().getShowSequenceCheckBox().addItemListener(GraphStylePanelActions.getInstance().getSequenceCheckboxListener(getAppFrame()));

        // Set listeners for the main graph panel (clicking on nodes and background)
        getAppFrame().getGraphPanel().getVisualization().getDisplay(0).addControlListener(GraphActions.getInstance().getGraphNodeClickControlAdapter(getAppFrame(), getSearcher()));
        getAppFrame().getGraphPanel().getVisualization().getDisplay(0).addControlListener(GraphActions.getInstance().getGraphBackgroundClickFocus(getAppFrame()));

        // Set the intial text for the data pane and add a listener that opens browser when clicking on links
        getAppFrame().getDataPane().setText("<html>" + Constants.HEAD_HTML + "<body><h1>ArchSchema</h1></body></html>");
        getAppFrame().getDataPane().addHyperlinkListener(DataPaneActions.getInstance().getDataPaneHyperlinkListener());

        // Pack and draw the ArchSchema window
        getAppFrame().setSize(1000, 700);
        getAppFrame().setVisible(true);
    }

    @Action
    public void sequenceSubmitAction() {
        SearchPanelActions.getInstance().sequenceSearchAction(getAppFrame(), getSearcher());
    }

    @Action
    public void drawGraphAction() {
        try {
            SearchPanelActions.getInstance().drawGraphAction(getAppFrame(), getSearcher());
        } catch (Exception e) {
            System.out.printf("Error executing Main.drawGraphAction().\n");
            e.printStackTrace();
        }

        // Move data pane scrollbar to the top and switch to graph panel
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getAppFrame().getDataScrollPane().getVerticalScrollBar().setValue(0);
                getAppFrame().selectTab("Graph");
            }
        });
    }

    @Action
    public void resetAction() {
        SearchPanelActions.getInstance().resetAction(getAppFrame());
    }

    @Action
    public void toggleGraphRenderingAction() {
        GraphStylePanelActions.getInstance().toggleGraphAction(getAppFrame());
    }

    private AppFrame getAppFrame() {
        return appFrame;
    }

    private void setAppFrame(AppFrame appFrame) {
        this.appFrame = appFrame;
    }

    private SwissPfamSearcher getSearcher() {
        return searcher;
    }

    private javax.swing.Action getAction(String actionName) {
        return getContext().getActionMap().get(actionName);
    }
}

