package uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.transformer;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model.BasicGeneProduct;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.results.transformer.AbstractValueInjector;

import java.util.List;

import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;

/**
 * This class is responsible for supplementing an {@link Annotation} instance, which contains
 * a gene product identifier, with a list of gene product synonyms, through the use of a RESTful service.
 * @author Tony Wardell
 * Date: 23/06/2017
 * Time: 14:56
 * Created with IntelliJ IDEA.
 */
public class GeneProductSynonymsInjector extends AbstractValueInjector<BasicGeneProduct, Annotation> {

    static final String GENE_PRODUCT_ID = "geneProductId";
    static final String GENE_PRODUCT_SYNONYMS= "synonyms";

    @Override
    public String getId() {
        return GENE_PRODUCT_SYNONYMS;
    }

    @Override
    public FilterRequest buildFilterRequest(Annotation annotation) {
        return FilterRequest.newBuilder()
                            .addProperty(getId())
                            .addProperty(GENE_PRODUCT_ID, annotation.geneProductId.split(":")[1])
                            .build();
    }

    @Override
    public void injectValueFromResponse(ConvertedFilter<BasicGeneProduct> convertedRequest, Annotation annotation) {
        BasicGeneProduct response = convertedRequest.getConvertedValue();

        List<BasicGeneProduct.Result> results = response.getResults();
        if (!results.isEmpty()) {
            annotation.synonyms = toCSV(results.get(0).getSynonyms());
        }
    }
}
