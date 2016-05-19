package uk.ac.ebi.quickgo.rest.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

/**
 * Simple filter for specifying cross-origin resource sharing headers.
 *
 * Created 16/05/16
 * @author Edd
 */
@Configuration
public class CORSFilter implements Filter {

    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN = "*";
    private static final String DEFAULT_ACCESS_CONTROL_MAX_AGE = "3600";
    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS = "true";
    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_METHODS = "POST, GET, OPTIONS, DELETE";
    private static final String DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS =
            "Content-Type, Accept, X-Requested-With";

    @Override public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Origin", DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN);
        response.setHeader("Access-Control-Allow-Credentials", DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS);
        response.setHeader("Access-Control-Allow-Methods", DEFAULT_ACCESS_CONTROL_ALLOW_METHODS);
        response.setHeader("Access-Control-Max-Age", DEFAULT_ACCESS_CONTROL_MAX_AGE);
        response.setHeader("Access-Control-Allow-Headers", DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override public void destroy() {

    }
}
