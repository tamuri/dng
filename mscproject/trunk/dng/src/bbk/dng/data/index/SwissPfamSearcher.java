package bbk.dng.data.index;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;

import java.util.*;

/**
 * Date: 13-Aug-2008 20:13:08
 */
public class SwissPfamSearcher {
    String indexPath;
    IndexSearcher architectureSearcher;
    QueryParser architectureParser;
    IndexSearcher domainSearcher;
    QueryParser domainParser;

    public SwissPfamSearcher() throws Exception{
        SwissPfamIndexer sp = new SwissPfamIndexer();

        architectureSearcher = new IndexSearcher(IndexReader.open(SwissPfamIndexer.INDEX_DIR + SwissPfamIndexer.SEQUENCES_DIR));
        architectureParser = new QueryParser("id", sp.getArchitectureIndexAnalyzer());

        domainSearcher = new IndexSearcher(IndexReader.open(SwissPfamIndexer.INDEX_DIR + SwissPfamIndexer.DOMAINS_DIR));
        domainParser = new QueryParser("accession", sp.getDomainIndexAnalyzer());
    }

    public Map<String, ArrayList<String>> getArchitecturesByDomains(ArrayList<String> domains) throws Exception {
        StringBuffer q = new StringBuffer();
        for (String d: domains) {
            q.append(" architecture:").append(d);
        }
        Hits hits = architectureSearcher.search(architectureParser.parse(q.toString()));

        if (hits.length() == 0) {
            System.out.printf("No architectures found with query:%s\n", q);
            return null;
        }

        Map<String, ArrayList<String>> architectures = new HashMap<String, ArrayList<String>>();
        for (int i = 0; i < hits.length(); i++) {
            if (architectures.containsKey(hits.doc(i).get("architecture"))) {
                architectures.get(hits.doc(i).get("architecture")).add(hits.doc(i).get("entry_name"));
            } else {
                ArrayList<String> sequence = new ArrayList<String>();
                sequence.add(hits.doc(i).get("entry_name"));
                architectures.put(hits.doc(i).get("architecture"), sequence);
            }
        }

        return architectures;
    }

    public ArrayList<String> getDomainsBySequence(String sequenceAccession) throws Exception {
	    Hits hits = architectureSearcher.search(architectureParser.parse("accession:" + sequenceAccession));

        if (hits.length() == 0) {
            System.out.printf("Sequence %s not found.\n", sequenceAccession);
            return null;
        } else if (hits.length() > 1) {
            System.out.printf("Sequence %s has %s entries!\n", sequenceAccession, hits.length());
            return null;
        }

        ArrayList<String> domains = new ArrayList<String>();
        String architecture = hits.doc(0).get("architecture");
        StringTokenizer st = new StringTokenizer(architecture, " ");
        while (st.hasMoreTokens()) {
            domains.add(st.nextToken());
        }

        return domains;
    }

    public Map<String, String> getSequenceByEntryName(String sequenceIdentifier) {

        String searchField;
        Hits hits;
        Map<String, String> sequence = null;

        if (sequenceIdentifier.indexOf("_") > -1) {
            searchField = "entry_name";
        } else {
            searchField = "accession";
        }


        try {
            hits = architectureSearcher.search(architectureParser.parse(searchField + ":" + sequenceIdentifier));
        } catch (Exception e) {
            System.out.printf("Error searching for \"entry_name:\"" + sequenceIdentifier);
            return null;
        }

        if (hits == null) {
            return null;
        } else if (hits.length() == 1) {
            sequence = new HashMap<String, String>();
            try {
                sequence.put("architecture", hits.doc(0).get("architecture"));
                sequence.put("accession", hits.doc(0).get("accession"));
                sequence.put("entry_name", hits.doc(0).get("entry_name"));
                sequence.put("organism", hits.doc(0).get("organism"));
                sequence.put("protein_name", hits.doc(0).get("protein_name"));
                sequence.put("status", hits.doc(0).get("status"));
            } catch (Exception e) {
                System.out.printf("Error populating sequence from Hit record.");
                e.printStackTrace();
                return null;
            }
        }

        return sequence;
    }

    public Set<String> getOrganismsByDomains(ArrayList<String> domains) throws Exception{
        Set<String> organisms = new HashSet<String>();

        StringBuffer q = new StringBuffer();
        for (String d: domains) {
            q.append(" architecture:").append(d);
        }
        Hits hits = architectureSearcher.search(architectureParser.parse(q.toString()));

        for (int i = 0; i < hits.length(); i++) {
            organisms.add(hits.doc(i).get("organism"));
        }

        return organisms;
    }
}
