package uk.ac.ebi.quickgo.annotation.download.header;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 23/05/2017
 * Time: 13:58
 * Created with IntelliJ IDEA.
 */
public class HeaderUriTest {

    private static final String URI = "/QuickGO/services/annotation/downloadSearch?downloadLimit=7&geneProductId" +
            "=UniProtKB:A0A000&includeFields=goName,taxonName";
    private HttpServletRequest servletRequest = mock(HttpServletRequest.class);
    private static final Map<String,String[]> parameterMap = new HashMap<>();
    static {
        parameterMap.put("downloadLimit",new String[] {"7"});
        parameterMap.put("geneProductId",new String[] {"UniProtKB:A0A000"});
        parameterMap.put("includeFields",new String[] {"goName,taxonName"});
    }

    @Before
    public void setup(){
        when(servletRequest.getRequestURI()).thenReturn("/QuickGO/services/annotation/downloadSearch");
        when(servletRequest.getParameterMap()).thenReturn(parameterMap);
    }

    @Test
    public void buildUriString(){
        String uri = HeaderUri.uri(servletRequest);

        assertThat(uri, is(URI));
    }

}
