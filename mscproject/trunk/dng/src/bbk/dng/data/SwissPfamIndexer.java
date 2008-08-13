package bbk.dng.data;

import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.util.*;

/**
 * Date: 13-Aug-2008 12:18:16
 */
public class SwissPfamIndexer extends AbstractSwissPfamParser {
    private static final String INDEX_DIR = "/home/aut/Documents/Mental/Bioinformatics/project/dng/data_index/";
    private IndexWriter writer;

    protected void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains) throws Exception {
        // Create architecture string
        List<Integer> sortedKeys = new ArrayList<Integer>(domains.keySet());
        Collections.sort(sortedKeys);

        Iterator<Integer> iter = sortedKeys.iterator();
        StringBuffer architecture = new StringBuffer(domains.get(iter.next()));
        while (iter.hasNext()) architecture.append(".").append(domains.get(iter.next()));

        // Create Lucene document
        Document doc = new Document();
        doc.add(new Field("id", proteinId, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("accession", proteinAccession, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("architecture", architecture.toString(), Field.Store.YES, Field.Index.TOKENIZED));

        // Add document to index
        writer.addDocument(doc);

        System.out.printf("Successfully saved %s (%s) with architecture %s\n", proteinId, proteinAccession, architecture);
    }

    private void createIndex() throws Exception {
        // Lucene index
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
        analyzer.addAnalyzer("id", new KeywordAnalyzer());
        analyzer.addAnalyzer("accession", new KeywordAnalyzer());
        analyzer.addAnalyzer("architecture", new KeywordAnalyzer());

        writer = new IndexWriter(INDEX_DIR + "architectures", analyzer, true);
    }

    public static void main(String[] args) throws Exception {
        SwissPfamIndexer indexer = new SwissPfamIndexer();
        long start = System.currentTimeMillis();
        System.out.printf("Creating index at %s.\n", start);
        indexer.createIndex();
        indexer.parse(AbstractSwissPfamParser.SWISSPFAM_FILEPATH);
        long end = System.currentTimeMillis();
        System.out.printf("Closing index at %s. Took %s\n", end, end - start);
        indexer.closeIndex();
    }

    private void closeIndex() throws Exception {
        writer.optimize();
        writer.close();
    }
}
