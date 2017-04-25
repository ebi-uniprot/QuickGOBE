package uk.ac.ebi.quickgo.rest.headers;

import java.util.function.Supplier;

/**
 * Data Structure for the contents of what should be added to a HTTP header.
 *
 * @author Tony Wardell
 * Date: 05/04/2017
 * Time: 10:12
 * Created with IntelliJ IDEA.
 */

public class HttpHeader {

    private final String headerName;
    private final String headerKey;
    private final Supplier<String> headerFunction;

    public HttpHeader(String headerName, String headerKey, Supplier<String> headerFunction) {
        this.headerName = headerName;
        this.headerKey = headerKey;
        this.headerFunction = headerFunction;
    }

    /**
     * A HTTP header name e.g. cache-control
     * @return HTTP header name
     */
    String getHeaderName() {
        return headerName;
    }

    /**
     * An argument for a HTTP header e.g. max-age=3600
     * @return HTTP header argument
     */
    String getHeaderArgument() {
        return  (headerKey + "=" + headerFunction.get());
    }

}
