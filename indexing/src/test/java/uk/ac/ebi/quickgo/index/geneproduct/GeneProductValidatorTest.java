package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ebi.quickgo.index.geneproduct.Columns.*;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductUtil.createUnconvertedTaxonId;

/**
 * Tests the behaviour of the {@link GeneProductValidator} class.
 */
class GeneProductValidatorTest {
    private static final String INTER_VALUE_DELIMITER_REGEX = "\\|";
    private static final String INTER_VALUE_DELIMITER = "|";
    private static final String INTRA_VALUE_DELIMITER = "=";

    private static final String NULL_FIELD_MESSAGE = "Found null value in field: %s";
    private static final String EMPTY_FIELD_MESSAGE = "Found empty value in field: %s";
    private static final String INVALID_FIELD_MESSAGE = "Found invalid value for field: %s";

    private GeneProductValidator validator;

    private GeneProduct geneProduct;

    @BeforeEach
    void setUp() {
        validator = new GeneProductValidator(INTER_VALUE_DELIMITER_REGEX, INTRA_VALUE_DELIMITER);
        geneProduct = createGeneProductWithPopulatedMandatoryFields();
    }

    private GeneProduct createGeneProductWithPopulatedMandatoryFields() {
        GeneProduct geneProduct = new GeneProduct();
        geneProduct.database = "UniProtKB";
        geneProduct.id = "A0A000";
        geneProduct.symbol = "moeA5";
        geneProduct.name = "MoeA5";
        geneProduct.type = "protein";
        geneProduct.parentId = "A0A001";
        geneProduct.taxonId = createUnconvertedTaxonId(9606);

        return geneProduct;
    }

