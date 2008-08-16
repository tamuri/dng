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
}
