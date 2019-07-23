package uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model.BasicGeneProduct;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer.GeneProductSynonymsInjector
        .CANONICAL_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer.GeneProductSynonymsInjector
        .GENE_PRODUCT_SYNONYMS;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;

/**
 * Created 11/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneProductSynonymsInjectorTest {
    private static final List<String> SYNONYMS = Arrays.asList("A0A000_9ACTN", "moeA5");
    private GeneProductSynonymsInjector nameInjector;

    @Before
    public void setUp() {
        nameInjector = new GeneProductSynonymsInjector();
    }

    @Test
    public void injectorIdIsGoName() {
        assertThat(nameInjector.getId(), is(GENE_PRODUCT_SYNONYMS));
    }

    @Test
    public void responseValueIsInjectedToAnnotation() {
        BasicGeneProduct mockedResponse = createBasicGeneProduct();
        ConvertedFilter<BasicGeneProduct> stubConvertedFilter = new ConvertedFilter<>(mockedResponse);
        Annotation annotation = new Annotation();
        assertThat(annotation.synonyms, is(nullValue()));

        nameInjector.injectValueFromResponse(stubConvertedFilter, annotation);

        assertThat(annotation.synonyms, is(not(nullValue())));
        assertThat(annotation.synonyms, is(toCSV(SYNONYMS)));
    }

    @Test
    public void correctFilterRequestIsBuilt() {
        Annotation annotation = new Annotation();
        annotation.canonicalId = "A0A000";
        FilterRequest filterRequest = nameInjector.buildFilterRequest(annotation);

        assertThat(filterRequest.getProperties(), hasEntry(nameInjector.getId(), emptyList()));
        assertThat(filterRequest.getProperties(), hasEntry(CANONICAL_ID, singletonList("A0A000")));
    }

    private BasicGeneProduct createBasicGeneProduct() {
        BasicGeneProduct geneProduct = new BasicGeneProduct();
        List<BasicGeneProduct.Result> results = new ArrayList<>();
        BasicGeneProduct.Result result = new BasicGeneProduct.Result();
        result.setId("ID:1");
        result.setSynonyms(SYNONYMS);
        results.add(result);
        geneProduct.setResults(results);
        return geneProduct;
    }
}
