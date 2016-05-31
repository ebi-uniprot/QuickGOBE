package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

/**
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:52
 * Created with IntelliJ IDEA.
 */
public class AnnotationDocConverterImpl implements AnnotationDocConverter {

    @Override public Annotation convert(AnnotationDocument annotationDocument) {
        Annotation annotation = new Annotation();

        //todo take elements
        annotation.assignedBy = annotationDocument.assignedBy;
        annotation.withFrom = annotationDocument.withFrom;
        return annotation;
    }
}
