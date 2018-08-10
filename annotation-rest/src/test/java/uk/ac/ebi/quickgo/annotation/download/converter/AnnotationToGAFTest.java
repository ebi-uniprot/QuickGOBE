package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;
import uk.ac.ebi.quickgo.annotation.model.GeneProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

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
public class AnnotationToGAFTest {

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

    @Before
    public void setup() {
        annotationToGAF = new AnnotationToGAF();
    }

    @Test
    public void uniProtGeneProductWithoutIsoForm() {
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
    public void uniProtGeneProductWithIsoForm() {
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
    public void complexPortal() {
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
    public void rNACentral() {
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
        assertThat(elements[COL_DB_OBJECT_TYPE], is(GeneProduct.GeneProductType.MI_RNA.getName()));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(""));
    }

    @Test
    public void createGAFStringWithEmptyQualifier() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        List<String> undisplayableQualifiers = asList("enables", "part_of", "involved_in", "spurious_value");

        for (String qualifierToBeEmptyInGaf : undisplayableQualifiers) {
            annotation.qualifier = qualifierToBeEmptyInGaf;
            String[] elements = annotationToDownloadColumns(annotation);
            assertThat(elements[COL_QUALIFIER], is(""));
        }
    }

    @Test
    public void createValidGAFQualifiers() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        List<String> displayableQualifiersForGAF = asList(
                "contributes_to", "NOT|contributes_to",
                "colocalizes_with", "NOT|colocalizes_with");

        for (String qualifier : displayableQualifiersForGAF) {
            annotation.qualifier = qualifier;
            String[] elements = annotationToDownloadColumns(annotation);
            assertThat(elements[COL_QUALIFIER], is(qualifier));
        }
    }

    @Test
    public void createGAFStringFromAnnotationWhereAspectIsBiologicalProcess() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goAspect = "biological_process";

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASPECT], is("P"));

    }

    @Test
    public void createGAFStringFromAnnotationWhereAspectIsCellularComponent() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goAspect = "cellular_component";

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASPECT], is("C"));
    }

    @Test
    public void slimmedToGoIdReplacesGoIdIfItExists() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        final String slimmedToGoId = "GO:0005524";
        annotation.slimmedIds = Collections.singletonList(slimmedToGoId);

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    @Test
    public void multipleSlimmedToGoIdsCreatesEqualQuantityOfAnnotationRecords() {
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
    public void interactingTaxId() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.interactingTaxonId = 9877;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID + "|taxon:" + 9877));
    }

    @Test(expected = NullPointerException.class)
    public void nullGeneProductIdThrowsException() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.geneProductId = null;
        annotation.setGeneProduct(null);

        annotationToDownloadColumns(annotation);

    }

    @Test
    public void nullSymbol() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.symbol = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(""));
    }

    @Test
    public void testForNullQualifier() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.qualifier = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_QUALIFIER], is(""));
    }

    @Test
    public void nullGoId() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goId = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_ID], is(""));
    }

    @Test
    public void nullReference() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.reference = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_REFERENCE], is(""));
    }

    @Test
    public void nullOrEmptyGoEvidenceIsNotValidGafRecord() {
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
    public void nullInWithFrom() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.withFrom = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void emptyWithFrom() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.withFrom = new ArrayList<>();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void nullAspect() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.goAspect = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASPECT], is(""));
    }

    @Test
    public void nullDate() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.date = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DATE], is(""));
    }

    @Test
    public void nullAssignedBy() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.assignedBy = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASSIGNED_BY], is(""));
    }

    @Test
    public void nullInExtensions() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.extensions = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void emptyExtensions() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.extensions = new ArrayList<>();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void nullName() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.name = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB_OBJECT_NAME], is(""));
    }

    @Test
    public void taxonHasInteractingValueAlso() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.interactingTaxonId = 777;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID + "|taxon:777"));
    }

    @Test
    public void qualifierContainsNot() {
        Annotation annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotation.qualifier = "not|part_of";

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_QUALIFIER], is("NOT"));
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
