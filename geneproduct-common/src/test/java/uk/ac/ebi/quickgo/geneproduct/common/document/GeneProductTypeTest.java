package uk.ac.ebi.quickgo.geneproduct.common.document;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Ensures interaction of the {@link GeneProductType} enumeration works as expected.
 *
 * Created 04/05/16
 * @author Edd
 */
class GeneProductTypeTest {
    @Test
    void typeOfSucceedsForAllValidUpperCaseGeneProductTypes() {
        for (GeneProductType geneProductType : GeneProductType.values()) {
            GeneProductType retrievedGeneProductType = GeneProductType.typeOf(geneProductType.getName().toUpperCase());
            assertThat(geneProductType, is(retrievedGeneProductType));
        }
    }

    @Test
    void typeOfSucceedsForAllValidLowerCaseGeneProductTypes() {
        for (GeneProductType geneProductType : GeneProductType.values()) {
            GeneProductType retrievedGeneProductType = GeneProductType.typeOf(geneProductType.getName().toLowerCase());
            assertThat(geneProductType, is(retrievedGeneProductType));
        }
    }

    @Test
    void typeOfProducesIllegalArgumentExceptionForUnknownGeneProductType() {
        assertThrows(IllegalArgumentException.class, () -> GeneProductType.typeOf("this doesn't exist"));
    }
}