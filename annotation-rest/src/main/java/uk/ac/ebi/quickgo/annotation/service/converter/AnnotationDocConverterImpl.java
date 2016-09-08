package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.ArrayList;

/**
 * Concrete implementation of the {@link AnnotationDocConverter}.
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:52
 */
public class AnnotationDocConverterImpl implements AnnotationDocConverter {

    @Override public Annotation convert(AnnotationDocument annotationDocument) {
        Annotation annotation = new Annotation();
        annotation.id = annotationDocument.id;
        annotation.geneProductId = annotationDocument.geneProductId;
        annotation.qualifier = annotationDocument.qualifier;
        annotation.goId = annotationDocument.goId;
        annotation.goEvidence = annotationDocument.goEvidence;
        annotation.goAspect = annotationDocument.goAspect;
        annotation.evidenceCode = annotationDocument.evidenceCode;
        annotation.reference = annotationDocument.reference;
        annotation.taxonId = annotationDocument.taxonId;
        annotation.symbol = annotationDocument.symbol;
        annotation.assignedBy = annotationDocument.assignedBy;

        if(annotationDocument.withFrom != null) {
            annotation.withFrom = new ArrayList<>(annotationDocument.withFrom);
        }

        if(annotationDocument.extensions != null) {
            annotation.extensions = new ArrayList<>(annotationDocument.extensions);
        }
        return annotation;
    }
}
