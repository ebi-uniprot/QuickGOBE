package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.index.annotation.AnnotationMocker.createValidAnnotation;

/**
 * Created 21/04/16
 * @author Edd
 */
public class AnnotationValidatorTest {

    private AnnotationValidator validator;
    private Annotation annotation;

    @Before
    public void setUp() {
        validator = new AnnotationValidator();
        annotation = createValidAnnotation();
    }

    // check required fields -------------------------------------------------
    @Test(expected = DocumentReaderException.class)
    public void nullAnnotationThrowsException() throws Exception {
        validator.validate(null);
    }

    @Test(expected = ValidationException.class)
    public void nullDatabaseThrowsException() throws Exception {
        annotation.db = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullDatabaseIDThrowsException() throws Exception {
        annotation.dbObjectId = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullQualifierThrowsException() throws Exception {
        annotation.qualifier = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullGODIThrowsException() throws Exception {
        annotation.goId = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullDatabaseReferencesException() throws Exception {
        annotation.dbReferences = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullEcoEvidenceThrowsException() throws Exception {
        annotation.ecoId = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullAssignedByThrowsException() throws Exception {
        annotation.assignedBy = null;
        validator.validate(annotation);
    }

    // bad qualifier field -------------------------------------------------
    @Test(expected = ValidationException.class)
    public void invalidNotCasingCausesValidationException() {
        annotation.qualifier = "not|contributes_to";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidQualifierDelimiterCausesValidationException() {
        annotation.qualifier = "NOT,contributes_to";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidQualifierValueCausesValidationException() {
        annotation.qualifier = "cntributes_to";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void tooManyQualifiersCausesValidationException() {
        annotation.qualifier = "NOT|enables|contributes_to";
        validator.validate(annotation);
    }

    // good qualifier field -------------------------------------------------
    @Test
    public void validatesInvolvedInQualifier() {
        annotation.qualifier = "involved_in";
        validator.validate(annotation);
    }

    @Test
    public void validatesEnablesQualifier() {
        annotation.qualifier = "enables";
        validator.validate(annotation);
    }

    @Test
    public void validatesPartOfQualifier() {
        annotation.qualifier = "part_of";
        validator.validate(annotation);
    }

    @Test
    public void validatesContributesToQualifier() {
        annotation.qualifier = "contributes_to";
        validator.validate(annotation);
    }

    @Test
    public void validatesColocalizesWithQualifier() {
        annotation.qualifier = "colocalizes_with";
        validator.validate(annotation);
    }

    @Test
    public void validatesNotInvolvedInQualifier() {
        annotation.qualifier = "NOT|involved_in";
        validator.validate(annotation);
    }

    @Test
    public void validatesNotEnablesQualifier() {
        annotation.qualifier = "NOT|enables";
        validator.validate(annotation);
    }

    @Test
    public void validatesNotPartOfQualifier() {
        annotation.qualifier = "NOT|part_of";
        validator.validate(annotation);
    }

    @Test
    public void validatesNotContributesToQualifier() {
        annotation.qualifier = "NOT|contributes_to";
        validator.validate(annotation);
    }

    @Test
    public void validatesNotColocalizesWithQualifier() {
        annotation.qualifier = "NOT|colocalizes_with";
        validator.validate(annotation);
    }

    // good with field -------------------------------------------------
    @Test
    public void validNullWithField() {
        annotation.with = null;
        validator.validate(annotation);
    }

    @Test
    public void validEmptyWithField() {
        annotation.with = "";
        validator.validate(annotation);
    }

    @Test
    public void validWithMultiTermedComponent() {
        annotation.with = "GO:1234,IP:1234";
        validator.validate(annotation);
    }

    @Test
    public void validRNACentralMultiTermedComponent() {
        annotation.with = "RNAcentral:URS00001989EA_10090,RNAcentral:URS00004AD914_10090," +
                "RNAcentral:URS00004BCD9C_10090,RNAcentral:URS00005B3525_10090";
        validator.validate(annotation);
    }

    @Test
    public void validWithNotSupplied() {
        annotation.with = "With:Not_supplied";
        validator.validate(annotation);
    }

    @Test
    public void validWithMultiSingleTermedComponents() {
        annotation.with = "GO:1234|IP:1234";
        validator.validate(annotation);
    }

    @Test
    public void validWithMultiMultiTermedComponents() {
        annotation.with = "GO:1234,GO:1235|GO:1236,IP:1234";
        validator.validate(annotation);
    }

    @Test
    public void validWithSingleTerm() {
        annotation.with = "GO:1234-34";
        validator.validate(annotation);
    }

    @Test
    public void validWithSingleTermEC() {
        annotation.with = "EC:3.1.21.3";
        validator.validate(annotation);
    }

    // bad with field -------------------------------------------------
    @Test(expected = ValidationException.class)
    public void invalidWithIntraTermDelimiterCausesValidationException() {
        annotation.with = "GO-asdfsdf";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidWithInterTermDelimiterCausesValidationException() {
        annotation.with = "GO:1234\tIP:1234";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidWithLeftPartCausesValidationException() {
        annotation.with = "asdf+:asdfsdf";
        validator.validate(annotation);
    }

    // good annotation properties field -------------------------------------------------
    @Test
    public void validNullAnnotationProperties() {
        // if we adhere to the column specification header, this can be null
        annotation.annotationProperties = null;
        validator.validate(annotation);
    }

    @Test
    public void validEmptyAnnotationProperties() {
        // if we adhere to the column specification header, this can be empty
        annotation.annotationProperties = "";
        validator.validate(annotation);
    }

    @Test
    public void validRequiredAnnotationProperties() {
        validator.validate(annotation);
    }

    @Test
    public void validAnnotationPropertiesAndDbObjectSymbol() {
        annotation.annotationProperties += "|db_object_symbol=moeA5";
        validator.validate(annotation);
    }

    @Test
    public void validAnnotationPropertiesAndDbSubset() {
        annotation.annotationProperties += "|db_subset=TrEMBL";
        validator.validate(annotation);
    }

    @Test
    public void validAnnotationPropertyWithAdditionalIgnoredTerms() {
        annotation.annotationProperties += "|a=b|c=d,e=f|g=h";
        validator.validate(annotation);
    }

    // bad annotation properties field -------------------------------------------------
    @Test(expected = ValidationException.class)
    public void invalidSingleTermedComponentAnnotationProperty() {
        annotation.annotationProperties = "go_evidence:IPI";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidAnnotationPropertyDueToMissingGOEvidence() {
        annotation.annotationProperties = "taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein";;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidAnnotationPropertyDueToMissingTaxonId() {
        annotation.annotationProperties = "go_evidence=IEA|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein";;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidAnnotationPropertyDueToMissingDbObjectType() {
        annotation.annotationProperties = "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5";;
        validator.validate(annotation);
    }

    // good annotation extension field -------------------------------------------------
    @Test
    public void validNullAnnotationExtension() {
        annotation.annotationExtension = null;
        validator.validate(annotation);
    }

    @Test
    public void validEmptyAnnotationExtension() {
        annotation.annotationExtension = "";
        validator.validate(annotation);
    }

    @Test
    public void validSingleTermedComponentAnnotationExtensionPartOf() {
        annotation.annotationExtension = "part_of(something)";
        validator.validate(annotation);
    }

    @Test
    public void validSingleTermedComponentAnnotationExtensionGO() {
        annotation.annotationExtension = "GO:0016540";
        validator.validate(annotation);
    }

    @Test
    public void validMultiTermedComponentAnnotationExtensionGO() {
        annotation.annotationExtension = "GO:0016540,GO:0016541";
        validator.validate(annotation);
    }

    @Test
    public void validSingleTermedComponentAnnotationExtensionHasInput() {
        annotation.annotationExtension = "has_input(PomBase:SPCC1183.12)";
        validator.validate(annotation);
    }

    @Test
    public void validSingleTermedComponentsAnnotationExtension() {
        annotation.annotationExtension = "part_of(something)|part_of(something_else)";
        validator.validate(annotation);
    }

    @Test
    public void validMultiTermedComponentAnnotationExtension() {
        annotation.annotationExtension = "part_of(something),part_of(something_else)";
        validator.validate(annotation);
    }

    @Test
    public void validMultiTermedComponentsAnnotationExtension() {
        annotation.annotationExtension = "part_of(something),part_of(something_else)|part_of(yet_another_thing)";
        validator.validate(annotation);
    }

    // bad annotation extension field -------------------------------------------------
    @Test(expected = ValidationException.class)
    public void invalidAnnotationExtensionNoClosingBrace() {
        annotation.annotationExtension = "part_of(something";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidAnnotationExtensionNoStartBrace() {
        annotation.annotationExtension = "part_ofsomething)";
        validator.validate(annotation);
    }

    // good taxon field
    @Test
    public void validInteractingTaxon() {
        annotation.interactingTaxonId = "taxon:652611";
        validator.validate(annotation);
    }

    @Test
    public void validNullInteractingTaxon() {
        annotation.interactingTaxonId = null;
        validator.validate(annotation);
    }

    @Test
    public void validEmptyInteractingTaxon() {
        annotation.interactingTaxonId = "";
        validator.validate(annotation);
    }

    // bad taxon field
    @Test(expected = ValidationException.class)
    public void invalidInteractingTaxon() {
        annotation.interactingTaxonId = "taxon:652611a";
        validator.validate(annotation);
    }

    // valid annotation conversion -------------------------------------------------
    @Test
    public void createsValidAnnotationWithMandatoryFields() {
        validator.validate(createValidAnnotation());
    }

}