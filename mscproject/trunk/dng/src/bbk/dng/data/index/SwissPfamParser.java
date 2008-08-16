package bbk.dng.data.index;

import java.util.Map;

/**
 * Date: 16-Aug-2008 08:16:48
 */
public interface SwissPfamParser {
    void parse(String filePath) throws Exception;
    void actionAllDomains(Map<String, Map> allDomains) throws Exception;
    abstract void actionPfamEntry(String proteinId, String proteinAccession, Map<Integer, String> domains) throws Exception;
}
