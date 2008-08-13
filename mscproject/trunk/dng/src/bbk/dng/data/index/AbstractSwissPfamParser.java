package bbk.dng.data.index;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

/**
 * Date: 13-Aug-2008 10:39:46
 */
public abstract class AbstractSwissPfamParser {
    public static final String SWISSPFAM_FILEPATH = "/home/aut/Documents/Mental/Bioinformatics/project/data/Pfam/swisspfam";
    private int sequenceCount = 0;

    AbstractSwissPfamParser() {

    }

    public void parse(String filePath) throws Exception {
        System.out.printf("Parsing %s\n", filePath);
        
        // Set up regex patterns
        Pattern startEntryPattern = Pattern.compile("^>(\\w+)\\s+\\|=+\\|\\s+\\b(\\w+)\\b");
        Pattern domainPattern = Pattern.compile("^([\\w\\-]+)\\s+\\d+[\\s\\-]+\\(\\d+\\)\\s\\b(\\w+)[\\.]?[\\d]{0,2}[\\s]?\\b(.*)\\s\\s(.+)$");
        Pattern positionPattern = Pattern.compile("\\b(\\d+)\\-(\\d+)\\b");
        Matcher startMatcher;
        Matcher domainMatcher;
        Matcher positionMatcher;

        // Where we store matches
        Map<String,Map> allDomains = new HashMap<String,Map>();
        Map<Integer,String> domains = new HashMap<Integer,String>();
        String proteinId = null;
        String proteinAccession = null;

        // Swisspfam file processing
        Reader reader = new FileReader(SWISSPFAM_FILEPATH);
        BufferedReader in = new BufferedReader(reader);

        // Read the first line in file
        String line = in.readLine();
        // Loop over each line in file
        while (line != null) {
            // if it matches the start of a Pfam entry
            startMatcher = startEntryPattern.matcher(line);

            if (startMatcher.find()) {
                // if we already have a record, save it
                if (proteinId != null) {
                    actionPfamEntry(proteinId, proteinAccession, domains);
                }

                proteinId = startMatcher.group(1);
                proteinAccession = startMatcher.group(2);
                // a new empty hash for storing domain positions
                domains = new HashMap<Integer,String>();
                sequenceCount++;

            } else {
                // if it matches the start of a domain entry (within a Pfam entry)
                domainMatcher = domainPattern.matcher(line);
                if (domainMatcher.find()) {
                    // save the domain in our global list (for accession->id & description lookup)
                    // group(1) = accession, group(2) = id, group(3) = description, group(4) = positions
                    if (!allDomains.containsKey(domainMatcher.group(2))) {
                        Map<String,String> d = new HashMap<String,String>();
                        d.put("id", domainMatcher.group(1));
                        d.put("description", domainMatcher.group(3));
                        allDomains.put(domainMatcher.group(2), d);
                    }

                    // we have multiple positions for domain. match each position and store domain using position as key
                    positionMatcher =  positionPattern.matcher(domainMatcher.group(4));
                    while (positionMatcher.find()) {
                        // group(1) = domain start position
                        domains.put(Integer.parseInt(positionMatcher.group(1)), domainMatcher.group(2));
                    }
                }
            }

            // get the next line in file
            line = in.readLine();
        }
        
        // action the last entry of the file
        actionPfamEntry(proteinId, proteinAccession, domains);

        // action the complete list of domains
        actionAllDomains(allDomains);
    }

    protected abstract void actionAllDomains(Map<String, Map> allDomains) throws Exception;

    protected abstract void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains) throws Exception;

    public int getSequenceCount() {
        return sequenceCount;
    }

    public void setSequenceCount(int sequenceCount) {
        this.sequenceCount = sequenceCount;
    }
}
