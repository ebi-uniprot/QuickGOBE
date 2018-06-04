package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.*;

/**
 * @author Tony Wardell
 * Date: 23/01/2017
 * Time: 09:51
 * Created with IntelliJ IDEA.
 */
public class AnnotationToGPADTest {

    private static final int COL_DB = 0;
    private static final int COL_DB_OBJECT_ID = 1;
    private static final int COL_QUALIFIER = 2;
    private static final int COL_GO_ID = 3;
    private static final int COL_REFERENCE = 4;
    private static final int COL_EVIDENCE = 5;
    private static final int COL_WITH = 6;
    private static final int COL_INTERACTING_DB = 7;
    private static final int COL_DATE = 8;
    private static final int COL_ASSIGNED_BY = 9;
    private static final int COL_ANNOTATION_EXTENSION = 10;
    private static final int COL_GO_EVIDENCE = 11;

    private AnnotationToGPAD annotationToGPAD;

    @Before
    public void setup() {
        annotationToGPAD = new AnnotationToGPAD();
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingComplex() {
        Annotation annotation = createValidComplexPortalAnnotation();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_COMPLEX_PORTAL));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_COMPLEX_PORTAL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(ECO_ID));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_INTERACTING_DB], is(""));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GO_EVIDENCE], is("goEvidence=" + GO_EVIDENCE));
    }

    @Test
    public void uniProtGeneProductAnnotationWithIsoForm() {
        Annotation annotation = createValidUniProtAnnotationWithIsoForm();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_UNIPROTKB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_UNIPROTKB_WITH_ISOFORM));
    }

    @Test
    public void uniProtGeneProductAnnotationWithoutGeneProductIsoForm() {
        Annotation annotation = createValidUniProtAnnotationWithoutIsoForm();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DB], is(DB_UNIPROTKB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID_UNIPROTKB_WITHOUT_ISOFORM));
    }

    @Test
    public void slimmedToGoIdReplacesGoIdIfItExists() {
        Annotation annotation = createValidComplexPortalAnnotation();
        final String slimmedToGoId = "GO:0005524";
        annotation.slimmedIds = Collections.singletonList(slimmedToGoId);

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    @Test(expected = NullPointerException.class)
    public void nullGeneProductId() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.geneProductId = null;
        annotation.setGeneProduct(null);

        annotationToDownloadColumns(annotation);

    }

    @Test
    public void nullQualifier() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.qualifier = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_QUALIFIER], is(""));
    }

    @Test
    public void nullGoId() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.goId = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_ID], is(""));
    }

    @Test
    public void nullReference() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.reference = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_REFERENCE], is(""));
    }

    @Test
    public void nullEvidence() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.evidenceCode = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_EVIDENCE], is(""));
    }

    @Test
    public void nullWithFrom() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.withFrom = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void emptyWithFrom() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.withFrom = new ArrayList<>();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void specifiedInteractingTaxonId() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.interactingTaxonId = INTERACTING_TAXON_ID;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_INTERACTING_DB], is(("taxon:" + INTERACTING_TAXON_ID)));
    }

    @Test
    public void lowestInteractingTaxonIdIsPopulatedCorrectly() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.interactingTaxonId = 1;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_INTERACTING_DB], is("taxon:1"));
    }

    @Test
    public void nullDate() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.date = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_DATE], is(""));
    }

    @Test
    public void nullAssignedBy() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.assignedBy = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ASSIGNED_BY], is(""));
    }

    @Test
    public void nullGoEvidence() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.goEvidence = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_GO_EVIDENCE], is("goEvidence="));
    }

    @Test
    public void emptyExtensions() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.extensions = new ArrayList<>();

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void nullInExtensions() {
        Annotation annotation = createValidComplexPortalAnnotation();
        annotation.extensions = null;

        String[] elements = annotationToDownloadColumns(annotation);

        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void multipleSlimmedToGoIdsCreatesEqualQuantityOfAnnotationRecords() {
        Annotation annotation = createValidComplexPortalAnnotation();
        final String slimmedToGoId0 = "GO:0005524";
        final String slimmedToGoId1 = "GO:1005524";
        final String slimmedToGoId2 = "GO:2005524";
        annotation.slimmedIds = Arrays.asList(slimmedToGoId0, slimmedToGoId1, slimmedToGoId2);

        List<String> converted = annotationToGPAD.apply(annotation, null);

        assertThat(converted, hasSize(annotation.slimmedIds.size()));
        checkReturned(slimmedToGoId0, converted.get(0));
        checkReturned(slimmedToGoId1, converted.get(1));
        checkReturned(slimmedToGoId2, converted.get(2));
    }

    private void checkReturned(String slimmedToGoId, String converted) {
        String[] elements = converted.split(AnnotationToGAF.OUTPUT_DELIMITER, -1);

        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    private String[] annotationToDownloadColumns(Annotation annotation) {
        return annotationToGPAD.apply(annotation, null)
                .get(0)
                .split(AnnotationToGAF.OUTPUT_DELIMITER, -1);
    }

}
