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
import static uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToTSV.*;
import static uk.ac.ebi.quickgo.annotation.model.AnnotationMocker.*;

/**
 * Test AnnotationToTSV.
 *
 * @author Tony Wardell
 * Date: 08/05/2017
 * Time: 16:45
 * Created with IntelliJ IDEA.
 */
public class AnnotationToTSVTest {

    private Annotation annotation;
    private AnnotationToTSV annotationToTSV;

    private static List<String[]> unslimmedFieldNames2Data = new ArrayList<>();
    static {
        unslimmedFieldNames2Data.add(new String[]{GENE_PRODUCT_ID_FIELD_NAME, DB + ":" + ID});
        unslimmedFieldNames2Data.add(new String[]{SYMBOL_FIELD_NAME,SYMBOL});
        unslimmedFieldNames2Data.add(new String[]{QUALIFIER_FIELD_NAME,QUALIFIER});
        unslimmedFieldNames2Data.add(new String[]{GO_TERM_FIELD_NAME,GO_ID});
        unslimmedFieldNames2Data.add(new String[]{GO_NAME_FIELD_NAME,GO_NAME});
        unslimmedFieldNames2Data.add(new String[]{ECO_ID_FIELD_NAME,ECO_ID});
        unslimmedFieldNames2Data.add(new String[]{GO_EVIDENCE_CODE_FIELD_NAME,GO_EVIDENCE});
        unslimmedFieldNames2Data.add(new String[]{REFERENCE_FIELD_NAME,REFERENCE});
        unslimmedFieldNames2Data.add(new String[]{WITH_FROM_FIELD_NAME,WITH_FROM_AS_STRING});
        unslimmedFieldNames2Data.add(new String[]{TAXON_ID_FIELD_NAME,Integer.toString(TAXON_ID)});
        unslimmedFieldNames2Data.add(new String[]{ASSIGNED_BY_FIELD_NAME,DB});
        unslimmedFieldNames2Data.add(new String[]{ANNOTATION_EXTENSION_FIELD_NAME,EXTENSIONS_AS_STRING});
        unslimmedFieldNames2Data.add(new String[]{DATE_FIELD_NAME,DATE_AS_STRING});
        unslimmedFieldNames2Data.add(new String[]{TAXON_NAME_FIELD_NAME,TAXON_NAME});
    }

    @Before
    public void setup() {
        annotation = AnnotationMocker.createValidAnnotation();
        annotationToTSV = new AnnotationToTSV();
    }

    @Test
    public void createTsvStringFromAnnotationModelContainingIntAct() {
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GENEPRODUCT], is(DB + ":" + ID));
        assertThat(elements[NonSlimmedColumns.COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[NonSlimmedColumns.COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[NonSlimmedColumns.COL_GO_ID], is(GO_ID));
        assertThat(elements[NonSlimmedColumns.COL_NAME], is(GO_NAME));
        assertThat(elements[NonSlimmedColumns.COL_ECO_ID], is(ECO_ID));
        assertThat(elements[NonSlimmedColumns.COL_GO_EVIDENCE], is(GO_EVIDENCE));
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
    public void createTsvStringFromAnnotationModelContainingSlimmedToIds(){
        annotation.slimmedIds = SLIMMED_TO_IDS;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[SlimmedColumns.COL_GENEPRODUCT], is(DB + ":" + ID));
        assertThat(elements[SlimmedColumns.COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[SlimmedColumns.COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[SlimmedColumns.COL_GO_ID], is(SLIMMED_TO_IDS.get(0)));
        assertThat(elements[SlimmedColumns.COL_SLIMMED_FROM], is(GO_ID));
        assertThat(elements[SlimmedColumns.COL_NAME], is(GO_NAME));
        assertThat(elements[SlimmedColumns.COL_ECO_ID], is(ECO_ID));
        assertThat(elements[SlimmedColumns.COL_GO_EVIDENCE], is(GO_EVIDENCE));
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
        List<String> selectedFields = Collections.emptyList();
        List<String> converted = annotationToTSV.apply(annotation, selectedFields);
        assertThat(converted, hasSize(annotation.slimmedIds.size()));
        checkReturned(slimmedToGoId0, converted.get(0));
        checkReturned(slimmedToGoId1, converted.get(1));
        checkReturned(slimmedToGoId2, converted.get(2));
    }

    @Test
    public void outputForIndividualSelectedFieldsWithNoSlimming() throws Exception {
        for(String[] fieldName2Data : unslimmedFieldNames2Data){

            String[] elements = annotationToElements(annotation, Collections.singletonList(fieldName2Data[0]));

            assertThat(elements[0], is(fieldName2Data[1]));
        }
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
    public void nullEcoId() {
        annotation.evidenceCode = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_ECO_ID], is(""));
    }

    @Test
    public void emptyEcoId() {
        annotation.evidenceCode = "";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_ECO_ID], is(""));
    }

    @Test
    public void nullGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GO_EVIDENCE], is(""));
    }

    @Test
    public void emptyGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[NonSlimmedColumns.COL_GO_EVIDENCE], is(""));
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
        List<String> selectedFields = Collections.emptyList();
        return annotationToElements(annotation, selectedFields);
    }

    private String[] annotationToElements(Annotation annotation, List<String> selectedFields) {
        return annotationToTSV.apply(annotation, selectedFields).get(0)
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
        private static final int COL_ECO_ID = 5;
        private static final int COL_GO_EVIDENCE = 6;
        private static final int COL_REFERENCE = 7;
        private static final int COL_WITH = 8;
        private static final int COL_TAXON = 9;
        private static final int COL_ASSIGNED_BY = 10;
        private static final int COL_ANNOTATION_EXTENSION = 11;
        private static final int COL_DATE = 12;
    }

    private class SlimmedColumns{
        private static final int COL_GENEPRODUCT = 0;
        private static final int COL_DB_OBJECT_SYMBOL = 1;
        private static final int COL_QUALIFIER = 2;
        private static final int COL_GO_ID = 3;
        private static final int COL_SLIMMED_FROM = 4;
        private static final int COL_NAME = 5;
        private static final int COL_ECO_ID = 6;
        private static final int COL_GO_EVIDENCE = 7;
        private static final int COL_REFERENCE = 8;
        private static final int COL_WITH = 9;
        private static final int COL_TAXON = 10;
        private static final int COL_ASSIGNED_BY = 11;
        private static final int COL_ANNOTATION_EXTENSION = 12;
        private static final int COL_DATE = 13;
    }
}
