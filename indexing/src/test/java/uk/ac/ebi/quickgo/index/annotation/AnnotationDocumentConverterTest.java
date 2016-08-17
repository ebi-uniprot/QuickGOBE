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
import static uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentConverter.DEFAULT_TAXON;
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
        annotation.evidenceCode = "ECO:0000353";
        annotation.assignedBy = "IntAct";
        annotation.annotationExtension = "occurs_in(CL:1000428)";
        annotation.annotationProperties = "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein|db_object_type=protein|target_set=BHF-UCL,Exosome,KRUK";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductId, is(constructGeneProductId(annotation)));
        assertThat(doc.goId, is(annotation.goId));
        assertThat(doc.evidenceCode, is(annotation.evidenceCode));
        assertThat(doc.assignedBy, is(annotation.assignedBy));
        assertThat(doc.qualifier, is(annotation.qualifier));
        assertThat(doc.reference, is(annotation.dbReferences));
        assertThat(doc.targetSets, contains("BHF-UCL","Exosome","KRUK"));
    }

    // interacting taxon
    @Test
    public void convertsEmptyInteractingTaxonToDefaultTaxon() throws Exception {
        annotation.interactingTaxonId = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(DEFAULT_TAXON));
    }

    @Test
    public void convertsValidNonEmptyInteractingTaxon() throws Exception {
        annotation.interactingTaxonId = "taxon:12345";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(12345));
    }

    @Test
    public void convertsInvalidNonEmptyInteractingTaxon() throws Exception {
        annotation.interactingTaxonId = "taxon:12345d";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(DEFAULT_TAXON));
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

    // annotation properties: go evidence
    @Test
    public void convertsNullGOEvidenceAnnotationPropertiesToNullValue() throws Exception {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goEvidence, is(nullValue()));
    }

    @Test
    public void convertsGOEvidenceAnnotationProperties() throws Exception {
        String evidence = "FIND_ME";
        annotation.annotationProperties = "go_evidence=" + evidence;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goEvidence, is(evidence));
    }

    // annotation properties: taxon id
    @Test
    public void convertsNullTaxonIdAnnotationPropertiesToDefaultTaxon() throws Exception {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonId, is(DEFAULT_TAXON));
    }

    @Test
    public void convertsTaxonIdAnnotationProperties() throws Exception {
        int taxon = 12345;
        annotation.annotationProperties = "taxon_id=" + taxon;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonId, is(taxon));
    }

    @Test
    public void convertsInvalidTaxonIdAnnotationPropertiesToDefaultTaxon() throws Exception {
        String taxon = "12345a";
        annotation.annotationProperties = "taxon_id=" + taxon;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonId, is(DEFAULT_TAXON));
    }

    // annotation properties: db object type
    @Test
    public void convertsNullDbObjectTypeAnnotationPropertiesToNullValue() throws Exception {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductType, is(nullValue()));
    }

    @Test
    public void convertsDbObjectTypeAnnotationProperties() throws Exception {
        String value = "FINDME";
        annotation.annotationProperties = "db_object_type=" + value;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductType, is(value));
    }

    // annotation properties: db object symbol
    @Test
    public void convertsNullDbObjectSymbolAnnotationPropertiesToNullValue() throws Exception {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.dbObjectSymbol, is(nullValue()));
    }

    @Test
    public void convertsDbObjectSymbolAnnotationProperties() throws Exception {
        String value = "FINDME";
        annotation.annotationProperties = "db_object_symbol=" + value;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.dbObjectSymbol, is(value));
    }

    // annotation properties: db subset
    @Test
    public void convertsNullDbSubsetAnnotationPropertiesToNullValue() throws Exception {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.dbSubset, is(nullValue()));
    }

    @Test
    public void convertsDbSubsetAnnotationProperties() throws Exception {
        String value = "FINDME";
        annotation.annotationProperties = "db_subset=" + value;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.dbSubset, is(value));
    }

    // annotation extensions
    @Test
    public void convertsNullAnnotationExtensionToNullValue() throws Exception {
        annotation.annotationExtension = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.extensions, is(nullValue()));
    }

    @Test
    public void convertsAnnotationExtensionsToRawExtension() throws Exception {
        annotation.annotationExtension = "x,y|z";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.extensions, containsInAnyOrder("x,y","z"));
    }

    // annotation properties: target sets
    @Test
    public void convertsEmptyTargetSetToNullValue() throws Exception {
        annotation.annotationProperties = "db_object_symbol=moeA5|db_object_type=protein";
        AnnotationDocument doc = converter.process(annotation);
        assertThat(doc.targetSets, is(nullValue()));
    }


    private String constructGeneProductId(Annotation annotation) {return annotation.db + ":" + annotation.dbObjectId;}

}
