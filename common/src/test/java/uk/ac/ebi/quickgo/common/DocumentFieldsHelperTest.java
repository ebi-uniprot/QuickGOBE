package uk.ac.ebi.quickgo.common;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.common.DocumentFieldsHelper.storeAndGet;

/**
 * Created 17/08/16
 * @author Edd
 */
class DocumentFieldsHelperTest {
    @Test
    void canStoreAndGetValue() {
        Set<String> values = new HashSet<>();
        String value = "value";
        assertThat(storeAndGet(values, value), is(value));
        assertThat(values, contains(value));
    }

    @Test
    void storeAndGetValueForNullValuesCausesException() {
        Set<String> values = null;
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> storeAndGet(values, value));
    }

    @Test
    void storeAndGetValueForNullValueCausesException() {
        Set<String> values = new HashSet<>();
        String value = null;
        assertThrows(IllegalArgumentException.class, () -> storeAndGet(values, value));
    }

    @Test
    void storeAndGetValueForEmptyValueCausesException() {
        Set<String> values = new HashSet<>();
        String value = "";
        assertThrows(IllegalArgumentException.class, () -> storeAndGet(values, value));
    }
}