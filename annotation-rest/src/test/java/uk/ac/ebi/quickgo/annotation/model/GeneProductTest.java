package uk.ac.ebi.quickgo.annotation.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GeneProductTest {

    @Test
    public void uniprot() {
        final String fullId = "UniProtKB:A0A000";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("A0A000"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    public void uniprotWithIsoformOrVarient() {
        final String fullId = "UniProtKB:A0A000-2";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("A0A000"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    public void rna() {
        final String fullId = "RNAcentral:URS00000064B1_559292";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("RNAcentral"));
        assertThat(uniprot.canonicalId(), is("URS00000064B1_559292"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("miRNA"));
    }

    @Test
    public void complexPortal() {
        final String fullId = "ComplexPortal:CPX-1004";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("ComplexPortal"));
        assertThat(uniprot.canonicalId(), is("CPX-1004"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("complex"));
    }

    @Test
    public void uniprotWithIsoformOrVarient2() {
        final String fullId = "UniProtKB:Q92583-PRO_0000005211";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("Q92583"));
        assertThat(uniprot.fullId(), equalTo("UniProtKB:Q92583-PRO_0000005211"));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test(expected = IllegalStateException.class)
    public void nulThrowsException() {
        GeneProduct.fromCurieId(null);
    }

    @Test(expected = IllegalStateException.class)
    public void emptyThrowsException() {
        GeneProduct.fromCurieId("");
    }

    @Test(expected = IllegalStateException.class)
    public void garbageGeneProductIdThrowsException() {
        GeneProduct.fromCurieId("siasdfia'sif'a");
    }

    @Test(expected = IllegalStateException.class)
    public void uniprotIdIsBrokenThrowsException() {
        final String fullId = "UniProtKB:123444444444444";

        GeneProduct.fromCurieId(fullId);
    }

}