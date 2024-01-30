package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationMocker.createValidAnnotation;

/**
 * Created 21/04/16
 * @author Edd
 */
class AnnotationValidatorTest {

    private AnnotationValidator validator;
    private Annotation annotation;

    @BeforeEach
    void setUp() {
        validator = new AnnotationValidator();
        annotation = createValidAnnotation();
    }

    // check required fields -------------------------------------------------
    @Test
    void nullAnnotationThrowsException() {
        assertThrows(DocumentReaderException.class, () -> {
            validator.validate(null);
        });
    }

    @Test
    void nullDatabaseThrowsException() {
        assertThrows(ValidationException.class, () -> {
            annotation.db = null;
            validator.validate(annotation);
        });
    }

    @Test
    void nullDatabaseIDThrowsException() {
        assertThrows(ValidationException.class, () -> {
            annotation.dbObjectId = null;
            validator.validate(annotation);
        });
    }

    @Test
    void nullQualifierThrowsException() {
        assertThrows(ValidationException.class, () -> {
            annotation.qualifier = null;
            validator.validate(annotation);
        });
    }

    @Test
    void nullGOIdThrowsException() {
        assertThrows(ValidationException.class, () -> {
            annotation.goId = null;
            validator.validate(annotation);
        });
    }

    @Test
    void nullDatabaseReferencesException() {
        assertThrows(ValidationException.class, () -> {
            annotation.dbReferences = null;
            validator.validate(annotation);
        });
    }

    @Test
    void nullEcoEvidenceThrowsException() {
        assertThrows(ValidationException.class, () -> {
            annotation.evidenceCode = null;
            validator.validate(annotation);
        });
    }

    @Test
    void nullAssignedByThrowsException() {
        assertThrows(ValidationException.class, () -> {
            annotation.assignedBy = null;
            validator.validate(annotation);
        });
    }

