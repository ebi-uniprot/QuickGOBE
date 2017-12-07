package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author Tony Wardell
 * Date: 24/10/2017
 * Time: 14:12
 * Created with IntelliJ IDEA.
 */
public class TaxonomyNameInjectorTestHelper {
    public static final BasicTaxonomyNode basicTaxonomyNode = createBasicTaxonomyNode();
    public static final int TEST_TAXON_ID = 100;

    private static BasicTaxonomyNode createBasicTaxonomyNode() {
        BasicTaxonomyNode node = new BasicTaxonomyNode();
        node.setScientificName("a scientific name");
        return node;
    }

    public static void buildFilterRequestSuccessfully(FilterRequest filterRequest, AbstractValueInjector nameInjector){
        assertThat(filterRequest.getProperties(), hasEntry(nameInjector.getId(), emptyList()));
        assertThat(filterRequest.getProperties(), hasEntry("taxonId", singletonList(String.valueOf(TEST_TAXON_ID))));
    }

    public static void injectValueSuccessfully(String taxName) {
        assertThat(taxName, is(not(nullValue())));
        assertThat(taxName, is(basicTaxonomyNode.getScientificName()));
    }
}
