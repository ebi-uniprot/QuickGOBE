package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
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

    void expectGoTermHasNameViaRest(String id, String name) {
        this.expectResultViaOntologyRest(GO_TERM_RESOURCE_FORMAT, id, name);
    }

    void expectGoTermHasNameViaRest(int number, Function<Integer, String> toId, Function<Integer, String> toName) {
        IntStream.range(0, number)
                .forEach(i -> expectGoTermHasNameViaRest(toId.apply(i), toName.apply(i)));
    }

    void expectEcoCodeHasNameViaRest(String id, String name, int number) {
        IntStream.range(0, number)
                .forEach(i -> expectResultViaOntologyRest(ECO_TERM_RESOURCE_FORMAT, id, name));
    }

    void expectFailureToGetNameForGoTermViaRest(int number, Function<Integer, String> toId) {
        IntStream.range(0, number)
                .mapToObj(i -> buildGOResource(toId.apply(i)))
                .forEach(r -> expectRestCall(r, withStatus(NOT_FOUND)));
    }

    void expectFailureToGetTaxonomyNameViaRest(String id, int number) {
        IntStream.range(0, number)
                .forEach(i -> expectRestCall(buildTaxResource(id), withStatus(NOT_FOUND)));
    }

    void expectTaxonIdHasNameViaRest(String id, String name) {
        String responseAsString = constructTaxonomyTermsResponseObject(name);
        expectRestCall(buildTaxResource(id), withSuccess(responseAsString, APPLICATION_JSON));
    }

    void expectFailureToGetEcoNameViaRest(String id, int number) {
        IntStream.range(0, number)
                .forEach(i -> expectRestCall(buildECOResource(id), withStatus(NOT_FOUND)));
    }

    private void expectResultViaOntologyRest(String resourceFormat, String id, String response) {
        this.expectRestCall(
                buildResource(resourceFormat, id), withSuccess(ontologyResponse(id, response), APPLICATION_JSON));
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

    private String buildGOResource(String... arguments) {
        return buildResource(GO_TERM_RESOURCE_FORMAT, arguments);
    }

    private String buildTaxResource(String... arguments) {
        return buildResource(TAXONOMY_RESOURCE_FORMAT, arguments);
    }

    private String buildECOResource(String... arguments) {
        return buildResource(ECO_TERM_RESOURCE_FORMAT, arguments);
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
        return format.formatted(args.toArray());
    }

    private <T> String getResponseAsString(T response) {
        try {
            return dtoMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem constructing mocked GO term REST response:", e);
        }
    }
}
