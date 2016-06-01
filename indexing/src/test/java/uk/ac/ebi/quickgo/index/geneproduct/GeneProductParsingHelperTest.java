package uk.ac.ebi.quickgo.index.geneproduct;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.splitValue;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatProperty;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.extractTaxonIdFromValue;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.taxonIdMatchesRegex;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductUtil.createUnconvertedTaxonId;

/**
 * Tests the methods of the {@link GeneProductParsingHelper} class.
 */
public class GeneProductParsingHelperTest {
    private static final String INTRA_VALUE_DELIMITER = "=";

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void nullDelimiterThrowsException() throws Exception {
        String value = "";

        thrown.expect(AssertionError.class);

        splitValue(value, null);
    }

    @Test
    public void nullPropertyReturnsEmptyArray() throws Exception {
        String prop = null;

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, is(emptyArray()));
    }

    @Test
    public void emptyPropertyReturnsEmptyArray() throws Exception {
        String prop = "";

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, is(emptyArray()));
    }

    @Test
    public void propWithNoDelimiterReturnsArrayWithOneElement() throws Exception {
        String key = "key";
        String prop = key;

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, arrayWithSize(1));
        assertThat(splitArray, arrayContaining(key));
    }

    @Test
    public void propWithDelimiterReturnsArrayWithTwoElements() throws Exception {
        String key = "key";
        String value = "value";
        String prop = concatProperty(key, value, INTRA_VALUE_DELIMITER);

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, arrayWithSize(2));
        assertThat(splitArray, arrayContaining(key, value));
    }

    @Test
    public void nullTaxonValueReturnsDefaultTaxonId() throws Exception {
        int taxonId = extractTaxonIdFromValue(null);

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    public void emptyTaxonValueReturnsDefaultTaxonId() throws Exception {
        int taxonId = extractTaxonIdFromValue("");

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    public void nonMatchingTaxonValueReturnsDefaultTaxonId() throws Exception {
        int taxonId = extractTaxonIdFromValue("undefined");

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    public void validTaxonValueReturnsCorrespondingTaxonId() throws Exception {
        int expectedTaxonId = 33;

        int taxonId = extractTaxonIdFromValue(createUnconvertedTaxonId(expectedTaxonId));

        assertThat(taxonId, is(expectedTaxonId));
    }

    @Test
    public void nullTaxonValueDoesNotMatchRegex() throws Exception {
        String taxonValue = null;

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    public void emptyTaxonValueDoesNotMatchRegex() throws Exception {
        String taxonValue = "";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    public void incorrectTaxonValueDoesNotMatchRegex() throws Exception {
        String taxonValue = "undefined";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    public void taxonValueWithNoTaxonIdDoesNotMatchRegex() throws Exception {
        String taxonValue = "taxon:";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    public void taxonValueWithNegativeTaxonIdDoesNotMatchRegex() throws Exception {
        String taxonValue = createUnconvertedTaxonId(-1);

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    public void taxonValueWithPositiveTaxonIdDoesMatchRegex() throws Exception {
        String taxonValue = createUnconvertedTaxonId(1);

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(true));
    }
}