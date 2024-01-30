package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Taxon.taxonIdToCurie;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Taxon.taxonIdToString;

class TaxonTest {

    @Test
    void singleIdNoInteractingId() {

        String output = taxonIdToCurie(234, 0);

        assertThat(output, is("taxon:234"));
    }

    @Test
    void bothTaxonAndInteractingId() {

        String output = taxonIdToCurie(234, 563);

        assertThat(output, is("taxon:234|taxon:563"));
    }

    @Test
    void onlyInteractingId() {

        String output = taxonIdToCurie(0, 563);

        assertThat(output, is("taxon:563"));
    }

    @Test
    void bothZero() {

        String output = taxonIdToCurie(0, 0);

        assertThat(output, is(""));
    }

    @Test
    void taxonIdToStringNoTaxId() {

        String output = taxonIdToString(0);

        assertThat(output, is(""));
    }

    @Test
    void taxonIdToStringHasTaxId() {

        String output = taxonIdToString(12345);

        assertThat(output, is("12345"));
    }
}
