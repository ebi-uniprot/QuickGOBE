package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.SimpleRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinRequestConverter.FROM_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinRequestConverter.FROM_TABLE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinRequestConverter.TO_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinRequestConverter.TO_TABLE_NAME;

/**
 * Created 06/06/16
 * @author Edd
 */
public class JoinRequestConverterTest {

    private static final String FROM_TABLE_VALUE = "FROM_TABLE";
    private static final String FROM_ATTRIBUTE_VALUE = "FROM_ATTRIBUTE";
    private static final String TO_TABLE_VALUE = "TO_TABLE";
    private static final String TO_ATTRIBUTE_VALUE = "TO_ATTRIBUTE";

    private RequestConfig requestConfig;
    private JoinRequestConverter converter;
    private Map<String, String> configPropertiesMap;

    @Before
    public void setUp() {
        this.requestConfig = new RequestConfig();
        this.configPropertiesMap = new HashMap<>();
    }

    private void initialiseConverter() {
        requestConfig.setProperties(configPropertiesMap);
        this.converter = new JoinRequestConverter(this.requestConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestConfigForConverterThrowsException() {
        new JoinRequestConverter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestForConverterThrowsException() {
        initialiseConverter();
        converter.apply(null);
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

        SimpleRequest request = new SimpleRequest(field, singletonList(value));
        QuickGOQuery resultingQuery = converter.apply(request);
        QuickGOQuery expectedQuery =
                QuickGOQuery.createJoinQueryWithFilter(
                        FROM_TABLE_VALUE,
                        FROM_ATTRIBUTE_VALUE,
                        TO_TABLE_VALUE,
                        TO_ATTRIBUTE_VALUE,
                        new SimpleRequestConverter(requestConfig).apply(request));

        assertThat(resultingQuery, is(expectedQuery));
    }

    private void addConfigProperty(String name, String value) {
        this.configPropertiesMap.put(name, value);
    }
}