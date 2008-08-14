package bbk.dng.data.index;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.*;

/**
 * Date: 13-Aug-2008 12:18:16
 */
public class SwissPfamIndexer extends AbstractSwissPfamParser {
    public static final String INDEX_DIR = "/home/aut/Documents/Mental/Bioinformatics/project/dng/data_index/";
    private IndexWriter writer;

    protected void actionAllDomains(Map<String, Map> allDomains) throws Exception {
        PerFieldAnalyzerWrapper analyzer = getDomainIndexAnalyzer();
        IndexWriter domainIndexWriter = new IndexWriter(INDEX_DIR + "domains", analyzer, true);

        for (String domain: allDomains.keySet()) {
            Document doc = new Document();
            doc.add(new Field("accession", domain, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field("id", (String) allDomains.get(domain).get("id"), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field("description", (String) allDomains.get(domain).get("description"), Field.Store.YES, Field.Index.TOKENIZED));
            domainIndexWriter.addDocument(doc);
            System.out.printf("Saved %s (%s, %s)\n", domain, allDomains.get(domain).get("id"), allDomains.get(domain).get("description"));
        }

        domainIndexWriter.optimize();
        domainIndexWriter.close();
    }

    protected void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains) throws Exception {
        // Create architecture string
        List<Integer> sortedKeys = new ArrayList<Integer>(domains.keySet());
        Collections.sort(sortedKeys);

        Iterator<Integer> iter = sortedKeys.iterator();
        StringBuffer architecture = new StringBuffer(domains.get(iter.next()));
        // we're using whitespace analyser to store architectures - separate by space
        while (iter.hasNext()) architecture.append(" ").append(domains.get(iter.next()));

        // Create Lucene document
        Document doc = new Document();
        doc.add(new Field("id", proteinId, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("accession", proteinAccession, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("architecture", architecture.toString(), Field.Store.YES, Field.Index.TOKENIZED));

        // Add document to index
        writer.addDocument(doc);

        System.out.printf("Saved %s (%s) with architecture %s\n", proteinId, proteinAccession, architecture);
    }

    private void createIndex() throws Exception {
        PerFieldAnalyzerWrapper analyzer = getArchitectureIndexAnalyzer();
        writer = new IndexWriter(INDEX_DIR + "architectures", analyzer, true);
    }

    public PerFieldAnalyzerWrapper getArchitectureIndexAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        analyzer.addAnalyzer("id", new KeywordAnalyzer());
        analyzer.addAnalyzer("accession", new KeywordAnalyzer());
        analyzer.addAnalyzer("architecture", new WhitespaceAnalyzer());
        return analyzer;
    }

    public PerFieldAnalyzerWrapper getDomainIndexAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        analyzer.addAnalyzer("accession", new KeywordAnalyzer());
        analyzer.addAnalyzer("id", new KeywordAnalyzer());
        analyzer.addAnalyzer("description", new StandardAnalyzer());
        return analyzer;
    }

    public static void main(String[] args) throws Exception {
        SwissPfamIndexer indexer = new SwissPfamIndexer();
        long start = System.currentTimeMillis();
        System.out.printf("Creating index at %s.\n", start);
        indexer.createIndex();
        indexer.parse(SWISSPFAM_FILEPATH);
        long end = System.currentTimeMillis();
        System.out.printf("Closing index at %s. Took %s\n", end, end - start);
        indexer.closeIndex();
    }

    private void closeIndex() throws Exception {
        writer.optimize();
        writer.close();
    }


}
