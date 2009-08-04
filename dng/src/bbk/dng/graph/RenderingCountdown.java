package bbk.dng.graph;

/**
 * Date: 24-Feb-2010 14:59:03
 * Author: Asif Tamuri
 *
 * Run this thread after drawing a graph and it will decrement the
 * speed of the force simulator and after 10 iterations freeze the graph.
 *
 * 
 */
import bbk.dng.actions.GraphStylePanelActions;
import bbk.dng.ui.panels.AppFrame;
import prefuse.Display;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.display.PaintListener;

import java.awt.*;

public class RenderingCountdown implements Runnable {
    private static boolean running = false;
    private static boolean restart = false;
    private static int countdownSteps = 10;
    private CountdownPainter dsp;

    public RenderingCountdown(AppFrame appFrame) {
        this.appFrame = appFrame;
        this.f = (ForceDirectedLayout) appFrame.getGraphPanel().getActionLayout().get(1);
    }

    private ForceDirectedLayout f;
    private AppFrame appFrame;

    public void run() {
        if (!running) {
            running = true;
            dsp = new CountdownPainter();
            appFrame.getGraphPanel().getVisualization().getDisplay(0).addPaintListener(dsp);
            // make sure we're always at the normal speed limit first!
            f.getForceSimulator().setSpeedLimit(1f);
            try {
                for (int i = 0; i < countdownSteps; i++) {
                    dsp.count = countdownSteps - i;
                    Thread.sleep(1000);
                    f.getForceSimulator().setSpeedLimit(f.getForceSimulator().getSpeedLimit() - (1.0f / countdownSteps));

                    // if there was a request for a new countdown, restart this countdown!
                    if (restart) {
                        f.getForceSimulator().setSpeedLimit(1f);
                        i = 0;
                        restart = false;
                    }

                    // if user pressed 'freeze graph' button, then break out of this countdown
                    if (GraphStylePanelActions.getInstance().getGraphRenderingStatus().equals("stopped")) {
                        break;
                    }
                }
            } catch (InterruptedException e) {

            }
            stop();
        } else {
            // reset iteration
            restart = true;
        }
    }

    public void stop() {
        if (running) {
            f.getForceSimulator().setSpeedLimit(1f);
            if (!GraphStylePanelActions.getInstance().getGraphRenderingStatus().equals("stopped")) {
                GraphStylePanelActions.getInstance().toggleGraphAction(appFrame);
            }
            appFrame.getGraphPanel().getVisualization().getDisplay(0).removePaintListener(dsp);
            running = false;
        }
    }
}

class CountdownPainter implements PaintListener {
    int count;
    public void prePaint(Display d, Graphics2D g) {
        // nothing
    }

    public void postPaint(Display d, Graphics2D g) {
        g.setFont(d.getFont());
        g.setColor(Color.BLACK);
        g.drawString("Rendering graph..." + count + "s", 5, 15);
    }
}