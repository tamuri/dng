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
        indexPath = SwissPfamIndexer.INDEX_DIR;

        architectureSearcher = new IndexSearcher(IndexReader.open(indexPath + "architectures"));
        architectureParser = new QueryParser("id", sp.getArchitectureIndexAnalyzer());

        domainSearcher = new IndexSearcher(IndexReader.open(indexPath + "domains"));
        domainParser = new QueryParser("accession", sp.getDomainIndexAnalyzer());
    }

    public ArrayList<String> getArchitecturesByDomains(ArrayList<String> domains) throws Exception {
        StringBuffer q = new StringBuffer();
        for (String d: domains) {
            q.append(" architecture:").append(d);
        }
        Hits hits = architectureSearcher.search(architectureParser.parse(q.toString()));

        if (hits.length() == 0) {
            System.out.printf("No architectures found with query:%s\n", q);
            return null;
        }

        Set<String> architectures = new HashSet<String>();
        // hits actually contains all sequences with matching architectures
        // get unique list of architectures
        for (int i = 0; i < hits.length(); i++) {
            architectures.add(hits.doc(i).get("architecture"));
        }

        return new ArrayList<String>(architectures);
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
