package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Properties for loading validation data for annotation filtering.
 *
 * @author Tony Wardell
 * Date: 25/11/2016
 * Time: 12:16
 * Created with IntelliJ IDEA.
 */
@Component
@ConfigurationProperties(prefix = "annotation.validation")
public class ValidationProperties {

    private int chunk;
    private Resource validationResource;
    private List<String> referenceDbs;

    /**
     * The batch size to process when reading validation properties.
     * @return chunk size in number of records.
     */
    public int getChunk() {
        return chunk;
    }

    /**
     * The batch size to process when reading validation properties.
     * @param chunk
     */
    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    /**
     * The source of validating data.
     * @return resource to use.
     */
    public Resource getValidationResource() {
        return validationResource;
    }

    /**
     * The source of validating data.
     */
    public void setValidationResource(Resource validationResource) {
        this.validationResource = validationResource;
    }

    /**
     * A list of database ids that are valid to use when filtering annotations by reference.
     * @return A list of database ids.
     */
    public List<String> getReferenceDbs() {
        return referenceDbs;
    }

    /**
     * A list of database codes that are valid to use when filtering annotations by reference.
     */
    public void setReferenceDbs(List<String> referenceDbs) {
        this.referenceDbs = referenceDbs;
    }
}
