package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.stream;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 10:37
 * Created with IntelliJ IDEA.
 */
public class HeaderUri {

    public static String uri(HttpServletRequest request) {
        return request.getRequestURI() + "?" + parameterString(request);
    }

    private static String parameterString(HttpServletRequest request) {
        return request.getParameterMap()
                      .entrySet()
                      .stream()
                      .map(s -> String.format("%s=%s", s.getKey(), stream(s.getValue())
                              .collect(Collectors.joining(","))))
                      .collect(Collectors.joining("&"));
    }
}
