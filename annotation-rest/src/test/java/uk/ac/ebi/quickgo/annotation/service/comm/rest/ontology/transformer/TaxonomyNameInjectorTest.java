package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjector.TAXON_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.TaxonomyNameInjector.TAXON_NAME;

/**
 * Created 12/04/17
 * @author Edd
 */
public class TaxonomyNameInjectorTest {
    private TaxonomyNameInjector nameInjector;

    @Before
    public void setUp() {
        nameInjector = new TaxonomyNameInjector();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is(TAXON_NAME));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        BasicTaxonomyNode mockedResponse = createBasicTaxonomyNode();
        ConvertedFilter<BasicTaxonomyNode> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        Annotation annotation = new Annotation();
        assertThat(annotation.taxonName, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, annotation);

        assertThat(annotation.taxonName, is(not(nullValue())));
        assertThat(annotation.taxonName, is(mockedResponse.getScientificName()));
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        Annotation annotation = new Annotation();
        int taxonId = 100;
        annotation.taxonId = taxonId;
        FilterRequest filterRequest = nameInjector.buildFilterRequest(annotation);

        assertThat(filterRequest.getProperties(), hasEntry(nameInjector.getId(), emptyList()));
        assertThat(filterRequest.getProperties(), hasEntry(TAXON_ID, singletonList(String.valueOf(taxonId))));
    }

    private BasicTaxonomyNode createBasicTaxonomyNode() {
        BasicTaxonomyNode node = new BasicTaxonomyNode();
        node.setScientificName("a scientific name");
        return node;
    }
}