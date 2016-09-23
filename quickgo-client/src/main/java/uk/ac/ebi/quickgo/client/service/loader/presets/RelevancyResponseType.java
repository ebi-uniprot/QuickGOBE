package uk.ac.ebi.quickgo.client.service.loader.presets;

import uk.ac.ebi.quickgo.rest.comm.ResponseType;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.List;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>Provides the data-type object that captures term relevancy information.
 * The {@code terms.relevancies} value is a list of {@link String}s (returned from an
 * external REST endpoint) which capture relevancy information in the form of:
 * {@code [ "term1", "term1Frequency", "term2", "term2Frequency" ... ]}.
 * <p>
 * Created 31/08/16
 * @author Edd
 */
public class RelevancyResponseType implements ResponseType {
    private static final Logger LOGGER = getLogger(RelevancyResponseType.class);
    public Terms terms;

    public static class Terms {
        public String termName;
        public List<String> relevancies;

        @JsonAnySetter
        public void set(String termName, List<String> termInfo) {
            if (this.termName == null) {
                this.termName = termName;
                this.relevancies = termInfo;
            } else {
                LOGGER.warn("RelevancyResponseType has already been populated with term info [{}, {}]. " +
                                "Will not overwrite existing information.",
                        this.termName, this.relevancies);
            }
        }
    }
}
