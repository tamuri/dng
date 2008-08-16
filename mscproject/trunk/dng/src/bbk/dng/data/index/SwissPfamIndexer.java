package bbk.dng.data.index;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.*;

/**
 * Date: 13-Aug-2008 12:18:16
 */
public class SwissPfamIndexer {
    public static final String INDEX_DIR = "/home/aut/Documents/Mental/Bioinformatics/project/dng/data_index/";
    private IndexWriter writer;
    public static final String SEQUENCES_DIR = "sequences";
    public static final String DOMAINS_DIR = "domains";

    public void saveAllDomains(Map<String, Map> allDomains) throws Exception {
        PerFieldAnalyzerWrapper analyzer = getDomainIndexAnalyzer();
        IndexWriter domainIndexWriter = new IndexWriter(INDEX_DIR + "domains", analyzer, true);

        for (String domain : allDomains.keySet()) {
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

    public void savePfamEntry(String accession, String architecture, String entryName,
                              String status, String proteinName, String organism) throws Exception {

        // Create Lucene document
        Document doc = new Document();
        doc.add(new Field("accession", accession, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("architecture", architecture, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("entry_name", entryName, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("status", status, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("protein_name", proteinName, Field.Store.YES, Field.Index.NO));
        doc.add(new Field("organism", organism, Field.Store.YES, Field.Index.UN_TOKENIZED));


        // Add document to index
        writer.addDocument(doc);

        System.out.printf("Saved %s (%s) with architecture %s\n", entryName, accession, architecture);
    }

    public void createIndex() throws Exception {
        PerFieldAnalyzerWrapper analyzer = getArchitectureIndexAnalyzer();
        writer = new IndexWriter(INDEX_DIR + "sequences", analyzer, true);
    }

    public PerFieldAnalyzerWrapper getArchitectureIndexAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        analyzer.addAnalyzer("accession", new KeywordAnalyzer());
        analyzer.addAnalyzer("architecture", new WhitespaceAnalyzer());
        analyzer.addAnalyzer("entry_name", new KeywordAnalyzer());
        analyzer.addAnalyzer("status", new KeywordAnalyzer());
        analyzer.addAnalyzer("status", new KeywordAnalyzer());
        analyzer.addAnalyzer("protein_name", new StandardAnalyzer());
        analyzer.addAnalyzer("organism", new KeywordAnalyzer());

        return analyzer;
    }

    public PerFieldAnalyzerWrapper getDomainIndexAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        analyzer.addAnalyzer("accession", new KeywordAnalyzer());
        analyzer.addAnalyzer("id", new KeywordAnalyzer());
        analyzer.addAnalyzer("description", new StandardAnalyzer());
        return analyzer;
    }

    public void closeIndex() throws Exception {
        writer.optimize();
        writer.close();
    }


}
