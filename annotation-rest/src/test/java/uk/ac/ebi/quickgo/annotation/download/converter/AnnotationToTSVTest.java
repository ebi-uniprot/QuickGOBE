package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;
import uk.ac.ebi.quickgo.common.model.Aspect;

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
import static uk.ac.ebi.quickgo.annotation.download.TSVDownload.*;
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

    private static final List<String[]> unSlimmedFieldNames2Data = new ArrayList<>();
    static {
        unSlimmedFieldNames2Data.add(new String[]{SYMBOL_FIELD_NAME, SYMBOL});
        unSlimmedFieldNames2Data.add(new String[]{QUALIFIER_FIELD_NAME, QUALIFIER});
        unSlimmedFieldNames2Data.add(new String[]{GO_TERM_FIELD_NAME, GO_ID});
        unSlimmedFieldNames2Data.add(new String[]{GO_ASPECT_FIELD_NAME,
                Aspect.fromScientificName(GO_ASPECT).get().getCharacter()});
        unSlimmedFieldNames2Data.add(new String[]{GO_NAME_FIELD_NAME, GO_NAME});
        unSlimmedFieldNames2Data.add(new String[]{ECO_ID_FIELD_NAME, ECO_ID});
        unSlimmedFieldNames2Data.add(new String[]{GO_EVIDENCE_CODE_FIELD_NAME, GO_EVIDENCE});
        unSlimmedFieldNames2Data.add(new String[]{REFERENCE_FIELD_NAME, REFERENCE});
        unSlimmedFieldNames2Data.add(new String[]{WITH_FROM_FIELD_NAME, WITH_FROM_AS_STRING});
        unSlimmedFieldNames2Data.add(new String[]{TAXON_ID_FIELD_NAME, Integer.toString(TAXON_ID)});
        unSlimmedFieldNames2Data.add(new String[]{ASSIGNED_BY_FIELD_NAME, ASSIGNED_BY});
        unSlimmedFieldNames2Data.add(new String[]{ANNOTATION_EXTENSION_FIELD_NAME, EXTENSIONS_AS_STRING});
        unSlimmedFieldNames2Data.add(new String[]{DATE_FIELD_NAME, DATE_AS_STRING});
        unSlimmedFieldNames2Data.add(new String[]{TAXON_NAME_FIELD_NAME, TAXON_NAME});
        unSlimmedFieldNames2Data.add(new String[]{GENE_PRODUCT_NAME_FIELD_NAME, NAME});
        unSlimmedFieldNames2Data.add(new String[]{GENE_PRODUCT_SYNONYMS_FIELD_NAME, SYNONYMS});
        unSlimmedFieldNames2Data.add(new String[]{GENE_PRODUCT_TYPE_FIELD_NAME,TYPE});
    }

    @Before
    public void setup() {
        annotation = AnnotationMocker.createValidComplexPortalAnnotation();
        annotationToTSV = new AnnotationToTSV();
    }

    @Test
    public void complexPortal() {
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_DB], is(DB_COMPLEX_PORTAL));
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_ID], is(ID_COMPLEX_PORTAL));
        assertThat(elements[DefaultColumns.COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[DefaultColumns.COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[DefaultColumns.COL_GO_ID], is(GO_ID));
        assertThat(elements[DefaultColumns.COL_GO_ASPECT],
                   is(Aspect.fromScientificName(GO_ASPECT).get().getCharacter()));
        assertThat(elements[DefaultColumns.COL_ECO_ID], is(ECO_ID));
        assertThat(elements[DefaultColumns.COL_GO_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[DefaultColumns.COL_REFERENCE], is(REFERENCE));
        assertThat(elements[DefaultColumns.COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[DefaultColumns.COL_TAXON], is(Integer.toString(TAXON_ID)));
        assertThat(elements[DefaultColumns.COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[DefaultColumns.COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[DefaultColumns.COL_DATE], equalTo(DATE_AS_STRING));
    }

    @Test
    public void createTsvStringFromAnnotationModelContainingSlimmedToIds(){
        annotation.slimmedIds = SLIMMED_TO_IDS;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumnsWithSlimming.COL_GENE_PRODUCT_DB], is(DB_COMPLEX_PORTAL));
        assertThat(elements[DefaultColumnsWithSlimming.COL_GENE_PRODUCT_ID], is(ID_COMPLEX_PORTAL));
        assertThat(elements[DefaultColumnsWithSlimming.COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[DefaultColumnsWithSlimming.COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[DefaultColumnsWithSlimming.COL_GO_ID], is(SLIMMED_TO_IDS.get(0)));
        assertThat(elements[DefaultColumnsWithSlimming.COL_GO_ASPECT],
                   is(Aspect.fromScientificName(GO_ASPECT).get().getCharacter()));
        assertThat(elements[DefaultColumnsWithSlimming.COL_SLIMMED_FROM], is(GO_ID));
        assertThat(elements[DefaultColumnsWithSlimming.COL_ECO_ID], is(ECO_ID));
        assertThat(elements[DefaultColumnsWithSlimming.COL_GO_EVIDENCE], is(GO_EVIDENCE));
        assertThat(elements[DefaultColumnsWithSlimming.COL_REFERENCE], is(REFERENCE));
        assertThat(elements[DefaultColumnsWithSlimming.COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[DefaultColumnsWithSlimming.COL_TAXON], is(Integer.toString(TAXON_ID)));
        assertThat(elements[DefaultColumnsWithSlimming.COL_ASSIGNED_BY], equalTo(ASSIGNED_BY));
        assertThat(elements[DefaultColumnsWithSlimming.COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[DefaultColumnsWithSlimming.COL_DATE], equalTo(DATE_AS_STRING));
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
        for(String[] fieldName2Data : unSlimmedFieldNames2Data){

            String[] elements = annotationToElements(annotation, Collections.singletonList(fieldName2Data[0]));

            assertThat(elements[0], is(fieldName2Data[1]));
        }

        //Test Gene Product separately
        String[] elements = annotationToElements(annotation, Collections.singletonList(GENE_PRODUCT_FIELD_NAME));
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_DB], is(DB_COMPLEX_PORTAL));
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_ID], is(ID_COMPLEX_PORTAL));
    }

    @Test
    public void outputCreatedInOrderOfSelectedFields(){
        List<String> selectedFields = Arrays.asList(ASSIGNED_BY_FIELD_NAME, ECO_ID_FIELD_NAME, QUALIFIER_FIELD_NAME );

        String[] elements = annotationToElements(annotation, selectedFields);

        assertThat(elements[0], is(equalTo(ASSIGNED_BY)));
        assertThat(elements[1], is(equalTo(ECO_ID)));
        assertThat(elements[2], is(equalTo(QUALIFIER)));
    }

    @Test
    public void nullGeneProductId() {
        annotation.geneProductId = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_DB], is(""));
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_ID], is(""));
    }

    @Test
    public void emptyGeneProductId() {
        annotation.geneProductId = "";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_DB], is(""));
        assertThat(elements[DefaultColumns.COL_GENE_PRODUCT_ID], is(""));
    }

    @Test
    public void nullSymbol() {
        annotation.symbol = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_DB_OBJECT_SYMBOL], is(""));
    }

    @Test
    public void nullQualifier() {
        annotation.qualifier = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_QUALIFIER], is(""));
    }

    @Test
    public void nullGoId() {
        annotation.goId = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GO_ID], is(""));
    }

    @Test
    public void nullAspect() {
        annotation.goAspect = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GO_ASPECT], is(""));
    }

    @Test
    public void emptyAspect() {
        annotation.goAspect = "";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GO_ASPECT], is(""));
    }

    @Test
    public void unknownAspect() {
        annotation.goAspect = "Dish_Washing";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GO_ASPECT], is(""));
    }

    @Test
    public void nullReference() {
        annotation.reference = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_REFERENCE], is(""));
    }

    @Test
    public void nullEcoId() {
        annotation.evidenceCode = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_ECO_ID], is(""));
    }

    @Test
    public void emptyEcoId() {
        annotation.evidenceCode = "";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_ECO_ID], is(""));
    }

    @Test
    public void nullGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GO_EVIDENCE], is(""));
    }

    @Test
    public void emptyGoEvidence() {
        annotation.goEvidence = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_GO_EVIDENCE], is(""));
    }


    @Test
    public void nullWithFrom() {
        annotation.withFrom = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_WITH], is(""));
    }

    @Test
    public void emptyWithFrom() {
        annotation.withFrom = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_WITH], is(""));
    }

    @Test
    public void emptyTaxonId() {
        annotation.taxonId = 0;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_TAXON], is(""));
    }

    @Test
    public void nullAssignedBy() {
        annotation.assignedBy = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_ASSIGNED_BY], is(""));
    }

    @Test
    public void nullInExtensions() {
        annotation.extensions = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void emptyExtensions() {
        annotation.extensions = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void nullDate() {
        annotation.date = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[DefaultColumns.COL_DATE], is(""));
    }

    @Test
    public void onlyGetRequestedColumn() {
        List<String> selectedFields = Collections.singletonList("qualifier");

        List<String> converted = annotationToTSV.apply(annotation, selectedFields);

        assertThat(converted, hasSize(1));
        assertThat(converted.get(0), equalTo(QUALIFIER));
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
        assertThat(elements[DefaultColumnsWithSlimming.COL_GO_ID], is(slimmedToGoId));
    }

    private class DefaultColumns{
        private static final int COL_GENE_PRODUCT_DB = 0;
        private static final int COL_GENE_PRODUCT_ID = 1;
        private static final int COL_DB_OBJECT_SYMBOL = 2;
        private static final int COL_QUALIFIER = 3;
        private static final int COL_GO_ID = 4;
        private static final int COL_GO_ASPECT = 5;
        private static final int COL_ECO_ID = 6;
        private static final int COL_GO_EVIDENCE = 7;
        private static final int COL_REFERENCE = 8;
        private static final int COL_WITH = 9;
        private static final int COL_TAXON = 10;
        private static final int COL_ASSIGNED_BY = 11;
        private static final int COL_ANNOTATION_EXTENSION = 12;
        private static final int COL_DATE = 13;
    }

    private class DefaultColumnsWithSlimming{
        private static final int COL_GENE_PRODUCT_DB = 0;
        private static final int COL_GENE_PRODUCT_ID = 1;
        private static final int COL_DB_OBJECT_SYMBOL = 2;
        private static final int COL_QUALIFIER = 3;
        private static final int COL_GO_ID = 4;
        private static final int COL_SLIMMED_FROM = 5;
        private static final int COL_GO_ASPECT = 6;
        private static final int COL_ECO_ID = 7;
        private static final int COL_GO_EVIDENCE = 8;
        private static final int COL_REFERENCE = 9;
        private static final int COL_WITH = 10;
        private static final int COL_TAXON = 11;
        private static final int COL_ASSIGNED_BY = 12;
        private static final int COL_ANNOTATION_EXTENSION = 13;
        private static final int COL_DATE = 14;
    }
}
