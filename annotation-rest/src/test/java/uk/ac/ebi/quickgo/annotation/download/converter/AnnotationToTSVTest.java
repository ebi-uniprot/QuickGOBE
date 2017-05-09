package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.*;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.EXTENSIONS_AS_STRING;

/**
 * @author Tony Wardell
 * Date: 08/05/2017
 * Time: 16:45
 * Created with IntelliJ IDEA.
 */
public class AnnotationToTSVTest {

    private Annotation annotation;
    private AnnotationToTSV annotationToTSV;

    @Before
    public void setup() {
        annotation = AnnotationMocker.createValidAnnotation();
        annotationToTSV = new AnnotationToTSV();
    }

    @Test
    public void createTSVStringFromAnnotationModelContainingIntAct() {
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GENEPRODUCT], is(DB + ":" + ID));
        assertThat(elements[NonSlimmedColumns.COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[NonSlimmedColumns.COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[NonSlimmedColumns.COL_GO_ID], is(GO_ID));
        assertThat(elements[NonSlimmedColumns.COL_NAME], is(GO_NAME));
        assertThat(elements[NonSlimmedColumns.COL_EVIDENCE], is(EVIDENCE_CODE + "(" + GO_EVIDENCE + ")"));
        assertThat(elements[NonSlimmedColumns.COL_REFERENCE], is(REFERENCE));
        assertThat(elements[NonSlimmedColumns.COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[NonSlimmedColumns.COL_TAXON], is(Integer.toString(TAXON_ID)));
        assertThat(elements[NonSlimmedColumns.COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[NonSlimmedColumns.COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
//        assertThat(elements[NonSlimmedColumns.COL_DB_OBJECT_NAME], is(""));      //Not yet available - requires GP data
//        assertThat(elements[NonSlimmedColumns.COL_DB_OBJECT_SYNONYM], is(""));   //Not yet available - requires GP data
//        assertThat(elements[NonSlimmedColumns.COL_DB_OBJECT_TYPE], is(gpType));  //Not yet available - requires GP data
        assertThat(elements[NonSlimmedColumns.COL_DATE], equalTo(DATE_AS_STRING));
    }

    @Test
    public void createTSVSTringFromAnnotationModelContainingSlimmedToIds(){
        annotation.slimmedIds = SLIMMED_TO_IDS;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[SlimmedColumns.COL_GENEPRODUCT], is(DB + ":" + ID));
        assertThat(elements[SlimmedColumns.COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[SlimmedColumns.COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[SlimmedColumns.COL_GO_ID], is(SLIMMED_TO_IDS.get(0)));
        assertThat(elements[SlimmedColumns.COL_SLIMMED_FROM], is(GO_ID));
        assertThat(elements[SlimmedColumns.COL_NAME], is(GO_NAME));
        assertThat(elements[SlimmedColumns.COL_EVIDENCE], is(EVIDENCE_CODE + " (" + GO_EVIDENCE + ")"));
        assertThat(elements[SlimmedColumns.COL_REFERENCE], is(REFERENCE));
        assertThat(elements[SlimmedColumns.COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[SlimmedColumns.COL_TAXON], is(Integer.toString(TAXON_ID)));
        assertThat(elements[SlimmedColumns.COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[SlimmedColumns.COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        //        assertThat(elements[SlimmedColumns.COL_DB_OBJECT_NAME], is(""));             //Not yet available - requires GP data
        //        assertThat(elements[SlimmedColumns.COL_DB_OBJECT_SYNONYM], is(""));          //Not yet available - requires GP data
        //        assertThat(elements[SlimmedColumns.COL_DB_OBJECT_TYPE], is(gpType));         //Not yet available - requires GP data
        assertThat(elements[SlimmedColumns.COL_DATE], equalTo(DATE_AS_STRING));
    }

    @Test
    public void multipleSlimmedToGoIdsCreatesEqualQuantityOfAnnotationRecords() {
        final String slimmedToGoId0 = "GO:0005524";
        final String slimmedToGoId1 = "GO:1005524";
        final String slimmedToGoId2 = "GO:2005524";
        annotation.slimmedIds = Arrays.asList(slimmedToGoId0, slimmedToGoId1, slimmedToGoId2);
        List<String> converted = annotationToTSV.apply(annotation);
        assertThat(converted, hasSize(annotation.slimmedIds.size()));
        checkReturned(slimmedToGoId0, converted.get(0));
        checkReturned(slimmedToGoId1, converted.get(1));
        checkReturned(slimmedToGoId2, converted.get(2));
    }

    @Test
    public void nullGeneProductId() {
        annotation.geneProductId = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GENEPRODUCT], is(""));
    }

    @Test
    public void emptyGeneProductId() {
        annotation.geneProductId = "";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GENEPRODUCT], is(""));
    }

    @Test
    public void nullSymbol() {
        annotation.symbol = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_DB_OBJECT_SYMBOL], is(""));
    }

    @Test
    public void nullQualifier() {
        annotation.qualifier = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_QUALIFIER], is(""));
    }

    @Test
    public void nullGoId() {
        annotation.goId = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GO_ID], is(""));
    }

    @Test
    public void nullReference() {
        annotation.reference = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_REFERENCE], is(""));
    }

    @Test
    public void nullEvidenceCode() {
        annotation.evidenceCode = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_EVIDENCE], is("(" + GO_EVIDENCE + ")"));
    }

    @Test
    public void emptyEvidenceCode() {
        annotation.evidenceCode = "";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_EVIDENCE], is("(" + GO_EVIDENCE + ")"));
    }

    @Test
    public void nullGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_EVIDENCE], is(EVIDENCE_CODE));
    }

    @Test
    public void emptyGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_EVIDENCE], is(EVIDENCE_CODE));
    }


    @Test
    public void nullWithFrom() {
        annotation.withFrom = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_WITH], is(""));
    }

    @Test
    public void emptyWithFrom() {
        annotation.withFrom = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_WITH], is(""));
    }

    @Test
    public void emptyTaxonId() {
        annotation.taxonId = 0;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_TAXON], is(""));
    }

    @Test
    public void nullAssignedBy() {
        annotation.assignedBy = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_ASSIGNED_BY], is(""));
    }

    @Test
    public void nullInExtensions() {
        annotation.extensions = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void emptyExtensions() {
        annotation.extensions = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void nullDate() {
        annotation.date = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_DATE], is(""));
    }

    private String[] annotationToElements(Annotation annotation) {
        return annotationToTSV.apply(annotation).get(0)
                              .split(AnnotationToTSV.OUTPUT_DELIMITER, -1);
    }

    private void checkReturned(String slimmedToGoId, String converted) {
        String[] elements = converted.split(AnnotationToTSV.OUTPUT_DELIMITER, -1);
        assertThat(elements[SlimmedColumns.COL_GO_ID], is(slimmedToGoId));
    }

    private class NonSlimmedColumns{
        private static final int COL_GENEPRODUCT = 0;
        private static final int COL_DB_OBJECT_SYMBOL = 1;
        private static final int COL_QUALIFIER = 2;
        private static final int COL_GO_ID = 3;
        private static final int COL_NAME = 4;
        private static final int COL_EVIDENCE = 5;
        private static final int COL_REFERENCE = 6;
        private static final int COL_WITH = 7;
        private static final int COL_TAXON = 8;
        private static final int COL_ASSIGNED_BY = 9;
        private static final int COL_ANNOTATION_EXTENSION = 10;
        private static final int COL_DATE = 11;
    }

    private class SlimmedColumns{
        private static final int COL_GENEPRODUCT = 0;
        private static final int COL_DB_OBJECT_SYMBOL = 1;
        private static final int COL_QUALIFIER = 2;
        private static final int COL_GO_ID = 3;
        private static final int COL_NAME = 4;
        private static final int COL_SLIMMED_FROM = 5;
        private static final int COL_EVIDENCE = 6;
        private static final int COL_REFERENCE = 7;
        private static final int COL_WITH = 8;
        private static final int COL_TAXON = 9;
        private static final int COL_ASSIGNED_BY = 10;
        private static final int COL_ANNOTATION_EXTENSION = 11;
        private static final int COL_DATE = 12;
    }
}
