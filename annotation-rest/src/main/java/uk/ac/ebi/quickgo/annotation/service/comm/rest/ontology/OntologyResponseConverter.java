package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.OntologyResponse;
import uk.ac.ebi.quickgo.rest.comm.ConversionContext;
import uk.ac.ebi.quickgo.rest.comm.ConvertedResponse;
import uk.ac.ebi.quickgo.rest.comm.ResponseConverter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.*;

/**
 * Converts a response to a quickgoquery
 *
 * Created 09/08/16
 * @author Edd
 */
public class OntologyResponseConverter implements ResponseConverter<OntologyResponse, QuickGOQuery> {

    public static final String ONTOLOGY_RESPONSE_CONTEXT_KEY = "ontology-response";

    @Override public ConvertedResponse<QuickGOQuery> convert(OntologyResponse response) {
        ConvertedResponse<QuickGOQuery> convertedResponse = new ConvertedResponse<>();
        ConversionContext context = new ConversionContext();

        Map<String, List<String>> descendantToTermMap = new HashMap<>();

        QuickGOQuery filterEverything = QuickGOQuery.createAllQuery().not();
        if (response.getResults() != null) {
            Set<QuickGOQuery> queries = new HashSet<>();
            for (OntologyResponse.Result result : response.getResults()) {
                for (String desc : result.getDescendants()) {
                    queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc));
                    if (!descendantToTermMap.containsKey(desc)) {
                        descendantToTermMap.put(desc, new ArrayList<>());
                    }
                    descendantToTermMap.get(desc).add(result.getId());
                }
            }

            context.getProperties().put(ONTOLOGY_RESPONSE_CONTEXT_KEY, descendantToTermMap);

            convertedResponse.setConvertedValue(
                    queries.stream()
                            .reduce(QuickGOQuery::or)
                            .orElse(filterEverything));
        } else {
            convertedResponse.setConvertedValue(filterEverything);
        }

        convertedResponse.setConversionContext(context);

        return convertedResponse;
    }
}
