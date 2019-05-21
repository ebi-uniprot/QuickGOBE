package uk.ac.ebi.quickgo.index.annotation;

import com.google.common.base.Strings;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.*;
import static uk.ac.ebi.quickgo.index.annotation.Columns.*;
import static uk.ac.ebi.quickgo.index.common.validation.ValidationHelper.checkIsNullOrEmpty;
import static uk.ac.ebi.quickgo.index.common.validation.ValidationHelper.handleFieldPatternMismatchError;

/**
 * Validates the contents of an {@link Annotation} object. The validation
 * step takes place before allowing an {@link Annotation} object to be
 * indexed -- as part of a Spring Batch job.
 *
 * Created 20/04/16
 * @author Edd
 */
class AnnotationValidator implements Validator<Annotation> {

    @Override public void validate(Annotation annotation) throws ValidationException {
        if (annotation == null) {
            throw new DocumentReaderException("Annotation cannot be null");
        }

        // required fields
        checkIsNullOrEmpty(annotation.db, COLUMN_DB.getName());
        checkIsNullOrEmpty(annotation.dbObjectId, COLUMN_DB_OBJECT_ID.getName());
        checkQualifier(annotation);
        checkIsNullOrEmpty(annotation.goId, COLUMN_GO_ID.getName());
        checkIsNullOrEmpty(annotation.dbReferences, COLUMN_DB_REFERENCES.getName());
        checkIsNullOrEmpty(annotation.evidenceCode, COLUMN_EVIDENCE_CODE.getName());
        checkIsNullOrEmpty(annotation.assignedBy, COLUMN_ASSIGNED_BY.getName());
        checkDate(annotation);

        // optional fields
        checkProperties(annotation);
        checkExtensions(annotation);
        checkWith(annotation);
        checkTaxon(annotation);
    }

    private void checkDate(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.date) &&
                !DATE_REGEX.matcher(annotation.date).matches()) {
            handleFieldPatternMismatchError(
                    "Date",
                    annotation.date,
                    DATE_REGEX.pattern(),
                    annotation);
        }
    }

    private void checkTaxon(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.interactingTaxonId) &&
                !INTERACTING_TAXON_REGEX.matcher(annotation.interactingTaxonId).matches()) {
            handleFieldPatternMismatchError(
                    "Interacting Taxon",
                    annotation.interactingTaxonId,
                    INTERACTING_TAXON_REGEX.pattern(),
                    annotation);
        }
    }

    private void checkProperties(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.annotationProperties)) {
            if (!ANNOTATION_PROPERTIES_REGEX.matcher(annotation.annotationProperties).matches()) {
                handleFieldPatternMismatchError(
                        "Annotation Properties",
                        annotation.annotationProperties,
                        ANNOTATION_PROPERTIES_REGEX.pattern(),
                        annotation);
            }
            else {
                checkMandatoryPropertiesFieldsExist(annotation);
            }
        }
    }

    private void checkMandatoryPropertiesFieldsExist(Annotation annotation) {
        if (!(PROPS_TAXON_REGEX.matcher(annotation.annotationProperties).find() &&
                      PROPS_DB_OBJECT_TYPE_REGEX.matcher(annotation.annotationProperties).find() &&
                      PROPS_TAXON_ANCESTORS_REGEX.matcher(annotation.annotationProperties).find() &&
                      PROPS_GP_RELATED_GO_IDS_REGEX.matcher(annotation.annotationProperties).find()
        )) {
            handleFieldPatternMismatchError(
                    "Annotation Properties: required field not found",
                    annotation.annotationProperties,
                    PROPS_TAXON_REGEX.pattern()
                            + " AND " + PROPS_DB_OBJECT_TYPE_REGEX.pattern()
                            + " AND " + PROPS_TAXON_ANCESTORS_REGEX.pattern()
                            + " AND " + PROPS_GP_RELATED_GO_IDS_REGEX.pattern(),
                    annotation);
        }
    }

    private void checkExtensions(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.annotationExtension) &&
                !ANNOTATION_EXTENSION_REGEX.matcher(annotation.annotationExtension).matches()) {
            handleFieldPatternMismatchError(
                    "Annotation Extension",
                    annotation.annotationExtension,
                    ANNOTATION_EXTENSION_REGEX.pattern(),
                    annotation);
        }
    }

    private void checkQualifier(Annotation annotation) {
        checkIsNullOrEmpty(annotation.qualifier, COLUMN_QUALIFIER.getName());
        if (!QUALIFIER_REGEX.matcher(annotation.qualifier).matches()) {
            handleFieldPatternMismatchError(
                    "Qualifier",
                    annotation.qualifier,
                    QUALIFIER_REGEX.pattern(),
                    annotation);
        }
    }

    private void checkWith(Annotation annotation) {
        if (!Strings.isNullOrEmpty(annotation.with) && !WITH_REGEX.matcher(annotation.with).matches()) {
            handleFieldPatternMismatchError(
                    "With",
                    annotation.with,
                    WITH_REGEX.pattern(),
                    annotation);
        }
    }
}
