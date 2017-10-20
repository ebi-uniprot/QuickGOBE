package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import com.google.common.base.Strings;
import java.util.Set;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id slimming information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants. Additionally, this slimming information (GO ids and their mappings to slimmed up
 * GO ids) is stored within an instance of {@link SlimmingConversionInfo} available from the {@link ConvertedFilter}'s
 * {@link FilterContext}.
 *
 * Created 09/08/16
 * @author Edd
 */
public class SlimmingFilterConverter extends AbstractOntologyFilterConverter {
    private final SlimmingConversionInfo conversionInfo;

    public SlimmingFilterConverter() {
        conversionInfo = new SlimmingConversionInfo();
    }

    @Override protected boolean validResult(OntologyRelatives.Result result) {
        return nonNull(result.getSlimsTo());
    }

    @Override protected void processResult(OntologyRelatives.Result result, Set<QuickGOQuery> queries) {
        queries.add(createQueryForOntologyId(result.getId()));
        result.getSlimsTo().stream()
                .filter(slim -> !Strings.isNullOrEmpty(slim))
                .forEach(slimId -> conversionInfo.addOriginal2SlimmedGOIdMapping(result.getId(), slimId)
                );
    }

    @Override protected ConvertedFilter<QuickGOQuery> createFilter(Set<QuickGOQuery> queries) {
        if (!queries.isEmpty()) {
            FilterContext context = new FilterContext();
            context.save(SlimmingConversionInfo.class, conversionInfo);
            return new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])), context);
        } else {
            return FILTER_EVERYTHING;
        }
    }
}
