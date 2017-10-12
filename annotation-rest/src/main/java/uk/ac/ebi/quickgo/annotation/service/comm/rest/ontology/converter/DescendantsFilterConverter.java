package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.Set;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants.
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsFilterConverter extends AbstractOntologyFilterConverter {
    @Override protected boolean validResult(OntologyRelatives.Result result) {
        return nonNull(result.getDescendants());
    }

    @Override protected void processResult(OntologyRelatives.Result result, Set<QuickGOQuery> queries) {
        result.getDescendants().stream()
                .filter(AbstractOntologyFilterConverter::notNullOrEmpty)
                .forEach(desc -> queries.add(createQueryForOntologyId(desc)));
    }

    @Override protected ConvertedFilter<QuickGOQuery> createFilter(Set<QuickGOQuery> queries) {
        if (!queries.isEmpty()) {
            return new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])));
        } else {
            return FILTER_EVERYTHING;
        }
    }
}
