package uk.ac.ebi.quickgo.rest.search.filter.converter;

/**
 * Created 02/06/16
 * @author Edd
 */
public interface FilterConverterConfigurer<T extends FilterConverter> {
    void configure(T filterConverter);
}
