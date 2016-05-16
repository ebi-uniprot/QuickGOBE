package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

/**
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:47
 * Created with IntelliJ IDEA.
 */
public interface AnnotationDocConverter {

    /**
     * Convert an Annotation Document into a model to be returned to the user
     * @param annotationDocument  A document retrieved from the Annotation data source
     * @return an Annotation model
     */
    Annotation convert(AnnotationDocument annotationDocument);
}
