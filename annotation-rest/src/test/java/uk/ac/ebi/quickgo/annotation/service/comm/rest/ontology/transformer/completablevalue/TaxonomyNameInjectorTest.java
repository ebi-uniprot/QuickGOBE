package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.model.CompletableValue;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjectorTestHelper
        .TEST_TAXON_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjectorTestHelper
        .basicTaxonomyNode;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjectorTestHelper
        .buildFilterRequestSuccessfully;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjectorTestHelper
        .injectValueSuccessfully;


/**
 * Created 12/04/17
 * @author Edd
 */
class TaxonomyNameInjectorTest {
    private TaxonomyNameInjector nameInjector;
    private CompletableValue completableValue;

    @BeforeEach
    void setUp() {
        nameInjector = new TaxonomyNameInjector();
        completableValue = new CompletableValue(String.valueOf(TEST_TAXON_ID));
    }

    @Test
    void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is("taxonName"));
    }

    @Test
    void responseValueIsInjectedToAnnotation() {
        ConvertedFilter<BasicTaxonomyNode> stubConvertedFilter = new ConvertedFilter<>(basicTaxonomyNode);
        assertThat(completableValue.value, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, completableValue);

        injectValueSuccessfully(completableValue.value);
    }

    @Test
    void correctFilterRequestIsBuilt() {
        FilterRequest filterRequest = nameInjector.buildFilterRequest(completableValue);

        buildFilterRequestSuccessfully(filterRequest, nameInjector);
    }
}
