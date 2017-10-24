package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import org.junit.Before;
import org.junit.Test;

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
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.TaxonomyNameInjector.TAXON_NAME;

/**
 * Created 12/04/17
 * @author Edd
 */
public class TaxonomyNameInjectorTest {
    private TaxonomyNameInjector nameInjector;
    private Annotation annotation;

    @Before
    public void setUp() {
        nameInjector = new TaxonomyNameInjector();
        annotation = new Annotation();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is(TAXON_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        ConvertedFilter<BasicTaxonomyNode> stubConvertedFilter = new ConvertedFilter<>(basicTaxonomyNode);
        assertThat(annotation.taxonName, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, annotation);

        injectValueSuccessfully(annotation.taxonName);
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        annotation.taxonId = TEST_TAXON_ID;
        FilterRequest filterRequest = nameInjector.buildFilterRequest(annotation);

        buildFilterRequestSuccessfully(filterRequest, nameInjector);
    }
}
