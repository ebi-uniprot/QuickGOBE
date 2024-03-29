package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 11/04/17
 * @author Edd
 */
class BasicOntologyIdentityFilterConverterTest {
    private BasicOntologyIdentityFilterConverter converter;

    @BeforeEach
    void setUp() {
        converter = new BasicOntologyIdentityFilterConverter();
    }

    @Test
    void inputIsValueInConvertedFilter() {
        BasicOntology ontology = createBasicOntology();

        ConvertedFilter<BasicOntology> convertedFilter = converter.transform(ontology);

        assertThat(convertedFilter.getConvertedValue(), is(ontology));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    private BasicOntology createBasicOntology() {
        BasicOntology ontology = new BasicOntology();
        List<BasicOntology.Result> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BasicOntology.Result result = new BasicOntology.Result();
            result.setId("ID:" + i);
            results.add(result);
        }
        ontology.setResults(results);
        return ontology;
    }
}