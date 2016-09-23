package uk.ac.ebi.quickgo.client.service.loader.presets.qualifier;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import java.util.List;

/**
 * <p>Provides the data-type object that captures taxon relevancy information.
 * The {@code terms.taxonIds} value is a list of {@link String}s (returned from an
 * external REST endpoint) which capture relevancy information in the form of:
 * {@code [ "term1", "term1Frequency", "term2", "term2Frequency" ... ]}.
 * <p>
 * Created 31/08/16
 * @author Edd
 */
public class QualifierRelevancyResponseType implements ResponseType {
    public Terms terms;

    public static class Terms {
        public List<String> taxonIds;
    }
}
