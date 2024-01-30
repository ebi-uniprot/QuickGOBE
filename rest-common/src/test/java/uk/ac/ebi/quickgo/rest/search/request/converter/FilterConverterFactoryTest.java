package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfigRetrieval;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestOperations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType.JOIN;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType.SIMPLE;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.FROM_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.FROM_TABLE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.TO_ATTRIBUTE_NAME;
import static uk.ac.ebi.quickgo.rest.search.request.converter.JoinFilterConverter.TO_TABLE_NAME;

/**
 * Created 06/06/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class FilterConverterFactoryTest {
    @Mock
    private FilterConfigRetrieval filterConfigRetrievalMock;
    @Mock
    private FilterConfig filterConfigMock;
    @Mock
    private RestOperations restOperationsMock;

    private FilterConverterFactory converter;

    @BeforeEach
    void setUp() {
        this.converter = new FilterConverterFactory(filterConfigRetrievalMock, restOperationsMock);
    }

    @Test
    void nullRestOperationsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FilterConverterFactory(filterConfigRetrievalMock, null));
    }

    @Test
    void nullConfigRetrievalThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FilterConverterFactory(null, restOperationsMock));
    }

    // simple request -> QuickGOQuery tests
    @Test
    void createsQueryForCorrectlyConfiguredSimpleRequest() {
        String value = "valueX";
        String field = "fieldX";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();

        when(filterConfigRetrievalMock.getBySignature(request.getSignature()))
                .thenReturn(Optional.of(filterConfigMock));
        when(filterConfigMock.getExecution()).thenReturn(SIMPLE);

        QuickGOQuery resultingQuery = converter.convert(request).getConvertedValue();
        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(field, value);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    void missingSignatureInConfigForSimpleRequestCausesException() {
        String value = "valueX";
        String field = "fieldX";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();

        when(filterConfigRetrievalMock.getBySignature(request.getSignature()))
          .thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> converter.convert(request));
    }

    // join request -> QuickGOQuery tests
    @Test
    void createsQueryForCorrectlyConfiguredJoinRequest() {
        String value = "valueX";
        String field = "fieldX";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();

        when(filterConfigRetrievalMock.getBySignature(request.getSignature()))
                .thenReturn(Optional.of(filterConfigMock));
        when(filterConfigMock.getExecution()).thenReturn(JOIN);

        String fromTable = "from table";
        String fromAttribute = "from attribute";
        String toTable = "to table";
        String toAttribute = "to attribute";
        Map<String, String> configPropertiesMap = new HashMap<>();
        configPropertiesMap.put(FROM_TABLE_NAME, fromTable);
        configPropertiesMap.put(FROM_ATTRIBUTE_NAME, fromAttribute);
        configPropertiesMap.put(TO_TABLE_NAME, toTable);
        configPropertiesMap.put(TO_ATTRIBUTE_NAME, toAttribute);
        when(filterConfigMock.getProperties()).thenReturn(configPropertiesMap);

        QuickGOQuery resultingQuery = converter.convert(request).getConvertedValue();
        QuickGOQuery expectedQuery = QuickGOQuery.createJoinQueryWithFilter(
                fromTable,
                fromAttribute,
                toTable,
                toAttribute,
                QuickGOQuery.createQuery(field, value)
        );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    void missingSignatureInConfigForJoinRequestCausesException() {
        String value = "valueX";
        String field = "fieldX";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, value).build();

        when(filterConfigRetrievalMock.getBySignature(request.getSignature()))
          .thenReturn(Optional.empty());

        setConfigPropertiesMap();
        assertThrows(IllegalStateException.class, () -> converter.convert(request));
    }

    private void setConfigPropertiesMap() {
        String fromTable = "from table";
        String fromAttribute = "from attribute";
        String toTable = "to table";
        String toAttribute = "to attribute";
        Map<String, String> configPropertiesMap = new HashMap<>();
        configPropertiesMap.put(FROM_TABLE_NAME, fromTable);
        configPropertiesMap.put(FROM_ATTRIBUTE_NAME, fromAttribute);
        configPropertiesMap.put(TO_TABLE_NAME, toTable);
        configPropertiesMap.put(TO_ATTRIBUTE_NAME, toAttribute);
    }
}