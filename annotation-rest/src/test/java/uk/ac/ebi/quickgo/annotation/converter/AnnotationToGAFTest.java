package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    private static final int COL_GENE_PRODUCT = 16;

    private Annotation annotation;
    private AnnotationToGAF annotationToGAF;
    @Before
    public void setup(){
        annotation = AnnotationMocker.createValidAnnotation();
        annotationToGAF = new AnnotationToGAF();
    }


    @Test
    public void createGAFStringFromAnnotationModelContainingIntAct(){
        final String gpType = "complex";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(DB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(EVIDENCE_CODE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(""));        //name
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(""));       //synonym
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:"+TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingUniProtGeneProductWithVariantOrIsoForm(){
        String gpId = "P04637-2";
        String gpIdCanonical = "P04637";
        String db = "UniProtKB";
        String gpType = "protein";
        annotation.id = String.format("%s:%s",db,gpId);
        annotation.geneProductId = gpId;
        annotation.assignedBy = db;
        annotation.symbol = gpId;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(db));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpIdCanonical));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(gpId));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(EVIDENCE_CODE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(""));        //name
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(""));       //synonym
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:"+TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(db));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GENE_PRODUCT], is(annotation.id));
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingRNACentralWithVariantOrIsoForm(){
        String gpId = "URS00000064B1_559292";
        String gpIdCanonical = "URS00000064B1";
        String db = "RNAcentral";
        String gpType = "miRNA";
        annotation.id = String.format("%s:%s",db,gpId);
        annotation.geneProductId = gpId;
        annotation.assignedBy = db;
        annotation.symbol = gpId;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(db));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpIdCanonical));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(gpId));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(EVIDENCE_CODE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(""));        //name
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(""));       //synonym
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:"+TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(db));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));

    }

    @Test
    public void createGAFStringFromAnnotationModelContainingIntActWithVariantOrIsoForm(){
        final String gpType = "complex";
        String gpId = "EBI-10043081";
        String gpIdCanonical = "EBI-10043081";
        String db = "IntAct";
        annotation.id = String.format("%s:%s",db,gpId);
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(DB));
        assertThat(elements[COL_DB_OBJECT_ID], is(gpIdCanonical));
        assertThat(elements[COL_DB_OBJECT_SYMBOL], is(SYMBOL));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(EVIDENCE_CODE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_ASPECT], is("F"));
        assertThat(elements[COL_DB_OBJECT_NAME], is(""));        //name
        assertThat(elements[COL_DB_OBJECT_SYNONYM], is(""));       //synonym
        assertThat(elements[COL_DB_OBJECT_TYPE], is(gpType));
        assertThat(elements[COL_TAXON], is("taxon:"+TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
    }

    @Test
    public void createGAFStringFromAnnotationWhereAspectIsBiologicalProcess(){
        annotation.goAspect = "biological_process";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ASPECT], is("P"));

    }

    @Test
    public void createGAFStringFromAnnotationWhereAspectIsCellularComponent(){
        annotation.goAspect = "cellular_component";
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ASPECT], is("C"));

    }

    @Test
    public void slimmedToGoIdReplacesGoIdIfItExists(){
        final String slimmedToGoId = "GO:0005524";
        annotation.slimmedIds = Arrays.asList(slimmedToGoId);
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_GO_ID], is(slimmedToGoId));
    }

    @Test
    public void testForNullInWithFrom(){
        annotation.withFrom = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void testForEmptyWithFrom(){
        annotation.withFrom = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_WITH], is(""));
    }

    @Test
    public void testForNullInExtensions(){
        annotation.extensions = null;
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    @Test
    public void testForEmptyExtensions(){
        annotation.extensions = new ArrayList<>();
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(""));
    }

    private String[] annotationToElements(Annotation annotation) {
        return annotationToGAF.apply(annotation)
                              .split(AnnotationToGAF.OUTPUT_DELIMITER, -1);
    }

}
