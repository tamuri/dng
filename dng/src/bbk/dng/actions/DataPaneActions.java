package bbk.dng.actions;

import edu.stanford.ejalbert.BrowserLauncher;

import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;

/**
 * Date: 12-Oct-2008 10:38:30
 */
public class DataPaneActions {
    private static DataPaneActions instance;

    private DataPaneActions() {}

    public static DataPaneActions getInstance() {
        return instance == null ? instance = new DataPaneActions() : instance;
    }

    public DataPaneHyperlinkListener getDataPaneHyperlinkListener() {
        return new DataPaneHyperlinkListener();
    }

    public class DataPaneHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent event) {
            URL url = event.getURL();
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    BrowserLauncher bl = new BrowserLauncher();
                    bl.openURLinBrowser(url.toString());
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
