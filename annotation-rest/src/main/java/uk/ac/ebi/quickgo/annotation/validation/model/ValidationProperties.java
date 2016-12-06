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

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public Resource getValidationResource() {
        return validationResource;
    }

    public void setValidationResource(Resource validationResource) {
        this.validationResource = validationResource;
    }

    public List<String> getReferenceDbs() {
        return referenceDbs;
    }

    public void setReferenceDbs(List<String> referenceDbs) {
        this.referenceDbs = referenceDbs;
    }
}
