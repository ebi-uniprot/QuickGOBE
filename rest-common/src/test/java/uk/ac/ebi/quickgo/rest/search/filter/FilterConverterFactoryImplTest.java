package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType;

/**
 * Tests the behaviour of the {@link FilterConverterFactoryImpl} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterConverterFactoryImplTest {
    private FilterConverterFactoryImpl factory;

    @Mock
    private RequestFilterConfigRetrieval configMock;

    @Before
    public void setUp() throws Exception {
        factory = new FilterConverterFactoryImpl(configMock);
    }

    @Test
    public void createsASimpleFilterConverterForFieldThatRequiresSimpleExecution() {
        String field = "field";

        Optional<RequestFilterConfig> fieldConfig = Optional.of(
                FilterUtil.createExecutionConfig(field, ExecutionType.SIMPLE)
        );

        when(configMock.getSignature(field)).thenReturn(fieldConfig);

        RequestFilterOld filter = new RequestFilterOld(field, "value");

        FilterConverter converter = factory.createConverter(filter);

        assertThat(converter, instanceOf(SimpleFilterConverter.class));
    }

    @Test
    public void createsAJoinFilterConverterForFieldThatRequiresJoinExecution() {
        String field = "field";

        Optional<RequestFilterConfig> fieldConfig = Optional.of(
                FilterUtil.createExecutionConfig(field, ExecutionType.JOIN)
        );

        when(configMock.getSignature(field)).thenReturn(fieldConfig);

        RequestFilterOld filter = new RequestFilterOld(field, "value");

        FilterConverter converter = factory.createConverter(filter);

        assertThat(converter, instanceOf(JoinFilterConverter.class));
    }
}