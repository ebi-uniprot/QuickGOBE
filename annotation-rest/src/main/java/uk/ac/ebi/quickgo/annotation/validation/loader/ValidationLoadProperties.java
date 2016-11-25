package uk.ac.ebi.quickgo.annotation.validation.loader;

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
public class ValidationLoadProperties {

    private String chunk;
    private String validationFile;

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
}
