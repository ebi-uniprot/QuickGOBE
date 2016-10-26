package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.ConvertedOntologyFilter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.FilterConverterHelper.*;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants.
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsFilterConverter implements FilterConverter<ConvertedOntologyFilter, QuickGOQuery> {
    @Override public ConvertedFilter<QuickGOQuery> transform(ConvertedOntologyFilter response) {
        ConvertedFilter<QuickGOQuery> convertedFilter = FILTER_EVERYTHING;

        StringJoiner idsWithNoDescendants = createNoDescendantRecorder();
        if (isNotNull(response.getResults())) {
            Set<QuickGOQuery> queries = new HashSet<>();

            for (ConvertedOntologyFilter.Result result : response.getResults()) {
                if (isNotNull(result.getDescendants())) {
                    forEachDescendantApply(
                            result,
                            desc -> queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc)));
                } else {
                    updateJoinerIfValid(idsWithNoDescendants, result.getId());
                }
            }

            convertedFilter = createFilterForAllDescendants(queries);
        }

        handleNoDescendants(idsWithNoDescendants);

        return convertedFilter;
    }

    private ConvertedFilter<QuickGOQuery> createFilterForAllDescendants(Set<QuickGOQuery> queries) {
        if (!queries.isEmpty()) {
            return new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])));
        } else {
            return FILTER_EVERYTHING;
        }
    }
}
