package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyDescendants;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.Set;
import java.util.function.Consumer;

import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants.
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsFilterConverter extends AbstractDescendantFilterConverter {
    @Override protected Consumer<String> processDescendant(
            OntologyDescendants.Result result, Set<QuickGOQuery> queries) {
        return desc -> queries.add(createQueryForOntologyId(desc));
    }

    @Override protected ConvertedFilter<QuickGOQuery> createFilterForAllDescendants(Set<QuickGOQuery> queries) {
        if (!queries.isEmpty()) {
            return new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])));
        } else {
            return FILTER_EVERYTHING;
        }
    }
}
