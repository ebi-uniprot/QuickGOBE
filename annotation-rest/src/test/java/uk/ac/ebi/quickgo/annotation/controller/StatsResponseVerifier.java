package uk.ac.ebi.quickgo.annotation.controller;

import org.springframework.test.web.servlet.ResultMatcher;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Helper class containing several methods that verifies the response from the stats endpoint.
 *
 * @author Ricardo Antunes
 */
final class StatsResponseVerifier {
    private static final String GROUP_NAME_TAG = "groupName";
    private static final String TYPE_NAME_TAG = "type";
    private static final String KEY_NAME_TAG = "key";
    private static final String TOTAL_GROUP_HITS_TAG = "totalHits";

    private StatsResponseVerifier() {}

    static ResultMatcher totalHitsInGroup(String groupName, int hits) {
        String hitsInGroupText = "%s[?(@.%s=='%s')].%s";

        return jsonPath(
                format(hitsInGroupText, ResponseVerifier.RESULTS, GROUP_NAME_TAG, groupName, TOTAL_GROUP_HITS_TAG)
        ).value(hits);
    }

    /**
     * Checks that the value specified in the {@param value} is present in the {@param
     * type} within the given {@param group}.
     *
     * @param group the stats group to check in
     * @param type the stats type defined within in the {@param group}
     * @param value the value to check for
     * @return a {@link ResultMatcher} that checks for the given inputs
     */
    static ResultMatcher numericValueForGroup(String group, String type, String field, int value) {
        String valuesInTypeForGroup = "%s[?(@.%s=='%s')]..[?(@.%s=='%s')]..%s";
        return jsonPath(
                format(valuesInTypeForGroup,
                        ResponseVerifier.RESULTS,
                        GROUP_NAME_TAG, group,
                        TYPE_NAME_TAG, type,
                        field),
                containsInAnyOrder(value)
        );
    }

    /**
     * Checks that all of the keys defined in the {@param keys} array are present in the {@param
     * type} within the given {@param group}.
     *
     * @param group the stats group to check in
     * @param type the stats type defined within in the {@param group}
     * @param keys the keys to check for
     * @return a {@link ResultMatcher} that checks for the given inputs
     */
    static ResultMatcher keysInTypeWithinGroup(String group, String type, String[] keys) {
        String valuesInTypeForGroup = "%s[?(@.%s=='%s')]..[?(@.%s=='%s')]..%s";
        return jsonPath(
                format(valuesInTypeForGroup,
                        ResponseVerifier.RESULTS,
                        GROUP_NAME_TAG, group,
                        TYPE_NAME_TAG, type,
                        KEY_NAME_TAG),
                containsInAnyOrder(keys)
        );
    }
}
