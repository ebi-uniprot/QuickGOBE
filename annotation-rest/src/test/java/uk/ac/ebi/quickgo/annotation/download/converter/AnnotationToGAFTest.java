package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private Annotation annotation;
    private AnnotationToGAF annotationToGAF;

    @Before
    public void setup() {
        annotation = AnnotationMocker.createValidAnnotation();
        annotation.interactingTaxonId = 0;  //most annotation records don't have an interacting taxon version.
        annotationToGAF = new AnnotationToGAF();
    }

    @Test
    public void uniProtGeneProductWITHOUTVariantOrIsoForm() {
        String gpId = "P04637";
        String gpIdCanonical = "P04637";
        String db = "UniProtKB";
        String gpType = "protein";
        annotation.id = String.format("%s:%s", db, gpId);
        annotation.geneProductId = String.format("%s:%s", db, gpId);
        annotation.assignedBy = db;
        annotation.symbol = gpId;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(db));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpIdCanonical));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(gpId));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(db));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(""));
    }

    @Test
    public void uniProtGeneProductWITHVariantOrIsoForm() {
        String gpId = "P04637-2";
        String gpIdCanonical = "P04637";
        String db = "UniProtKB";
        String gpType = "protein";
        annotation.id = String.format("%s:%s", db, gpId);
        annotation.geneProductId = String.format("%s:%s", db, gpId);
        annotation.assignedBy = db;
        annotation.symbol = gpId;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(db));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpIdCanonical));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(gpId));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(db));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(annotation.id));
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingIntAct() {
        final String gpType = "complex";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(DB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingRNACentral() {
        String gpId = "URS00000064B1_559292";
        String db = "RNAcentral";
        String gpType = "miRNA";
        annotation.id = String.format("%s:%s", db, gpId);
        annotation.geneProductId = String.format("%s:%s", db, gpId);
        annotation.assignedBy = db;
        annotation.symbol = gpId;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(db));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpId));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(gpId));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(db));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(""));
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingIntActWithVariantOrIsoForm() {
        final String gpType = "complex";
        String gpId = "EBI-10043081";
        String gpIdCanonical = "EBI-10043081";
        String db = "IntAct";
        annotation.id = String.format("%s:%s", db, gpId);
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(DB));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpIdCanonical));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(NAME));        //name
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(SYNONYMS));       //synonym
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT_FORM_ID], is(""));
    }

    @Test
    public void createGAFStringWithEmptyQualifier() {
        List<String> undisplayableQualifiers = asList("enables", "part_of", "involved_in", "spurious_value");

        for (String qualifierToBeEmptyInGaf : undisplayableQualifiers) {
            annotation.qualifier = qualifierToBeEmptyInGaf;
            String[] elements = annotationToElements(annotation);
            assertThat(elements[COL_QUALIFIER], is(""));
        }
    }

    @Test
    public void createValidGAFQualifiers() {
        List<String> displayableQualifiersForGAF = asList(
                "contributes_to", "NOT|contributes_to",
                "colocalizes_with", "NOT|colocalizes_with");

        for (String qualifier : displayableQualifiersForGAF) {
            annotation.qualifier = qualifier;
            String[] elements = annotationToElements(annotation);
            assertThat(elements[COL_QUALIFIER], is(qualifier));
        }
    }

    @Test
    public void createGAFStringFromAnnotationWhereAspectIsBiologicalProcess() {
        annotation.goAspect = "biological_process";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ASPECT], is("P"));

    }

    @Test
    public void createGAFStringFromAnnotationWhereAspectIsCellularComponent() {
        annotation.goAspect = "cellular_component";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ASPECT], is("C"));
    }

    @Test
    public void slimmedToGoIdReplacesGoIdIfItExists() {
        final String slimmedToGoId = "GO:0005524";
        annotation.slimmedIds = Collections.singletonList(slimmedToGoId);
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    @Test
    public void multipleSlimmedToGoIdsCreatesEqualQuantityOfAnnotationRecords() {
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

    @Test(expected = IllegalArgumentException.class)
    public void nullGeneProductIdThrowsException() {
        annotation.geneProductId = null;

        annotationToElements(annotation);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGeneProductIdThrowsException() {
        annotation.geneProductId = "";

        annotationToElements(annotation);
    }

    @Test
    public void nullSymbol() {
        annotation.symbol = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(""));
    }

    @Test
    public void testForNullQualifier() {
        annotation.qualifier = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_QUALIFIER], is(""));
    }

    @Test
    public void nullGoId() {
        annotation.goId = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_GO_ID], is(""));
    }

    @Test
    public void nullReference() {
        annotation.reference = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_REFERENCE], is(""));
    }

    @Test
    public void nullGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_EVIDENCE], is(""));
    }

    @Test
    public void nullInWithFrom() {
        annotation.withFrom = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void emptyWithFrom() {
        annotation.withFrom = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void nullAspect() {
        annotation.goAspect = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ASPECT], is(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullGeneProductType() {
        annotation.geneProductId = null;
        annotationToElements(annotation);
    }

    @Test
    public void nullDate() {
        annotation.date = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DATE], is(""));
    }

    @Test
    public void nullAssignedBy() {
        annotation.assignedBy = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ASSIGNED_BY], is(""));
    }

    @Test
    public void nullInExtensions() {
        annotation.extensions = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void emptyExtensions() {
        annotation.extensions = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void nullName() {
        annotation.name = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB_OBJECT_NAME], is(""));
    }

    @Test
    public void taxonHasInteractingValueAlso() {
        annotation.interactingTaxonId = 777;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_TAXON], is("taxon:" + TAXON_ID + "|777"));
    }

    @Test
    public void qualifierContainsNot() {
        annotation.qualifier = "not|part_of";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_QUALIFIER], is("NOT"));
    }

    private void checkReturned(String slimmedToGoId, String converted) {
        String[] elements = converted.split(OUTPUT_DELIMITER, -1);
        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    private String[] annotationToElements(Annotation annotation) {
        return annotationToGAF.apply(annotation, null).get(0)
                .split(OUTPUT_DELIMITER, -1);
    }
}
