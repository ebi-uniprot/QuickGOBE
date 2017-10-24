package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;


import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
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
 * @author Tony Wardell
 * Date: 06/10/2017
 * Time: 16:08
 * Created with IntelliJ IDEA.
 */
public class TaxonomyNameInjectorTest {

    private TaxonomyNameInjector nameInjector;
    private StatisticsValue statisticsValue;

    @Before
    public void setUp() {
        nameInjector = new TaxonomyNameInjector();
        statisticsValue = new StatisticsValue(String.valueOf(TEST_TAXON_ID), 5, 10L);
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is("taxonName"));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        ConvertedFilter<BasicTaxonomyNode> stubConvertedFilter = new ConvertedFilter<>(basicTaxonomyNode);
        assertThat(statisticsValue.getName(), is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, statisticsValue);

        injectValueSuccessfully(statisticsValue.getName());
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        FilterRequest filterRequest = nameInjector.buildFilterRequest(statisticsValue);

        buildFilterRequestSuccessfully(filterRequest, nameInjector);
    }
}
