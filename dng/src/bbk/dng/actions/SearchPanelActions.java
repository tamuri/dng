package bbk.dng.actions;

import bbk.dng.utils.NameValue;
import bbk.dng.utils.SwingUtils;
import bbk.dng.utils.CollectionUtils;
import bbk.dng.ui.panels.AppFrame;
import bbk.dng.data.index.SwissPfamSearcher;
import bbk.dng.data.SimilarityCalculator;
import bbk.dng.graph.ArchitectureGraphBuilder;
import bbk.dng.Main;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import com.mallardsoft.tuple.Pair;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.Visualization;
import prefuse.Constants;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class SearchPanelActions {
    private static SearchPanelActions instance;
    private String parentArchitecture;
    private String parentSequence;
    private Map<String, Map<String, String>> domainDetails;
    private String masterText;

    private SearchPanelActions() { }

    public static SearchPanelActions getInstance() {
        return instance == null ? instance = new SearchPanelActions() : instance;
    }

    public void sequenceSearchAction(AppFrame appFrame, SwissPfamSearcher searcher) {
        // Get the sequence identifier to search for
        String seqId = appFrame.getInputPanel().getSequenceTextField().getText().toUpperCase();
        appFrame.getInputPanel().getSequenceTextField().setText(seqId);

        // If it's empty, do nothing
        if (seqId.length() == 0) return;

        // Get information about the sequence - architecture, accession, entry_name, organism, protein_name, status
        Map<String, String> sequence = searcher.getSequenceByEntryName(seqId);

        // If this sequence wasn't found, do nothing
        if (sequence == null) return;

        // Display sequence information in the panel
        appFrame.getGraphCriteriaPanel().getSequenceInfoLabel().setText("<html>" + sequence.get("entry_name") + "<br>" +
                sequence.get("protein_name") + "</html>");

        // Store the parent architecture and sequence for future reference
        parentArchitecture = sequence.get("architecture");
        parentSequence = sequence.get("entry_name");

        // Get the unique list of domains in the sequence architecture
        List<String> domains = new ArrayList<String>(CollectionUtils.setOf(sequence.get("architecture").split("\\s")));

        // Get domain descriptions for all the domains in the sequence architecture
        Map<String, Map<String, String>> domainDetails = searcher.getDomainDetails(domains);
        Collections.sort(domains);

        // Update the Pfam domain list in the panel
        DefaultListModel model = (DefaultListModel) appFrame.getGraphCriteriaPanel().getDomainList().getModel();
        model.clear();
        for (String d: domains) {
            model.add(model.size(),
                    new NameValue(domainDetails.get(d).get("accession") + " " + domainDetails.get(d).get("description"), d));
        }

        // Default to all domains selected
        appFrame.getGraphCriteriaPanel().getDomainList().setSelectionInterval(0, model.size() - 1);

        // Get all organisms that have these domains
        // TODO: Have a standard list of organisms - don't search!
        Set<String> organisms=null;
        try {
            organisms = searcher.getOrganismsByDomains(domains);
        } catch (Exception e) {
            System.out.printf("Error invoking searcher.getOrganismsByDomains()\n");
            e.printStackTrace();
        }

        // If organism search was unsuccessful, exit
        if (organisms==null) return;

        // Clear the current organism combobox and add the [sorted] organisms we have found
        if (appFrame.getGraphCriteriaPanel().getOrganismComboBox().getItemCount() > 0) {
            appFrame.getGraphCriteriaPanel().getOrganismComboBox().removeAllItems();
        }

        List<String> sortedOrganisms = new ArrayList<String>(organisms);
        Collections.sort(sortedOrganisms);

        // Add fixed items
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("ALL", "ALL"));
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("Homo sapiens (Human)", "Homo sapiens (Human)"));
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("Mus musculus (Mouse)", "Mus musculus (Mouse)"));
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("Drosphilia elegans (Fruit fly)", "Drosphilia elegans (Fruit fly)"));
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("Caenorhabditis elegans", "Caenorhabditis elegans"));
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("Saccharomyces cerevisiae (Baker's yeast)", "Saccharomyces cerevisiae (Baker's yeast)"));
        appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(new NameValue("------------------------------", "------------------------------"));

        for (String organism: sortedOrganisms) {
            NameValue nameValue;
            if (organism.length() > 30) {
                nameValue = new NameValue(organism.substring(0, 25) + "...", organism);
            } else {
                nameValue = new NameValue(organism, organism);
            }
            appFrame.getGraphCriteriaPanel().getOrganismComboBox().addItem(nameValue);
        }

        // Enable the 'Draw Graph' button
        appFrame.getGraphCriteriaPanel().getDrawGraphButton().setEnabled(true);
    }

    public void drawGraphAction(AppFrame appFrame, SwissPfamSearcher searcher) throws Exception {
        // Get the sequence identifier of the parent sequence
        String sequenceIdentifier = appFrame.getInputPanel().getSequenceTextField().getText();

        // Get the Pfam domain logic operator (and/or)
        String pfamDomainOperator = SwingUtils.getSelection(appFrame.getGraphCriteriaPanel().getDomainOperatorRadioButtonGroup()).getText().trim();

        // Get the selected Pfam domains
        List<String> pfamDomainsSelected = CollectionUtils.newList();
        int originalArchitectureCount;
        for (Object t: appFrame.getGraphCriteriaPanel().getDomainList().getSelectedValues()) {
            pfamDomainsSelected.add(((NameValue) t).getValue());
        }

        // Get selected organism
        String organism = ((NameValue) appFrame.getGraphCriteriaPanel().getOrganismComboBox().getSelectedItem()).getValue();

        // Get PDB selection option
        boolean pdbOnly = !appFrame.getGraphCriteriaPanel().getPdbOptionComboBox().getSelectedItem().toString().equals("ALL");

        Map<String, List<String>> architectures;
        List<String> domains;

        SimilarityCalculator calculator = new SimilarityCalculator();

        // find domains for this sequence
        domains = searcher.getDomainsBySequence(sequenceIdentifier);

        // find architectures with these domains
        architectures = searcher.getArchitecturesByDomains(pfamDomainsSelected, pfamDomainOperator, organism, pdbOnly);
        originalArchitectureCount = architectures.size();

        if (architectures.size() > bbk.dng.Constants.MAX_ARCHITECTURES) {
            Map<String, Double> matrix = calculator.getSimilarityScoresForSingleArchitecture(CollectionUtils.join(domains, ' '), architectures.keySet());
            List<String> mostSimilarArchitectures = calculator.getMostSimilarArchitectures(matrix, bbk.dng.Constants.MAX_ARCHITECTURES);

            List<String> toRemove = CollectionUtils.newList();
            for (String a : architectures.keySet()) {
                if (!mostSimilarArchitectures.contains(a)) {
                    toRemove.add(a);
                }
            }

            for (String a : toRemove) {
                architectures.remove(a);
            }

        }

        // add the original sequence + architecture to the set, if it doesn't exist
        if (!architectures.containsKey(parentArchitecture)) {
            // add the parent architecture
            List<String> tmp = CollectionUtils.newList();
            tmp.add(parentSequence);
            architectures.put(parentArchitecture, tmp);
        } else if (!architectures.get(parentArchitecture).contains(parentSequence)) {
            // parent architecture exists, doesn't have parent sequence
            architectures.get(parentArchitecture).add(parentSequence);
        }


        Map<Pair<String, String>, Double> similarityMatrix = calculator.getArchitectureSimilarityMatrix(architectures.keySet());

        ArchitectureGraphBuilder graphBuilder = new ArchitectureGraphBuilder();
        Main.graph = graphBuilder.initialiseGraph(architectures, CollectionUtils.join(domains, ' '));
        graphBuilder.addEdgesByMatrix(Main.graph, similarityMatrix, CollectionUtils.join(domains, ' '));

        //TODO: better way to get colours?

        // set domain colours
        Map<String,Integer> domainColour = CollectionUtils.newMap();

        // all distinct domains
        Set<String> allDistinctDomains = CollectionUtils.newSet();
        for (String arch: architectures.keySet()) {
            Collections.addAll(allDistinctDomains, arch.split("\\s"));
        }

        // get domain data
        domainDetails = searcher.getDomainDetails(new ArrayList<String>(allDistinctDomains));

        Set<String> distinctPfamADomains = CollectionUtils.newSet();
        Set<String> distinctPfamBDomains = CollectionUtils.newSet();

        for (String domain: allDistinctDomains) {
            if (domain.substring(0,2).equals("PF")) {
                distinctPfamADomains.add(domain);
            } else {
                distinctPfamBDomains.add(domain);
            }
        }

        int[] colors = ColorLib.getCategoryPalette(distinctPfamADomains.size(), 1.f, 0.2f, 1.f, 1.0f);
        int colourIndex = 0;

        // get the most distinct colours for the main domains of the chain - Pfam A
        for (String domain: distinctPfamADomains) {
            domainColour.put(domain, colors[colourIndex]);
            colourIndex++;
        }

        colors = ColorLib.getCategoryPalette(distinctPfamBDomains.size(), 1.f, 0.2f, 1.f, 1.0f);
        colourIndex = 0;

        // get the most distinct colours for the main domains of the chain - Pfam B
        for (String thisDomain: distinctPfamBDomains) {
            domainColour.put(thisDomain, colors[colourIndex]);
            colourIndex++;
        }

        appFrame.getGraphPanel().setDomainColours(domainColour);

        appFrame.getGraphPanel().getVisualization().getGroup(Visualization.FOCUS_ITEMS).clear();

        appFrame.getGraphPanel().getVisualization().removeGroup("graph");

        // if the graph rendering is currently off, turn it on
        if (GraphStylePanelActions.getInstance().getGraphRenderingStatus().equals("stopped")) {
            GraphStylePanelActions.getInstance().toggleGraphAction(appFrame);
        }

        VisualGraph vg = appFrame.getGraphPanel().getVisualization().addGraph("graph", Main.graph);

        VisualItem f = (VisualItem)vg.getNode(0);
        appFrame.getGraphPanel().getVisualization().getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);

        appFrame.getGraphPanel().getVisualization().setValue("graph.nodes", null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);
        appFrame.getGraphPanel().getVisualization().setValue("graph.edges", null, VisualItem.INTERACTIVE, Boolean.FALSE);

        appFrame.getGraphPanel().getVisualization().repaint();
        //graphPanel.getVisualization().runAfter("draw","layout");

        // show sequence nodes
        if (appFrame.getGraphStylePanel().getShowSequenceCheckBox().isSelected()) {
            GraphStylePanelActions.getInstance().addSequenceNodes(appFrame);
        }

        // fit the graph
        Rectangle2D bounds  = appFrame.getGraphPanel().getVisualization().getBounds("graph");
        GraphicsLib.expand(bounds, 300 + (int)(1/appFrame.getGraphPanel().getVisualization().getDisplay(0).getScale()));
        DisplayLib.fitViewToBounds(appFrame.getGraphPanel().getVisualization().getDisplay(0), bounds, 0);

        // GENERATE HTML
        StringBuilder sb = new StringBuilder();

        //TODO: rewrite this html generation. perhaps use http://www.stringtemplate.org/

        // add total architecture count
        sb.append("<p>").append(originalArchitectureCount).append(" architectures found");
        if (originalArchitectureCount > bbk.dng.Constants.MAX_ARCHITECTURES) {
            sb.append(" (showing ").append(bbk.dng.Constants.MAX_ARCHITECTURES).append(" most similar)");
        }
        sb.append(" with ").append(allDistinctDomains.size()).append(" domains.</p>");

        // build domain legend html
        List<String> sortedPfamADomains = new ArrayList<String>(distinctPfamADomains);
        Collections.sort(sortedPfamADomains);
        List<String> sortedPfamBDomains = new ArrayList<String>(distinctPfamBDomains);
        Collections.sort(sortedPfamBDomains);

        // pfam-a colours
        sb.append("<p><table><tr><td>Pfam-A:</td>");

        int count = 0;
        for (String d : sortedPfamADomains) {
            if (count++ == 5) {
                sb.append("</tr><tr><td>&nbsp;</td>");
                count = 1;
            }
            sb.append("<td bgcolor=\"");
            Color c = ColorLib.getColor(domainColour.get(d));
            sb.append(String.format( "#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue() ));
            sb.append("\">&nbsp;</td><td>")
                    .append(domainDetails.get(d).get("id"))
                    .append(" (<a href=\"http://pfam.sanger.ac.uk/family?acc=")
                    .append(d).append("\">")
                    .append(d).append("</a>)")
                    .append("</td>");
        }

        sb.append("</tr>");
        // pfam-b colours
        sb.append("<tr><td>Pfam-B:</td>");
        count=0;
        for (String d : sortedPfamBDomains) {
            if (count++ == 6) {
                sb.append("</tr><tr><td>&nbsp;</td>");
                count = 1;
            }
            sb.append("<td bgcolor=\"");
            Color c = ColorLib.getColor(domainColour.get(d));
            sb.append(String.format( "#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue() ));
            sb.append("\">&nbsp;</td><td>");
            sb.append("<a href=\"http://pfam.sanger.ac.uk/pfamb?entry=").append(d).append("\">");
            sb.append(d);
            sb.append("</a></td>");
        }

        sb.append("</tr></table></p>");

        int sequenceCount = 0;

        Map<String, Integer> domainOccurrences = CollectionUtils.newMap();
        Map<String, Integer> domainArchitectureOccurrences = CollectionUtils.newMap();
        for (String d: allDistinctDomains) {
            domainOccurrences.put(d, 0);
            domainArchitectureOccurrences.put(d, 0);
        }

        for (String a : architectures.keySet()) {
            sequenceCount += architectures.get(a).size();
            Set<String> tmp = CollectionUtils.newSet();
            for (String d: a.split("\\s")) {
                int i = domainOccurrences.get(d) + 1;
                domainOccurrences.put(d, i);

                if (!tmp.contains(d)) {
                    int j = domainArchitectureOccurrences.get(d) + 1;
                    domainArchitectureOccurrences.put(d, j);
                    tmp.add(d);
                }
            }
        }

        sb.append("<p>").append(sequenceCount).append(" sequences found.").append("</p>");
        sb.append("<p>Domain (architecture) count:</p><table>");

        List<String> entries = CollectionUtils.getKeysSortedByValue(domainOccurrences, true);

        for (String d : entries) {
            sb.append("<tr><td align=\"right\">")
                    .append(domainOccurrences.get(d))
                    .append(" (").append(domainArchitectureOccurrences.get(d))
                    .append(")</td><td>")
                    .append(domainDetails.get(d).get("id")).append(" (").append(d).append(")");
            if (d.substring(0, 2).equals("PF")) {
                sb.append(" <i>").append(domainDetails.get(d).get("description")).append("</i>");
            }
            sb.append("</td></tr>");
        }
        sb.append("</table>");

        masterText = "<html>" + bbk.dng.Constants.HEAD_HTML + "<body font=\"sans-serif\">" + sb + "</body></html>";
        appFrame.getDataPane().setText(masterText);
    }

    public void resetAction(AppFrame appFrame) {
        // clear graph
        appFrame.getGraphPanel().clearGraph();

        // clear data pane
        // appFrame.getDataPane().setAutoscrolls(false);
        appFrame.getDataPane().setText("");

        // reset graph style panel


        // set search panel
        appFrame.getInputPanel().getSequenceTextField().setText("");
        appFrame.getGraphCriteriaPanel().getSequenceInfoLabel().setText("");
        
    }

    public String getParentArchitecture() {
        return parentArchitecture;
    }

    public String getParentSequence() {
        return parentSequence;
    }

    public Map<String, Map<String, String>> getDomainDetails() {
        return domainDetails;
    }

    public String getMasterText() {
        return masterText;
    }
}
