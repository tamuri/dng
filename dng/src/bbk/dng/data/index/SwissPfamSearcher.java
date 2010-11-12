package bbk.dng.data.index;

// import org.apache.lucene.search.IndexSearcher;
// import org.apache.lucene.queryParser.QueryParser;

import bbk.dng.Constants;
import bbk.dng.utils.CollectionUtils;
import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Date: 13-Aug-2008 20:13:08
 */
public class SwissPfamSearcher {

  /* RAL 9 Dec 09 --> */
  /* RAL 9 Dec 09 --> */
  public static final int RUN_STATUS = 0;

  public static final int UNKNOWN = -9;
  public static final int ERROR_MESSAGE = -1;
  public static final int RUN_SUCCESSFUL = 0;

  public static final int ERR_TYPE = 1;
  public static final int ERR_MESSAGE = 2;

  public static final int RESULT_NSEQS = 1;
  public static final int RESULT_ARCHITECTURES = 2;
  public static final int RESULT_NCOLLAPSED = 3;
  public static final int RESULT_NFILTERED = 4;
  public static final int RESULT_NDOMAINS = 5;
  public static final int RESULT_NPFAM = 6;
  public static final int RESULT_NSPECIES = 7;
  public static final int RESULT_PARENT_SEQUENCE = 8;
  public static final int RESULT_HITS = 9;
  public static final int RESULT_EC_NUMBERS = 10;
  public static final int RESULT_SPECIES = 11;
  public static final int RESULT_PFAM = 12;
  public static final int RESULT_ENZYME_NAMES = 13;
  public static final int RESULT_ARCHINDEX = 14;
  public static final int RESULT_CONNECTIONS_MATRIX = 15;
  public static final int RESULT_MAX_ARCHITECTURES = 16;

  public static final int NSUMMARY_DATA = 7;

  /* RAL 20 Jul 09 --> 
  public static final int NONE = -1;
  public static final int SEQSEARCH_PACKAGE = 0;
  public static final int GRAPHDATA_PACKAGE = 1;

  public static final int RESULTS_PACKAGE_TYPE = 0;
  public static final int SEQSEARCH_PARENT_SEQUENCE = 1;
  public static final int SEQSEARCH_NDOMAINS = 2;
  public static final int SEQSEARCH_NSEQUENCES = 3;
  public static final int SEQSEARCH_NSPECIES = 4;
  public static final int SEQSEARCH_SEQDATA = 5;
  public static final int SEQSEARCH_DOMAINLIST = 6;
  public static final int SEQSEARCH_DOMAINDETAILS = 7;
  public static final int SEQSEARCH_DOMAINSEQS = 8;
  public static final int SEQSEARCH_SPECIESLIST = 9;
  public static final int GRAPHDATA_ARCHSET = 10;
  public static final int GRAPHDATA_DOMAINDETAILS = 11;
   <-- RAL 20 Jul 09 */
  // IndexSearcher architectureSearcher;
  // QueryParser architectureParser;
  /* RAL 21 Jul 09 --> */
  // QueryParser archParser;
  /* <-- RAL 21 Jul 09 */
  // IndexSearcher domainSearcher;
  // QueryParser domainParser;
  /* RAL 20 Jul 09 --> */
  // IndexSearcher domSeqsSearcher;
  // QueryParser domSeqsParser;
  // IndexSearcher specListSearcher;
  // QueryParser specListParser;

  /* <-- RAL 20 Jul 09 */

  //
  // C O N S T A N T S
  //
  public enum LineType {
    ERROR_MESSAGE, RUN_SUMMARY, PARENT_SEQUENCE, ARCHITECTURE_RECORD,
    HIT_RECORD, EC_RECORD, EC_NAME_RECORD, PFAM_RECORD, SPECIES_RECORD,
    CONNECTIONS_RECORD, MAX_ARCH_RECORD, NOT_RECOGNIZED
  }

  private String remotePath;

  public SwissPfamSearcher(String remotePath) {
    this.remotePath = remotePath;
  }

  public SwissPfamSearcher() {
  }

