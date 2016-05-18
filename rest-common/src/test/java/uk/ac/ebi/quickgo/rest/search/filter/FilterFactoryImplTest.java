package uk.ac.ebi.quickgo.rest.search.filter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Tests the behaviour of the {@link FilterFactoryImpl} class.
 */
public class FilterFactoryImplTest {
    private FilterFactoryImpl factory;

    @Before
    public void setUp() throws Exception {
        factory = new FilterFactoryImpl();
    }

    @Test
    public void createsASimpleFilterForFieldThatRequiresSimpleProcessing() {
        String field = "field";
        String value = "value";

        RequestFilter filter = factory.createFilter(field, value);

        assertThat(filter, instanceOf(SimpleFilter.class));
    }
}