    @Test
    void nullInterValueDelimiterThrowsException() {
        String interValueDelimiter = null;
        String intraValueDelimiter = INTRA_VALUE_DELIMITER;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> validator = new GeneProductValidator(interValueDelimiter, intraValueDelimiter));
        assertTrue(exception.getMessage().contains("Inter value delimiter can not be null or empty"));
    }

    @Test
    void nullIntraValueDelimiterThrowsException() {
        String interValueDelimiter = INTER_VALUE_DELIMITER_REGEX;
        String intraValueDelimiter = null;
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> validator = new GeneProductValidator(interValueDelimiter, intraValueDelimiter));
        assertTrue(exception.getMessage().contains("Intra value delimiter can not be null or empty"));
    }

    @Test
    void nullGeneProductThrowsException() {
        assertThrows(DocumentReaderException.class, () -> validator.validate(null));
    }

    @Test
    void nullDatabaseThrowsException() {
        geneProduct.database = null;

        assertActualCauseUponValidation(createValidationException(NULL_FIELD_MESSAGE.formatted(COLUMN_DB.getName())));
    }

    @Test
    void emptyDatabaseThrowsException() {
        geneProduct.database = "";

        assertActualCauseUponValidation(createValidationException(EMPTY_FIELD_MESSAGE.formatted(COLUMN_DB.getName())));
    }

    @Test
    void databaseValidates() {
        geneProduct.database = "UniProtKB";

        validator.validate(geneProduct);
    }

    @Test
    void nullIdThrowsException() {
        geneProduct.id = null;

        assertActualCauseUponValidation(createValidationException(NULL_FIELD_MESSAGE.formatted(COLUMN_ID.getName())));
    }

    @Test
    void emptyIdThrowsException() {
        geneProduct.id = "";

        assertActualCauseUponValidation(createValidationException(EMPTY_FIELD_MESSAGE.formatted(COLUMN_ID.getName())));
    }

    @Test
    void idValidates() {
        geneProduct.id = "moeA5";

        validator.validate(geneProduct);
    }

    @Test
    void nullSymbolThrowsException() {
        geneProduct.symbol = null;

        assertActualCauseUponValidation(createValidationException(NULL_FIELD_MESSAGE.formatted(COLUMN_SYMBOL.getName())));
    }

    @Test
    void emptySymbolThrowsException() {
        geneProduct.symbol = "";

        assertActualCauseUponValidation(createValidationException(EMPTY_FIELD_MESSAGE.formatted(COLUMN_SYMBOL.getName())));
    }

    @Test
    void symbolValidates() {
        geneProduct.symbol = "A0A000";

        validator.validate(geneProduct);
    }

    @Test
    void nullTypeThrowsException() {
        geneProduct.type = null;

        assertActualCauseUponValidation(createValidationException(NULL_FIELD_MESSAGE.formatted(COLUMN_TYPE.getName())));
    }

    @Test
    void emptyTypeThrowsException() {
        geneProduct.type = "";

        assertActualCauseUponValidation(createValidationException(EMPTY_FIELD_MESSAGE.formatted(COLUMN_TYPE.getName())));
    }

    @Test
    void invalidTypeThrowsException() {
        geneProduct.type = "invalid";

        String errorMsg = "Error in field: " + COLUMN_TYPE.getName() + " - [No type maps to provided name: " +
                geneProduct.type + "]";

        assertActualCauseUponValidation(createValidationException(errorMsg));
    }

    @Test
    void typeIsValid() {
        geneProduct.type = GeneProductType.PROTEIN.getName();

        validator.validate(geneProduct);
    }

    @Test
    void nullParentIdIsValid() {
        geneProduct.parentId = null;

        validator.validate(geneProduct);
    }

    @Test
    void emptyParentIdIsValid() {
        geneProduct.parentId = "";

        validator.validate(geneProduct);
    }

    @Test
    void singleParentIdIsValid() {
        geneProduct.parentId = "A0A001";

        validator.validate(geneProduct);
    }

    @Test
    void multipleParentIdThrowsException() {
        geneProduct.parentId = "A0A001" + INTER_VALUE_DELIMITER + "A0A002";

        String errorMsg = "Found more than one id in field: " + COLUMN_PARENT_ID.getName();

        assertActualCauseUponValidation(createValidationException(errorMsg));
    }

    @Test
    void nullTaxonIdThrowsException() {
        geneProduct.taxonId = null;

        assertActualCauseUponValidation(createValidationException(NULL_FIELD_MESSAGE.formatted(COLUMN_TAXON_ID.getName())));
    }

    @Test
    void emptyTaxonIdThrowsException() {
        geneProduct.taxonId = "";

        assertActualCauseUponValidation(createValidationException(EMPTY_FIELD_MESSAGE.formatted(COLUMN_TAXON_ID.getName())));
    }

    @Test
    void taxonIdIsValid() {
        geneProduct.taxonId = createUnconvertedTaxonId(9606);

        validator.validate(geneProduct);
    }

    @Test
    void negativeTaxonIdThrowsException() {
        geneProduct.taxonId = createUnconvertedTaxonId(-9606);

        assertActualCauseUponValidation(createValidationException("Taxon id column does not conform to regex: "
                + createUnconvertedTaxonId(-9606)));
    }

    @Test
    void noIsIsoformIsValid() {
        validator.validate(geneProduct);
    }

    @Test
    void noCompleteProteomeIsValid() {
        validator.validate(geneProduct);
    }

    @Test
    void noReferenceProteomeIsValid() {
        validator.validate(geneProduct);
    }

    private void assertActualCauseUponValidation(Exception expectedException) {
        var actualException = assertThrows(ValidationException.class, () -> validator.validate(geneProduct));
        assertEquals(expectedException.getMessage(), actualException.getCause().getMessage());
        assertEquals(expectedException.getClass(), actualException.getCause().getClass());
    }

    private void assertYValue(String field) {
        String pair = concatProperty(field, "Y");

        geneProduct.properties = appendToProperties(geneProduct.properties, Collections.singletonList(pair));

        validator.validate(geneProduct);
    }

    private void assertNValue(String field) {
        String pair = concatProperty(field, "N");

        geneProduct.properties = appendToProperties(geneProduct.properties, Collections.singletonList(pair));

        validator.validate(geneProduct);
    }

    private String concatProperty(String key, String value) {
        return GOADataFileParsingUtil.concatProperty(key, value, INTRA_VALUE_DELIMITER);
    }

    private String appendToProperties(String geneProductProperties, List<String> valuesToAppend) {
        List<String> allValues = new ArrayList<>();
        allValues.add(geneProduct.properties);
        allValues.addAll(valuesToAppend);

        return GOADataFileParsingUtil.concatStrings(allValues, INTER_VALUE_DELIMITER);
    }

    private ValidationException createValidationException(String message) {
        return new ValidationException(message);
    }
}