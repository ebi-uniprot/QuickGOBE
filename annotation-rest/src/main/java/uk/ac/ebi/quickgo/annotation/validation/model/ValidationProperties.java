package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    private String chunk;
    private String validationFile;
    private List<String> referenceDbs;

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public String getValidationFile() {
        return validationFile;
    }

    public void setValidationFile(String validationFile) {
        this.validationFile = validationFile;
    }

    public List<String> getReferenceDbs() {
        return referenceDbs;
    }

    public void setReferenceDbs(List<String> referenceDbs) {
        this.referenceDbs = referenceDbs;
    }
}