    // bad qualifier field -------------------------------------------------
    @Test
    void invalidNotCasingCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.qualifier = "not|contributes_to";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidQualifierDelimiterCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.qualifier = "NOT,contributes_to";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidQualifierValueCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.qualifier = "cntributes_to";
            validator.validate(annotation);
        });
    }

    @Test
    void tooManyQualifiersCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.qualifier = "NOT|enables|contributes_to";
            validator.validate(annotation);
        });
    }

    // good qualifier field -------------------------------------------------
    @Test
    void validatesInvolvedInQualifier() {
        annotation.qualifier = "involved_in";
        validator.validate(annotation);
    }

    @Test
    void validatesEnablesQualifier() {
        annotation.qualifier = "enables";
        validator.validate(annotation);
    }

    @Test
    void validatesPartOfQualifier() {
        annotation.qualifier = "part_of";
        validator.validate(annotation);
    }

    @Test
    void validatesContributesToQualifier() {
        annotation.qualifier = "contributes_to";
        validator.validate(annotation);
    }

    @Test
    void validatesColocalizesWithQualifier() {
        annotation.qualifier = "colocalizes_with";
        validator.validate(annotation);
    }

    @Test
    void validatesNotInvolvedInQualifier() {
        annotation.qualifier = "NOT|involved_in";
        validator.validate(annotation);
    }

    @Test
    void validatesNotEnablesQualifier() {
        annotation.qualifier = "NOT|enables";
        validator.validate(annotation);
    }

    @Test
    void validatesNotPartOfQualifier() {
        annotation.qualifier = "NOT|part_of";
        validator.validate(annotation);
    }

    @Test
    void validatesNotContributesToQualifier() {
        annotation.qualifier = "NOT|contributes_to";
        validator.validate(annotation);
    }

    @Test
    void validatesNotColocalizesWithQualifier() {
        annotation.qualifier = "NOT|colocalizes_with";
        validator.validate(annotation);
    }

    @Test
    void qualifierValidation(){
        String[] qualifier = new String[] {"acts_upstream_of","acts_upstream_of_positive_effect",
                "acts_upstream_of_negative_effect",
                "acts_upstream_of_or_within",
                "acts_upstream_of_or_within_positive_effect",
                "acts_upstream_of_or_within_negative_effect",
                "is_active_in", "located_in"};

        Arrays.stream(qualifier).forEach(q -> {
            annotation.qualifier = q;

            validator.validate(annotation);
        });
    }

    @Test
    void negatedQualifierValidation(){
        String[] qualifier = new String[] {"NOT|acts_upstream_of","NOT|acts_upstream_of_positive_effect",
                "NOT|acts_upstream_of_negative_effect",
                "NOT|acts_upstream_of_or_within",
                "NOT|acts_upstream_of_or_within_positive_effect",
                "NOT|acts_upstream_of_or_within_negative_effect",
                "NOT|is_active_in", "NOT|located_in"};

        Arrays.stream(qualifier).forEach(q -> {
            annotation.qualifier = q;

            validator.validate(annotation);
        });
    }

    // good with field -------------------------------------------------
    @Test
    void validNullWithField() {
        annotation.with = null;
        validator.validate(annotation);
    }

    @Test
    void validEmptyWithField() {
        annotation.with = "";
        validator.validate(annotation);
    }

    @Test
    void validWithMultiTermedComponent() {
        annotation.with = "GO:1234,IP:1234";
        validator.validate(annotation);
    }

    @Test
    void validRNACentralMultiTermedComponent() {
        annotation.with = "RNAcentral:URS00001989EA_10090,RNAcentral:URS00004AD914_10090," +
                "RNAcentral:URS00004BCD9C_10090,RNAcentral:URS00005B3525_10090";
        validator.validate(annotation);
    }

    @Test
    void validWithNotSupplied() {
        annotation.with = "With:Not_supplied";
        validator.validate(annotation);
    }

    @Test
    void validWithMultiSingleTermedComponents() {
        annotation.with = "GO:1234|IP:1234";
        validator.validate(annotation);
    }

    @Test
    void validWithMultiMultiTermedComponents() {
        annotation.with = "GO:1234,GO:1235|GO:1236,IP:1234";
        validator.validate(annotation);
    }

    @Test
    void validWithSingleTerm() {
        annotation.with = "GO:1234-34";
        validator.validate(annotation);
    }

    @Test
    void validWithSingleTermEC() {
        annotation.with = "EC:3.1.21.3";
        validator.validate(annotation);
    }

    // bad with field -------------------------------------------------
    @Test
    void invalidWithIntraTermDelimiterCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.with = "GO-asdfsdf";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidWithInterTermDelimiterCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.with = "GO:1234\tIP:1234";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidWithLeftPartCausesValidationException() {
        assertThrows(ValidationException.class, () -> {
            annotation.with = "asdf+:asdfsdf";
            validator.validate(annotation);
        });
    }

    // good annotation properties field -------------------------------------------------
    @Test
    void validNullAnnotationProperties() {
        // if we adhere to the column specification header, this can be null
        annotation.annotationProperties = null;
        validator.validate(annotation);
    }

    @Test
    void validEmptyAnnotationProperties() {
        // if we adhere to the column specification header, this can be empty
        annotation.annotationProperties = "";
        validator.validate(annotation);
    }

    @Test
    void validRequiredAnnotationProperties() {
        validator.validate(annotation);
    }

    @Test
    void validAnnotationPropertiesAndDbObjectSymbol() {
        annotation.annotationProperties += "|db_object_symbol=moeA5";
        validator.validate(annotation);
    }

    @Test
    void validAnnotationPropertiesAndDbSubset() {
        annotation.annotationProperties += "|db_subset=TrEMBL";
        validator.validate(annotation);
    }

    @Test
    void validAnnotationPropertyWithAdditionalIgnoredTerms() {
        annotation.annotationProperties += "|a=b|c=d,e=f|g=h";
        validator.validate(annotation);
    }

    @Test
    void complexPortalAnnotationsCanBeMissingGoEvidenceIdsAndThatsOK() {
        annotation.annotationProperties =
                "go_aspect=cellular_component|taxon_id=10090|db_object_symbol=methylosome_mouse" +
                        "|db_object_type=complex|taxon_lineage=10090|gp_related_go_ids=GO:1|row_num=1";
        validator.validate(annotation);
    }

    // bad annotation properties field -------------------------------------------------
    @Test
    void invalidSingleTermedComponentAnnotationProperty() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence:IPI";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToTaxonIdBeing0() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence=IEA|taxon_id=0|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                    "|taxon_lineage=10,200|gp_related_go_ids=GO:0005515|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToMissingTaxonId() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence=IEA|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                    "|taxon_lineage=1,2|gp_related_go_ids=GO:0005515|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToMissingDbObjectType() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5" +
                    "|taxon_lineage=1,2|gp_related_go_ids=GO:0005515|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToTaxonLineageBeing0() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties =
                    "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                            "|taxon_lineage=0,10,200|gp_related_go_ids=GO:0005515|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToMissingTaxonLineage() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5" +
                    "|db_object_type=protein|gp_related_go_ids=GO:0005515|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToGpRelatedGoIdsCaseIncorrect() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties =
                    "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                            "|taxon_lineage=10,200|gp_related_go_ids=go:1,Go:2|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToMissingGpRelatedGoIds() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5" +
                    "|db_object_type=protein|taxon_lineage=10,200|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToGpRelatedGoIdsIsEmpty() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties =
                    "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                            "|taxon_lineage=10,200|gp_related_go_ids=|row_num=1";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToMissingRowNum() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties = "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5" +
                    "|db_object_type=protein|taxon_lineage=10,200|gp_related_go_ids=GO:0005515";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationPropertyDueToRowNumIsEmpty() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationProperties =
                    "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                            "|taxon_lineage=10,200|gp_related_go_ids=GO:0005515|row_num=";
            validator.validate(annotation);
        });
    }

    // good annotation extension field -------------------------------------------------
    @Test
    void validNullAnnotationExtension() {
        annotation.annotationExtension = null;
        validator.validate(annotation);
    }

    @Test
    void validEmptyAnnotationExtension() {
        annotation.annotationExtension = "";
        validator.validate(annotation);
    }

    @Test
    void validSingleTermedComponentAnnotationExtensionPartOf() {
        annotation.annotationExtension = "part_of(something)";
        validator.validate(annotation);
    }

    @Test
    void validSingleTermedComponentAnnotationExtensionGO() {
        annotation.annotationExtension = "GO:0016540";
        validator.validate(annotation);
    }

    @Test
    void validMultiTermedComponentAnnotationExtensionGO() {
        annotation.annotationExtension = "GO:0016540,GO:0016541";
        validator.validate(annotation);
    }

    @Test
    void validSingleTermedComponentAnnotationExtensionHasInput() {
        annotation.annotationExtension = "has_input(PomBase:SPCC1183.12)";
        validator.validate(annotation);
    }

    @Test
    void validSingleTermedComponentsAnnotationExtension() {
        annotation.annotationExtension = "part_of(something)|part_of(something_else)";
        validator.validate(annotation);
    }

    @Test
    void validMultiTermedComponentAnnotationExtension() {
        annotation.annotationExtension = "part_of(something),part_of(something_else)";
        validator.validate(annotation);
    }

    @Test
    void validMultiTermedComponentsAnnotationExtension() {
        annotation.annotationExtension = "part_of(something),part_of(something_else)|part_of(yet_another_thing)";
        validator.validate(annotation);
    }

    // bad annotation extension field -------------------------------------------------
    @Test
    void invalidAnnotationExtensionNoClosingBrace() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationExtension = "part_of(something";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidAnnotationExtensionNoStartBrace() {
        assertThrows(ValidationException.class, () -> {
            annotation.annotationExtension = "part_ofsomething)";
            validator.validate(annotation);
        });
    }

    // good taxon field
    @Test
    void validInteractingTaxon() {
        annotation.interactingTaxonId = "taxon:652611";
        validator.validate(annotation);
    }

    @Test
    void validNullInteractingTaxon() {
        annotation.interactingTaxonId = null;
        validator.validate(annotation);
    }

    @Test
    void validEmptyInteractingTaxon() {
        annotation.interactingTaxonId = "";
        validator.validate(annotation);
    }

    // bad taxon field
    @Test
    void invalidInteractingTaxonWhenIncludingLetter() {
        assertThrows(ValidationException.class, () -> {
            annotation.interactingTaxonId = "taxon:652611a";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidInteractingTaxonWhenZero() {
        assertThrows(ValidationException.class, () -> {
            annotation.interactingTaxonId = "taxon:0";
            validator.validate(annotation);
        });
    }

    // valid annotation conversion -------------------------------------------------
    @Test
    void createsValidAnnotationWithMandatoryFields() {
        validator.validate(createValidAnnotation());
    }

    // good date field
    @Test
    void validDate() {
        annotation.date = "20121111";
        validator.validate(annotation);
    }

    @Test
    void validNullDate() {
        annotation.date = null;
        validator.validate(annotation);
    }

    // bad date field
    @Test
    void invalidDateDueToNumberFormatTooShort() {
        assertThrows(ValidationException.class, () -> {
            annotation.date = "2012123";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidDateDueToNumberFormatTooLong() {
        assertThrows(ValidationException.class, () -> {
            annotation.date = "201212345";
            validator.validate(annotation);
        });
    }

    @Test
    void invalidDate() {
        assertThrows(ValidationException.class, () -> {
            annotation.date = "asdf201squiggle21111KABLAMO!";
            validator.validate(annotation);
        });
    }

}
