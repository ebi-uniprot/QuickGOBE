package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
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
    private static final String TAXONOMY_RESOURCE_FORMAT = "/proteins/api/taxonomy/id/%s/node";
    private static final String BASE_URL = "https://localhost";
    private final ObjectMapper dtoMapper = new ObjectMapper();
    private final MockRestServiceServer mockRestServiceServer;

    StatsSetupHelper(MockRestServiceServer mockRestServiceServer) {
        this.mockRestServiceServer = mockRestServiceServer;
    }

    void expectGORestCallResponse(String id, ResponseCreator response) {
        expectRestCall(buildResource(GO_TERM_RESOURCE_FORMAT, id), response);
    }

    void expectTaxonomyRestCallResponse(String id, ResponseCreator response) {
        expectRestCall(buildResource(TAXONOMY_RESOURCE_FORMAT, id), response);
    }

    void expectEcoRestCallResponse(String id, ResponseCreator response) {
        expectRestCall(buildResource(ECO_TERM_RESOURCE_FORMAT, id), response);
    }

    void expectGoTermHasNameViaRest(String id, String name) {
        this.expectIdHasGivenResultViaOntologyRest(GO_TERM_RESOURCE_FORMAT, id, name);
    }

    void expectGoTermHasNameViaRest(int number) {
        for (int i = 0; i < number; i++) {
            expectIdHasGivenResultViaOntologyRest(GO_TERM_RESOURCE_FORMAT, goId(i), goName(i));
        }
    }

    void expectEcoCodeHasNameViaRest(String ecoId, String ecoTermName, int number) {
        for (int i = 0; i < number; i++) {
            expectIdHasGivenResultViaOntologyRest(ECO_TERM_RESOURCE_FORMAT, ecoId, ecoTermName);
        }
    }

    String goId(int id) {
        return IdGeneratorUtil.createGoId(id);
    }

    String goName(int id) {
        return IdGeneratorUtil.createGoId(id) + " name";
    }

    void expectTaxonIdHasNameViaRest(String id, String name) {
        String responseAsString = constructTaxonomyTermsResponseObject(name);
        expectRestCall(
                buildResource(TAXONOMY_RESOURCE_FORMAT, id), withSuccess(responseAsString, MediaType
                        .APPLICATION_JSON));
    }

    String[] expectedNames(int expectedSize, String source) {
        String[] names = new String[expectedSize];
        IntStream.range(0, names.length)
                .forEach(i -> names[i] = source);
        return names;
    }

    private void expectIdHasGivenResultViaOntologyRest(String resourceFormat, String id, String response) {
        this.expectRestCall(
                buildResource(resourceFormat, id),
                withSuccess(ontologyResponse(id, response), MediaType.APPLICATION_JSON));
    }

    private void expectRestCall(String url, ResponseCreator response) {
        mockRestServiceServer.expect(
                requestTo(BASE_URL + url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(response);
    }

    private String ontologyResponse(String id, String termName) {
        BasicOntology response = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();
        BasicOntology.Result result = new BasicOntology.Result();
        result.setId(id);
        result.setName(termName);
        results.add(result);
        response.setResults(results);
        return getResponseAsString(response);
    }

    private String constructTaxonomyTermsResponseObject(String name) {
        BasicTaxonomyNode expectedResponse = new BasicTaxonomyNode();
        expectedResponse.setScientificName(name);
        return this.getResponseAsString(expectedResponse);
    }

    private String buildResource(String format, String... arguments) {
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

    private <T> String getResponseAsString(T response) {
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked GO term REST response:", e);
        }
    }
}
