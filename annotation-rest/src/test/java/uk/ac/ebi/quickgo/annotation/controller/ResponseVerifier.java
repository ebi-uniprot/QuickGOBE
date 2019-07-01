package uk.ac.ebi.quickgo.annotation.controller;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Helper class that contains several methods to verify that the response coming back from the Annotation REST
 * service is working as expected.
 */
final class ResponseVerifier {
    static final String ASSIGNED_BY_FIELD = "assignedBy";
    static final String GENEPRODUCT_ID_FIELD = "geneProductId";
    static final String GO_EVIDENCE_FIELD = "goEvidence";
    static final String GO_ID_FIELD = "goId";
    static final String SLIMMED_ID_FIELD = "slimmedIds";
    static final String RESULTS = "results";
    static final String QUALIFIER_FIELD = "qualifier";
    static final String REFERENCE_FIELD = "reference";
    static final String TAXON_ID_FIELD = "taxonId";
    static final String DATE_FIELD = "date";

    private static final String ERROR_MESSAGE = "messages";
    private static final String RESULTS_CONTENT_BY_INDEX = RESULTS + "[%d].";

    private ResponseVerifier() {
    }

    static ResultMatcher valuesOccurInErrorMessage(String... values) {
        return jsonPath(ERROR_MESSAGE, contains(values));
    }

    static ResultMatcher valueStartingWithOccursInErrorMessage(String value) {
        return jsonPath(ERROR_MESSAGE, hasItem(startsWith(value)));
    }

    static ResultMatcher valuesOccurInField(String fieldName, String... values) {
        return jsonPath(RESULTS + ".*." + fieldName, containsInAnyOrder(values));
    }

    static <T> ResultMatcher valuesOccurInField(String fieldName, List<T> match) {
        return jsonPath(RESULTS + ".*." + fieldName, is(match));
    }

    static ResultMatcher valuesOccursInField(String fieldName, Integer... values) {
        return jsonPath(RESULTS + ".*." + fieldName, containsInAnyOrder(values));
    }

    static ResultMatcher fieldDoesNotExist(String fieldName) {
        return jsonPath(RESULTS + ".*." + fieldName).doesNotExist();
    }

    static ResultMatcher atLeastOneResultHasItem(String fieldName, String value) {
        return jsonPath(RESULTS + ".*." + fieldName, hasItem(value));
    }

    static ResultMatcher itemExistsExpectedTimes(String fieldName, String value, int expectedCount) {
        return jsonPath(RESULTS + ".*.[?(@." + fieldName + " == '" + value + "')]", hasSize(expectedCount));
    }

    static <T> ResultMatcher valueOccursInField(String fieldName, T value) {
        return jsonPath(RESULTS + ".*." + fieldName, hasItem(value));
    }

    static <T> ResultMatcher valueOccursInFieldList(String fieldName, T value) {
        return jsonPath(RESULTS + ".*." + fieldName + ".*", hasItem(value));
    }

    private static ResultMatcher fieldsInResultExist(int resultIndex) {
        String path = String.format(RESULTS_CONTENT_BY_INDEX, resultIndex);

        return new CompositeResultMatcher().addMatcher(jsonPath(path + "id").exists())
                                           .addMatcher(jsonPath(path + "geneProductId").exists())
                                           .addMatcher(jsonPath(path + "qualifier").exists())
                                           .addMatcher(jsonPath(path + "goId").exists())
                                           .addMatcher(jsonPath(path + "goEvidence").exists())
                                           .addMatcher(jsonPath(path + "evidenceCode").exists())
                                           .addMatcher(jsonPath(path + "reference").exists())
                                           .addMatcher(jsonPath(path + "withFrom").exists())
                                           .addMatcher(jsonPath(path + "taxonId").exists())
                                           .addMatcher(jsonPath(path + "assignedBy").exists())
                                           .addMatcher(jsonPath(path + "targetSets").exists())
                                           .addMatcher(jsonPath(path + "symbol").exists())
                                           .addMatcher(jsonPath(path + "date").exists())
                                           .addMatcher(jsonPath(path + "extensions").exists());
    }

    static ResultMatcher fieldsInAllResultsExist(int numResults) {
        CompositeResultMatcher matcher = new CompositeResultMatcher();

        for (int i = 0; i < numResults; i++) {
            matcher.addMatcher(fieldsInResultExist(i));
        }

        return matcher;
    }

    static ResultMatcher fieldInRowHasValue(String fieldName, int index, String value) {
        String path = String.format(RESULTS_CONTENT_BY_INDEX, index);
        return new CompositeResultMatcher().addMatcher(jsonPath(path + fieldName, is(value)));

    }

    static ResultMatcher resultsInPage(int numResults) {
        return jsonPath("$.results", hasSize(numResults));
    }

    static ResultMatcher pageInfoMatches(int pageNumber, int totalPages, int resultsPerPage) {
        return new CompositeResultMatcher().addMatcher(jsonPath("$.pageInfo").exists())
                                           .addMatcher(jsonPath("$.pageInfo.resultsPerPage").value(resultsPerPage))
                                           .addMatcher(jsonPath("$.pageInfo.total").value(totalPages))
                                           .addMatcher(jsonPath("$.pageInfo.current").value(pageNumber));
    }

    static ResultMatcher pageInfoExists() {
        return new CompositeResultMatcher().addMatcher(jsonPath("$.pageInfo").exists())
                                           .addMatcher(jsonPath("$.pageInfo.resultsPerPage").exists())
                                           .addMatcher(jsonPath("$.pageInfo.total").exists())
                                           .addMatcher(jsonPath("$.pageInfo.current").exists());
    }

    static ResultMatcher contentTypeToBeJson() {
        return content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    static ResultMatcher totalNumOfResults(int numResults) {
        return jsonPath("$.numberOfHits").value(numResults);
    }

    static ResultMatcher numOfResults(String path, int numResults) {
        return jsonPath(path).value(numResults);
    }

    static ResultMatcher expectedValues(String path, List<String> expectedValues) {
        return jsonPath(path).value(is(expectedValues));
    }

    static TimeoutResponseCreator withTimeout() {
        return new TimeoutResponseCreator();
    }

    private static class CompositeResultMatcher implements ResultMatcher {
        private final List<ResultMatcher> matchers = new ArrayList<>();

        @Override public void match(MvcResult result) throws Exception {
            matchers.forEach(matcher -> {
                try {
                    matcher.match(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        CompositeResultMatcher addMatcher(ResultMatcher matcher) {
            matchers.add(matcher);

            return this;
        }
    }

    public static class ResponseItem {
        private final Map<String, String> contents;

        private ResponseItem() {
            contents = new HashMap<>();
        }

        static ResponseItem responseItem() {
            return new ResponseItem();
        }

        public Map<String, String> build() {
            return Collections.unmodifiableMap(contents);
        }

        ResponseItem withAttribute(String key, String value) {
            contents.put(key, value);
            return this;
        }
    }

    static class TimeoutResponseCreator implements ResponseCreator {
        @Override public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
            throw new SocketTimeoutException("Socket timeout generated.");
        }
    }
}
