package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.comm.FilterContext;

import java.util.Optional;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 19/08/16
 * @author Edd
 */
public class ConvertedFilterTest {
    @Test
    public void canCreateWithOnlyValue() {
        String value = "value";

        ConvertedFilter<String> convertedFilter = new ConvertedFilter<>(value);

        assertThat(convertedFilter, is(notNullValue()));
        assertThat(convertedFilter.getConvertedValue(), is(value));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    public void canCreateWithValueAndContext() {
        String value = "value";
        FilterContext filterContext = new FilterContext();

        ConvertedFilter<String> convertedFilter = new ConvertedFilter<>(value, filterContext);

        assertThat(convertedFilter, is(notNullValue()));
        assertThat(convertedFilter.getConvertedValue(), is(value));
        assertThat(convertedFilter.getFilterContext(), is(not(Optional.empty())));
        assertThat(convertedFilter.getFilterContext().get(), is(filterContext));

    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateWithNullValue() {
        new ConvertedFilter<>(null);
    }

}