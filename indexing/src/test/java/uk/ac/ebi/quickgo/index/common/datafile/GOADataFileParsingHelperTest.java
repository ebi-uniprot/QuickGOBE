package uk.ac.ebi.quickgo.index.common.datafile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.*;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatProperty;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatStrings;

/**
 * Created 28/04/16
 * @author Edd
 */
public class GOADataFileParsingHelperTest {
    private static final String INTER_VALUE_DELIMITER = "|";
    private static final String INTER_VALUE_DELIMITER_REGEX = "\\|";
    public static final String INTRA_VALUE_DELIMITER = "=";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullInterValueDelimiterThrowsException() throws Exception {
        String propsText = "";

        thrown.expect(AssertionError.class);
        thrown.expectMessage("InterValueDelimiter cannot be null");

        convertLinePropertiesToMap(propsText, null, INTRA_VALUE_DELIMITER);
    }

    @Test
    public void nullIntraValueDelimiterThrowsException() throws Exception {
        String propsText = "";

        thrown.expect(AssertionError.class);
        thrown.expectMessage("IntraValueDelimiter cannot be null");

        convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER, null);
    }

    @Test
    public void nullPropertiesTextReturnsEmptyMap() throws Exception {
        Map<String, String>
                propsMap = convertLinePropertiesToMap(null, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.isEmpty(), is(true));
    }

    @Test
    public void emptyPropertiesTextReturnsEmptyMap() throws Exception {
        String propsText = "";

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.isEmpty(), is(true));
    }

    @Test
    public void singlePropertyWithNoValueReturnsMapWithSingleEntryWithKeyAndNoValue() throws Exception {
        String propKey = "key";
        String propsText = propKey;

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.size(), is(1));
        assertThat(propsMap, hasKey(propKey));
        assertThat(propsMap.get(propKey), isEmptyString());
    }

    @Test
    public void singlePropertyWithValueReturnsMapWithSingleEntry() throws Exception {
        String propKey = "key";
        String propValue = "value";

        String propsText = concatProperty(propKey, propValue, INTRA_VALUE_DELIMITER);

        Map<String, String> propsMap = convertLinePropertiesToMap(propsText, INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        assertThat(propsMap.size(), is(1));
        assertThat(propsMap, hasEntry(propKey, propValue));
    }

    @Test
    public void twoPropertiesReturnMapWithTwoEntries() throws Exception {
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

    @Test(expected = IllegalArgumentException.class)
    public void splittingOnNullDelimiterCausesException() {
        splitValue("some value", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void splittingIntegerListOnNullDelimiterCausesException() {
        splitValueToIntegerList("1,3", null);
    }

    @Test
    public void splittingNullValueReturnsEmptyStringArray() {
        String[] splitValues = splitValue(null, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.length, is(0));
    }

    @Test
    public void splittingUnsplittableValueReturnsStringArrayOfSizeOne() {
        String value = "thisCannotBeSplit";
        String[] splitValues = splitValue(value, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.length, is(1));
        assertThat(splitValues[0], is(value));
    }

    @Test
    public void splittingSplittableValueReturnsCorrectlySplitStringArray() {
        String[] splitValues = splitValue("a-b-c", "-");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.length, is(3));
        assertThat(splitValues, arrayContainingInAnyOrder("a", "b", "c"));
    }

    @Test
    public void splittingNullValueReturnsEmptyIntegerList() {
        List<Integer> splitValues = splitValueToIntegerList(null, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues, is(empty()));
    }

    @Test
    public void splittingUnsplittableValueReturnsIntegerListOfSizeOne() {
        String value = "12345";
        List<Integer> splitValues = splitValueToIntegerList(value, "whatever");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.size(), is(1));
        assertThat(splitValues.get(0), is(Integer.valueOf(value)));
    }

    @Test
    public void splittingSplittableValueReturnsCorrectlySplitIntegerList() {
        List<Integer> splitValues = splitValueToIntegerList("1234-5678-9", "-");
        assertThat(splitValues, is(notNullValue()));
        assertThat(splitValues.size(), is(3));
        assertThat(splitValues, contains(1234, 5678, 9));
    }
}