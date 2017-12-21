package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Tony Wardell
 * Date: 20/12/2017
 * Time: 13:17
 * Created with IntelliJ IDEA.
 */
public class StatsSetupHelper {
    private static final String GO_TERM_RESOURCE_FORMAT = "/ontology/go/terms/%s";
    private static final String ECO_TERM_RESOURCE_FORMAT = "/ontology/eco/terms/%s";
    private static final String TAXONOMY_ID_NODE_RESOURCE_FORMAT = "/proteins/api/taxonomy/id/%s/node";
    private static final String BASE_URL = "https://localhost";
    private ObjectMapper dtoMapper = new ObjectMapper();
    private MockRestServiceServer mockRestServiceServer;

    StatsSetupHelper(
            MockRestServiceServer mockRestServiceServer) {
        this.mockRestServiceServer = mockRestServiceServer;
    }

    String constructOntologyTermsResponseObject(List<String> termIds, List<String> termNames) {
        checkArgument(termIds != null, "termIds cannot be null");
        checkArgument(termNames != null, "termIds cannot be null");
        checkArgument(termIds.size() == termNames.size(),
                "termIds and termNames lists must be the same size");

        BasicOntology response = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();

        for (int i = 0; i < termIds.size(); i++) {
            BasicOntology.Result result = new BasicOntology.Result();
            result.setId(termIds.get(i));
            result.setName(termNames.get(i));
            results.add(result);
        }

        response.setResults(results);
        return getResponseAsString(response);
    }

    <T> String getResponseAsString(T response) {
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked GO term REST response:", e);
        }
    }

    void expectRestCallSuccess(String url, String response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    void expectGORestCallResponse(String id, ResponseCreator
            response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + this.buildResource(GO_TERM_RESOURCE_FORMAT, id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(response);
    }

    void expectTaxonomyRestCallResponse(String id, ResponseCreator
            response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + this.buildResource(TAXONOMY_ID_NODE_RESOURCE_FORMAT, id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(response);
    }

    void expectEcoRestCallResponse(String id, ResponseCreator
            response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + this.buildResource(ECO_TERM_RESOURCE_FORMAT, id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(response);
    }

    String[] expectedNames(int expectedSize, String source) {
        String[] names = new String[expectedSize];
        IntStream.range(0, names.length)
                .forEach(i -> names[i] = source);
        return names;
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

    void expectGoTermHasNameViaRest(String termId, String termName) {
        this.expectIdHasGivenResultViaOntologyRest(GO_TERM_RESOURCE_FORMAT, termId, termName);
    }

    void expectEcoCodeHasNameViaRest(String ecoId, String ecoTermName) {
        this.expectIdHasGivenResultViaOntologyRest(ECO_TERM_RESOURCE_FORMAT, ecoId,
                ecoTermName);
    }

    private void expectIdHasGivenResultViaOntologyRest(String
            resourceFormat, String id, String result) {
        this.expectRestCallSuccess(
                this.buildResource(resourceFormat, id),
                this.constructOntologyTermsResponseObject(singletonList(id), singletonList(result)));
    }

    void expectTaxonIdHasNameViaRest(String id, String name) {
        BasicTaxonomyNode expectedResponse = new BasicTaxonomyNode();
        expectedResponse.setScientificName(name);
        String responseAsString = this.getResponseAsString(expectedResponse);
        this.expectRestCallSuccess(
                this.buildResource(TAXONOMY_ID_NODE_RESOURCE_FORMAT, id), responseAsString);
    }
}
