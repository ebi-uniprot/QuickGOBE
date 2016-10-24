package uk.ac.ebi.quickgo.index.annotation;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.slf4j.Logger;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.validation.BindException;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.index.annotation.Columns.*;

/**
 * Converts a String representing an annotation into an {@link Annotation} object.
 *
 * Created 19/04/16
 * @author Edd
 */
public class StringToAnnotationMapper implements FieldSetMapper<Annotation> {
    private static final Logger LOGGER = getLogger(StringToAnnotationMapper.class);
    static final String ANNOTATION_DATE_FORMAT = "yyyyMMdd";
    private final DateTimeFormatter dateTimeFormatter;

    public StringToAnnotationMapper() {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(ANNOTATION_DATE_FORMAT);
    }

    @Override public Annotation mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new IllegalArgumentException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < numColumns()) {
            throw new IncorrectTokenCountException("Incorrect number of columns, expected: " + numColumns() + "; " +
                    "found: " + fieldSet.getFieldCount(), numColumns(), fieldSet.getFieldCount());
        }

        Annotation annotation = new Annotation();

        annotation.db = trimIfNotNull(fieldSet.readString(COLUMN_DB.getPosition()));
        annotation.dbObjectId = trimIfNotNull(fieldSet.readString(COLUMN_DB_OBJECT_ID.getPosition()));
        annotation.qualifier = trimIfNotNull(fieldSet.readString(COLUMN_QUALIFIER.getPosition()));
        annotation.goId = trimIfNotNull(fieldSet.readString(COLUMN_GO_ID.getPosition()));
        annotation.dbReferences = trimIfNotNull(fieldSet.readString(COLUMN_DB_REFERENCES.getPosition()));
        annotation.evidenceCode = trimIfNotNull(fieldSet.readString(COLUMN_EVIDENCE_CODE.getPosition()));
        annotation.with = trimIfNotNull(fieldSet.readString(COLUMN_WITH.getPosition()));
        annotation.interactingTaxonId = trimIfNotNull(fieldSet.readString(COLUMN_INTERACTING_TAXON_ID.getPosition()));
        annotation.assignedBy = trimIfNotNull(fieldSet.readString(COLUMN_ASSIGNED_BY.getPosition()));
        annotation.annotationExtension = trimIfNotNull(fieldSet.readString(COLUMN_ANNOTATION_EXTENSION.getPosition()));
        annotation.annotationProperties =
                trimIfNotNull(fieldSet.readString(COLUMN_ANNOTATION_PROPERTIES.getPosition()));
        annotation.date = createDateFromString(fieldSet.readString(COLUMN_DATE.getPosition()));

        return annotation;
    }

    /**
     * <p>Creates a date from a date string. The date string is expected to be of the form: YYYYMMDD,
     * e.g., 20120123 for 23rd January 2012. Any problem parsing this date will have the error logged.
     * Since such errors are not critical, indexing will set the date to null in such circumstances
     * and continue.
     * <p>The {@link Date} instance created needs to be in UTC format, required by the underlying data repository,
     * into which the instance will be persisted.
     *
     * @param dateString the date string, expected to be of the form YYYYMMDD
     * @return a {@link Date} instance representing the date.
     */
    Date createDateFromString(String dateString) {
        if (dateString != null && !dateString.trim().isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(dateString.trim(), dateTimeFormatter);
                return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (IllegalArgumentException|DateTimeParseException iae) {
                LOGGER.error("Could not parse annotation date: " + dateString, iae);
            }
        }
        return null;
    }

    private String trimIfNotNull(String value) {
        return value == null ? null : value.trim();
    }
}
