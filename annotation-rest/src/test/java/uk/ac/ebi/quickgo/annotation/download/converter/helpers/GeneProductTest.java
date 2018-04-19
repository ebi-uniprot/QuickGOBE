package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Can the GeneProduct helper class turn full, null or empty strings in to the right result?
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 08:42
 * Created with IntelliJ IDEA.
 */
public class GeneProductTest {

    @Test
    public void uniprot() {
        final String fullId = "UniProtKB:A0A000";

        GeneProduct uniprot = GeneProduct.fromString(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.id(), is("A0A000"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(null));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    public void uniprotWithIsoformOrVarient() {
        final String fullId = "UniProtKB:A0A000-2";

        GeneProduct uniprot = GeneProduct.fromString(fullId);

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.id(), is("A0A000"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    public void rna() {
        final String fullId = "RNAcentral:URS00000064B1_559292";

        GeneProduct uniprot = GeneProduct.fromString(fullId);

        assertThat(uniprot.db(), is("RNAcentral"));
        assertThat(uniprot.id(), is("URS00000064B1_559292"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(null));
        assertThat(uniprot.type(), is("miRNA"));
    }

    @Test
    public void complexPortal() {
        final String fullId = "ComplexPortal:CPX-1004";

        GeneProduct uniprot = GeneProduct.fromString(fullId);

        assertThat(uniprot.db(), is("ComplexPortal"));
        assertThat(uniprot.id(), is("CPX-1004"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(null));
        assertThat(uniprot.type(), is("complex"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nulThrowsException() {
        GeneProduct.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyThrowsException() {
        GeneProduct.fromString("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void garbageGeneProductIdThrowsException() {
        GeneProduct.fromString("siasdfia'sif'a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void uniprotIdIsBrokenThrowsException() {
        final String fullId = "UniProtKB:123444444444444";

        GeneProduct.fromString(fullId);
    }
}
