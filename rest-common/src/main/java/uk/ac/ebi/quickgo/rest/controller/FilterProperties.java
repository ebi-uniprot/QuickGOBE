package uk.ac.ebi.quickgo.rest.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created 20/12/16
 * @author Edd
 */
public class FilterProperties {
    static final boolean DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS = true;
    static final String DEFAULT_ACCESS_CONTROL_MAX_AGE = "3600";
    static final Set<String> DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS =
            asSet("Origin",
                    "Accept",
                    "X-Requested-With",
                    "Content-Type",
                    "Access-Control-Request-Method",
                    "Access-Control-Request-Headers");
    static final Set<String> DEFAULT_ACCESS_CONTROL_ALLOW_METHODS = asSet("GET");
    static final Set<String> DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN = asSet("*");
    static final String DEFAULT_PATH = "/**";
    static final HashSet<String> DEFAULT_EXPOSE_HEADERS = new HashSet<>();
    private boolean allowCredentials = DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS;
    private Set<String> allowHeaders = DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS;
    private Set<String> allowMethods = DEFAULT_ACCESS_CONTROL_ALLOW_METHODS;
    private Set<String> allowOrigins = DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN;
    private String path = DEFAULT_PATH;
    private Set<String> exposeHeaders = DEFAULT_EXPOSE_HEADERS;
    private String maxAge = DEFAULT_ACCESS_CONTROL_MAX_AGE;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public Set<String> getAllowHeaders() {
        return allowHeaders;
    }

    public void setAllowHeaders(Set<String> allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public Set<String> getAllowMethods() {
        return allowMethods;
    }

    public void setAllowMethods(Set<String> allowMethods) {
        this.allowMethods = allowMethods;
    }

    public Set<String> getAllowOrigins() {
        return allowOrigins;
    }

    public void setAllowOrigins(Set<String> allowOrigins) {
        this.allowOrigins = allowOrigins;
    }

    public Set<String> getExposeHeaders() {
        return exposeHeaders;
    }

    public void setExposeHeaders(Set<String> exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    private static Set<String> asSet(String... values) {
        return Stream.of(values).collect(Collectors.toSet());
    }
}
