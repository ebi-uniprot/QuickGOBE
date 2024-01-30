package uk.ac.ebi.quickgo.annotation.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneProductTest {

    @Test
    void uniprot() {
        final String fullId = "UniProtKB:A0A000";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("A0A000"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    void uniprotWithIsoformOrVarient() {
        final String fullId = "UniProtKB:A0A000-2";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("A0A000"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    void rna() {
        final String fullId = "RNAcentral:URS00000064B1_559292";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("RNAcentral"));
        assertThat(uniprot.canonicalId(), is("URS00000064B1_559292"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("miRNA"));
    }

    @Test
    void complexPortal() {
        final String fullId = "ComplexPortal:CPX-1004";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("ComplexPortal"));
        assertThat(uniprot.canonicalId(), is("CPX-1004"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("complex"));
    }

    @Test
    void uniprotWithIsoformOrVarient2() {
        final String fullId = "UniProtKB:Q92583-PRO_0000005211";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("Q92583"));
        assertThat(uniprot.fullId(), equalTo("UniProtKB:Q92583-PRO_0000005211"));
        assertThat(uniprot.type(), is("protein"));
    }


    @Test
    void uniprotWithIsoformOrVarient3() {
        final String fullId = "UniProtKB:P02649-VAR_000652";

        GeneProduct uniprot = GeneProduct.fromCurieId(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.canonicalId(), is("P02649"));
        assertThat(uniprot.fullId(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    void nulThrowsException() {
        assertThrows(IllegalStateException.class, () -> GeneProduct.fromCurieId(null));
    }

    @Test
    void emptyThrowsException() {
        assertThrows(IllegalStateException.class, () -> GeneProduct.fromCurieId(""));
    }

    @Test
    void garbageGeneProductIdThrowsException() {
        assertThrows(IllegalStateException.class, () -> GeneProduct.fromCurieId("siasdfia'sif'a"));
    }

    @Test
    void uniprotIdIsBrokenThrowsException() {
        final String fullId = "UniProtKB:123444444444444";
        assertThrows(IllegalStateException.class, () -> GeneProduct.fromCurieId(fullId));
    }

}