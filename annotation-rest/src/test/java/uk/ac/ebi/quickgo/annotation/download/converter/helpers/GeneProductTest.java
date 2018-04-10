package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.Optional;
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

        GeneProduct uniprot = GeneProduct.fromString(fullId).get();

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.id(), is("A0A000"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(null));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    public void uniprotWithIsoformOrVarient() {
        final String fullId = "UniProtKB:A0A000-2";

        GeneProduct uniprot = GeneProduct.fromString(fullId).get();

        assertThat(uniprot.db(), is("UniProtKB"));
        assertThat(uniprot.id(), is("A0A000"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(fullId));
        assertThat(uniprot.type(), is("protein"));
    }

    @Test
    public void rna() {
        final String fullId = "RNAcentral:URS00000064B1_559292";

        GeneProduct uniprot = GeneProduct.fromString(fullId).get();

        assertThat(uniprot.db(), is("RNAcentral"));
        assertThat(uniprot.id(), is("URS00000064B1_559292"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(null));
        assertThat(uniprot.type(), is("miRNA"));
    }

    @Test
    public void intact() {
        final String fullId = "IntAct:EBI-10043081";

        GeneProduct uniprot = GeneProduct.fromString(fullId).get();

        assertThat(uniprot.db(), is("IntAct"));
        assertThat(uniprot.id(), is("EBI-10043081"));
        assertThat(uniprot.withIsoformOrVariant(), equalTo(null));
        assertThat(uniprot.type(), is("complex"));
    }

    @Test
    public void nullKeptAsNull() {

        Optional<GeneProduct> uniprot = GeneProduct.fromString(null);

        assertThat(uniprot, equalTo(Optional.empty()));
    }

    @Test
    public void emptyStringAsNull() {

        Optional<GeneProduct> uniprot = GeneProduct.fromString("");

        assertThat(uniprot, equalTo(Optional.empty()));
    }

    @Test
    public void garbageAsNull() {

        Optional<GeneProduct> uniprot = GeneProduct.fromString("siasdfia'sif'a");

        assertThat(uniprot, equalTo(Optional.empty()));
    }

    @Test
    public void uniprotIdIsBroken() {
        final String fullId = "UniProtKB:123444444444444";

        Optional<GeneProduct> uniprot = GeneProduct.fromString(fullId);

        assertThat(uniprot, equalTo(Optional.empty()));
    }
}
