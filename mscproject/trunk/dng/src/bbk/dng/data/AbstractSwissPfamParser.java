package bbk.dng.data;

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

    AbstractSwissPfamParser() {

    }

    public void parse(String filePath) throws Exception {
        System.out.println(filePath);
        // Set up regex patterns
        Pattern startEntryPattern = Pattern.compile("^>(\\w+)\\s+\\|=+\\|\\s+\\b(\\w+)\\b");
        Pattern domainPattern = Pattern.compile("^([\\w\\-]+)\\s+\\d+[\\s\\-]+\\(\\d+\\)\\s\\b(\\w+)\\b(?:.+)\\s\\s(.+)$");
        Pattern positionPattern = Pattern.compile("\\b(\\d+)\\-(\\d+)\\b");
        Matcher startMatcher;
        Matcher domainMatcher;
        Matcher positionMatcher;

        // Where we store matches
        Map<String,String> allDomains = new HashMap<String,String>();
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

            } else {
                // if it matches the start of a domain entry (within a Pfam entry)
                domainMatcher = domainPattern.matcher(line);
                if (domainMatcher.find()) {
                    // save the domain in our global list (for accession->id lookup)
                    if (!allDomains.containsKey(domainMatcher.group(2))) {
                        allDomains.put(domainMatcher.group(2), domainMatcher.group(1));
                    }

                    // we have multiple positions for domain. match each position and store domain using position as key
                    positionMatcher =  positionPattern.matcher(domainMatcher.group(3));
                    while (positionMatcher.find()) {
                        domains.put(Integer.parseInt(positionMatcher.group(1)),domainMatcher.group(2));
                    }
                }
            }

            // get the next line in file
            line = in.readLine();
        }
    }

    protected abstract void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains);
}
