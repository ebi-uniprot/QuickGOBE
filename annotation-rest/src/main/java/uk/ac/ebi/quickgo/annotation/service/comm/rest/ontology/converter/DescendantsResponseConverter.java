package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyResponse;
import uk.ac.ebi.quickgo.rest.comm.ConvertedResponse;
import uk.ac.ebi.quickgo.rest.comm.QueryContext;
import uk.ac.ebi.quickgo.rest.comm.ResponseConverter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for converting an ontology model containing GO id descendant information, to a
 * {@link ConvertedResponse}. This result encapsulates a {@link QuickGOQuery} applicable to filtering the annotation
 * core data, by these descendants. Additionally, this process records slimming information consisting of original
 * GO ids and their mappings to slimmed up GO ids; this is stored within an instance of {@link SlimmingConversionInfo}
 * available from the {@link ConvertedResponse}'s {@link QueryContext}.
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsResponseConverter implements ResponseConverter<OntologyResponse, QuickGOQuery> {

    @Override public ConvertedResponse<QuickGOQuery> convert(OntologyResponse response) {
        ConvertedResponse<QuickGOQuery> convertedResponse = new ConvertedResponse<>();
        QueryContext context = new QueryContext();

        SlimmingConversionInfo conversionInfo = new SlimmingConversionInfo();

        QuickGOQuery filterEverything = QuickGOQuery.createAllQuery().not();
        if (response.getResults() != null) {
            Set<QuickGOQuery> queries = new HashSet<>();
            for (OntologyResponse.Result result : response.getResults()) {
                for (String desc : result.getDescendants()) {
                    queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc));
                    conversionInfo.addOriginal2SlimmedGOIdMapping(desc, result.getId());
                }
            }

            context.save(SlimmingConversionInfo.class, conversionInfo);

            convertedResponse.setConvertedValue(
                    queries.stream()
                            .reduce(QuickGOQuery::or)
                            .orElse(filterEverything));
        } else {
            convertedResponse.setConvertedValue(filterEverything);
        }

        convertedResponse.setQueryContext(context);

        return convertedResponse;
    }
}
