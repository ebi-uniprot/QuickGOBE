package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.springframework.batch.item.ItemProcessor;

import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.*;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.COLON;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.EQUALS;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.PIPE;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.convertLinePropertiesToMap;

/**
 * Converts an {@link Annotation} object into an {@link AnnotationDocument} object.
 *
 * Created 19/04/16
 * @author Edd
 */
public class AnnotationDocumentConverter implements ItemProcessor<Annotation, AnnotationDocument> {
    static final int DEFAULT_TAXON = 0;

    @Override public AnnotationDocument process(Annotation annotation) throws Exception {
        if (annotation == null) {
            throw new DocumentReaderException("Annotation object is null");
        }

        Map<String, String> propertiesMap = convertLinePropertiesToMap(annotation.annotationProperties, PIPE, EQUALS);

        AnnotationDocument doc = new AnnotationDocument();

        doc.geneProductId = constructGeneProductId(annotation);
        doc.qualifier = annotation.qualifier;
        doc.goId = annotation.goId;
        doc.reference = annotation.dbReferences;
        doc.assignedBy = annotation.assignedBy;
        doc.ecoId = annotation.ecoId;
        doc.extensions = constructExtensions(annotation);

        doc.withFrom = constructWithFrom(annotation);
        doc.interactingTaxonId = extractInteractingTaxonId(annotation);

        doc.goEvidence = propertiesMap.get(GO_EVIDENCE);
        doc.dbSubset = propertiesMap.get(DB_OBJECT_SUBSET);
        doc.dbObjectSymbol = propertiesMap.get(DB_OBJECT_SYMBOL);
        doc.dbObjectType = propertiesMap.get(DB_OBJECT_TYPE);
        doc.taxonId = extractTaxonId(propertiesMap.get(TAXON_ID));

        return doc;
    }

    private int extractInteractingTaxonId(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.interactingTaxonId)) {
            Matcher matcher = INTERACTING_TAXON_REGEX.matcher(annotation.interactingTaxonId);
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return DEFAULT_TAXON;
    }

    private int extractTaxonId(String rawTaxonId) {
        if (!Strings.isNullOrEmpty(rawTaxonId)) {
            Matcher matcher = RAW_TAXON_REGEX.matcher(rawTaxonId);
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return DEFAULT_TAXON;
    }

    private List<String> constructExtensions(Annotation annotation) {
        return annotation.annotationExtension == null ? null :
                Arrays.asList(annotation.annotationExtension.split(PIPE));
    }

    private List<String> constructWithFrom(Annotation annotation) {
        return annotation.with == null ? null : Arrays.asList(annotation.with.split(PIPE));
    }

    private String constructGeneProductId(Annotation annotation) {
        return annotation.db + COLON + annotation.dbObjectId;
    }
}
