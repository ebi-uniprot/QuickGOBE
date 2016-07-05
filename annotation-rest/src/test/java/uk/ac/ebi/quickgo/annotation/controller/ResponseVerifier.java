package uk.ac.ebi.quickgo.annotation.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Helper class that contains several methods to verify that the response coming back from the Annotation REST
 * service is working as expected.
 */
final class ResponseVerifier {
    public static final String GENEPRODUCT_ID_FIELD = "geneProductId";
    public static final String GO_EVIDENCE_FIELD = "goEvidence";
    public static final String QUALIFIER = "qualifier";

    private static final String RESULTS = "results";
    private static final String RESULTS_CONTENT_BY_INDEX = RESULTS + "[%d].";

    private ResponseVerifier() {}

    static ResultMatcher valuesOccurInField(String fieldName, String... values) {
        return jsonPath(RESULTS + ".*." + fieldName, contains(values));
    }

    static ResultMatcher atLeastOneResultHasItem(String fieldName, String value) {
        return jsonPath(RESULTS + ".*." + fieldName, hasItem(value));
    }

    static ResultMatcher itemExistsExpectedTimes(String fieldName, String value, int expectedCount) {
        return jsonPath(RESULTS + ".*.[?(@." + fieldName + " == " + value + ")]", hasSize(expectedCount));
    }

    static ResultMatcher valueOccurInField(String fieldName, String value) {
        return jsonPath(RESULTS + ".*." + fieldName, hasItem(value));
    }


    static ResultMatcher fieldsInResultExist(int resultIndex) throws Exception {
        String path = String.format(RESULTS_CONTENT_BY_INDEX, resultIndex);

        return new CompositeResultMatcher().addMatcher(jsonPath(path + "id").exists())
                .addMatcher(jsonPath(path + "id").exists())
                .addMatcher(jsonPath(path + "qualifier").exists())
                .addMatcher(jsonPath(path + "goId").exists())
                .addMatcher(jsonPath(path + "goEvidence").exists())
                .addMatcher(jsonPath(path + "ecoId").exists())
                .addMatcher(jsonPath(path + "reference").exists())
                .addMatcher(jsonPath(path + "withFrom").exists())
                .addMatcher(jsonPath(path + "taxonId").exists())
                .addMatcher(jsonPath(path + "assignedBy").exists())
                .addMatcher(jsonPath(path + "extensions").exists());
    }

    static ResultMatcher fieldsInAllResultsExist(int numResults) throws Exception {
        CompositeResultMatcher matcher = new CompositeResultMatcher();

        for (int i = 0; i < numResults; i++) {
            matcher.addMatcher(fieldsInResultExist(i));
        }

        return matcher;
    }

    static ResultMatcher resultsInPage(int numResults) throws Exception {
        return jsonPath("$.results", hasSize(numResults));
    }

    static ResultMatcher pageInfoMatches(int pageNumber, int totalPages, int resultsPerPage) {
        return new CompositeResultMatcher().addMatcher(jsonPath("$.pageInfo").exists())
                .addMatcher(jsonPath("$.pageInfo.resultsPerPage").value(resultsPerPage))
                .addMatcher(jsonPath("$.pageInfo.total").value(totalPages))
                .addMatcher(jsonPath("$.pageInfo.current").value(pageNumber));
    }

    static ResultMatcher pageInfoExists() throws Exception {
        return new CompositeResultMatcher().addMatcher(jsonPath("$.pageInfo").exists())
                .addMatcher(jsonPath("$.pageInfo.resultsPerPage").exists())
                .addMatcher(jsonPath("$.pageInfo.total").exists())
                .addMatcher(jsonPath("$.pageInfo.current").exists());
    }

    static ResultMatcher contentTypeToBeJson() throws Exception {
        return content().contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    static ResultMatcher totalNumOfResults(int numResults) {
        return jsonPath("$.numberOfHits").value(numResults);
    }

    private static class CompositeResultMatcher implements ResultMatcher {
        private final List<ResultMatcher> matchers = new ArrayList<>();

        @Override public void match(MvcResult result) throws Exception {
            matchers.stream().forEach(matcher -> {
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
}
