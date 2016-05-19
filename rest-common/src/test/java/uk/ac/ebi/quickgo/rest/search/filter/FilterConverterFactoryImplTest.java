package uk.ac.ebi.quickgo.rest.search.filter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Tests the behaviour of the {@link FilterConverterFactoryImpl} class.
 */
public class FilterConverterFactoryImplTest {
    private FilterConverterFactoryImpl factory;

    @Before
    public void setUp() throws Exception {
        factory = new FilterConverterFactoryImpl();
    }

    @Test
    public void createsASimpleFilterForFieldThatRequiresSimpleProcessing() {
        RequestFilter filter = new RequestFilter("field", "value");

        FilterConverter converter = factory.createConverter(filter);

        assertThat(converter, instanceOf(SimpleFilterConverter.class));
    }
}
