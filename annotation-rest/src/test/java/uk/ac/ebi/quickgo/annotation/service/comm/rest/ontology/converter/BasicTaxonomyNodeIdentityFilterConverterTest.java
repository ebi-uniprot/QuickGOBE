package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicTaxonomyNode;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 12/04/17
 * @author Edd
 */
class BasicTaxonomyNodeIdentityFilterConverterTest {
    private BasicTaxonomyNodeIdentityFilterConverter converter;

    @BeforeEach
    void setUp() {
        converter = new BasicTaxonomyNodeIdentityFilterConverter();
    }

    @Test
    void inputIsValueInConvertedFilter() {
        BasicTaxonomyNode node = createBasicTaxonomyNode();

        ConvertedFilter<BasicTaxonomyNode> convertedFilter = converter.transform(node);

        assertThat(convertedFilter.getConvertedValue(), is(node));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    private BasicTaxonomyNode createBasicTaxonomyNode() {
        BasicTaxonomyNode node = new BasicTaxonomyNode();
        node.setScientificName("a scientific name");
        return node;
    }
}