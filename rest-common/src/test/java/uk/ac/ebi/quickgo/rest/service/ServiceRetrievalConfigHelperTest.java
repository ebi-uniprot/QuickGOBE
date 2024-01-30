package uk.ac.ebi.quickgo.rest.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.rest.service.ServiceRetrievalConfigHelper.extractFieldMappings;

/**
 * Created 08/02/16
 * @author Edd
 */
class ServiceRetrievalConfigHelperTest {
    private static final String COMMA = ",";

    @Test
    void canConvertEmptyNameMappingProperty() {
        Map<String, String> mappings = extractFieldMappings("", COMMA);
        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(true));
    }

    @Test
    void canConvertSingleNameMappingPropertyWithNoSpaces() {
        Map<String, String> mappings = extractFieldMappings("a->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(1));

        assertThat(mappings, hasEntry("a", "b"));
    }

    @Test
    void canConvertSingleNameMappingPropertyWithSpaces() {
        Map<String, String> mappings = extractFieldMappings(" a->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(1));

        assertThat(mappings, hasEntry("a", "b"));
    }

    @Test
    void canConvertTwoNameMappingPropertiesWithNoSpaces() {
        Map<String, String> mappings = extractFieldMappings("a->b,x->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(2));

        assertThat(mappings, hasEntry("a", "b"));
        assertThat(mappings, hasEntry("x", "b"));
    }

    @Test
    void canConvertTwoNameMappingPropertiesWithSpaces() {
        Map<String, String> mappings = extractFieldMappings("a->b, x ->b", COMMA);

        assertThat(mappings, is(notNullValue()));
        assertThat(mappings.isEmpty(), is(false));
        assertThat(mappings.entrySet().size(), is(2));

        assertThat(mappings, hasEntry("a", "b"));
        assertThat(mappings, hasEntry("x", "b"));
    }

    @Test
    void expectsServiceConfigExceptionWhenFieldMappingsAreOneToManyNotManyToOne() {
        assertThrows(ServiceConfigException.class, () -> extractFieldMappings("a->b, a ->b", COMMA));
    }
}