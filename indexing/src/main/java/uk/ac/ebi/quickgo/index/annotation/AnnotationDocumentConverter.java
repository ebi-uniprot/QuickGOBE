package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.springframework.batch.item.ItemProcessor;

import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.*;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.*;

/**
 * Converts an {@link Annotation} object into an {@link AnnotationDocument} object.
 *
 * Created 19/04/16
 * @author Edd
 */
class AnnotationDocumentConverter implements ItemProcessor<Annotation, AnnotationDocument> {
    static final int DEFAULT_TAXON = 0;
    private static final Logger LOGGER = getLogger(AnnotationDocumentConverter.class);
    private static final String ANNOTATION_DATE_FORMAT = "yyyyMMdd";
    private final DateTimeFormatter dateTimeFormatter;

    private final AtomicLong documentCounter;

    AnnotationDocumentConverter() {
        documentCounter = new AtomicLong(0L);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(ANNOTATION_DATE_FORMAT);
    }

    @Override
    public AnnotationDocument process(Annotation annotation) {
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
        doc.evidenceCode = annotation.evidenceCode;
        doc.extensions = annotation.annotationExtension;
        doc.withFrom = constructWithFrom(annotation);
        doc.interactingTaxonId = extractInteractingTaxonId(annotation);
        doc.goEvidence = propertiesMap.get(GO_EVIDENCE);
        doc.geneProductSubset = propertiesMap.get(DB_OBJECT_SUBSET);
        doc.symbol = propertiesMap.get(DB_OBJECT_SYMBOL);
        doc.geneProductType = propertiesMap.get(DB_OBJECT_TYPE);
        doc.taxonId = extractTaxonId(propertiesMap.get(TAXON_ID));
        doc.targetSets = constructTargetSets(propertiesMap.get(TARGET_SET));
        doc.goAspect = propertiesMap.get(GO_ASPECT);
        doc.date = createDateFromString(annotation);
        doc.taxonAncestors = constructTaxonAncestors(propertiesMap.get(TAXON_ANCESTORS));

        return doc;
    }

    /**
     * <p>Creates a date from a date string. The date string is expected to be of the form: YYYYMMDD,
     * e.g., 20120123 for 23rd January 2012. Any problem parsing this date will have the error logged.
     * Since such errors are not critical, indexing will set the date to null in such circumstances
     * and continue.
     * <p>The {@link Date} instance created needs to be in UTC format, required by the underlying data repository,
     * into which the instance will be persisted.
     *
     * @param annotation the annotation whose date string, of the form YYYYMMDD, is to be converted to a {@link Date}
     *                   instance
     * @return a {@link Date} instance representing the date.
     */
    private Date createDateFromString(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.date)) {
            try {
                LocalDate localDate = LocalDate.parse(annotation.date, dateTimeFormatter);
                return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (IllegalArgumentException|DateTimeParseException iae) {
                LOGGER.error("Could not parse annotation date: " + annotation.date, iae);
            }
        }
        return null;
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

    private List<Integer> constructTaxonAncestors(String rawAncestors) {
        if (!Strings.isNullOrEmpty(rawAncestors)) {
            Matcher matcher = RAW_TAXON_ANCESTORS_REGEX.matcher(rawAncestors);
            if (matcher.matches()) {
                return createNullableIntegerListFromDelimitedValues(rawAncestors, COMMA);
            }
        }
        return singletonList(DEFAULT_TAXON);
    }

    private List<Integer> createNullableIntegerListFromDelimitedValues(String value, String delimiter) {
        return value == null ? null : splitValueToIntegerList(value, delimiter);
    }

    private List<String> constructWithFrom(Annotation annotation) {
        return createNullableStringListFromDelimitedValues(annotation.with, PIPE);
    }

    private List<String> createNullableStringListFromDelimitedValues(String value, String delimiter) {
        return value == null ? null : Arrays.asList(splitValue(value, delimiter));
    }

    private String constructGeneProductId(Annotation annotation) {
        return annotation.db + COLON + annotation.dbObjectId;
    }

    private List<String> constructTargetSets(String value) {
        return createNullableStringListFromDelimitedValues(value, COMMA);
    }
}