package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationMocker;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    public void setup(){
        annotation = AnnotationMocker.createValidAnnotation();
        annotationToGPAD = new AnnotationToGPAD();
    }

    @Test
    public void createGAFStringFromAnnotationModelContainingIntAct(){
        String[] elements = annotationToElements(annotation);
        assertThat(elements[COL_DB], is(DB));
        assertThat(elements[COL_DB_OBJECT_ID], is(ID));
        assertThat(elements[COL_QUALIFIER], is(QUALIFIER));
        assertThat(elements[COL_GO_ID], is(GO_ID));
        assertThat(elements[COL_REFERENCE], is(REFERENCE));
        assertThat(elements[COL_EVIDENCE], is(EVIDENCE_CODE));
        assertThat(elements[COL_WITH], equalTo(WITH_FROM_AS_STRING));
        assertThat(elements[COL_INTERACTING_DB], is(INTERACTING_TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo(DB));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(EXTENSIONS_AS_STRING));
        assertThat(elements[COL_GO_EVIDENCE], is("goEvidence=" + GO_EVIDENCE));
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
        return annotationToGPAD.apply(annotation)
                              .split(AnnotationToGAF.OUTPUT_DELIMITER, -1);
    }

}