  public Vector<Object> pfamDomainSearch(String userId, String pfamId,
/* RAL 17 Jun 10 --> */
//          boolean initialSearch, Vector<Object> filterCriteria)
          boolean initialSearch, Vector<Object> filterCriteria,
          boolean useCATH, int maxArchitectures, boolean reviewedOnly, boolean useSSG)
/* <-- RAL 17 Jun 10 */
          throws Exception {

    // Test: http://wwwdev.ebi.ac.uk/thornton-apps/archschema/archSchemaSearch?method=pfamDomainSearch&domains=PF02159,PF00105,PB011345,PB000636,PF00104&domainOperator=AND&organism=ALL&pdbOnly=false

    // Define the method to be called on the remote server
    String method = "pfamDomainSearch";
    Vector<Object> o = runSearch(method, userId, null, pfamId, initialSearch,
/* RAL 17 Jun 10 --> */
//            filterCriteria);
            filterCriteria, useCATH, maxArchitectures, reviewedOnly, useSSG);
/* <-- RAL 17 Jun 10 */

    // Return the results package
    return o;
  }

  @SuppressWarnings("unchecked")
  public Vector<Object> uniprotSequenceSearch(String userId, String seqId,
/* RAL 17 Jun 10 --> */
//          String pfamId, boolean initialSearch, Vector<Object> filterCriteria)
          String pfamId, boolean initialSearch, Vector<Object> filterCriteria,
          boolean useCATH, int maxArchitectures, boolean reviewedOnly, boolean useSSG)
/* <-- RAL 17 Jun 10 */
          throws Exception {

    // Test: http://wwwdev.ebi.ac.uk/thornton-apps/archschema/archSchemaSearch?method=uniprotSequenceSearch&seqId=Q76RF1&domainOperator=OR&organism=ALL&pdbOnly=false

    // Define the method to be called on the remote server
    String method = "uniprotSequenceSearch";
    Vector<Object> o = runSearch(method, userId, seqId, pfamId, initialSearch,
/* RAL 17 Jun 10 --> */
//            filterCriteria);
            filterCriteria, useCATH, maxArchitectures, reviewedOnly, useSSG);
/* <-- RAL 17 Jun 10 */

    // Return the results package
    return o;
  }
  /* <-- RAL 24 Jul 09 */

  // Convert list of PDB codes in string into a list of strings
  public List<String> getPDBList(String pdbCodes) {

    List<String> pdbList = CollectionUtils.newList();

    // Split the PDB codes corresponding to the current
    // UniProt sequence
    if (pdbCodes != null && pdbCodes.length() != 0) {
      // Split list into individual PDB codes
      for (String code : pdbCodes.split(" ")) {

        // If code too long (ie has too long a chain list)
        // truncate and add ellipsis
        if (code.length() > 8) {
          // Truncate
          code = code.substring(0, 8) + "...";
        }

        // Add to PDB list
        pdbList.add(code);
      }
    }

    // Return the list
    return pdbList;
  }

