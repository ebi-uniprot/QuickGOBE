package uk.ac.ebi.quickgo.common.service;

import java.util.Map;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.ac.ebi.quickgo.common.service.ServiceRetrievalConfigHelper.extractFieldMappings;

/**
 * Created 08/02/16
 * @author Edd
 */
public class ServiceRetrievalConfigHelperTest {
    private static final String COMMA = ",";

    @Test
    public void canConvertEmptyNameMappingProperty() {
        Map<String, String> mappings = extractFieldMappings("", COMMA);
        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(true));
    }

    @Test
    public void canConvertSingleNameMappingPropertyWithNoSpaces() {
        Map<String, String> mappings = extractFieldMappings("a->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(1));

        assertThat(mappings, hasEntry("a", "b"));
    }

    @Test
    public void canConvertSingleNameMappingPropertyWithSpaces() {
        Map<String, String> mappings = extractFieldMappings(" a->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(1));

        assertThat(mappings, hasEntry("a", "b"));
    }

    @Test
    public void canConvertTwoNameMappingPropertiesWithNoSpaces() {
        Map<String, String> mappings = extractFieldMappings("a->b,x->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(2));

        assertThat(mappings, hasEntry("a", "b"));
        assertThat(mappings, hasEntry("x", "b"));
    }

    @Test
    public void canConvertTwoNameMappingPropertiesWithSpaces() {
        Map<String, String> mappings = extractFieldMappings("a->b, x ->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(2));

        assertThat(mappings, hasEntry("a", "b"));
        assertThat(mappings, hasEntry("x", "b"));
    }

    @Test(expected = ServiceConfigException.class)
    public void expectsServiceConfigExceptionWhenFieldMappingsAreOneToManyNotManyToOne() {
        extractFieldMappings("a->b, a ->b", COMMA);
    }
}