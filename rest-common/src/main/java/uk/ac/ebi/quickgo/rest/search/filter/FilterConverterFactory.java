package uk.ac.ebi.quickgo.rest.search.filter;

/**
 * A factory responsible for creating {@link RequestFilter} instances based on the type of field and the values
 * provided.
 *
 * @author Ricardo Antunes
 */
public interface FilterConverterFactory {
    /**
     * Will create the appropriate {@link uk.ac.ebi.quickgo.common.converter.FieldConverter} for the
     * {@link RequestFilter}. The decision is based on what type of value is conteind within the
     * {@link RequestFilter#getField()}.
     *
     * @param requestFilter the filter to convert
     * @return a filter capable of converting the filter into a {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}
     */
    FilterConverter createConverter(RequestFilter requestFilter);
}