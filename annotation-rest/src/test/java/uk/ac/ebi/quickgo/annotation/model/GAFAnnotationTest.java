package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Before;
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
public class GAFAnnotationTest {

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

    @Before
    public void setup(){
        annotation = AnnotationMocker.createValidAnnotation();
    }


    @Test
    public void createGAFStringFromAnnotationModel(){

        String converted = AnnotationToGAF.convert(annotation);

        String[] elements = converted.split("\t");
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
        assertThat(elements[COL_DB_OBJECT_TYPE], is(PROTEIN_TYPE));
        assertThat(elements[COL_TAXON], is("taxon:"+TAXON_ID));
        assertThat(elements[COL_DATE], equalTo(DATE_AS_STRING));
        assertThat(elements[COL_ASSIGNED_BY], equalTo("IntAct"));
        assertThat(elements[COL_ANNOTATION_EXTENSION], is(DB));
        assertThat(elements[COL_GENE_PRODUCT], is(""));
    }


}
