package uk.ac.ebi.quickgo.index.common.datafile;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.*;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatProperty;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatStrings;

/**
 * Created 28/04/16
 * @author Edd
 */
class GOADataFileParsingHelperTest {
    private static final String INTER_VALUE_DELIMITER = "|";
    private static final String INTER_VALUE_DELIMITER_REGEX = "\\|";
    public static final String INTRA_VALUE_DELIMITER = "=";

    @Test
    void nullInterValueDelimiterThrowsException() {
        Throwable exception = assertThrows(AssertionError.class, () -> {
            String propsText = "";

            convertLinePropertiesToMap(propsText, null, INTRA_VALUE_DELIMITER);
        });
        assertTrue(exception.getMessage().contains("InterValueDelimiter cannot be null"));
    }

    @Test
    void nullIntraValueDelimiterThrowsException() {
        Throwable exception = assertThrows(AssertionError.class, () -> {
            String propsText = "";

            convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER, null);
        });
        assertTrue(exception.getMessage().contains("IntraValueDelimiter cannot be null"));
    }

    @Test
    void nullPropertiesTextReturnsEmptyMap() {
        Map<String, String>
                propsMap = convertLinePropertiesToMap(null, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.isEmpty(), is(true));
    }

    @Test
    void emptyPropertiesTextReturnsEmptyMap() {
        String propsText = "";

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.isEmpty(), is(true));
    }

    @Test
    void singlePropertyWithNoValueReturnsMapWithSingleEntryWithKeyAndNoValue() {
        String propKey = "key";
        String propsText = propKey;

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.size(), is(1));
        assertThat(propsMap, hasKey(propKey));
        assertThat(propsMap.get(propKey), emptyString());
    }

    @Test
    void singlePropertyWithValueReturnsMapWithSingleEntry() {
        String propKey = "key";
        String propValue = "value";

        String propsText = concatProperty(propKey, propValue, INTRA_VALUE_DELIMITER);

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.size(), is(1));
        assertThat(propsMap, hasEntry(propKey, propValue));
    }

    @Test
    void twoPropertiesReturnMapWithTwoEntries() {
        String propKey1 = "key1";
        String propValue1 = "value1";
        String concatProp1 = concatProperty(propKey1, propValue1, INTRA_VALUE_DELIMITER);

        String propKey2 = "key2";
        String propValue2 = "value2";
        String concatProp2 = concatProperty(propKey2, propValue2, INTRA_VALUE_DELIMITER);

        String propsText = concatStrings(Arrays.asList(concatProp1, concatProp2), INTER_VALUE_DELIMITER);

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.size(), is(2));
        assertThat(propsMap, hasEntry(propKey1, propValue1));
        assertThat(propsMap, hasEntry(propKey2, propValue2));
    }

    @Test
    void splittingOnNullDelimiterCausesException() {
        assertThrows(IllegalArgumentException.class, () -> splitValue("some value", null));
    }

    @Test
    void splittingIntegerListOnNullDelimiterCausesException() {
        assertThrows(IllegalArgumentException.class, () -> splitValueToIntegerList("1,3", null));
    }

    @Test
    void splittingNullValueReturnsEmptyStringArray() {
        String[] splitValues = splitValue(null, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.length, is(0));
    }

    @Test
    void splittingEmptyValueReturnsEmptyStringArray() {
        String[] splitValues = splitValue("", "whatever");
        assertThat(splitValues.length, is(0));
    }

    @Test
    void splittingUnsplittableValueReturnsStringArrayOfSizeOne() {
        String value = "thisCannotBeSplit";
        String[] splitValues = splitValue(value, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.length, is(1));
        assertThat(splitValues[0], is(value));
    }

    @Test
    void splittingSplittableValueReturnsCorrectlySplitStringArray() {
        String[] splitValues = splitValue("a-b-c", "-");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.length, is(3));
        assertThat(splitValues, arrayContainingInAnyOrder("a", "b", "c"));
    }

    @Test
    void splittingNullValueReturnsEmptyIntegerList() {
        List<Integer> splitValues = splitValueToIntegerList(null, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues, is(empty()));
    }

    @Test
    void splittingEmptyValueReturnsEmptyIntegerList() {
        List<Integer> splitValues = splitValueToIntegerList("", "whatever");
        assertThat(splitValues, is(empty()));
    }


    @Test
    void splittingUnsplittableValueReturnsIntegerListOfSizeOne() {
        String value = "12345";
        List<Integer> splitValues = splitValueToIntegerList(value, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.size(), is(1));
        assertThat(splitValues.get(0), is(Integer.valueOf(value)));
    }

    @Test
    void splittingSplittableValueReturnsCorrectlySplitIntegerList() {
        List<Integer> splitValues = splitValueToIntegerList("1234-5678-9", "-");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.size(), is(3));
        assertThat(splitValues, contains(1234, 5678, 9));
    }
}