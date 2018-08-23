package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.index.ExceptionMatcher;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.batch.item.validator.ValidationException;

import static uk.ac.ebi.quickgo.index.geneproduct.Columns.*;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductUtil.createUnconvertedTaxonId;

/**
 * Tests the behaviour of the {@link GeneProductValidator} class.
 */
public class GeneProductValidatorTest {
    private static final String INTER_VALUE_DELIMITER_REGEX = "\\|";
    private static final String INTER_VALUE_DELIMITER = "|";
    private static final String INTRA_VALUE_DELIMITER = "=";

    private static final String NULL_FIELD_MESSAGE = "Found null value in field: %s";
    private static final String EMPTY_FIELD_MESSAGE = "Found empty value in field: %s";
    private static final String INVALID_FIELD_MESSAGE = "Found invalid value for field: %s";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private GeneProductValidator validator;

    private GeneProduct geneProduct;

    @Before
    public void setUp() throws Exception {
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
    public void nullInterValueDelimiterThrowsException() throws Exception {
        String interValueDelimiter = null;
        String intraValueDelimiter = INTRA_VALUE_DELIMITER;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Inter value delimiter can not be null or empty");
        validator = new GeneProductValidator(interValueDelimiter, intraValueDelimiter);
    }

    @Test
    public void nullIntraValueDelimiterThrowsException() throws Exception {
        String interValueDelimiter = INTER_VALUE_DELIMITER_REGEX;
        String intraValueDelimiter = null;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Intra value delimiter can not be null or empty");
        validator = new GeneProductValidator(interValueDelimiter, intraValueDelimiter);
    }

    @Test(expected = DocumentReaderException.class)
    public void nullGeneProductThrowsException() throws Exception {
        validator.validate(null);
    }

    @Test
    public void nullDatabaseThrowsException() throws Exception {
        geneProduct.database = null;

        assertExceptionThrown(createValidationException(String.format(NULL_FIELD_MESSAGE, COLUMN_DB.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void emptyDatabaseThrowsException() throws Exception {
        geneProduct.database = "";

        assertExceptionThrown(createValidationException(String.format(EMPTY_FIELD_MESSAGE, COLUMN_DB.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void databaseValidates() throws Exception {
        geneProduct.database = "UniProtKB";

        validator.validate(geneProduct);
    }

    @Test
    public void nullIdThrowsException() throws Exception {
        geneProduct.id = null;

        assertExceptionThrown(createValidationException(String.format(NULL_FIELD_MESSAGE, COLUMN_ID.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void emptyIdThrowsException() throws Exception {
        geneProduct.id = "";

        assertExceptionThrown(createValidationException(String.format(EMPTY_FIELD_MESSAGE, COLUMN_ID.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void idValidates() throws Exception {
        geneProduct.id = "moeA5";

        validator.validate(geneProduct);
    }

    @Test
    public void nullSymbolThrowsException() throws Exception {
        geneProduct.symbol = null;

        assertExceptionThrown(createValidationException(String.format(NULL_FIELD_MESSAGE, COLUMN_SYMBOL.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void emptySymbolThrowsException() throws Exception {
        geneProduct.symbol = "";

        assertExceptionThrown(createValidationException(String.format(EMPTY_FIELD_MESSAGE, COLUMN_SYMBOL.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void symbolValidates() throws Exception {
        geneProduct.symbol = "A0A000";

        validator.validate(geneProduct);
    }

    @Test
    public void nullTypeThrowsException() throws Exception {
        geneProduct.type = null;

        assertExceptionThrown(createValidationException(String.format(NULL_FIELD_MESSAGE, COLUMN_TYPE.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void emptyTypeThrowsException() throws Exception {
        geneProduct.type = "";

        assertExceptionThrown(createValidationException(String.format(EMPTY_FIELD_MESSAGE, COLUMN_TYPE.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void invalidTypeThrowsException() throws Exception {
        geneProduct.type = "invalid";

        String errorMsg = "Error in field: " + COLUMN_TYPE.getName() + " - [No type maps to provided name: " +
                geneProduct.type + "]";

        assertExceptionThrown(createValidationException(errorMsg));
        validator.validate(geneProduct);
    }

    @Test
    public void typeIsValid() throws Exception {
        geneProduct.type = GeneProductType.PROTEIN.getName();

        validator.validate(geneProduct);
    }

    @Test
    public void nullParentIdIsValid() throws Exception {
        geneProduct.parentId = null;

        validator.validate(geneProduct);
    }

    @Test
    public void emptyParentIdIsValid() throws Exception {
        geneProduct.parentId = "";

        validator.validate(geneProduct);
    }

    @Test
    public void singleParentIdIsValid() throws Exception {
        geneProduct.parentId = "A0A001";

        validator.validate(geneProduct);
    }

    @Test
    public void multipleParentIdThrowsException() throws Exception {
        geneProduct.parentId = "A0A001" + INTER_VALUE_DELIMITER + "A0A002";

        String errorMsg = "Found more than one id in field: " + COLUMN_PARENT_ID.getName();

        assertExceptionThrown(createValidationException(errorMsg));
        validator.validate(geneProduct);
    }

    @Test
    public void nullTaxonIdThrowsException() throws Exception {
        geneProduct.taxonId = null;

        assertExceptionThrown(createValidationException(String.format(NULL_FIELD_MESSAGE, COLUMN_TAXON_ID.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void emptyTaxonIdThrowsException() throws Exception {
        geneProduct.taxonId = "";

        assertExceptionThrown(createValidationException(String.format(EMPTY_FIELD_MESSAGE, COLUMN_TAXON_ID.getName())));
        validator.validate(geneProduct);
    }

    @Test
    public void taxonIdIsValid() throws Exception {
        geneProduct.taxonId = createUnconvertedTaxonId(9606);

        validator.validate(geneProduct);
    }

    @Test
    public void negativeTaxonIdThrowsException() throws Exception {
        geneProduct.taxonId = createUnconvertedTaxonId(-9606);

        assertExceptionThrown(createValidationException("Taxon id column does not conform to regex: "
                + createUnconvertedTaxonId(-9606)));

        validator.validate(geneProduct);
    }

    @Test
    public void noIsIsoformIsValid() throws Exception {
        validator.validate(geneProduct);
    }

    @Test
    public void noCompleteProteomeIsValid() throws Exception {
        validator.validate(geneProduct);
    }

    @Test
    public void noReferenceProteomeIsValid() throws Exception {
        validator.validate(geneProduct);
    }

    private void assertExceptionThrown(Exception exception) {
        thrown.expectCause(new ExceptionMatcher(exception.getClass(), exception.getMessage()));
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