  /* RAL 9 Dec 09 --> */
  // Call the ArchIndex program via remote call to
  // http://www.ebi.ac.uk/thornton-srv/databases/cgi-bin/archschema/ArchSearch.pl
  private Vector<Object> callArchIndex(String request) {
    boolean done = false;
    boolean haveError = false;
    BufferedReader inputStream = null;
    InputStreamReader fileStream = null;
    int iarch = 0;
    int jarch = 0;
    int line = 0;
    int maxArch = Constants.MAX_ARCHITECTURES;
    int nArch = 0;
    int nlines = 0;
    int nEnzymes = 0;
    int summaryData[] = new int[NSUMMARY_DATA];
    for (int i = 0; i < NSUMMARY_DATA; i++) {
      summaryData[i] = 0;
    }
    String nSeqs = "0";
    LineType lType = LineType.NOT_RECOGNIZED;
    String errorMessage[] = new String[2];
    String inputLine;
    String architecture = null;
    String parentSeqId = null;
    Vector<Object> v = new Vector<Object>();
    Map<Integer, Map<String, String>> archIndex = CollectionUtils.newMap();
    Map<Pair<String,String>, Integer> connectionsList = CollectionUtils.newMap();
    Map<String, Map<String, String>> domainMap = CollectionUtils.newMap();
    Map<Integer, Map<String, String>> enzymeMap = CollectionUtils.newMap();
    Map<String, Map<String, String>> speciesMap = CollectionUtils.newMap();
    Set<Map<String, String>> archSet = CollectionUtils.newSet();
    Set<Map<String, String>> enzSet = CollectionUtils.newSet();

    // If this is a URL, open input stream
    if (request.substring(0, 7).equals("http://")) {
      try {
        // Create a URL object
        URL url = new URL(request);

        try {
          // Open the file input stream for the archindex output
          fileStream = new InputStreamReader(url.openStream());
        } catch (IOException ex) {
          System.out.println("*** ERROR. Failed to open file stream: " + url);
          errorMessage[0] = "Connection error";
          errorMessage[1] = "Unable to connect to EBI to perform search";
          haveError = true;
        }
      } catch (MalformedURLException ex) {
        System.out.println("*** ERROR. Malformed URL: " + request);
        errorMessage[0] = "URL error";
        errorMessage[1] = "Invalid URL request: " + request;
        haveError = true;
      }
    } else {
      try {
        // Otherwise, open file
        fileStream = new FileReader(request);
      } catch (FileNotFoundException ex) {
        System.out.println("*** ERROR. File not found: " + request);
        errorMessage[0] = "File error";
        errorMessage[1] = "Unable to open file: " + request;
        haveError = true;
      }
    }

    // If error opening URL and performing search, return error message
    if (haveError) {
      // Mark the results as containing an error message
      v.add(ERROR_MESSAGE);

      // Add the error type and the error message
      v.add(errorMessage[0]);
      v.add(errorMessage[1]);
      return v;
    }

    // Open the URL input stream
    inputStream = new BufferedReader(fileStream);

    try {
      try {
        // Loop while reading through the file
        while ((inputLine = inputStream.readLine()) != null && !done) {
          // Increment count of lines read in
          nlines++;

          // If this is a key record, determine the type of the lines to
          // follow
          if (inputLine.charAt(0) == ':') {
            // Determine line type
            lType = getLineType(inputLine);

            // Initialise line count
            line = 0;

            // Otherwise, read in the current line's data
          } else {
            // Increment line count for this line type
            line++;
            // Process line according to its type
            switch (lType) {

              // Error message - get details
              case ERROR_MESSAGE:

                // Extract the error type and the error message
                int i = 0;
                for (String err : inputLine.split("\\t")) {
                  // Save the the error type or message
                  errorMessage[i] = err;
                  i++;
                }
                // Set flag that we're done
                done = true;
                haveError = true;
                break;

            // Summary run data: no. of seqs, domains and species
              case RUN_SUMMARY:
                // Get the number of hits returned
                int number = (int) Integer.parseInt(inputLine.trim());

                // If no hits returned, then return error message
                if (line == 1 && number == 0) {

                  // Mark the results as containing an error message
                  haveError = true;

                  // Add error type and message
                  errorMessage[0] = "No match";
                  errorMessage[0] = "No sequences match the selection criteria!";

                  // Set flag that we're done
                  done = true;
                } else {

                  // Otherwise, save the number read in
                  if (line - 1 < NSUMMARY_DATA) {
                    summaryData[line - 1] = number;
                  }
                }
                break;

            // Summary run data: no. of seqs, domains and species
              case PARENT_SEQUENCE:
                // Initialise count
                i = 0;

                // Loop over the tab-separated fields to pick out the
                // UniProt id of the parent sequence
                for (String field : inputLine.split("\\t")) {
                  // If UniProt id, then save
                  if (i == 1) {
                    parentSeqId = field;
                  }
                  i++;
                }
                break;

            // Architecture record: architecture, etc
              case ARCHITECTURE_RECORD:

                // Extract the fields on this line
                String[] afield = new String[3];
                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  afield[i] = f;
                  i++;
                }
                // Store the architecture
                architecture = afield[0];
                nSeqs = afield[1];

                // Create a set of key-value records
                Map<String, String> archdata = CollectionUtils.mapOf(
                        "id", nArch,
                        "architecture", architecture,
                        "nseqs", nSeqs,
                        "dist", afield[2]);

                // Store this architecture's details
                archIndex.put(nArch, archdata);

                // Increment count of architectures
                nArch++;

                // Define next record as a hit record
                lType = LineType.HIT_RECORD;

                break;


            // Hit records: UniProt seq, architecture, protein, etc
              case HIT_RECORD:

                // Extract the fields on this line
                String[] field = new String[7];

                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  field[i] = f;
                  i++;
                }
                // Extract the species id
                int ipos = field[1].indexOf('_');
                String speciesId = field[1].substring(ipos + 1);

                // Convert numerical fields to strings
                String nArchString = "" + nArch;
                String nSeqsString = "" + nSeqs;

                // Create a set of key-value records
                Map<String, String> hit
                        = CollectionUtils.mapOf(
                        "uniprot_acc", field[0],
                        "uniprot_id", field[1],
                        "architecture", architecture,
                        "species_id", speciesId,
                        "protein_name", field[2],
                        "seq_len", field[3],
                        "pdb_codes", field[4],
                        "3D_coverage", field[5],
                        "arch_id", nArchString,
                        "nseqs", nSeqsString);

                // Add to architecture set
                archSet.add(hit);
                break;

              // EC records: EC number and number of sequences in current
              // architecture
              case EC_RECORD:

                // Extract the fields on this line
                String[] efield = new String[3];

                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  efield[i] = f;
                  i++;
                }

                // Create a set of key-value records
                Map<String, String> ecNo = CollectionUtils.mapOf(
                        "id", efield[0],
                        "nseqs", efield[1],
                        "architecture", architecture);

                // Add to architecture set
                enzSet.add(ecNo);
                break;

                // Species record: id, organism, no. seqs
              case SPECIES_RECORD:

                // Extract the fields on this line
                String[] sfield = new String[3];

                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  sfield[i] = f;
                  i++;
                }

                // Create a set of key-value records
                Map<String, String> species
                        = CollectionUtils.mapOf(
                        "id", sfield[0],
                        "organism", sfield[1],
                        "nseqs", sfield[2]);

                // Add to list of species vector
                speciesMap.put(sfield[0], species);

                break;

                // Pfam record: id, short name, long name, no. seqs
              case PFAM_RECORD:

                // Extract the fields on this line
                String[] pfield = new String[4];

                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  pfield[i] = f;
                  i++;
                }

                // Create a set of key-value records
                Map<String, String> pfam
                        = CollectionUtils.mapOf(
                        "id", pfield[0],
                        "description", pfield[1],
                        "short_name", pfield[2],
                        "nseqs", pfield[3]);

                // Add to list of Pfam domains vector
                domainMap.put(pfield[0], pfam);

                break;

                // EC name record: EC code, name, no. seqs
              case EC_NAME_RECORD:

                // Extract the fields on this line
                String[] nfield = new String[5];

                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  nfield[i] = f;
                  i++;
                }

