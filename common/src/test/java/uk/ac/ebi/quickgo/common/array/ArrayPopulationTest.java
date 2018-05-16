package uk.ac.ebi.quickgo.common.array;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.quickgo.common.array.ArrayPopulation.ensureArrayContains;
import static uk.ac.ebi.quickgo.common.array.ArrayPopulation.ensureArrayContainsCommonValue;

public class ArrayPopulationTest {

    // ------ ensureArrayContains

    @Test
    public void ensureArrayContainsWithoutTargetValue() {
        String[] array = new String[]{"bother", "tiresome", "bugbear"};

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(4));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("bother", "tiresome", "bugbear", "irritation"));
    }

    @Test
    public void ensureArrayContainsWithTargetValue() {
        String[] array = new String[]{"bother", "tiresome", "bugbear", "irritation"};

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(4));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("bother", "tiresome", "bugbear", "irritation"));
    }

    @Test
    public void ensureArrayContainsStartingWithNullArray() {
        String[] array = null;

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    public void ensureArrayContainsStartingWithEmptyArray() {
        String[] array = new String[0];

        String[] updated = ensureArrayContains(array, "irritation");

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    // ------ ensureArrayContainsCommonValue

    @Test
    public void updateFieldsWithCheckFieldsWithoutValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear"};
        String[] arrayTarget = new String[0];
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(0));
    }

    @Test
    public void updateFieldsWithCheckFieldsWithValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear", "irritation"};
        String[] arrayTarget = new String[0];
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    public void valueAlreadyExistsInCheckArrayAndTargetArray() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear", "irritation"};
        String[] arrayTarget = new String[]{"irritation"};
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    public void valueDoesNotExistInCheckArrayButDoesExistInTargetArray() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear"};
        String[] arrayTarget = new String[]{"irritation"};
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }

    @Test
    public void checkArrayIsNull() {
        String[] arrayToCheck = null;
        String[] arrayTarget = new String[0];
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(0));
    }

    @Test
    public void targetArrayIsNullAndCheckArrayDoesNotContainValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear"};
        String[] arrayTarget = null;
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(0));
    }

    @Test
    public void targetArrayIsNullAndCheckArrayDoesContainValue() {
        String[] arrayToCheck = new String[]{"bother", "tiresome", "bugbear", "irritation"};
        String[] arrayTarget = null;
        String value = "irritation";

        String[] updated = ensureArrayContainsCommonValue(arrayToCheck, arrayTarget, value);

        assertThat(updated.length, is(1));
        List<String> updatedList = Arrays.asList(updated);
        assertThat(updatedList, contains("irritation"));
    }
}