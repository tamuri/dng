package bbk.dng.data.index;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;

import java.util.*;
import java.io.IOException;

import bbk.dng.utils.CollectionUtils;

/**
 * Date: 13-Aug-2008 20:13:08
 */
public class SwissPfamSearcher {
    IndexSearcher architectureSearcher;
    QueryParser architectureParser;
    IndexSearcher domainSearcher;
    QueryParser domainParser;

    SwissPfamSearcher() {}

    public SwissPfamSearcher(String indexDir) throws Exception{
        SwissPfamIndexer sp = new SwissPfamIndexer();

        architectureSearcher = new IndexSearcher(IndexReader.open(indexDir + SwissPfamIndexer.SEQUENCES_DIR));
        architectureParser = new QueryParser("id", sp.getArchitectureIndexAnalyzer());

        domainSearcher = new IndexSearcher(IndexReader.open(indexDir + SwissPfamIndexer.DOMAINS_DIR));
        domainParser = new QueryParser("accession", sp.getDomainIndexAnalyzer());
    }

    public Map<String, List<String>> getArchitecturesByDomains(List<String> domains, String domainOperator, String organism, boolean pdbOnly) throws Exception {
        StringBuilder q = new StringBuilder();
        String operatorSymbol = domainOperator.equals("AND") ? "+" : "";

        q.append("+(");
        for (String d: domains) {
            q.append(operatorSymbol).append("architecture:").append(d).append(" ");
        }
        q.append(")");

        if (!organism.equals("ALL")) q.append(" +organism:\"").append(organism).append("\"");
        if (pdbOnly) q.append(" +has_pdb:Y");

        System.out.printf("query = %s\n", q);
        
        Hits hits = architectureSearcher.search(architectureParser.parse(q.toString()));
        if (hits.length() == 0) {
            System.out.printf("No architectures found with query:%s\n", q);
            return null;
        }

        Map<String, List<String>> architectures = CollectionUtils.newMap();
        for (int i = 0; i < hits.length(); i++) {
            if (architectures.containsKey(hits.doc(i).get("architecture"))) {
                architectures.get(hits.doc(i).get("architecture")).add(hits.doc(i).get("entry_name"));
            } else {
                List<String> sequence = CollectionUtils.newList();
                sequence.add(hits.doc(i).get("entry_name"));
                architectures.put(hits.doc(i).get("architecture"), sequence);
            }
        }
        return architectures;
    }

    public List<String> getDomainsBySequence(String id) throws Exception {
        String query = (id.indexOf("_") > -1 ? "entry_name:" : "accession:") + id;
        Hits hits = architectureSearcher.search(architectureParser.parse(query));
        if (hits.length() == 0) {
            System.out.printf("Sequence %s not found.\n", id);
            return null;
        } else if (hits.length() > 1) {
            System.out.printf("Sequence %s has %s entries!\n", id, hits.length());
            return null;
        }
        return new ArrayList<String>(Arrays.asList(hits.doc(0).get("architecture").split("\\s")));
    }

    public Map<String, String> getSequenceByEntryName(String id) {
        Hits hits;
        Map<String, String> seq = null;

        String field = id.indexOf("_") > -1 ? "entry_name" : "accession";

        try {
            hits = architectureSearcher.search(architectureParser.parse(field + ":" + id));
        } catch (Exception e) {
            System.err.printf("Error searching for %s:%s\n", field, id);
            e.printStackTrace();
            return null;
        }

        if (hits != null && hits.length() == 1) {
            try {
                seq = CollectionUtils.mapOf(
                        "architecture",     hits.doc(0).get("architecture"),
                        "accession",        hits.doc(0).get("accession"),
                        "entry_name",       hits.doc(0).get("entry_name"),
                        "organism",         hits.doc(0).get("organism"),
                        "protein_name",     hits.doc(0).get("protein_name"),
                        "status",           hits.doc(0).get("status")
                );
            } catch (Exception e) {
                System.err.printf("Error populating sequence from Hit.");
                e.printStackTrace();
            }
        }
        return seq;
    }

    public Set<String> getOrganismsByDomains(List<String> domains) throws Exception{
        Set<String> organisms = CollectionUtils.newSet();
        StringBuilder q = new StringBuilder();
        for (String d: domains) {
            q.append(" architecture:").append(d);
        }
        Hits hits = architectureSearcher.search(architectureParser.parse(q.toString()));
        for (int i = 0; i < hits.length(); i++) {
            organisms.add(hits.doc(i).get("organism"));
        }
        return organisms;
    }

    public Map<String, Map<String, String>> getDomainDetails(List<String> domains) {
        Map<String, Map<String, String>> details = CollectionUtils.newMap();
        for (String thisDomain: domains) {
            try {
                Hits hits = domainSearcher.search(domainParser.parse("accession:" + thisDomain));
                if (hits.length() > 0) {
                    Map<String, String> d = CollectionUtils.mapOf(
                            "accession",    hits.doc(0).get("accession"),
                            "id",           hits.doc(0).get("id"),
                            "description",  hits.doc(0).get("description")
                    );
                    details.put(thisDomain, d);
                }
            } catch (ParseException e) {
                System.out.printf("getDomainDetails() - Error parsing query for searching domains: %s\n", thisDomain);
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                System.out.printf("getDomainDetails() - IOException: %s\n", thisDomain);
                e.printStackTrace();
                return null;
            }
        }
        return details;
    }
}
