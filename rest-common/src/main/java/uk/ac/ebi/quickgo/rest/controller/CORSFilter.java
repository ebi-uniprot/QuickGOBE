package uk.ac.ebi.quickgo.rest.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
@Component
@ConfigurationProperties(prefix="cors")
public class CORSFilter implements Filter {

    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS = "true";
    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS =
            "Origin, Accept, X-Requested-With, Content-Type, " +
                    "Access-Control-Request-Method, Access-Control-Request-Headers";
    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_METHODS = "GET";
    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN = "*";
    private static final String DEFAULT_ACCESS_CONTROL_MAX_AGE = "3600";

    private String allowCredentials = DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS;
    private String allowHeaders = DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS;
    private String allowMethods = DEFAULT_ACCESS_CONTROL_ALLOW_METHODS;
    private String allowOrigins = DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN;
    private String exposeHeaders;
    private String maxAge = DEFAULT_ACCESS_CONTROL_MAX_AGE;

    @Override public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        response.setHeader("Access-Control-Allow-Headers", allowHeaders);
        response.setHeader("Access-Control-Allow-Origin", allowOrigins);
        response.setHeader("Access-Control-Allow-Methods", allowMethods);
        response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
        response.setHeader("Access-Control-Max-Age", maxAge);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override public void destroy() {

    }

    public void setOrigins(String allowOrigins) {
        this.allowOrigins = allowOrigins;
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

    public void setAllowOrigins(String allowOrigins) {
        this.allowOrigins = allowOrigins;
    }

    public void setExposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }
}
