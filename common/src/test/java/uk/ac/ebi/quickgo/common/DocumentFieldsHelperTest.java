package uk.ac.ebi.quickgo.common;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.common.DocumentFieldsHelper.storeAndGet;

/**
 * Created 17/08/16
 * @author Edd
 */
public class DocumentFieldsHelperTest {
    @Test
    public void canStoreAndGetValue() {
        Set<String> values = new HashSet<>();
        String value = "value";
        assertThat(storeAndGet(values, value), is(value));
        assertThat(values, contains(value));
    }

    @Test(expected = IllegalArgumentException.class)
    public void storeAndGetValueForNullValuesCausesException() {
        Set<String> values = null;
        String value = "value";
        storeAndGet(values, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void storeAndGetValueForNullValueCausesException() {
        Set<String> values = new HashSet<>();
        String value = null;
        storeAndGet(values, value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void storeAndGetValueForEmptyValueCausesException() {
        Set<String> values = new HashSet<>();
        String value = "";
        storeAndGet(values, value);
    }
}