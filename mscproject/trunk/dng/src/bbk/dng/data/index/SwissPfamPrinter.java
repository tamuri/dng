package bbk.dng.data.index;

import java.util.*;

/**
 * Date: 13-Aug-2008 13:25:26
 */
public class SwissPfamPrinter extends AbstractSwissPfamParser {
    protected void actionAllDomains(Map<String, Map> allDomains) throws Exception {
        System.out.printf("Found %s domains\n", allDomains.size());
        for (String d: allDomains.keySet()) {
            System.out.printf("%s, %s, %s\n", d, allDomains.get(d).get("id"), allDomains.get(d).get("description"));
        }
    }

    protected void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains) throws Exception {
        // Create architecture string
        List<Integer> sortedKeys = new ArrayList<Integer>(domains.keySet());
        Collections.sort(sortedKeys);

        Iterator<Integer> iter = sortedKeys.iterator();
        Integer key = iter.next();
        StringBuffer architecture = new StringBuffer(domains.get(key) + "(" + key + ")");
        while (iter.hasNext()) {
            key = iter.next();
            architecture.append(".").append(domains.get(key)).append("(").append(key).append(")");
        }
    
        System.out.printf("%s (%s): %s\n", proteinId, proteinAccession, architecture);
    }

    public static void main(String[] args) throws Exception {
        SwissPfamPrinter p = new SwissPfamPrinter();
        p.parse(SWISSPFAM_FILEPATH);
        System.out.printf("Parsed %s sequences.", p.getSequenceCount());
    }
}