                // Create a set of key-value records
                Map<String, String> enzyme
                        = CollectionUtils.mapOf(
                        "id", nfield[0],
                        "description", nfield[1],
                        "narch", nfield[2],
                        "nseqs", nfield[3]);

                // Add to list of EC codes and descriptions
                enzymeMap.put(nEnzymes, enzyme);

                // Increment enzyme count
                nEnzymes++;

                break;

                // Connections record giving link joining two architecture
                // nodes
              case CONNECTIONS_RECORD:

                // Add the Pfam vector to the results vector
                if (line == 1) {
                  // Initialise array subscripts
                  iarch = 0;
                  jarch = 1;
                }

                // Extract the fields on this line
                String[] cfield = new String[3];

                // Extract the fields in this line
                i = 0;
                for (String f : inputLine.split("\\t")) {
                  cfield[i] = f;
                  i++;
                }

                // Get the identifiers of the two connected architecture
                // nodes
                iarch = (int) Integer.parseInt(cfield[0]);
                jarch = (int) Integer.parseInt(cfield[1]);
                int dist = (int) Integer.parseInt(cfield[2]);

                // Get the two architectures
                String architecture1 = archIndex.get(iarch).get("architecture");
                String architecture2 = archIndex.get(jarch).get("architecture");

                // Create a key record from the pair of architectures
                Pair<String, String> edge
                        = Tuple.from(architecture1, architecture2);
                connectionsList.put(edge, dist);

                break;

              // Maximum architectures
              case MAX_ARCH_RECORD:

                // Get the maximum number of architectures defined for plot
                maxArch = (int) Integer.parseInt(inputLine.trim());

                break;

