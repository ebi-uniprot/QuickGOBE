package uk.ac.ebi.quickgo.rest.search.request.config;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;

/**
 * Created 17/06/16
 * @author Edd
 */
class FilterConfigTest {

    @Test
    void settingSignatureRequiresNonNullValue() {
        FilterConfig config = new FilterConfig();
        assertThrows(IllegalArgumentException.class, () -> config.setSignature(null));
    }

    @Test
    void settingSignatureRequiresNonEmptyValue() {
        FilterConfig config = new FilterConfig();
        assertThrows(IllegalArgumentException.class, () -> config.setSignature("  "));
    }

    @Test
    void settingSignatureWithSingleValue() {
        FilterConfig config = new FilterConfig();
        String value = "value1";
        config.setSignature(value);
        assertThat(config.getSignature(), is(asSet(value)));
    }

    @Test
    void settingSignatureWithMultipleValues() {
        FilterConfig config = new FilterConfig();
        String value1 = "value1";
        String value2 = "value1";
        String value3 = "value1";
        String signature = value1 + "," + value2 + "," + value3;

        config.setSignature(signature);
        assertThat(config.getSignature(), is(asSet(value1, value2, value3)));
    }

}