package uk.ac.ebi.quickgo.index.annotation;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationMocker.createValidAnnotation;

/**
 * Created 21/04/16
 * @author Edd
 */
public class AnnotationDocumentConverterTest {
    private AnnotationDocumentConverter converter;
    private Annotation annotation;

    @Before
    public void setUp() {
        converter = new AnnotationDocumentConverter();
        annotation = createValidAnnotation();
    }

    @Test(expected = DocumentReaderException.class)
    public void nullAnnotationThrowsException() throws Exception {
        converter.process(null);
    }

    @Test
    public void convertsDirectlyTranslatableFieldsInAnnotation() throws Exception {
        annotation.db = "IntAct";
        annotation.dbObjectId = "EBI-10043081";
        annotation.dbReferences = "PMID:12871976";
        annotation.qualifier = "enables";
        annotation.goId = "GO:0000977";
        annotation.ecoId = "ECO:0000353";
        //        annotation.with = "GO:0036376,GO:1990573";
        annotation.interactingTaxonId = "taxon:12345";
        //        annotation.date = "20150122";
        annotation.assignedBy = "IntAct";
        annotation.annotationExtension = "occurs_in(CL:1000428)";
        //        annotation.annotationProperties = "go_evidence=IPI";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductId, is(constructGeneProductId(annotation)));
        assertThat(doc.goId, is(annotation.goId));
        assertThat(doc.ecoId, is(annotation.ecoId));
        assertThat(doc.assignedBy, is(annotation.assignedBy));
//        assertThat(doc.extension, is(annotation.annotationExtension));
        assertThat(doc.qualifier, is(annotation.qualifier));
        assertThat(doc.reference, is(annotation.dbReferences));
    }

    // taxon
    @Test
    public void convertsEmptyTaxonToNullValue() throws Exception {
        annotation.interactingTaxonId = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(nullValue()));
    }

    // with
    @Test
    public void convertsEmptyWithToNullValue() throws Exception {
        annotation.with = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.withFrom, is(nullValue()));
    }

    @Test
    public void convertsSingleValuedWithToListOfSize1() throws Exception {
        annotation.with = "GO:0036376";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.withFrom, contains("GO:0036376"));
    }

    @Test
    public void convertsMultiValuedWithToCorrectListOfSize2() throws Exception {
        annotation.with = "GO:0036376|GO:0036377";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.withFrom, containsInAnyOrder("GO:0036376", "GO:0036377"));
    }

    // annotation properties
    @Test
    public void convertsNullAnnotationPropertiesToNullValue() throws Exception {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goEvidence, is(nullValue()));
    }

    @Test
    public void convertsAnnotationPropertiesForGoEvidence() throws Exception {
        annotation.annotationProperties = "go_evidence=FIND_ME";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goEvidence, is("FIND_ME"));
    }

    private String constructGeneProductId(Annotation annotation) {return annotation.db + ":" + annotation.dbObjectId;}

}