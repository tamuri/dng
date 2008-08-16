package bbk.dng.data.index;

import java.util.Map;
import java.util.HashMap;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Date: 16-Aug-2008 14:27:33
 *
 * This file is used to create a Lucene index from the joined swisspfam + uniprot file, created
 * using the load_swisspfam.pl file and standard unix commands.
 *
 * doesn't index domains!
 *
 * TODO: document procedure
 */
public class PreparedFileIndexer {
    static final String DATA_FILEPATH = "/home/aut/Documents/Mental/Bioinformatics/project/data/lucene/" +
            "swisspfam.uniprot.joined.by_accession";
    private SwissPfamIndexer indexer;

    PreparedFileIndexer(SwissPfamIndexer indexer) {
        this.indexer = indexer;
    }

    public void run(String preparedFilePath) throws Exception {

        Reader reader = new FileReader(preparedFilePath);
        BufferedReader in = new BufferedReader(reader);

        String line = in.readLine();

        while (line != null) {
            String[] fields = line.split("\\t");
            /*
            * 0 - accession
            * 1 - architecture, dot-delimited
            * 2 - entry name
            * 3 - status: reviewed / unreviewed
            * 4 - protein name
            * 5 - gene
            * 6 - organism
            * 7 - length
            * */

            String architecture = fields[1].replace(".", " ");// convert architecture "." to " "
            String status = fields[3].substring(0,1).toUpperCase(); // convert status to 'R' or 'U'

            this.indexer.savePfamEntry(fields[0], architecture, fields[2], status, fields[4], fields[6]);

            line = in.readLine();
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.printf("Creating index at %s.\n", start);

        SwissPfamIndexer indexer = new SwissPfamIndexer();
        indexer.createIndex();

        PreparedFileIndexer fileIndexer = new PreparedFileIndexer(indexer);
        fileIndexer.run(DATA_FILEPATH);

        indexer.closeIndex();

        long end = System.currentTimeMillis();
        System.out.printf("Closing index at %s. Took %s\n", end, end - start);
    }
}
