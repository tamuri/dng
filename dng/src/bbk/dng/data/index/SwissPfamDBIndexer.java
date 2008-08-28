package bbk.dng.data.index;

/**
 * Date: 16-Aug-2008 08:48:58
 */
public class SwissPfamDBIndexer {
    SwissPfamIndexer indexer;

    SwissPfamDBIndexer(SwissPfamIndexer indexer) {
        this.indexer = indexer;
    }

    public void run() throws Exception {
        // get a connect to the database

        // run query to get all annotated pfam entries

        // loop over each entry in query

            // add the sequence record

            // save domain information

        // process the domain index
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.printf("Creating index at %s.\n", start);

        SwissPfamIndexer indexer = new SwissPfamIndexer();
        indexer.createIndex();

        SwissPfamDBIndexer fileIndexer = new SwissPfamDBIndexer(indexer);
        fileIndexer.run();

        indexer.closeIndex();

        long end = System.currentTimeMillis();
        System.out.printf("Closing index at %s. Took %s\n", end, end - start);
    }
}
