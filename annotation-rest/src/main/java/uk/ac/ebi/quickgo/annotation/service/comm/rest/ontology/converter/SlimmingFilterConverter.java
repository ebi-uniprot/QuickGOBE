package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyDescendants;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.Set;
import java.util.function.Consumer;

import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedFilter}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants. Additionally, this process records slimming information consisting of original
 * GO ids and their mappings to slimmed up GO ids; this is stored within an instance of {@link SlimmingConversionInfo}
 * available from the {@link ConvertedFilter}'s {@link FilterContext}.
 *
 * Created 09/08/16
 * @author Edd
 */
public class SlimmingFilterConverter extends AbstractDescendantFilterConverter {
    private final SlimmingConversionInfo conversionInfo;

    public SlimmingFilterConverter() {
        conversionInfo = new SlimmingConversionInfo();
    }

    @Override protected Consumer<String> processDescendant(
            OntologyDescendants.Result result, Set<QuickGOQuery> queries) {
        return desc -> {
            queries.add(createQueryForOntologyId(desc));
            conversionInfo.addOriginal2SlimmedGOIdMapping(desc, result.getId());
        };
    }

    @Override protected ConvertedFilter<QuickGOQuery> createFilterForAllDescendants(Set<QuickGOQuery> queries) {
        if (!queries.isEmpty()) {
            FilterContext context = new FilterContext();
            context.save(SlimmingConversionInfo.class, conversionInfo);
            return new ConvertedFilter<>(or(queries.toArray(new QuickGOQuery[queries.size()])), context);
        } else {
            return FILTER_EVERYTHING;
        }
    }
}
