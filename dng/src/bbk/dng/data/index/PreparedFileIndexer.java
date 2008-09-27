package bbk.dng.data.index;

import bbk.dng.utils.CollectionUtils;

import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Map;

/**
 * Date: 16-Aug-2008 14:27:33
 *
 * This file is used to create a Lucene index from the joined swisspfam + uniprot file, created
 * using the load_swisspfam.pl file and standard unix commands.
 *
 */
public class PreparedFileIndexer {
    static final String DATA_FILEPATH = "./";
    static final String SEQUENCES_FILE = "swisspfam.uniprot.pdbsws.joined.by.accession";
    static final String DOMAINS_FILE = "pfam_domains.parsed";
    private SwissPfamIndexer indexer;

    PreparedFileIndexer(SwissPfamIndexer indexer) {
        this.indexer = indexer;
    }

    public void run(String preparedFilePath) throws Exception {
        System.out.printf("Creating 'sequence' index.\n");
        indexer.createSequenceIndex();

        // Populate the SEQUENCES index
        Reader reader = new FileReader(preparedFilePath + SEQUENCES_FILE);
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
            * 8 - pdb codes (space-delimited) or NONE
            * */

            String architecture = fields[1].replace(".", " ");// convert architecture "." to " "
            String status = fields[3].substring(0,1).toUpperCase(); // convert status to 'R' or 'U'

            String hasPDB;
            String pdbCodes = "";
            if (fields[8].equals("NONE")) {
                hasPDB = "N";
            } else {
                hasPDB = "Y";
                pdbCodes = fields[8].toUpperCase();
            }

            this.indexer.savePfamEntry(fields[0], architecture, fields[2], status, fields[4], fields[6], hasPDB, pdbCodes);

            line = in.readLine();
        }

        in.close();
        reader.close();

        System.out.printf("Optimizing and closing 'sequence' index.\n");
        indexer.closeAndOptimizeSequenceIndex();

        // Populate the DOMAINS index
        System.out.printf("Loading all domains.\n");
        Map<String,Map> allDomains = CollectionUtils.newMap();
        reader = new FileReader(preparedFilePath + DOMAINS_FILE);
        in = new BufferedReader(reader);

        line = in.readLine();

        while (line != null) {
            String[] fields = line.split("\\t");
            // 0 - id, 1 - accession, 2 - description
            Map<String,String> d = CollectionUtils.mapOf("id", fields[0], "description", fields[2]);
            allDomains.put(fields[1], d);
        }

        System.out.printf("Indexing all domains...");
        this.indexer.saveAllDomains(allDomains);
        System.out.printf("complete.\n");
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.printf("Start creating index at %s.\n", start);

        SwissPfamIndexer indexer = new SwissPfamIndexer();
        PreparedFileIndexer fileIndexer = new PreparedFileIndexer(indexer);
        fileIndexer.run(DATA_FILEPATH);

        long end = System.currentTimeMillis();
        System.out.printf("Completed indexing at %s. Took %s\n", end, end - start);
    }
}
