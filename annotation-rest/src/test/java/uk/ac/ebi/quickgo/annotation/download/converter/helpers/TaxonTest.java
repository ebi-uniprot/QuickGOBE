package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Taxon.taxonIdToCurie;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Taxon.taxonIdToString;

public class TaxonTest {

    @Test
    public void singleIdNoInteractingId() {

        String output = taxonIdToCurie(234, 0);

        assertThat(output, is("taxon:234"));
    }

    @Test
    public void bothTaxonAndInteractingId() {

        String output = taxonIdToCurie(234, 563);

        assertThat(output, is("taxon:234|taxon:563"));
    }

    @Test
    public void onlyInteractingId() {

        String output = taxonIdToCurie(0, 563);

        assertThat(output, is("taxon:563"));
    }

    @Test
    public void bothZero() {

        String output = taxonIdToCurie(0, 0);

        assertThat(output, is(""));
    }

    @Test
    public void taxonIdToStringNoTaxId() {

        String output = taxonIdToString(0);

        assertThat(output, is(""));
    }

    @Test
    public void taxonIdToStringHasTaxId() {

        String output = taxonIdToString(12345);

        assertThat(output, is("12345"));
    }
}
