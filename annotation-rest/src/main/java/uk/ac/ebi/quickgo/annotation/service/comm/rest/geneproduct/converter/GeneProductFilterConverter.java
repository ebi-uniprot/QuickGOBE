package uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model.BasicGeneProduct;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * <p>This specialised simple filter converter acts as an identity function, where a
 * {@link uk.ac.ebi.quickgo.annotation.service.comm.rest.geneproduct.model.BasicGeneProduct}
 * instance is returned as the value of a {@link ConvertedFilter}.
 *
 * <p>This class is used when transforming {@link QueryResult}s of {@link Annotation}s.
 *
 * @author Tony Wardell
 * Date: 23/06/2017
 * Time: 16:37
 * Created with IntelliJ IDEA.
 */
public class GeneProductFilterConverter implements FilterConverter<BasicGeneProduct, BasicGeneProduct> {
    @Override public ConvertedFilter<BasicGeneProduct> transform(BasicGeneProduct basicGeneProduct) {
        return new ConvertedFilter<>(basicGeneProduct);
    }
}
