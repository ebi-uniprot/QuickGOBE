package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Easy set up for faking calls to a RESTful service.
 *
 * @author Tony Wardell
 * Date: 02/03/2017
 * Time: 13:46
 * Created with IntelliJ IDEA.
 */
public class RestTestSupport {

    private final RestOperations restOperations;
    private MockRestServiceServer mockRestServiceServer;
    private ObjectMapper dtoMapper;

    static final String GO_DESCENDANTS_RESOURCE_FORMAT = "/ontology/go/terms/%s/descendants?relations=%s";
    static final String ECO_DESCENDANTS_RESOURCE_FORMAT = "/ontology/eco/terms/%s/descendants?relations=%s";
    private final String resourceFormat;
    private static final String BASE_URL = "http://localhost";
    private static final String COMMA = ",";

    public RestTestSupport(RestOperations restOperations, String resourceFormat) {
        this.restOperations = restOperations;
        this.resourceFormat = resourceFormat;
    }

    String buildResource(String format, String... arguments) {
        int requiredArgsCount = format.length() - format.replace("%", "").length();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < requiredArgsCount; i++) {
            if (i < arguments.length) {
                args.add(arguments[i]);
            } else {
                args.add("");
            }
        }
        return String.format(format, args.toArray());
    }

    void expectRestCallHasDescendants(
            List<String> termIds,
            List<String> usageRelations,
            List<List<String>> descendants) {

        reset();
        String termIdsCSV = termIds.stream().collect(Collectors.joining(COMMA));
        String relationsCSV = usageRelations.stream().collect(Collectors.joining(COMMA));

        expectRestCallSuccess(
                GET,
                buildResource(
                        resourceFormat,
                        termIdsCSV,
                        relationsCSV
                ),
                constructResponseObject(termIds, descendants)
        );
    }

    private void reset() {
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
        dtoMapper = new ObjectMapper();
    }

    void expectRestCallResponse(HttpMethod method, String url, ResponseCreator response) {
        reset();
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                             .andExpect(method(method))
                             .andRespond(response);
    }

    private String constructResponseObject(List<String> termIds, List<List<String>> descendants) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(descendants != null, "descendants cannot be null");
        checkArgument(termIds.size() == descendants.size(), "Term ID list and the (list of lists) of their " +
                "descendants should be the same size");

        ConvertedOntologyFilter response = new ConvertedOntologyFilter();
        List<ConvertedOntologyFilter.Result> results = new ArrayList<>();

        Iterator<List<String>> descendantListsIterator = descendants.iterator();
        termIds.forEach(t -> {
            ConvertedOntologyFilter.Result result = new ConvertedOntologyFilter.Result();
            result.setId(t);
            result.setDescendants(descendantListsIterator.next());
            results.add(result);
        });

        response.setResults(results);
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked REST response:", e);
        }
    }

    private void expectRestCallSuccess(HttpMethod method, String url, String response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                             .andExpect(method(method))
                             .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }
}
