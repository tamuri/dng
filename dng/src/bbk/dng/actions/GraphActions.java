package bbk.dng.actions;

import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.ui.panels.AppFrame;
import bbk.dng.utils.NameValue;
import bbk.dng.utils.CollectionUtils;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.List;

public class GraphActions {

    private static GraphActions instance;

    private GraphActions() {}

    public static GraphActions getInstance() {
        return instance == null ? instance = new GraphActions() : instance;
    }

    public GraphNodeClickControlAdapter getGraphNodeClickControlAdapter(AppFrame appFrame, SwissPfamSearcher searcher) {
        return new GraphNodeClickControlAdapter(appFrame, searcher);
    }

    public GraphBackgroundClickFocus getGraphBackgroundClickFocus(AppFrame appFrame) {
        return new GraphBackgroundClickFocus(appFrame);
    }

    private class GraphNodeClickControlAdapter extends ControlAdapter {
        private AppFrame appFrame;
        private SwissPfamSearcher searcher;

        GraphNodeClickControlAdapter(AppFrame appFrame, SwissPfamSearcher searcher) {
            this.appFrame = appFrame;
            this.searcher = searcher;
        }

        public void itemClicked(VisualItem item, MouseEvent event) {
            if (event.getClickCount() == 2) {

                String sequence;

                // if item is architecture, get random sequence
                if (!item.getString("name").equals(item.getString("sequences"))) {
                    sequence = item.getString("sequences").split(",")[0];
                } else {
                    sequence = item.getString("sequences");
                }

                // set the sequence field
                appFrame.getInputPanel().getSequenceTextField().setText(sequence);

                // get list of current chosen domains
                List<String> pfamDomainsSelected = CollectionUtils.newList();
                //int originalArchitectureCount;

                for (Object t: appFrame.getGraphCriteriaPanel().getDomainList().getSelectedValues()) {
                    pfamDomainsSelected.add(((NameValue) t).getValue());
                }

                // get current species
                //String organism = ((NameValue) appFrame.getGraphCriteriaPanel().getOrganismComboBox().getSelectedItem()).getValue();
                // get current pdb entry
                //boolean pdbOnly = !appFrame.getGraphCriteriaPanel().getPdbOptionComboBox().getSelectedItem().toString().equals("ALL");

                // search for this sequence
                SearchPanelActions.getInstance().sequenceSearchAction(appFrame, searcher);

                // re-select the current domains
                appFrame.getGraphCriteriaPanel().getDomainList().clearSelection();
                for (int i = 0; i < appFrame.getGraphCriteriaPanel().getDomainList().getModel().getSize(); i++) {
                    NameValue t = (NameValue) appFrame.getGraphCriteriaPanel().getDomainList().getModel().getElementAt(i);
                    if (pfamDomainsSelected.contains(t.getValue())) {
                        System.out.printf("setting %s (%s)", i, t.getValue());
                        appFrame.getGraphCriteriaPanel().getDomainList().addSelectionInterval(i,i);
                    }
                }

                // re-select the species (if present, else 'ALL')


                // re-select the pdb code


                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        appFrame.getGraphCriteriaPanel().getDrawGraphButton().doClick();
                    }
                });


            } else if (event.getClickCount() == 1 && (!item.getString("name").equals(item.getString("sequences")))) {
                System.out.printf("%s -> %s\n", item.getString("name"), item.getString("sequences"));
                // update data panel for architecture/sequence
                StringBuilder sb = new StringBuilder();

                // if item is architecture
                if (!item.getString("name").equals(item.getString("sequences"))) {
                    // display architecture
                    sb.append("<html>").append(bbk.dng.Constants.HEAD_HTML).append("<body><p><b>Architecture:</b></p><p>");

                    for (String s : item.getString("name").split("\\s")) {

                        sb.append(SearchPanelActions.getInstance().getDomainDetails().get(s).get("id"));

                        sb.append(" (");

                        if (s.substring(0, 2).equals("PF")) {
                            sb.append("<a href=\"http://pfam.sanger.ac.uk/family?acc=");
                            sb.append(s).append("\">").append(s).append("</a>");
                        } else {
                            sb.append("<a href=\"http://pfam.sanger.ac.uk/pfamb?entry=");
                            sb.append(s).append("\">").append(s).append("</a>");
                        }

                        sb.append(") ");

                        sb.append("<i>").append(SearchPanelActions.getInstance().getDomainDetails().get(s).get("description")).append("</i><br>");
                    }

                    String[] seq = item.getString("sequences").split(",");

                    sb.append("</p><p><b>Sequences (").append(seq.length).append("):</b></p><p>");

                    for (String s: seq) {
                        sb.append("<a href=\"http://www.uniprot.org/uniprot/");
                        sb.append(s).append("\">").append(s).append("</a><br>");
                    }

                    sb.append("</p></body></html>");


                }

                appFrame.getDataPane().setAutoscrolls(false);
                appFrame.getDataPane().setText(sb.toString());

                // Move data pane scrollbar to the top
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        appFrame.getDataScrollPane().getVerticalScrollBar().setValue(0);
                    }
                });
            }

        }
    }

    private class GraphBackgroundClickFocus extends FocusControl {
        private AppFrame appFrame;
        GraphBackgroundClickFocus(AppFrame appFrame) {
            super(2);
            this.appFrame = appFrame;
        }

        public void mouseClicked(MouseEvent event) {
            Visualization vis = ((Display) event.getSource()).getVisualization();

            if (event.getButton() == MouseEvent.BUTTON2) {
                // middle click on background = focus on selected sequence
                appFrame.getGraphPanel().panToParent(vis);
                return;
            }

            if ((event.getButton() != MouseEvent.BUTTON1)
                    || (event.getClickCount() != ccount)) {
                return;
            }


            this.curFocus = null;
            TupleSet ts = vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            ts.clear();
            if (activity != null) {
                vis.run(activity);
            }

            appFrame.getDataPane().setAutoscrolls(false);
            appFrame.getDataPane().setText(SearchPanelActions.getInstance().getMasterText());

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    appFrame.getDataScrollPane().getVerticalScrollBar().setValue(0);
                }
            });

        }

    }

}
