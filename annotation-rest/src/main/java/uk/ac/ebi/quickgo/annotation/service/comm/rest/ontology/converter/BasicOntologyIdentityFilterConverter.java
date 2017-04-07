package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.BasicOntology;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

/**
 * <p>This specialised simple filter converter acts as an identity function, where a {@link BasicOntology}
 * instance is returned as the value of a {@link ConvertedFilter}.
 *
 * <p>This class is used when transforming {@link QueryResult}s of {@link Annotation}s.
 *
 * Created 07/04/17
 * @author Edd
 */
public class BasicOntologyIdentityFilterConverter implements FilterConverter<BasicOntology, BasicOntology> {
    @Override public ConvertedFilter<BasicOntology> transform(BasicOntology basicOntology) {
        return new ConvertedFilter<>(basicOntology);
    }
}
