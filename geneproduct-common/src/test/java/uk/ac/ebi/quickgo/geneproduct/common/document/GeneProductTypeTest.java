package uk.ac.ebi.quickgo.geneproduct.common.document;

import org.junit.Test;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Ensures interaction of the {@link GeneProductType} enumeration works as expected.
 *
 * Created 04/05/16
 * @author Edd
 */
public class GeneProductTypeTest {
    @Test
    public void typeOfSucceedsForAllValidUpperCaseGeneProductTypes() {
        for (GeneProductType geneProductType : GeneProductType.values()) {
            GeneProductType retrievedGeneProductType = GeneProductType.typeOf(geneProductType.getName().toUpperCase());
            assertThat(geneProductType, is(retrievedGeneProductType));
        }
    }

    @Test
    public void typeOfSucceedsForAllValidLowerCaseGeneProductTypes() {
        for (GeneProductType geneProductType : GeneProductType.values()) {
            GeneProductType retrievedGeneProductType = GeneProductType.typeOf(geneProductType.getName().toLowerCase());
            assertThat(geneProductType, is(retrievedGeneProductType));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void typeOfProducesIllegalArgumentExceptionForUnknownGeneProductType() {
        GeneProductType.typeOf("this doesn't exist");
    }
}