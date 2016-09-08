package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Collections;
import java.util.List;

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
        annotation.evidenceCode = annotationDocument.evidenceCode;
        annotation.reference = annotationDocument.reference;
        annotation.taxonId = annotationDocument.taxonId;
        annotation.symbol = annotationDocument.symbol;
        annotation.assignedBy = annotationDocument.assignedBy;

        annotation.targetSets = asUnmodifiableList(annotationDocument.targetSets);
        annotation.withFrom = asUnmodifiableList(annotationDocument.withFrom);
        annotation.extensions = asUnmodifiableList(annotationDocument.extensions);

        return annotation;
    }

    private <T> List<T> asUnmodifiableList(List<T> list) {
        List<T> unmodifiableList;

        if (list != null) {
            unmodifiableList = Collections.unmodifiableList(list);
        } else {
            unmodifiableList = null;
        }

        return unmodifiableList;
    }
}
