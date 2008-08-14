package bbk.dng.data.index;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.StringTokenizer;

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
