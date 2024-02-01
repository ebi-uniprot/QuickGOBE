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
    void nullDelimiterThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            String value = "";

            splitValue(value, null);
        });
    }

    @Test
    void nullPropertyReturnsEmptyArray() {
        String prop = null;

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, is(emptyArray()));
    }

    @Test
    void emptyPropertyReturnsEmptyArray() {
        String prop = "";

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, is(emptyArray()));
    }

    @Test
    void propWithNoDelimiterReturnsArrayWithOneElement() {
        String key = "key";
        String prop = key;

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, arrayWithSize(1));
        assertThat(splitArray, arrayContaining(key));
    }

    @Test
    void propWithDelimiterReturnsArrayWithTwoElements() {
        String key = "key";
        String value = "value";
        String prop = concatProperty(key, value, INTRA_VALUE_DELIMITER);

        String[] splitArray = splitValue(prop, INTRA_VALUE_DELIMITER);

        assertThat(splitArray, arrayWithSize(2));
        assertThat(splitArray, arrayContaining(key, value));
    }

    @Test
    void nullTaxonValueReturnsDefaultTaxonId() {
        int taxonId = extractTaxonIdFromValue(null);

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    void emptyTaxonValueReturnsDefaultTaxonId() {
        int taxonId = extractTaxonIdFromValue("");

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    void nonMatchingTaxonValueReturnsDefaultTaxonId() {
        int taxonId = extractTaxonIdFromValue("undefined");

        assertThat(taxonId, is(GeneProductParsingHelper.DEFAULT_TAXON_ID));
    }

    @Test
    void validTaxonValueReturnsCorrespondingTaxonId() {
        int expectedTaxonId = 33;

        int taxonId = extractTaxonIdFromValue(createUnconvertedTaxonId(expectedTaxonId));

        assertThat(taxonId, is(expectedTaxonId));
    }

    @Test
    void nullTaxonValueDoesNotMatchRegex() {
        String taxonValue = null;

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void emptyTaxonValueDoesNotMatchRegex() {
        String taxonValue = "";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void incorrectTaxonValueDoesNotMatchRegex() {
        String taxonValue = "undefined";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void taxonValueWithNoTaxonIdDoesNotMatchRegex() {
        String taxonValue = "taxon:";

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void taxonValueWithNegativeTaxonIdDoesNotMatchRegex() {
        String taxonValue = createUnconvertedTaxonId(-1);

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(false));
    }

    @Test
    void taxonValueWithPositiveTaxonIdDoesMatchRegex() {
        String taxonValue = createUnconvertedTaxonId(1);

        boolean matches = taxonIdMatchesRegex(taxonValue);

        assertThat(matches, is(true));
    }
}