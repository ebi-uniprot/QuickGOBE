package uk.ac.ebi.quickgo.index.geneproduct;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.splitValue;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingUtil.concatProperty;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.extractTaxonIdFromValue;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.taxonIdMatchesRegex;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductUtil.createUnconvertedTaxonId;

/**
 * Tests the methods of the {@link GeneProductParsingHelper} class.
 */
class GeneProductParsingHelperTest {
    private static final String INTRA_VALUE_DELIMITER = "=";


    @Test
    void nullDelimiterThrowsException() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            String value = "";

            splitValue(value, null);
        });
    }

    @Test
    void nullPropertyReturnsEmptyArray() throws Exception {
        String prop = null;

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, is(emptyArray()));
    }

    @Test
    void emptyPropertyReturnsEmptyArray() throws Exception {
        String prop = "";

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, is(emptyArray()));
    }

    @Test
    void propWithNoDelimiterReturnsArrayWithOneElement() throws Exception {
        String key = "key";
        String prop = key;

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, arrayWithSize(1));
        assertThat(splitArray, arrayContaining(key));
    }

    @Test
    void propWithDelimiterReturnsArrayWithTwoElements() throws Exception {
        String key = "key";
        String value = "value";
        String prop = concatProperty(key, value, INTRA_VALUE_DELIMITER);

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, arrayWithSize(2));
        assertThat(splitArray, arrayContaining(key, value));
    }

    @Test
    void nullTaxonValueReturnsDefaultTaxonId() throws Exception {
        int taxonId = extractTaxonIdFromValue(null);

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    void emptyTaxonValueReturnsDefaultTaxonId() throws Exception {
        int taxonId = extractTaxonIdFromValue("");

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    void nonMatchingTaxonValueReturnsDefaultTaxonId() throws Exception {
        int taxonId = extractTaxonIdFromValue("undefined");

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    void validTaxonValueReturnsCorrespondingTaxonId() throws Exception {
        int expectedTaxonId = 33;

        int taxonId = extractTaxonIdFromValue(createUnconvertedTaxonId(expectedTaxonId));

        assertThat(taxonId, is(expectedTaxonId));
    }

    @Test
    void nullTaxonValueDoesNotMatchRegex() throws Exception {
        String taxonValue = null;

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void emptyTaxonValueDoesNotMatchRegex() throws Exception {
        String taxonValue = "";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void incorrectTaxonValueDoesNotMatchRegex() throws Exception {
        String taxonValue = "undefined";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void taxonValueWithNoTaxonIdDoesNotMatchRegex() throws Exception {
        String taxonValue = "taxon:";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void taxonValueWithNegativeTaxonIdDoesNotMatchRegex() throws Exception {
        String taxonValue = createUnconvertedTaxonId(-1);

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void taxonValueWithPositiveTaxonIdDoesMatchRegex() throws Exception {
        String taxonValue = createUnconvertedTaxonId(1);

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(true));
    }
}