package uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model.BasicGeneProduct;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Tony Wardell
 * Date: 26/06/2017
 * Time: 15:23
 * Created with IntelliJ IDEA.
 */
public class GeneProductFilterConverterTest {
    private GeneProductFilterConverter converter;

    @Before
    public void setUp() {
        converter = new GeneProductFilterConverter();
    }

    @Test
    public void inputIsValueInConvertedFilter() {
        BasicGeneProduct geneProduct = createBasicGeneProduct();

        ConvertedFilter<BasicGeneProduct> convertedFilter = converter.transform(geneProduct);

        assertThat(convertedFilter.getConvertedValue(), is(geneProduct));
        assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
    }

    private BasicGeneProduct createBasicGeneProduct() {
        BasicGeneProduct geneProduct = new BasicGeneProduct();
        List<BasicGeneProduct.Result> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BasicGeneProduct.Result result = new BasicGeneProduct.Result();
            result.setId("ID:" + i);
            result.setName("name" + i);
            result.setSynonyms(Collections.singletonList("nameish"+ i));
            results.add(result);
        }
        geneProduct.setResults(results);
        return geneProduct;
    }
}
