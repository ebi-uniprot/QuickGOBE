package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.FROM_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.FROM_TABLE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.TO_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.TO_TABLE_NAME;

/**
 * Created 06/06/16
 * @author Edd
 */
public class JoinFilterConverterTest {

    private static final String FROM_TABLE_VALUE = "FROM_TABLE";
    private static final String FROM_ATTRIBUTE_VALUE = "FROM_ATTRIBUTE";
    private static final String TO_TABLE_VALUE = "TO_TABLE";
    private static final String TO_ATTRIBUTE_VALUE = "TO_ATTRIBUTE";

    private RequestConfig requestConfig;
    private JoinFilterConverter converter;
    private Map<String, String> configPropertiesMap;

    @Before
    public void setUp() {
        this.requestConfig = new RequestConfig();
        this.configPropertiesMap = new HashMap<>();
    }

    private void initialiseConverter() {
        requestConfig.setProperties(configPropertiesMap);
        this.converter = new JoinFilterConverter(this.requestConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestConfigForConverterThrowsException() {
        new JoinFilterConverter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestForConverterThrowsException() {
        initialiseConverter();
        converter.transform(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingFromTablePropertyForConverterCausesException() {
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);

        initialiseConverter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingFromAttributePropertyForConverterCausesException() {
        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);

        initialiseConverter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingToTablePropertyForConverterCausesException() {
        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);

        initialiseConverter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingToAttributePropertyForConverterCausesException() {
        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);

        initialiseConverter();
    }

    @Test
    public void convertsRequestIntoJoinQuery() {
        String field = "fieldX";
        String value = "valueX";

        addConfigProperty(FROM_TABLE_NAME, FROM_TABLE_VALUE);
        addConfigProperty(FROM_ATTRIBUTE_NAME, FROM_ATTRIBUTE_VALUE);
        addConfigProperty(TO_TABLE_NAME, TO_TABLE_VALUE);
        addConfigProperty(TO_ATTRIBUTE_NAME, TO_ATTRIBUTE_VALUE);
        initialiseConverter();

        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();
        QuickGOQuery resultingQuery = converter.transform(request);
        QuickGOQuery expectedQuery =
                QuickGOQuery.createJoinQueryWithFilter(
                        FROM_TABLE_VALUE,
                        FROM_ATTRIBUTE_VALUE,
                        TO_TABLE_VALUE,
                        TO_ATTRIBUTE_VALUE,
                        new SimpleFilterConverter(requestConfig).transform(request));

        assertThat(resultingQuery, is(expectedQuery));
    }

    private void addConfigProperty(String name, String value) {
        this.configPropertiesMap.put(name, value);
    }
}