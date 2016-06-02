package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.rest.comm.RESTRequesterImpl;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created 31/05/16
 * @author Edd
 */
public class RESTCommFilterConverter<T> implements FilterConverter {

    private static final String IP = "ip";
    private static final String ENDPOINT = "endpoint";
    private final String ip;
    private final String endpoint;
    private final CompletableFuture<T> futureResponse;

    public RESTCommFilterConverter(String ip, String endpoint, Class<T> responseType) {
        this.ip = ip;
        this.endpoint = endpoint;

        this.futureResponse = RESTRequesterImpl.newBuilder(createURL(ip, endpoint))
                .addRequestParameter("name", "value")
                .build().get(responseType);
    }

    String createURL(String ip, String endpoint) {
        ip = ip.trim();
        endpoint = endpoint.trim();

        if (!ip.endsWith("/")) {
            ip += "/";
        }

        if (endpoint.startsWith("/")) {
            endpoint = endpoint.substring(1);
        }

        return ip + endpoint;
    }

    @Override public QuickGOQuery transform() {
        // get resulting id list from rest lookup
        // or these together with a simple query


        return null;
    }

    static <ResponseType> RESTCommFilterConverter<ResponseType> createRESTCommFilterConverter(
            Map<String, String> restCommProperties,
            Class<ResponseType> responseType,
            RequestFilter requestFilter) {
        Preconditions.checkArgument(restCommProperties != null, "Map containing REST communication properties cannot " +
                "be null.");

        String ip = restCommProperties.get(IP);
        String endpoint = restCommProperties.get(ENDPOINT);

        return new RESTCommFilterConverter<>(ip, endpoint, responseType);
    }
}
