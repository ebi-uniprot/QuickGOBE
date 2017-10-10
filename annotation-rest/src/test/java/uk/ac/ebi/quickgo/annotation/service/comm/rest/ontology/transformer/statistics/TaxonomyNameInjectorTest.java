package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics;

import uk.ac.ebi.quickgo.annotation.model.StatisticsValue;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics.TaxonomyNameInjector
        .TAXON_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.statistics.TaxonomyNameInjector
        .TAXON_NAME;

/**
 * @author Tony Wardell
 * Date: 06/10/2017
 * Time: 16:08
 * Created with IntelliJ IDEA.
 */
public class TaxonomyNameInjectorTest {

    private TaxonomyNameInjector nameInjector;

    @Before
    public void setUp() {
        nameInjector = new TaxonomyNameInjector();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), Is.is(TAXON_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        BasicTaxonomyNode mockedResponse = createBasicTaxonomyNode();
        ConvertedFilter<BasicTaxonomyNode> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        String taxonId = "100";
        StatisticsValue statisticsValue = new StatisticsValue(taxonId, 5, 10L);
        assertThat(statisticsValue.getName(), Is.is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, statisticsValue);

        assertThat(statisticsValue.getName(), Is.is(not(nullValue())));
        assertThat(statisticsValue.getName(), Is.is(mockedResponse.getScientificName()));
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        String taxonId = "100";
        StatisticsValue statisticsValue = new StatisticsValue(taxonId, 5, 10L);
        FilterRequest filterRequest = nameInjector.buildFilterRequest(statisticsValue);

        assertThat(filterRequest.getProperties(), hasEntry(nameInjector.getId(), emptyList()));
        assertThat(filterRequest.getProperties(), hasEntry(TAXON_ID, singletonList(String.valueOf(taxonId))));
    }

    private BasicTaxonomyNode createBasicTaxonomyNode() {
        BasicTaxonomyNode node = new BasicTaxonomyNode();
        node.setScientificName("a scientific name");
        return node;
    }
}
