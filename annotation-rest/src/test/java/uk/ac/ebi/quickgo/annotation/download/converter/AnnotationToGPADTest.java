package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;

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

    private Annotation annotation;
    private AnnotationToGPAD annotationToGPAD;

    @Before
    public void setup() {
        annotation = AnnotationMocker.createValidAnnotation();
        annotationToGPAD = new AnnotationToGPAD();
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingIntAct() {
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(DB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(ECO_ID));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_INTERACTING_DB], is("taxon:" + Integer.toString(INTERACTING_TAXON_ID)));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GO_EVIDENCE], is("goEvidence=" + GO_EVIDENCE));
    }

    @Test
    public void slimmedToGoIdReplacesGoIdIfItExists() {
        final String slimmedToGoId = "GO:0005524";
        annotation.slimmedIds = Collections.singletonList(slimmedToGoId);
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullGeneProductId() {
        annotation.geneProductId = null;

        annotationToElements(annotation);

    }

    @Test
    public void nullQualifier() {
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
    public void nullEvidence() {
        annotation.evidenceCode = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_EVIDENCE], is(""));
    }

    @Test
    public void nullWithFrom() {
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
    public void emptyInteractingTaxonId() {
        annotation.interactingTaxonId = 0;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_INTERACTING_DB], is(""));
    }

    @Test
    public void lowestInteractingTaxonIdIsPopulatedCorrectly() {
        annotation.interactingTaxonId = 1;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_INTERACTING_DB], is("taxon:1"));
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
    public void nullGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_GO_EVIDENCE], is("goEvidence="));
    }

    @Test
    public void emptyExtensions() {
        annotation.extensions = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void nullInExtensions() {
        annotation.extensions = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void multipleSlimmedToGoIdsCreatesEqualQuantityOfAnnotationRecords() {
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

    private String[] annotationToElements(Annotation annotation) {
        return annotationToGPAD.apply(annotation, null)
                .get(0)
                .split(AnnotationToGAF.OUTPUT_DELIMITER, -1);
    }

}
