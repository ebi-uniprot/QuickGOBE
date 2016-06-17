package uk.ac.ebi.quickgo.rest.search.request.config;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;

/**
 * Created 17/06/16
 * @author Edd
 */
public class RequestConfigTest {

    @Test(expected = IllegalArgumentException.class)
    public void settingSignatureRequiresNonNullValue() {
        RequestConfig config = new RequestConfig();
        config.setSignature(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void settingSignatureRequiresNonEmptyValue() {
        RequestConfig config = new RequestConfig();
        config.setSignature("  ");
    }

    @Test
    public void settingSignatureWithSingleValue() {
        RequestConfig config = new RequestConfig();
        String value = "value1";
        config.setSignature(value);
        assertThat(config.getSignature(), is(asSet(value)));
    }

    @Test
    public void settingSignatureWithMultipleValues() {
        RequestConfig config = new RequestConfig();
        String value1 = "value1";
        String value2 = "value1";
        String value3 = "value1";
        String signature = value1 + "," + value2 + "," + value3;

        config.setSignature(signature);
        assertThat(config.getSignature(), is(asSet(value1, value2, value3)));
    }

}