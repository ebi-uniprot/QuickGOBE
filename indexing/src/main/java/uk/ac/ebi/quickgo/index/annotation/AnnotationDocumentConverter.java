package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import org.springframework.batch.item.ItemProcessor;

import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.*;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.*;

/**
 * Converts an {@link Annotation} object into an {@link AnnotationDocument} object.
 *
 * Created 19/04/16
 * @author Edd
 */
public class AnnotationDocumentConverter implements ItemProcessor<Annotation, AnnotationDocument> {
    static final int DEFAULT_TAXON = 0;

    private final AtomicLong documentCounter;

    AnnotationDocumentConverter() {
        documentCounter = new AtomicLong(0L);
    }

    @Override public AnnotationDocument process(Annotation annotation) throws Exception {
        if (annotation == null) {
            throw new DocumentReaderException("Annotation object is null");
        }

        Map<String, String> propertiesMap = convertLinePropertiesToMap(annotation.annotationProperties, PIPE, EQUALS);

        AnnotationDocument doc = new AnnotationDocument();

        doc.id = Long.toString(documentCounter.getAndIncrement());
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
        doc.geneProductType = propertiesMap.get(DB_OBJECT_TYPE);
        doc.taxonId = extractTaxonId(propertiesMap.get(TAXON_ID));
        doc.targetSet = constructTargetSets(propertiesMap.get(TARGET_SET));

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
        return createNullableListFromDelimitedValues(annotation.annotationExtension, PIPE);
    }

    private List<String> constructWithFrom(Annotation annotation) {
        return createNullableListFromDelimitedValues(annotation.with, PIPE);
    }

    private List<String> createNullableListFromDelimitedValues(String value, String delimiter) {
        return value == null ? null : Arrays.asList(splitValue(value, delimiter));
    }

    private String constructGeneProductId(Annotation annotation) {
        return annotation.db + COLON + annotation.dbObjectId;
    }

    private List<String> constructTargetSets(String value) {
        return createNullableListFromDelimitedValues(value, COMMA);
    }
}
