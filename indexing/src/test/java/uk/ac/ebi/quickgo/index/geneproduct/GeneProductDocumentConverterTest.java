package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.*;
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
    private static final String SPECIFIC_VALUE_DELIMITER = ",";

    private GeneProductDocumentConverter converter;
    private GeneProduct geneProduct;

    @Before
    public void setUp() {
        converter = new GeneProductDocumentConverter(INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER, SPECIFIC_VALUE_DELIMITER);
        geneProduct = new GeneProduct();
    }

    @Test
    public void nullInterValueDelimiterThrowsException() {
        String interValueDelimiter = null;
        String intraValueDelimiter = INTRA_VALUE_DELIMITER;

        try {
            converter = new GeneProductDocumentConverter(interValueDelimiter, intraValueDelimiter, SPECIFIC_VALUE_DELIMITER);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Inter value delimiter can not be null or empty"));
        }
    }

    @Test
    public void nullIntraValueDelimiterThrowsException() {
        String interValueDelimiter = INTER_VALUE_DELIMITER;
        String intraValueDelimiter = null;

        try {
            converter = new GeneProductDocumentConverter(interValueDelimiter, intraValueDelimiter, SPECIFIC_VALUE_DELIMITER);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Intra value delimiter can not be null or empty"));
        }
    }

    @Test(expected = DocumentReaderException.class)
    public void nullGeneProductThrowsException() {
        converter.process(null);
    }

    @Test
    public void convertsDirectlyTranslatableFieldsInGeneProduct() {
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
    public void convertsEmptyTaxonIdInGeneProductTo0() {
        geneProduct.taxonId = null;

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonId, is(0));
    }

    @Test
    public void convertsTaxonIdInGeneProduct() {
        int taxonId = 35758;
        geneProduct.taxonId = createUnconvertedTaxonId(taxonId);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonId, is(taxonId));
    }

    @Test
    public void convertsTaxonNameInPropertiesInGeneProductToField() {
        String taxonName = "Homo sapiens";
        geneProduct.properties = concatProperty(TAXON_NAME_KEY, taxonName);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(taxonName));
    }

    @Test
    public void convertsAbsenceOfTaxonNameInPropertiesInGeneProductToNull() {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(nullValue()));
    }

    @Test
    public void convertsNoSynonymsInGeneProductToNullList() {
        geneProduct.synonym = null;

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.synonyms, is(nullValue()));
    }

    @Test
    public void converts3SynonymsInGeneProductToListWith3Synonyms() {
        List<String> synonyms = Arrays.asList("A0A009DWW0_ACIBA", "J503_3808", "J503_4252");
        geneProduct.synonym = concatStrings(synonyms, INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.synonyms, containsInAnyOrder(synonyms.toArray(new String[synonyms.size()])));
    }

    @Test
    public void converts3TargetSetValuesInPropertiesToListWith3TargetSets() {
        List<String> targetSets = Arrays.asList("KRUK", "Parkinsons", "BHF-UCL");
        String targetSet = concatProperty(TARGET_SET_KEY, concatStrings(targetSets, SPECIFIC_VALUE_DELIMITER));
        geneProduct.properties = concatStrings(Collections.singletonList(targetSet), INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.targetSet, containsInAnyOrder(targetSets.toArray()));
    }

    @Test
    public void convertsYValuePropertiesInGeneProductToTrueBooleanFields() {
        String isAnnotated = concatProperty(IS_ANNOTATED_KEY, "Y");
        String isIsoform = concatProperty(IS_ISOFORM_KEY, "Y");
        String proteome = concatProperty(COMPLETE_PROTEOME_KEY, "Y");
        geneProduct.type = GeneProductType.PROTEIN.getName();
        geneProduct.properties = concatStrings(Arrays.asList(isAnnotated, isIsoform, proteome), INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.isAnnotated, is(true));
        assertThat(doc.isIsoform, is(true));
        assertThat(doc.isCompleteProteome, is(true));
        assertThat(doc.proteomeMembership, is(COMPLETE));
    }

    @Test
    public void convertsNValuePropertiesInGeneProductToTrueBooleanFields() {
        String isAnnotated = concatProperty(IS_ANNOTATED_KEY, "N");
        String isIsoform = concatProperty(IS_ISOFORM_KEY, "N");
        String proteome = concatProperty(COMPLETE_PROTEOME_KEY, "N");
        geneProduct.type = GeneProductType.PROTEIN.getName();
        geneProduct.properties = concatStrings(Arrays.asList(isAnnotated, isIsoform, proteome), INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.isAnnotated, is(false));
        assertThat(doc.isIsoform, is(false));
        assertThat(doc.isCompleteProteome, is(false));
        assertThat(doc.proteomeMembership, is(NONE));
    }

    @Test
    public void convertsAbsenceOfBooleanValuePropertiesInGeneProductToFalseBooleanFields() {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.isAnnotated, is(false));
        assertThat(doc.isIsoform, is(false));
        assertThat(doc.isCompleteProteome, is(false));
        assertThat(doc.proteomeMembership, is(NOT_APPLICABLE));
    }

    @Test
    public void convertsReferenceProteomeInPropertiesInGeneProduct() {
        String referenceProteome = "UP000005640";
        geneProduct.properties = concatProperty(REFERENCE_PROTEOME_KEY, referenceProteome);
        geneProduct.type = GeneProductType.PROTEIN.getName();

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.referenceProteome, is(referenceProteome));
        assertThat(doc.proteomeMembership, is(REFERENCE));
    }

    @Test
    public void convertsAbsenceReferenceProteomeInPropertiesInGeneProductToNullField() {
        geneProduct.properties = "";
        GeneProductDocument doc = converter.process(geneProduct);
        assertThat(doc.referenceProteome, is(nullValue()));
    }

    @Test
    public void convertsDBSubsetInPropertiesInGeneProductToList() {
        String db = "UniProtKB";
        geneProduct.properties = concatProperty(DATABASE_SUBSET_KEY, db);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.databaseSubset, is(db));
    }

    @Test
    public void convertsAbsenceOfDBSubsetInPropertiesInGeneProductToNullList() {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.databaseSubset, is(nullValue()));
    }

    private String concatProperty(String key, String value) {
        return GOADataFileParsingUtil.concatProperty(key, value, INTRA_VALUE_DELIMITER);
    }
}
