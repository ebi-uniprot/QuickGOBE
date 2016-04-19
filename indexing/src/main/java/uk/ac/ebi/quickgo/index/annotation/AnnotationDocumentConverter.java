package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import java.util.Arrays;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created 19/04/16
 * @author Edd
 */
public class AnnotationDocumentConverter implements ItemProcessor<Annotation, AnnotationDocument> {
    private static final String COLON = ":";
    private static final String PIPE = "|";

    @Override public AnnotationDocument process(Annotation annotation) throws Exception {
        if (annotation == null) {
            throw new DocumentReaderException("Annotation object is null");
        }

        AnnotationDocument doc = new AnnotationDocument();

        // gene product ID
        doc.geneProductId = annotation.db + COLON + annotation.dbObjectId;

        // symbol
        // doc.symbol = ??

        // qualifier
        doc.qualifier = annotation.qualifier;

        // goId
        doc.goId = annotation.goId;

        // goEvidence
        // doc.goEvidence = ??

        // ecoId
        doc.ecoId = annotation.eco;

        // reference
        doc.reference = annotation.dbReferences;

        // withFrom
        doc.withFrom = annotation.with == null? null : Arrays.asList(annotation.with.split(PIPE));

        // taxonId
        doc.taxonId = annotation.interactingTaxonId;

        // assignedBy
        doc.assignedBy = annotation.assignedBy;

        // extension
        doc.extension = annotation.annotationExtension;

        return doc;
    }
}
