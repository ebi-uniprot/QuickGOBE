package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.metadata.MetaData;

/**
 * !! Stub class. This class is defined purely to allow Swagger to provide a concrete
 * example. The actual implementation of the '/about' end-point uses {@link MetaData},
 * which is a map, and which Swagger does not currently understand, e.g., see
 * <ul>
 *     <li>https://github.com/swagger-api/swagger-ui/issues/558</li>
 *     <li>https://github.com/springfox/springfox/issues/1324</li>
 *     </ul>
 *
 * Created 26/09/17
 * @author Edd
 */
public class About {
    public AnnotationMetaData annotation;

    public static class AnnotationMetaData {
        public String timestamp;
    }
}
