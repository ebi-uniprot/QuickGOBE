package uk.ac.ebi.quickgo.common.service;

import java.util.Map;

/**
 * Configuration information relating to service data retrieval.
 *
 * Created 08/02/16
 * @author Edd
 */
public interface ServiceRetrievalConfig {

    /**
     * Retrieves a {@link Map} over {@link String}s defining
     * mappings of field names used in the underlying data-store,
     * and field names that are relevant to the service layer.
     * <p>
     * Note that this map must represent a function, i.e., each element
     * of the domain maps to exactly 1 element of the co-domain (it is not possible
     * for example, to map a->b, and a->c).
     *
     * @return a map of associations between field names at the data-store and service layers
     */
    Map<String, String> searchRepo2DomainFieldMap();
}
