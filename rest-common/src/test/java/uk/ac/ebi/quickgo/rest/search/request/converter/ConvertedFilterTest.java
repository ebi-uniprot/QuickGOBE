package uk.ac.ebi.quickgo.rest.search.request.converter;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 19/08/16
 * @author Edd
 */
class ConvertedFilterTest {
    @Test
    void canCreateWithOnlyValue() {
        String value = "value";

        ConvertedFilter<String> convertedFilter = new ConvertedFilter<>(value);

        assertThat(convertedFilter, is(notNullValue()));
        assertThat(convertedFilter.getConvertedValue(), is(value));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    @Test
    void canCreateWithValueAndContext() {
        String value = "value";
        FilterContext filterContext = new FilterContext();

        ConvertedFilter<String> convertedFilter = new ConvertedFilter<>(value, filterContext);

        assertThat(convertedFilter, is(notNullValue()));
        assertThat(convertedFilter.getConvertedValue(), is(value));
        assertThat(convertedFilter.getFilterContext(), is(not(Optional.empty())));
        assertThat(convertedFilter.getFilterContext().get(), is(filterContext));

    }

    @Test
    void cannotCreateWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new ConvertedFilter<>(null));
    }

}