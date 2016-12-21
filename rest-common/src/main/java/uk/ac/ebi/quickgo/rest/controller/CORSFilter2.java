package uk.ac.ebi.quickgo.rest.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Simple filter for specifying cross-origin resource sharing headers.
 *
 * <p>To specify your own CORS properties, please specify one of the following properties
 * in your application.properties file.
 * <ul>
 *     <li>cors.allow-credentials</li>
 *     <li>cors.allow-headers</li>
 *     <li>cors.allow-methods</li>
 *     <li>cors.allow-origins</li>
 *     <li>cors.expose-headers</li>
 *     <li>cors.max-age</li>
 * </ul>
 *
 * Created 16/05/16
 * @author Edd
 */
//@Component
//@ConfigurationProperties(prefix = "cors")
public class CORSFilter2 implements Filter {
    static final String DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS = "true";
    static final String DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS =
            "Origin, " +
                    "Accept, " +
                    "X-Requested-With, " +
                    "Content-Type, " +
                    "Access-Control-Request-Method, " +
                    "Access-Control-Request-Headers";
    static final String DEFAULT_ACCESS_CONTROL_ALLOW_METHODS = "GET";
    static final String DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN = "*";
    static final String DEFAULT_ACCESS_CONTROL_MAX_AGE = "3600";
    static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    private static final Logger LOGGER = getLogger(CORSFilter2.class);
    private static final Set<String> DEFAULT_CONTROL_ALLOW_ORIGINS_LIST =
            Stream.of(DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN).collect(Collectors.toSet());
    private static final String DELIMITER = " ";
    public static final String PROTOCOL_HOST_DELIMITER = "://";

    private String allowCredentials = DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS;
    private String allowHeaders = DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS;
    private String allowMethods = DEFAULT_ACCESS_CONTROL_ALLOW_METHODS;
    private Set<String> allowOrigins = DEFAULT_CONTROL_ALLOW_ORIGINS_LIST;
    private String allowOriginsStr = DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN;
    private String exposeHeaders;
    private String maxAge = DEFAULT_ACCESS_CONTROL_MAX_AGE;

    @Override public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        addHeaderIfNotNull(response, ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials);
        addHeaderIfNotNull(response, ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        addHeaderIfNotNull(response, ACCESS_CONTROL_ALLOW_METHODS, allowMethods);
        addHeaderIfNotNull(response, ACCESS_CONTROL_EXPOSE_HEADERS, exposeHeaders);
        addHeaderIfNotNull(response, ACCESS_CONTROL_MAX_AGE, maxAge);
        setAllowOrigin(servletRequest, response);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override public void destroy() {

    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setAllowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
    }

    public void setAllowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
    }

    public void setExposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }

    public void setAllowOrigins(Set<String> allowOrigins) {
        Set<String> originsInProperties = allowOrigins.stream()
                .map(origin -> {
                    try {
                        return new URI(origin).getHost();
                    } catch (URISyntaxException e) {
                        LOGGER.warn("Could not parse {} header: {}", ACCESS_CONTROL_ALLOW_ORIGIN, e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!originsInProperties.isEmpty()) {
            this.allowOrigins = originsInProperties;
        } else {
            this.allowOrigins = DEFAULT_CONTROL_ALLOW_ORIGINS_LIST;
        }

        this.allowOriginsStr = allowOrigins.stream().collect(Collectors.joining(DELIMITER));
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    private void setAllowOrigin(ServletRequest servletRequest, HttpServletResponse response) {
        String requestHost = servletRequest.getServerName();
        String protocol = servletRequest.getScheme();
        String origin = buildOrigin(protocol, requestHost);

        if (allowOrigins.contains(DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN)) {
            response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN);
        } else if (allowOrigins.contains(requestHost)) {
            response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        } else {
            LOGGER.warn("Attempted access from unauthorized origin: {}", origin);
            response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOriginsStr);
        }

    }

    private String buildOrigin(String protocol, String requestHost) {
        return protocol + PROTOCOL_HOST_DELIMITER + requestHost;
    }

    private void addHeaderIfNotNull(HttpServletResponse response, String header, String value) {
        if (value != null) {
            response.setHeader(header, value);
        }
    }
}