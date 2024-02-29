package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;

import static java.util.Arrays.stream;

/**
 * Recreate the full requested URI plus parameters from the HttpServletRequest instance.
 *
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 10:37
 * Created with IntelliJ IDEA.
 */
public class HeaderUri {

    /**
     * Recreate the full requested URI plus parameters from the HttpServletRequest instance.
     * @param request the original HTTP request.
     * @return requested URI as String.
     */
    public static String uri(HttpServletRequest request) {
        return request.getRequestURI() + "?" + parameterString(request);
    }

    private static String parameterString(HttpServletRequest request) {
        return request.getParameterMap()
                      .entrySet()
                      .stream()
                      .map(s -> "%s=%s".formatted(s.getKey(), stream(s.getValue())
                              .collect(Collectors.joining(","))))
                      .collect(Collectors.joining("&"));
    }
}
