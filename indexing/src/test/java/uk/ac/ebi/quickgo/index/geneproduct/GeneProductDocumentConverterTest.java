package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatStrings;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.*;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductUtil.createUnconvertedTaxonId;

/**
 * Tests the behaviour of the {@link GeneProductDocumentConverter} class.
 */
public class GeneProductDocumentConverterTest {
    private static final String INTER_VALUE_DELIMITER = "|";
    private static final String INTER_VALUE_DELIMITER_REGEX = "\\|";
    private static final String INTRA_VALUE_DELIMITER = "=";

    private GeneProductDocumentConverter converter;

    private GeneProduct geneProduct;

    @Before
    public void setUp() throws Exception {
        converter = new GeneProductDocumentConverter(INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);

        geneProduct = new GeneProduct();
    }

    @Test
    public void nullInterValueDelimiterThrowsException() throws Exception {
        String interValueDelimiter = null;
        String intraValueDelimiter = INTRA_VALUE_DELIMITER;

        try {
            converter = new GeneProductDocumentConverter(interValueDelimiter, intraValueDelimiter);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Inter value delimiter can not be null or empty"));
        }
    }

    @Test
    public void nullIntraValueDelimiterThrowsException() throws Exception {
        String interValueDelimiter = INTER_VALUE_DELIMITER;
        String intraValueDelimiter = null;

        try {
            converter = new GeneProductDocumentConverter(interValueDelimiter, intraValueDelimiter);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Intra value delimiter can not be null or empty"));
        }
    }

    @Test(expected = DocumentReaderException.class)
    public void nullGeneProductThrowsException() throws Exception {
        converter.process(null);
    }

    @Test
    public void convertsDirectlyTranslatableFieldsInGeneProduct() throws Exception {
        geneProduct.database = "UniProtKB";
        geneProduct.id = "A0A000";
        geneProduct.symbol = "moeA5";
        geneProduct.name = "MoeA5";
        geneProduct.type = "protein";
        geneProduct.parentId = "A0A001";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.database, is(geneProduct.database));
        assertThat(doc.id, is(geneProduct.id));
        assertThat(doc.symbol, is(geneProduct.symbol));
        assertThat(doc.name, is(geneProduct.name));
        assertThat(doc.type, is(geneProduct.type));
        assertThat(doc.parentId, is(geneProduct.parentId));
    }

    @Test
    public void convertsEmptyTaxonIdInGeneProductTo0() throws Exception {
        geneProduct.taxonId = null;

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonId, is(0));
    }

    @Test
    public void convertsTaxonIdInGeneProduct() throws Exception {
        int taxonId = 35758;

        geneProduct.taxonId = createUnconvertedTaxonId(taxonId);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonId, is(taxonId));
    }

    @Test
    public void convertsNoSynonymsInGeneProductToNullList() throws Exception {
        geneProduct.synonym = null;

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.synonyms, is(nullValue()));
    }

    @Test
    public void converts3SynonymsInGeneProductToListWith3Synonyms() throws Exception {
        List<String> synonyms = Arrays.asList("A0A009DWW0_ACIBA", "J503_3808", "J503_4252");

        geneProduct.synonym = concatStrings(synonyms, INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.synonyms, containsInAnyOrder(synonyms.toArray(new String[synonyms.size()])));
    }

    @Test
    public void convertsYValuePropertiesInGeneProductToTrueBooleanFields() throws Exception {
        String isAnnotated = concatProperty(IS_ANNOTATED_KEY, "Y");
        String isIsoform = concatProperty(IS_ISOFORM_KEY, "Y");
        String proteome = concatProperty(COMPLETE_PROTEOME_KEY, "Y");

        geneProduct.properties = concatStrings(Arrays.asList(isAnnotated, isIsoform, proteome), INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.isAnnotated, is(true));
        assertThat(doc.isIsoform, is(true));
        assertThat(doc.isCompleteProteome, is(true));
    }

    @Test
    public void convertsNValuePropertiesInGeneProductToTrueBooleanFields() throws Exception {
        String isAnnotated = concatProperty(IS_ANNOTATED_KEY, "N");
        String isIsoform = concatProperty(IS_ISOFORM_KEY, "N");
        String proteome = concatProperty(COMPLETE_PROTEOME_KEY, "N");

        geneProduct.properties = concatStrings(Arrays.asList(isAnnotated, isIsoform, proteome), INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.isAnnotated, is(false));
        assertThat(doc.isIsoform, is(false));
        assertThat(doc.isCompleteProteome, is(false));
    }

    @Test
    public void convertsAbsenceOfBooleanValuePropertiesInGeneProductToFalseBooleanFields() throws Exception {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.isAnnotated, is(false));
        assertThat(doc.isIsoform, is(false));
        assertThat(doc.isCompleteProteome, is(false));
    }

    @Test
    public void convertsReferenceProteomeInPropertiesInGeneProduct() throws Exception {
        String referenceProteome = "UP000005640";
        geneProduct.properties = concatProperty(REFERENCE_PROTEOME_KEY, referenceProteome);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.referenceProteome, is(referenceProteome));
    }

    @Test
    public void convertsAbsenceReferenceProteomeInPropertiesInGeneProductToNullField() throws Exception {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.referenceProteome, is(nullValue()));
    }

    @Test
    public void convertsDBSubsetInPropertiesInGeneProductToList() throws Exception {
        String db = "UniProtKB";
        geneProduct.properties = concatProperty(DATABASE_SUBSET_KEY, db);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.databaseSubsets, contains(db));
    }

    @Test
    public void convertsAbsenceOfDBSubsetInPropertiesInGeneProductToNullList() throws Exception {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(nullValue()));
    }

    @Test
    public void convertsTaxonNameInPropertiesInGeneProductToField() throws Exception {
        String taxonName = "Homo sapiens";
        geneProduct.properties = concatProperty(TAXON_NAME_KEY, taxonName);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(taxonName));
    }

    @Test
    public void convertsAbsenceOfTaxonNameInPropertiesInGeneProductToNull() throws Exception {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(nullValue()));
    }

    private String concatProperty(String key, String value) {
        return GOADataFileParsingUtil.concatProperty(key, value, INTRA_VALUE_DELIMITER);
    }
}