package bbk.dng.servlets;

import bbk.dng.data.index.LuceneSwissPfamSearcher;
import bbk.dng.utils.CollectionUtils;

import java.io.*;
import java.util.*;
import java.beans.XMLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;


public class Search extends HttpServlet {

    private LuceneSwissPfamSearcher searcher;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String indexDir = config.getInitParameter("indexDir");
        try {
            searcher = new LuceneSwissPfamSearcher(indexDir);
        } catch (Exception e) {
            System.out.printf("Error loading SwissPfamSearch component!\n");
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml");

        OutputStream out = response.getOutputStream();
        XMLEncoder encoder = new XMLEncoder(out);

        String method = request.getParameter("method");

        System.out.printf("Method: %s\n", method);

        if (method.equals("getSequenceByEntryName")) {
            String sequenceIdentifier = request.getParameter("identifier");
            Map<String,String> sequence = searcher.getSequenceByEntryName(sequenceIdentifier);
            encoder.writeObject(sequence);

        } else if (method.equals("getDomainDetails")) {
            List<String> domains = CollectionUtils.listOf(request.getParameter("domains").split(","));
            Map<String, Map<String, String>> domainDetails = searcher.getDomainDetails(domains);
            encoder.writeObject(domainDetails);

        } else if (method.equals("getOrganismsByDomains")) {
            List<String> domains = CollectionUtils.listOf(request.getParameter("domains").split(","));
            Set<String> organisms = null;
            try {
                organisms = searcher.getOrganismsByDomains(domains);
            } catch (Exception e) {
                e.printStackTrace();
            }
            encoder.writeObject(organisms);

        } else if (method.equals("getArchitecturesByDomains")) {
            List<String> domains = CollectionUtils.listOf(request.getParameter("domains").split(","));
            String domainOperator = request.getParameter("domainOperator");
            String organism = request.getParameter("organism");
            boolean pdbOnly = Boolean.parseBoolean(request.getParameter("organism"));

            Map<String, List<String>> architectures = null;
            try {
                architectures = searcher.getArchitecturesByDomains(domains, domainOperator, organism, pdbOnly);
            } catch (Exception e) {
                e.printStackTrace();
            }
            encoder.writeObject(architectures);

        } else if (method.equals("getDomainsBySequence")) {
            String identifier = request.getParameter("identifier");
            List<String> domains = null;
            try {
                domains = searcher.getDomainsBySequence(identifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            encoder.writeObject(domains);
            
        } else {
            // unknown method
            encoder.writeObject(null);
        }

        encoder.close();
        out.close();
    }
}
