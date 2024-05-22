package uk.ac.ebi.quickgo.annotation.download.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;
import uk.ac.ebi.quickgo.annotation.model.GeneProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToGAF.OUTPUT_DELIMITER;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.*;

/**
 * @author Tony Wardell
 * Date: 19/01/2017
 * Time: 14:19
 * Created with IntelliJ IDEA.
 */
class AnnotationToGAFTest {

    private static final int COL_DB = 0;
    private static final int COL_DB_OBJECT_ID = 1;
    private static final int COL_DB_OBJECT_SYMBOL = 2;
    private static final int COL_QUALIFIER = 3;
    private static final int COL_GO_ID = 4;
    private static final int COL_REFERENCE = 5;
    private static final int COL_EVIDENCE = 6;
    private static final int COL_WITH = 7;
    private static final int COL_ASPECT = 8;
    private static final int COL_DB_OBJECT_NAME = 9;
    private static final int COL_DB_OBJECT_SYNONYM = 10;
    private static final int COL_DB_OBJECT_TYPE = 11;
    private static final int COL_TAXON = 12;
    private static final int COL_DATE = 13;
    private static final int COL_ASSIGNED_BY = 14;
    private static final int COL_ANNOTATION_EXTENSION = 15;
    private static final int COL_GENE_PRODUCT_FORM_ID = 16;

    private AnnotationToGAF annotationToGAF;

    @BeforeEach
    void setup() {
        annotationToGAF = new AnnotationToGAF();
    }

    @Test
    void uniProtGeneProductWithoutIsoForm() {
        Annotation annotation = AnnotationMocker.createValidUniProtAnnotationWithoutIsoForm();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_UNIPROTKB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_UNIPROTKB_CANONICAL));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("C"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(GeneProduct.GeneProductType.PROTEIN.getName()));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(""));
    }

    @Test
    void uniProtGeneProductWithIsoForm() {
        Annotation annotation = AnnotationMocker.createValidUniProtAnnotationWithIsoForm();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_UNIPROTKB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_UNIPROTKB_CANONICAL));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("C"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(GeneProduct.GeneProductType.PROTEIN.getName()));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(annotation.id));
    }

    @Test
    void complexPortal() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_COMPLEX_PORTAL));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_COMPLEX_PORTAL));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("C"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(GeneProduct.GeneProductType.COMPLEX.getName()));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
    }

    @Test
    void rNACentral() {
        Annotation annotation = AnnotationMocker.createValidRNACentralAnnotation();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_RNA_CENTRAL));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_RNA_CENTRAL));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("C"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(GeneProduct.GeneProductType.RNA.getName()));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"enables", "part_of", "involved_in", "located_in"})
    void createGAFStringWithQualifiers(String qualifierToBeEmptyInGaf) {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();

        annotation.qualifier = qualifierToBeEmptyInGaf;
        String[] elements = annotationToDownloadColumns(annotation);
        assertThat(elements[COL_QUALIFIER], is(qualifierToBeEmptyInGaf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"contributes_to", "NOT|contributes_to", "colocalizes_with", "NOT|colocalizes_with"})
    void createValidGAFQualifiers(String qualifier) {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();

        annotation.qualifier = qualifier;
        String[] elements = annotationToDownloadColumns(annotation);
        assertThat(elements[COL_QUALIFIER], is(qualifier));
    }

    @Test
    void createGAFStringFromAnnotationWhereAspectIsBiologicalProcess() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goAspect = "biological_process";

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASPECT], is("P"));

    }

    @Test
    void createGAFStringFromAnnotationWhereAspectIsCellularComponent() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goAspect = "cellular_component";

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASPECT], is("C"));
    }

    @Test
    void slimmedToGoIdReplacesGoIdIfItExists() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        final String slimmedToGoId = "GO:0005524";
        annotation.slimmedIds = Collections.singletonList(slimmedToGoId);

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    @Test
    void multipleSlimmedToGoIdsCreatesEqualQuantityOfAnnotationRecords() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        final String slimmedToGoId0 = "GO:0005524";
        final String slimmedToGoId1 = "GO:1005524";
        final String slimmedToGoId2 = "GO:2005524";
        annotation.slimmedIds = asList(slimmedToGoId0, slimmedToGoId1, slimmedToGoId2);

        List<String> converted = annotationToGAF.apply(annotation, null);

        assertThat(converted, hasSize(annotation.slimmedIds.size()));
        checkReturned(slimmedToGoId0, converted.get(0));
        checkReturned(slimmedToGoId1, converted.get(1));
        checkReturned(slimmedToGoId2, converted.get(2));
    }

    @Test
    void interactingTaxId() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.interactingTaxonId = 9877;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID + "|taxon:" + 9877));
    }

    @Test
    void nullGeneProductIdThrowsException() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.geneProductId = null;
        annotation.setGeneProduct(null);

        Assertions.assertThrows(NullPointerException.class, () -> annotationToDownloadColumns(annotation));
    }

    @Test
    void nullSymbol() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.symbol = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(""));
    }

    @Test
    void testForNullQualifier() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.qualifier = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_QUALIFIER], is(""));
    }

    @Test
    void nullGoId() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goId = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_ID], is(""));
    }

    @Test
    void nullReference() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.reference = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_REFERENCE], is(""));
    }

    @Test
    void nullOrEmptyGoEvidenceIsNotValidGafRecord() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();

        annotation.goEvidence = null;
        List<String> records = annotationToGAF.apply(annotation, null);
        assertThat(records, Matchers.notNullValue());
        assertThat(records, Matchers.empty());

        annotation.goEvidence = "";
        records = annotationToGAF.apply(annotation, null);
        assertThat(records, Matchers.notNullValue());
        assertThat(records, Matchers.empty());
    }

    @Test
    void nullInWithFrom() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.withFrom = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    void emptyWithFrom() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.withFrom = new ArrayList<>();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    void nullAspect() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goAspect = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASPECT], is(""));
    }

    @Test
    void nullDate() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.date = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DATE], is(""));
    }

    @Test
    void nullAssignedBy() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.assignedBy = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASSIGNED_BY], is(""));
    }

    @Test
    void nullInExtensions() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.extensions = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    void emptyExtensions() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.extensions = new ArrayList<>();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    void nullName() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.name = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB_OBJECT_NAME], is(""));
    }

    @Test
    void taxonHasInteractingValueAlso() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.interactingTaxonId = 777;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID + "|taxon:777"));
    }

    @Test
    void qualifierContainsNotInUpper_remainingAsItIs() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.qualifier = "not|part_of";

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_QUALIFIER], is("NOT|part_of"));
    }

    private void checkReturned(String slimmedToGoId, String converted) {
        String[] elements = converted.split(OUTPUT_DELIMITER, -1);
        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    private String[] annotationToDownloadColumns(Annotation annotation) {
        return annotationToGAF.apply(annotation, null).get(0)
                .split(OUTPUT_DELIMITER, -1);
    }
}
