package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

/**
 * Created 21/04/16
 * @author Edd
 */
public class AnnotationValidatorTest {

    private static final String QUALIFIER_CONTRIBUTES_TO = "contributes_to";
    private static final String QUALIFIER_COLOCALIZES_WITH = "colocalizes_with";
    private static final String GOOD_QUALIFIER_LIST = "NOT|involved_in|enables|part_of|contributes_to|colocalizes_with";
    private static final String GOOD_QUALIFIER = "involved_in";
    private AnnotationValidator validator;
    private Annotation annotation;

    @Before
    public void setUp() {
        validator = new AnnotationValidator();
        annotation = createValidAnnotation();
    }

    // check required fields -------------------------------------------------
    @Test(expected = DocumentReaderException.class)
    public void nullGeneProductThrowsException() throws Exception {
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
        annotation.eco = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullDateThrowsException() throws Exception {
        annotation.date = null;
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void nullAssignedByThrowsException() throws Exception {
        annotation.assignedBy = null;
        validator.validate(annotation);
    }

    // bad qualifier field -------------------------------------------------
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

    // good qualifier field -------------------------------------------------
    @Test
    public void validatesAllGoodQualifiers() {
        annotation.qualifier = GOOD_QUALIFIER_LIST;
        validator.validate(annotation);
    }

    @Test
    public void validatesSingleGoodQualifiers() {
        annotation.qualifier = GOOD_QUALIFIER;
        validator.validate(annotation);
    }

    // good with field -------------------------------------------------
    @Test
    public void validWithMultiTerms() {
        annotation.with = "GO:1234,IP:1234";
        validator.validate(annotation);
    }

    @Test
    public void validWithSingleTerm() {
        annotation.with = "GO:1234";
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
        annotation.with = "GO:1234|IP:1234";
        validator.validate(annotation);
    }

    @Test(expected = ValidationException.class)
    public void invalidWithLeftPartCausesValidationException() {
        annotation.with = "asdf:asdfsdf";
        validator.validate(annotation);
    }

    // valid annotation conversion -------------------------------------------------
    @Test
    public void createsValidAnnotationWithMandatoryFields() {
        validator.validate(createValidAnnotation());
    }

    private Annotation createValidAnnotation() {
        Annotation annotation = new Annotation();
        annotation.db = "IntAct";
        annotation.dbObjectId = "EBI-10043081";
        annotation.dbReferences = "PMID:12871976";
        annotation.qualifier = "enables";
        annotation.goId = "GO:0000977";
        annotation.eco = "ECO:0000353";
        annotation.with = "GO:0036376,GO:1990573";
        annotation.interactingTaxonId = null;
        annotation.date = "20150122";
        annotation.assignedBy = "IntAct";
        annotation.annotationExtension = "occurs_in(CL:1000428)";
        annotation.annotationProperties = "go_evidence=IPI";

        return annotation;
    }

}