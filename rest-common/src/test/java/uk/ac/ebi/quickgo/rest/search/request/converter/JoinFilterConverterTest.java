package uk.ac.ebi.quickgo.rest.search.request.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.FROM_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.FROM_TABLE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.TO_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.TO_TABLE_NAME;

/**
 * Created 06/06/16
 * @author Edd
 */
class JoinFilterConverterTest {

    private static final String FROM_TABLE_VALUE = "FROM_TABLE";
    private static final String FROM_ATTRIBUTE_VALUE = "FROM_ATTRIBUTE";
    private static final String TO_TABLE_VALUE = "TO_TABLE";
    private static final String TO_ATTRIBUTE_VALUE = "TO_ATTRIBUTE";

    private FilterConfig filterConfig;
    private JoinFilterConverter converter;
    private Map<String, String> configPropertiesMap;

    @BeforeEach
    void setUp() {
        this.filterConfig = new FilterConfig();
        this.configPropertiesMap = new HashMap<>();
    }

    private void initialiseConverter() {
        filterConfig.setProperties(configPropertiesMap);
        this.converter = new JoinFilterConverter(this.filterConfig);
    }

    @Test
    void nullRequestConfigForConverterThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new JoinFilterConverter(null));
    }

    @Test
    void nullRequestForConverterThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            initialiseConverter();
            converter.transform(null);
        });
    }

    @Test
    void missingFromTablePropertyForConverterCausesException() {
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);
        assertThrows(IllegalArgumentException.class, () -> initialiseConverter());
    }

    @Test
    void missingFromAttributePropertyForConverterCausesException() {
        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);
        assertThrows(IllegalArgumentException.class, () -> initialiseConverter());
    }

    @Test
    void missingToTablePropertyForConverterCausesException() {
        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);
        assertThrows(IllegalArgumentException.class, () -> initialiseConverter());
    }

    @Test
    void missingToAttributePropertyForConverterCausesException() {
        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        assertThrows(IllegalArgumentException.class, () -> initialiseConverter());
    }

    @Test
    void convertsRequestIntoJoinQuery() {
        String field = "fieldX";
        String value = "valueX";

        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);
        initialiseConverter();

        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
                QuickGOQuery.createJoinQueryWithFilter(
                        FROM_TABLE_VALUE,
                        FROM_ATTRIBUTE_VALUE,
                        TO_TABLE_VALUE,
                        TO_ATTRIBUTE_VALUE,
                        new SimpleFilterConverter(filterConfig).transform(request).getConvertedValue());

        assertThat(resultingQuery, is(expectedQuery));
    }

    private void addConfigProperty(String name, String value) {
        this.configPropertiesMap.put(name, value);
    }
}