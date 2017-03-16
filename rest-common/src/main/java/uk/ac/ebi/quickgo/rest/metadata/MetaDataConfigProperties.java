package uk.ac.ebi.quickgo.rest.metadata;

import org.springframework.core.io.Resource;

/**
 * Hold configuration information required to load metadata information.
 *
 * @author Tony Wardell
 * Date: 09/03/2017
 * Time: 13:39
 * Created with IntelliJ IDEA.
 */
public class MetaDataConfigProperties {

    private Resource source;

    public void setSource(Resource source){
        this.source = source;
    }

    public Resource getSource() {
        return source;
    }
}
