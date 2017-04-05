package uk.ac.ebi.quickgo.rest.headers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/**
 * On request, provide a map of HTTP headers.
 *
 * @author Tony Wardell
 * Date: 05/04/2017
 * Time: 10:06
 * Created with IntelliJ IDEA.
 */
public class HttpHeadersProvider {

    private final List<HttpHeader> headerSources;

    /**
     * Instantiate this class with a list of header sources.
     * @param headerSources list of sources of HTTP headers.
     */
    public HttpHeadersProvider(List<HttpHeader> headerSources) {
        this.headerSources = headerSources;
    }

    /**
     * Provide a map of key value pairs that contain HTTP header information.
     * @return header map
     */
    public MultiValueMap<String, String> provide(){
        MultiValueMap<String, String> headers = new HttpHeaders();
        headerSources.forEach(e -> headers.add(e.getHeaderName(), e.getHeaderArgument()));
        return headers;
    }
}
