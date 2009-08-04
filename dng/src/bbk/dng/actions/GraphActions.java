package bbk.dng.actions;

import bbk.dng.Constants;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.ui.panels.AppFrame;
import bbk.dng.utils.CollectionUtils;
import edu.stanford.ejalbert.BrowserLauncher;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GraphActions {

  private static GraphActions instance;

  private GraphActions() {
  }

  public static GraphActions getInstance() {
    return instance == null ? instance = new GraphActions() : instance;
  }

  public GraphNodeClickControlAdapter getGraphNodeClickControlAdapter(AppFrame appFrame,
          SwissPfamSearcher searcher, boolean useCATH) {
    return new GraphNodeClickControlAdapter(appFrame, searcher, useCATH);
  }

  public GraphBackgroundClickFocus getGraphBackgroundClickFocus(AppFrame appFrame) {
    return new GraphBackgroundClickFocus(appFrame);
  }

  private class GraphNodeClickControlAdapter extends ControlAdapter {

    private AppFrame appFrame;
    private boolean useCATH;
    private SwissPfamSearcher searcher;

    GraphNodeClickControlAdapter(AppFrame appFrame, SwissPfamSearcher searcher,
            boolean useCATH) {
      this.appFrame = appFrame;
      this.searcher = searcher;
      this.useCATH = useCATH;
    }

    @Override
    public void itemClicked(VisualItem item, MouseEvent event) {

      if (event.getClickCount() == 2) {
/* RAL 27 Jul 09 -->
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

        for (Object t : appFrame.getGraphCriteriaPanel().getDomainList()
                .getSelectedValues()) {
          pfamDomainsSelected.add(((NameValue) t).getValue());
        }

        // get current species
        //String organism = ((NameValue) appFrame.getGraphCriteriaPanel().getOrganismComboBox().getSelectedItem()).getValue();
        // get current pdb entry
        //boolean pdbOnly = !appFrame.getGraphCriteriaPanel().getPdbOptionComboBox().getSelectedItem().toString().equals("ALL");

        // re-select the current domains
        appFrame.getGraphCriteriaPanel().getDomainList().clearSelection();
        for (int i = 0; i < appFrame.getGraphCriteriaPanel().getDomainList().getModel().getSize(); i++) {
          NameValue t = (NameValue) appFrame.getGraphCriteriaPanel().getDomainList().getModel().getElementAt(i);
          if (pfamDomainsSelected.contains(t.getValue())) {
            System.out.printf("setting %s (%s)", i, t.getValue());
            appFrame.getGraphCriteriaPanel().getDomainList().addSelectionInterval(i, i);
          }
        }

        // re-select the species (if present, else 'ALL')


        // re-select the pdb code


        EventQueue.invokeLater(new Runnable() {

          public void run() {
            appFrame.getGraphCriteriaPanel().getDrawGraphButton().doClick();
          }
        });

<-- RAL 27 Jul */

      } else if (event.getClickCount() == 1 && (!item.getString("name").equals(item.getString("sequences")))) {
        //System.out.printf("%s -> %s\n", item.getString("name"), item.getString("sequences"));
        // update data panel for architecture/sequence
        StringBuilder sb = new StringBuilder();

        // if item is architecture
        if (!item.getString("name").equals(item.getString("sequences"))) {

          // display architecture
          /* RAL 8 Jul 09 -->
          sb.append("<html>").append(bbk.dng.Constants.HEAD_HTML).
                  append("<body><p><b>Architecture:</b></p><p>"); */
          sb.append("<html>").append(bbk.dng.Constants.HEAD_HTML).
                  append("<body>");

          // Get the architecture
          String architecture = item.getString("name");

          // Get the domains making up the selected architecture
          /* RAL 22 Jul 09 -->
          String[] nodeDomains = item.getString("name").split("\\s"); */
          String[] nodeDomains = item.getString("name")
                  .split(SearchPanelActions.getInstance().getDomainSeparator());
          /* <-- RAL 22 Jul 09 */

          // Get a list of domains in this architecture
          List<String> entries = CollectionUtils.newList();
          for (String d : nodeDomains) {
            entries.add(d);
          }

          // Check whether this is the parent architecture
          String nodeType = "any";
          if (item.getString("name").equals(SearchPanelActions.getInstance()
                  .getParentArchitecture())) {
            nodeType = "parent";
          }

          // Get the domain colours
          Map<String, Integer> domainColour
                  = SearchPanelActions.getInstance().getDomainColour();

          // Get the 3D coverage of this architecture
          String coverage
                  = SearchPanelActions.getInstance()
                  .getCoverage(architecture);

          // Add spacer at top of frame
          sb = SearchPanelActions.getInstance().addSpacer(sb, 1, 6);

          // Start the heading table
          sb.append("<table cellpadding=0 cellspacing=0>");
          sb.append("<tr>");
          sb.append("<td>&nbsp;&nbsp;</td>");
          sb.append("<td><b>Selected architecture:</b></td>");
          sb.append("<td>&nbsp;&nbsp;</td>");
          sb.append("<td>");

          // Write out selected sequence's domain architecture
          sb = SearchPanelActions.getInstance().nodeDomainsTable(sb,
                  nodeDomains, coverage, domainColour, nodeType);

          // Write out URL to start new search from current architecture
          sb.append("</td>");
          sb.append("<td>&nbsp;&nbsp;</td>");
          sb = SearchPanelActions.getInstance().newSearchIcon(sb, architecture,
                  useCATH);
          
          // Close off headings line
          sb.append("</tr>");
          sb.append("</table>");

          // Add spacer
          sb = SearchPanelActions.getInstance().addSpacer(sb, 1, 10);

          // List the domains in this architecture
          sb = SearchPanelActions.getInstance().showDomainList(sb,
                  entries, null, null, domainColour);

          // Add spacer
          sb = SearchPanelActions.getInstance().addSpacer(sb, 1, 10);

          // Get the UniProt sequences having this architecture
          String[] seq = item.getString("sequences").split(",");

          // List the sequences
          sb = SearchPanelActions.getInstance().showSequencesList(sb,
                  architecture);
          /* <-- RAL 8 Jul 09 */

          /* RAL 8 Jul 09 -->
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

          sb.append("</p><p><b>Sequences (").append(seq.length).append("):</b></p><p>");

          for (String s : seq) {
            sb.append("<a href=\"http://www.uniprot.org/uniprot/");
            sb.append(s).append("\">").append(s).append("</a><br>");
          }
          <-- RAL 8 Jul 09 */

          // Close off the page
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

        // If clicked on a PDB or UniProt sequence node, call appopriate
        // web page
      } else if (event.getClickCount() == 1 &&
              (item.getString("name").equals(item.getString("sequences")))) {

        // Form appropriate URL to link to
        String URL = null;

        // PDB code node
        if (item.getString("type").equalsIgnoreCase("pdb_codes")) {

          // Get the PDB code
          String pdbCode = item.getString("name").substring(0,4);

          // Form URL to PDBsum entry
          URL = Constants.URL_PDBSUM + pdbCode;
        }

        // UniProt sequence
        else if (item.getString("type").equalsIgnoreCase("sequences")) {

          // Get the UniProt sequence
          String seqId = item.getString("name");

          // Get URL to UniProt
          URL = Constants.URL_UNIPROT + seqId;
        }

        // Enzyme
        else if (item.getString("type").equalsIgnoreCase("enzymes")) {

          // Get the E.C. number
          String label = item.getString("name") + " ";
          String ecNumber = "";

          // Process the E.C. number to form URL for calling EC->PDB
          boolean done = false;
          int ipos = 0;
          int level = 0;
          int len = label.length();
          String numberString = "";

          // Loop through the characters in the label
          while (!done && ipos < len) {

            // Get this character
            char ch = label.charAt(ipos);

            // If this is a number, add to current number string
            if (ch >= '0' && ch <= '9') {
              // Add to number string
              numberString = numberString + ch;
            }

            // Otherwise, retrieve previous number, if there is one
            else {
              // If have a number, add to E.C. string
              if (!numberString.equalsIgnoreCase("")) {
                // If not the first, then append dot
                if (level > 0) {
                  ecNumber = ecNumber + ".";
                }

                // Add current number to E.C. number
                ecNumber = ecNumber + numberString;

                // Re-initialise the number string
                numberString = "";

                // Increment level
                level++;
              }

              // If current character is not a dot, then we are done
              if (ch != '.') done = true;
            }

            // Increment position
            ipos++;
          }

          // Get URL
          URL = Constants.URL_EC_PDB + ecNumber;
        }

        // Call the appropriate URL
        if (URL != null) {
          try {
            BrowserLauncher bl = new BrowserLauncher();
            bl.openURLinBrowser(URL);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
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

      if ((event.getButton() != MouseEvent.BUTTON1) || (event.getClickCount() != ccount)) {
        return;
      }


      this.curFocus = null;
      TupleSet ts = vis.getFocusGroup(Visualization.FOCUS_ITEMS);
      ts.clear();
      if (activity != null) {
        vis.run(activity);
      }

      // Double left-click on back-ground redisplays master Data panel
      appFrame.getDataPane().setAutoscrolls(false);
      boolean enzymesOn
              = appFrame.getGraphStylePanel().getAddEnzymesRadioButton().isSelected();
      if (!enzymesOn) {
        appFrame.getDataPane().setText(SearchPanelActions.getInstance().getMasterText());
      } else {
        appFrame.getDataPane().setText(SearchPanelActions.getInstance().getEnzymesText());
      }

      EventQueue.invokeLater(new Runnable() {

        public void run() {
          appFrame.getDataScrollPane().getVerticalScrollBar().setValue(0);
        }
      });

    }
  }
}