            // Ignore all other records
              default:
                break;
            }
          }
        }
      } catch (IOException ex) {
        System.out.println("*** ERROR. File read error: " + request);
        return null;
      }
    } finally {

      // If have error, then write out message
      if (haveError) {
        v.add(ERROR_MESSAGE);

        // Add the error type and the error message
        v.add(errorMessage[0]);
        v.add(errorMessage[1]);
        return v;
      }

      // Start return vector
      v.add(RUN_SUCCESSFUL);

      // Save the summary data in the return vector
      for (int i = 0; i < NSUMMARY_DATA; i++) {
        v.add(summaryData[i]);
      }

      // Add the parent sequence
      v.add(parentSeqId);

      // Add the hits and each architecture's enzyme classes
      v.add(archSet);
      v.add(enzSet);

      // Add the species vector to the results vector
      v.add(speciesMap);

      // Save the Pfam data
      v.add(domainMap);

      // Add the enzyme names map
      v.add(enzymeMap);

      // Add the architectures and distance matrix data to the
      // results vector
      v.add(archIndex);

      // Add the architecture-node connectivities list
      v.add(connectionsList);

      // Add maximum architecture setting for plot
      v.add(maxArch);

      // Close the input file
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ex) {
          System.out.println("*** ERROR. Unable to close input stream");
        }
      }
    }

    // Return the search results
    return v;
  }

  // Determine the line type of the line just read in
  private LineType getLineType(String inputLine) {
    LineType lType = LineType.NOT_RECOGNIZED;

    // Define line type
    if (inputLine.substring(1).equals("ERROR")) {
      lType = LineType.ERROR_MESSAGE;
    } else if (inputLine.substring(1).equals("SUMMARY")) {
      lType = LineType.RUN_SUMMARY;
    } else if (inputLine.substring(1).equals("PARENT")) {
      lType = LineType.PARENT_SEQUENCE;
    } else if (inputLine.substring(1).equals("HITS")) {
      lType = LineType.HIT_RECORD;
    } else if (inputLine.substring(1).equals("A")) {
      lType = LineType.ARCHITECTURE_RECORD;
    } else if (inputLine.substring(1).equals("E")) {
      lType = LineType.EC_RECORD;
    } else if (inputLine.substring(1).equals("PFAM")) {
      lType = LineType.PFAM_RECORD;
    } else if (inputLine.substring(1).equals("EC_NAME")) {
      lType = LineType.EC_NAME_RECORD;
    } else if (inputLine.substring(1).equals("SPECIES")) {
      lType = LineType.SPECIES_RECORD;
    } else if (inputLine.substring(1).equals("CONNECTIONS")) {
      lType = LineType.CONNECTIONS_RECORD;
    } else if (inputLine.substring(1).equals("MAX_ARCH")) {
      lType = LineType.MAX_ARCH_RECORD;
    }

    // Return line type
    return lType;
  }
  /* <-- RAL 9 Dec 09 */

  /* RAL 24 Jul 09 --> */
  private Vector<Object> runSearch(String method, String userId, String seqId,
/* RAL 17 Jun 10 --> */
//          String pfamId, boolean initialSearch, Vector<Object> filterCriteria)
          String pfamId, boolean initialSearch, Vector<Object> filterCriteria,
          boolean useCATH, int maxArchitectures, boolean reviewedOnly,
          boolean useSSG)
/* <-- RAL 17 Jun 10 */
          throws Exception  {

    boolean pdbOnly = false;
    List<String> domainList = CollectionUtils.newList();
    String pfamDomainOperator = "OR";
    String organism = "ALL";
    char splitChar = '.';
      if (useCATH) {
        splitChar = '_';
      }

    // Extract the filter criteria, if any
    if (filterCriteria != null) {
      pfamDomainOperator = (String) filterCriteria.get(0);
      domainList = (List<String>) filterCriteria.get(1);
      organism = (String) filterCriteria.get(2);
      pdbOnly = (Boolean) filterCriteria.get(3);
    }

    // Form the URL of the call to the remote Searcher servlet
    StringBuilder sb = new StringBuilder();
    sb.append(this.remotePath);
/* RAL 8 Dec 09 -->
    sb.append(this.remotePath).append("/archSchemaSearch?");
    sb.append("method=" + method + "&");
<-- RAL 8 Dec 09 */
    if (remotePath.substring(0, 7).equals("http://")) {
      if (userId != null) {
        sb.append("userId=" + userId + "&");
      }
      if (seqId != null) {
        sb.append("seqId=" + seqId + "&");
      }
      if (pfamId != null) {
        sb.append("pfamId=" + pfamId + "&");
      }
      if (domainList != null && !domainList.isEmpty()) {
        sb.append("domains=").append(CollectionUtils.join(domainList, splitChar)).append("&");
      }
      if (filterCriteria != null) {
        sb.append("domainOperator=").append(pfamDomainOperator).append("&");
        sb.append("organism=").append(URLEncoder.encode(organism, "UTF-8")).append("&");
        sb.append("pdbOnly=").append(pdbOnly);
      }
/* RAL 17 Jun 10 --> */
      if (useCATH) {
        sb.append("&cath=TRUE");
      }
      if (useSSG) {
        sb.append("&ssg=TRUE");
      }
      if (reviewedOnly) {
        sb.append("&rev=TRUE");
      }
      if (maxArchitectures > 0) {
        sb.append("&maxa=" + maxArchitectures);
      }
/* <-- RAL 17 Jun 10 */
    }
/* RAL 8 Dec 09 -->
    if (initialSearch) {
      sb.append("init=").append(initialSearch);
    }
<-- RAL 8 Dec 09 */

    // Perform the search
/* RAL 9 Dec 09 -->
    Object o = getResponse(sb.toString());
<-- RAL 9 Dec 09 */
    Vector<Object> v = callArchIndex(sb.toString());

    // Return the results received
    return v;
  }
  /* <-- RAL 24 Jul 09 */
}
