package uk.ac.ebi.quickgo.common.array;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.common.array.ArrayPopulation.ensureArrayContains;
import static uk.ac.ebi.quickgo.common.array.ArrayPopulation.ensureArrayContainsCommonValue;

class ArrayPopulationTest {

    // ------ ensureArrayContains

    @Test
    void ensureArrayContainsWithoutTargetValue() {
        String[] array = new String[]{"bother", "tiresome", "bugbear"};

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(4));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("bother", "tiresome", "bugbear", "irritation"));
    }

    @Test
    void ensureArrayContainsWithTargetValue() {
        String[] array = new String[]{"bother", "tiresome", "bugbear", "irritation"};

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(4));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("bother", "tiresome", "bugbear", "irritation"));
    }

    @Test
    void ensureArrayContainsStartingWithNullArray() {
        String[] array = null;

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    void ensureArrayContainsStartingWithEmptyArray() {
        String[] array = new String[0];

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    // ------ ensureArrayContainsCommonValue

    @Test
    void updateFieldsWithCheckFieldsWithoutValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear"};
        String[] arrayTarget = new String[0];
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(0));
    }

    @Test
    void updateFieldsWithCheckFieldsWithValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear", "irritation"};
        String[] arrayTarget = new String[0];
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    void valueAlreadyExistsInCheckArrayAndTargetArray() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear", "irritation"};
        String[] arrayTarget = new String[]{"irritation"};
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    void valueDoesNotExistInCheckArrayButDoesExistInTargetArray() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear"};
        String[] arrayTarget = new String[]{"irritation"};
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    void checkArrayIsNull() {
        String[] arrayToCheck = null;
        String[] arrayTarget = new String[0];
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(0));
    }

    @Test
    void targetArrayIsNullAndCheckArrayDoesNotContainValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear"};
        String[] arrayTarget = null;
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(0));
    }

    @Test
    void targetArrayIsNullAndCheckArrayDoesContainValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear", "irritation"};
        String[] arrayTarget = null;
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }
}