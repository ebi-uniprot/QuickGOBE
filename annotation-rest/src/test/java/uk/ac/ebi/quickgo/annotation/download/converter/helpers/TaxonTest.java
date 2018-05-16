package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TaxonTest {

    @Test
    public void singleIdNoInteractingId() {

        String output = Taxon.taxonIdToCurie(234, 0);

        assertThat(output, is("taxon:234"));
    }

    @Test
    public void bothTaxonAndInteractingId() {

        String output = Taxon.taxonIdToCurie(234, 563);

        assertThat(output, is("taxon:234|taxon:563"));
    }

    @Test
    public void onlyInteractingId() {

        String output = Taxon.taxonIdToCurie(0, 563);

        assertThat(output, is("taxon:563"));
    }

    @Test
    public void bothZero() {

        String output = Taxon.taxonIdToCurie(0, 0);

        assertThat(output, is(""));
    }
}