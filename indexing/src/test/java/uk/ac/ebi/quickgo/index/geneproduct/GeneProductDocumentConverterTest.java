package uk.ac.ebi.quickgo.index.geneproduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatStrings;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.*;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductUtil.createUnconvertedTaxonId;

/**
 * Tests the behaviour of the {@link GeneProductDocumentConverter} class.
 */
class GeneProductDocumentConverterTest {
    private static final String INTER_VALUE_DELIMITER = "|";
    private static final String INTER_VALUE_DELIMITER_REGEX = "\\|";
    private static final String INTRA_VALUE_DELIMITER = "=";
    private static final String SPECIFIC_VALUE_DELIMITER = ",";

    private GeneProductDocumentConverter converter;
    private GeneProduct geneProduct;

    @BeforeEach
    void setUp() {
        converter = new GeneProductDocumentConverter(INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER, SPECIFIC_VALUE_DELIMITER);
        geneProduct = new GeneProduct();
    }

    @Test
    void nullInterValueDelimiterThrowsException() {
        String interValueDelimiter = null;
        String intraValueDelimiter = INTRA_VALUE_DELIMITER;

        try {
            converter = new GeneProductDocumentConverter(interValueDelimiter, intraValueDelimiter, SPECIFIC_VALUE_DELIMITER);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Inter value delimiter can not be null or empty"));
        }
    }

    @Test
    void nullIntraValueDelimiterThrowsException() {
        String interValueDelimiter = INTER_VALUE_DELIMITER;
        String intraValueDelimiter = null;

        try {
            converter = new GeneProductDocumentConverter(interValueDelimiter, intraValueDelimiter, SPECIFIC_VALUE_DELIMITER);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Intra value delimiter can not be null or empty"));
        }
    }

    @Test
    void nullGeneProductThrowsException() {
        assertThrows(DocumentReaderException.class, () -> {
            converter.process(null);
        });
    }

    @Test
    void convertsDirectlyTranslatableFieldsInGeneProduct() {
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
    void convertsEmptyTaxonIdInGeneProductTo0() {
        geneProduct.taxonId = null;

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonId, is(0));
    }

    @Test
    void convertsTaxonIdInGeneProduct() {
        int taxonId = 35758;
        geneProduct.taxonId = createUnconvertedTaxonId(taxonId);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonId, is(taxonId));
    }

    @Test
    void convertsTaxonNameInPropertiesInGeneProductToField() {
        String taxonName = "Homo sapiens";
        geneProduct.properties = concatProperty(TAXON_NAME_KEY, taxonName);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(taxonName));
    }

    @Test
    void convertsAbsenceOfTaxonNameInPropertiesInGeneProductToNull() {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.taxonName, is(nullValue()));
    }

    @Test
    void convertsNoSynonymsInGeneProductToNullList() {
        geneProduct.synonym = null;

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.synonyms, is(nullValue()));
    }

    @Test
    void converts3SynonymsInGeneProductToListWith3Synonyms() {
        List<String> synonyms = Arrays.asList("A0A009DWW0_ACIBA", "J503_3808", "J503_4252");
        geneProduct.synonym = concatStrings(synonyms, INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.synonyms, containsInAnyOrder(synonyms.toArray(new String[synonyms.size()])));
    }

    @Test
    void converts3TargetSetValuesInPropertiesToListWith3TargetSets() {
        List<String> targetSets = Arrays.asList("KRUK", "Parkinsons", "BHF-UCL");
        String targetSet = concatProperty(TARGET_SET_KEY, concatStrings(targetSets, SPECIFIC_VALUE_DELIMITER));
        geneProduct.properties = concatStrings(Collections.singletonList(targetSet), INTER_VALUE_DELIMITER);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.targetSet, containsInAnyOrder(targetSets.toArray()));
    }

    @Test
    void convertsReferenceProteomeInPropertiesInGeneProduct() {
        String proteome = "gcrpCan";
        geneProduct.properties = concatProperty(PROTEOME_KEY, proteome);
        geneProduct.type = GeneProductType.PROTEIN.getName();

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.proteome, is(proteome));
    }

    @Test
    void convertsAbsenceReferenceProteomeInPropertiesInGeneProductToNullField() {
        geneProduct.properties = "";
        GeneProductDocument doc = converter.process(geneProduct);
        assertThat(doc.proteome, is(nullValue()));
    }

    @Test
    void convertsDBSubsetInPropertiesInGeneProductToList() {
        String db = "UniProtKB";
        geneProduct.properties = concatProperty(DATABASE_SUBSET_KEY, db);

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.databaseSubset, is(db));
    }

    @Test
    void convertsAbsenceOfDBSubsetInPropertiesInGeneProductToNullList() {
        geneProduct.properties = "";

        GeneProductDocument doc = converter.process(geneProduct);

        assertThat(doc.databaseSubset, is(nullValue()));
    }

    private String concatProperty(String key, String value) {
        return GOADataFileParsingUtil.concatProperty(key, value, INTRA_VALUE_DELIMITER);
    }
}
