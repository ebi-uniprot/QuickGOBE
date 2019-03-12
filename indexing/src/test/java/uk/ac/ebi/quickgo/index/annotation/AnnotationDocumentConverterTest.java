package uk.ac.ebi.quickgo.index.annotation;

import org.junit.Assert;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationDocumentConverter.DEFAULT_TAXON;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationMocker.createValidAnnotation;
import static uk.ac.ebi.quickgo.index.annotation.AnnotationParsingHelper.*;

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
    public void nullAnnotationThrowsException() {
        converter.process(null);
    }

    @Test
    public void convertsDirectlyTranslatableFieldsInAnnotation() {
        annotation.db = "IntAct";
        annotation.dbObjectId = "EBI-10043081";
        annotation.dbReferences = "PMID:12871976";
        annotation.qualifier = "enables";
        annotation.goId = "GO:0000977";
        annotation.evidenceCode = "ECO:0000353";
        annotation.assignedBy = "IntAct";
        annotation.annotationExtension = "occurs_in(CL:1000428)";
        annotation.annotationProperties =
                mergeKeyValuesPairs(
                        buildKeyValuesPair(GO_EVIDENCE, "IEA"),
                        buildKeyValuesPair(TAXON_ID, "35758"),
                        buildKeyValuesPair(DB_OBJECT_SUBSET, "TrEMBL"),
                        buildKeyValuesPair(DB_OBJECT_SYMBOL, "moeA5"),
                        buildKeyValuesPair(DB_OBJECT_TYPE, "protein"),
                        buildKeyValuesPair(PROTEOME, "gcrpIso"),
                        buildKeyValuesPair(TARGET_SET, "BHF-UCL", "Exosome", "KRUK"),
                        buildKeyValuesPair(GP_RELATED_GO_IDS, "GO:0005886"));

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductId, is(constructGeneProductId(annotation)));
        assertThat(doc.goId, is(annotation.goId));
        assertThat(doc.evidenceCode, is(annotation.evidenceCode));
        assertThat(doc.assignedBy, is(annotation.assignedBy));
        assertThat(doc.qualifier, is(annotation.qualifier));
        assertThat(doc.reference, is(annotation.dbReferences));
        assertThat(doc.proteome, is("gcrpIso"));
        assertThat(doc.targetSets, contains("BHF-UCL", "Exosome", "KRUK"));
        assertThat(doc.defaultSort, is("9EBI-10043081"));
        assertThat(doc.gpRelatedGoIds, is("GO:0005886"));
    }

    // interacting taxon
    @Test
    public void convertsEmptyInteractingTaxonToDefaultTaxon() {
        annotation.interactingTaxonId = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(DEFAULT_TAXON));
    }

    @Test
    public void convertsValidNonEmptyInteractingTaxon() {
        annotation.interactingTaxonId = "taxon:12345";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(12345));
    }

    @Test
    public void convertsInvalidNonEmptyInteractingTaxon() {
        annotation.interactingTaxonId = "taxon:12345d";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.interactingTaxonId, is(DEFAULT_TAXON));
    }

    // with
    @Test
    public void convertsEmptyWithToNullValue() {
        annotation.with = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.withFrom, is(nullValue()));
    }

    @Test
    public void convertsSingleValuedWithToListOfSize1() {
        annotation.with = "GO:0036376";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.withFrom, contains("GO:0036376"));
    }

    @Test
    public void convertsMultiValuedWithToCorrectListOfSize2() {
        annotation.with = "GO:0036376|GO:0036377";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.withFrom, containsInAnyOrder("GO:0036376", "GO:0036377"));
    }

    // annotation properties: go evidence
    @Test
    public void convertsNullGOEvidenceAnnotationPropertiesToNullValue() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goEvidence, is(nullValue()));
    }

    @Test
    public void convertsGOEvidenceAnnotationProperties() {
        String evidence = "FIND_ME";
        annotation.annotationProperties = buildKeyValuesPair(GO_EVIDENCE, evidence);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goEvidence, is(evidence));
    }

    // annotation properties: taxon id
    @Test
    public void convertsNullTaxonIdAnnotationPropertiesToDefaultTaxon() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonId, is(DEFAULT_TAXON));
    }

    @Test
    public void convertsTaxonIdAnnotationProperties() {
        int taxon = 12345;
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ID, String.valueOf(taxon));

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonId, is(taxon));
    }

    @Test
    public void convertsInvalidTaxonIdAnnotationPropertiesToDefaultTaxon() {
        String taxon = "12345a";
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ID, taxon);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonId, is(DEFAULT_TAXON));
    }

    // annotation properties: db object type
    @Test
    public void convertsNullDbObjectTypeAnnotationPropertiesToNullValue() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductType, is(nullValue()));
    }

    @Test
    public void convertsDbObjectTypeAnnotationProperties() {
        String value = "FINDME";
        annotation.annotationProperties = buildKeyValuesPair(DB_OBJECT_TYPE, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductType, is(value));
    }

    // annotation properties: db object symbol
    @Test
    public void convertsNullDbObjectSymbolAnnotationPropertiesToNullValue() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.symbol, is(nullValue()));
    }

    @Test
    public void convertsDbObjectSymbolAnnotationProperties() {
        String value = "FINDME";
        annotation.annotationProperties = buildKeyValuesPair(DB_OBJECT_SYMBOL, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.symbol, is(value));
    }

    // annotation properties: db subset
    @Test
    public void convertsNullDbSubsetAnnotationPropertiesToNullValue() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductSubset, is(nullValue()));
    }

    @Test
    public void convertsDbSubsetAnnotationProperties() {
        String value = "FINDME";
        annotation.annotationProperties = buildKeyValuesPair(DB_OBJECT_SUBSET, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.geneProductSubset, is(value));
    }

    // annotation extensions
    @Test
    public void convertsNullAnnotationExtensionToNullValue() {
        annotation.annotationExtension = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.extensions, is(nullValue()));
    }

    @Test
    public void keepsRawExtensionUnchanged() {
        String annotationExtension = "x,y|z";
        annotation.annotationExtension = annotationExtension;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.extensions, is(annotationExtension));
    }

    // annotation properties: target sets
    @Test
    public void convertsEmptyTargetSetToNullValue() {
        annotation.annotationProperties = mergeKeyValuesPairs(
                buildKeyValuesPair(DB_OBJECT_TYPE, "protein"),
                buildKeyValuesPair(DB_OBJECT_SYMBOL, "moeA5"));

        AnnotationDocument doc = converter.process(annotation);
        assertThat(doc.targetSets, is(nullValue()));
    }

    // annotation properties: go aspect
    @Test
    public void convertsNullGoAspectAnnotationPropertiesToNullValue() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goAspect, is(nullValue()));
    }

    @Test
    public void convertsGoAspectAnnotationProperties() {
        String value = "cellular_component";
        annotation.annotationProperties = buildKeyValuesPair(GO_ASPECT, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.goAspect, is(value));
    }

    // annotation properties: taxon ancestors
    @Test
    public void convertsNullAnnotationPropertiesToDefaultTaxonAncestorsList() {
        annotation.annotationProperties = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonAncestors, contains(DEFAULT_TAXON));
    }

    @Test
    public void convertsNullTaxonAncestorsAnnotationPropertiesToDefaultTaxonAncestorsList() {
        String value = null;
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ANCESTORS, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonAncestors, contains(DEFAULT_TAXON));
    }

    @Test
    public void convertsEmptyTaxonAncestorsAnnotationPropertiesToDefaultTaxonAncestorsList() {
        String value = "";
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ANCESTORS, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonAncestors, contains(DEFAULT_TAXON));
    }

    @Test
    public void convertsInvalidTaxonAncestorsAnnotationPropertiesToDefaultTaxonAncestorsList() {
        String value = "1234d";
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ANCESTORS, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonAncestors, contains(DEFAULT_TAXON));
    }

    @Test
    public void convertsSingleTaxonAncestorsAnnotationProperties() {
        String value = "1234";
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ANCESTORS, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonAncestors, contains(Integer.valueOf(value)));
    }

    @Test
    public void convertsMultipleTaxonAncestorsAnnotationProperties() {
        String taxon1 = "1234";
        String taxon2 = "55";
        String value = taxon1 + "," + taxon2;
        annotation.annotationProperties = buildKeyValuesPair(TAXON_ANCESTORS, value);

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.taxonAncestors, contains(Integer.valueOf(taxon1), Integer.valueOf(taxon2)));
    }

    // date
    @Test
    public void convertsValidDateSuccessfully() {
        annotation.date = "20150122";
        LocalDate expectedLocalDate = LocalDate.of(2015, 1, 22);
        Date expectedDate = Date.from(expectedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.date, is(expectedDate));
    }

    @Test
    public void convertsInvalidDateToNull() {
        annotation.date = "3dd333stopAskingMeForADate320150122";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.date, is(nullValue()));
    }

    @Test
    public void convertsEmptyDateToNull() {
        annotation.date = "";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.date, is(CoreMatchers.nullValue()));
    }

    @Test
    public void convertsSpaceFilledDateToNull() {
        annotation.date = "    ";

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.date, is(CoreMatchers.nullValue()));
    }

    @Test
    public void convertsNullDateToNull() {
        annotation.date = null;

        AnnotationDocument doc = converter.process(annotation);

        assertThat(doc.date, is(CoreMatchers.nullValue()));
    }

    @Test
    public void forUniProtKBDefaultSortStartsWith3AndRemainingIsTheIdentifier() {
        annotation.db = "UniProtKB";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("3"));
        assertThat(doc.defaultSort, is("3" + annotation.dbObjectId));
    }

    @Test
    public void forUniprotkbIgnoreCaseDefaultSortStartsWith3AndRemainingIsTheIdentifier() {
        annotation.db = "uniprotkb";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("3"));
        assertThat(doc.defaultSort, is("3" + annotation.dbObjectId));
    }

    @Test
    public void forComplexPortalDefaultSortStartsWith5AndRemainingIsTheIdentifier() {
        annotation.db = "ComplexPortal";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("5"));
        assertThat(doc.defaultSort, is("5" + annotation.dbObjectId));
    }

    @Test
    public void forComplexportalIgnoreCaseDefaultSortStartsWith5AndRemainingIsTheIdentifier() {
        annotation.db = "complexportal";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("5"));
        assertThat(doc.defaultSort, is("5" + annotation.dbObjectId));
    }

    @Test
    public void forRNAcentralDefaultSortStartsWith7AndRemainingIsTheIdentifier() {
        annotation.db = "RNAcentral";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("7"));
        assertThat(doc.defaultSort, is("7" + annotation.dbObjectId));
    }

    @Test
    public void forRnacentralIgnoreCaseDefaultSortStartsWith7AndRemainingIsTheIdentifier() {
        annotation.db = "rnacentral";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("7"));
        assertThat(doc.defaultSort, is("7" + annotation.dbObjectId));
    }

    @Test
    public void forAnyOtherDatabaseDefaultSortStartsWith9AndRemainingIsTheIdentifier() {
        annotation.db = "anyDb";

        AnnotationDocument doc = converter.process(annotation);

        Assert.assertTrue(doc.defaultSort.startsWith("9"));
        assertThat(doc.defaultSort, is("9" + annotation.dbObjectId));
    }

    private String constructGeneProductId(Annotation annotation) {return annotation.db + ":" + annotation.dbObjectId;}

    private String buildKeyValuesPair(String key, String... values) {
        return key + GOADataFileParsingHelper.EQUALS +
                Arrays.stream(values)
                        .collect(Collectors.joining(GOADataFileParsingHelper.COMMA));
    }

    private String mergeKeyValuesPairs(String... keyValuePairs) {
        return Arrays.stream(keyValuePairs)
                .collect(Collectors.joining(GOADataFileParsingHelper.PIPE));
    }
}