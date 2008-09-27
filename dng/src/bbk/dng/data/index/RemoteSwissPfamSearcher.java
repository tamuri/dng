package bbk.dng.data.index;

import bbk.dng.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.beans.XMLDecoder;

public class RemoteSwissPfamSearcher extends SwissPfamSearcher {
    private String remotePath;

    public RemoteSwissPfamSearcher(String remotePath) {
        this.remotePath = remotePath;
    }

    @SuppressWarnings("unchecked") 
    public Map<String, List<String>> getArchitecturesByDomains(List<String> domains, String domainOperator, String organism, boolean pdbOnly) throws Exception {
        // http://localhost:8080/archschema/archSchemaSearch?method=getArchitecturesByDomains&domains=PF02159,PF00105,PB011345,PB000636,PF00104&domainOperator=AND&organism=ALL&pdbOnly=false
        StringBuilder sb = new StringBuilder();
        sb.append(this.remotePath).append("/archSchemaSearch?");
        sb.append("method=getArchitecturesByDomains&");
        sb.append("domains=").append(CollectionUtils.join(domains, ',')).append("&");
        sb.append("domainOperator=").append(domainOperator).append("&");
        sb.append("organism=").append(URLEncoder.encode(organism, "UTF-8")).append("&");
        sb.append("pdbOnly=").append(pdbOnly);

        Object o = getResponse(sb.toString());
        return (Map<String, List<String>>) o;
    }

    @SuppressWarnings("unchecked")
    public List<String> getDomainsBySequence(String id) throws Exception {
        // http://localhost:8080/archschema/archSchemaSearch?method=getDomainsBySequence&identifier=ESR1_HUMAN
        StringBuilder sb = new StringBuilder();
        sb.append(this.remotePath).append("/archSchemaSearch?");
        sb.append("method=getDomainsBySequence&");
        sb.append("identifier=").append(id).append("&");

        Object o = getResponse(sb.toString());
        return (List<String>) o;

    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getSequenceByEntryName(String id) {
        // http://localhost:8080/archschema/archSchemaSearch?method=getSequenceByEntryName&identifier=ESR1_HUMAN
        StringBuilder sb = new StringBuilder();
        sb.append(this.remotePath).append("/archSchemaSearch?");
        sb.append("method=getSequenceByEntryName&");
        sb.append("identifier=").append(id).append("&");

        Object o = getResponse(sb.toString());
        return (Map<String, String>) o;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getOrganismsByDomains(List<String> domains) throws Exception {
        // http://localhost:8080/archschema/archSchemaSearch?method=getOrganismsByDomains&domains=PF02159,PF00105,PB011345,PB000636,PF00104
        StringBuilder sb = new StringBuilder();
        sb.append(this.remotePath).append("/archSchemaSearch?");
        sb.append("method=getOrganismsByDomains&");
        sb.append("domains=").append(CollectionUtils.join(domains, ',')).append("&");
        
        Object o = getResponse(sb.toString());
        return (Set<String>) o;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, String>> getDomainDetails(List<String> domains) {
        // http://localhost:8080/archschema/archSchemaSearch?method=getDomainDetails&domains=PF02159,PF00105,PB011345,PB000636,PF00104
        StringBuilder sb = new StringBuilder();
        sb.append(this.remotePath).append("/archSchemaSearch?");
        sb.append("method=getDomainDetails&");
        sb.append("domains=").append(CollectionUtils.join(domains, ',')).append("&");

        Object o = getResponse(sb.toString());
        return (Map<String, Map<String, String>>) o;
    }

    private Object getResponse(String request) {
        System.out.printf("request = %s\n", request);
        try {
            URL url = new URL(request);

            URLConnection URLconnection = url.openConnection() ;
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;

            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream in = httpConnection.getInputStream();
            XMLDecoder d = new XMLDecoder(in);
            Object result = d.readObject();
            d.close();
            in.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
