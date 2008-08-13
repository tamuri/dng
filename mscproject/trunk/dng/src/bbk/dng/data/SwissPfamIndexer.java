package bbk.dng.data;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Date: 13-Aug-2008 12:18:16
 */
public class SwissPfamIndexer extends AbstractSwissPfamParser {
    public void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains) {
        System.out.printf("%s, %s\n", proteinId, proteinAccession);
        List<Integer> sortedKeys = new ArrayList<Integer>(domains.keySet());
        Collections.sort(sortedKeys);
        for (Integer position: sortedKeys) {
            System.out.printf("\t%s, %s\n", position, domains.get(position));
        }
    }

    public static void main(String[] args) throws Exception {
        SwissPfamIndexer indexer = new SwissPfamIndexer();
        indexer.parse(AbstractSwissPfamParser.SWISSPFAM_FILEPATH);
    }
}
