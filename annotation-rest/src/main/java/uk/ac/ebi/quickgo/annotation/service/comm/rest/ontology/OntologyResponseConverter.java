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

    @Override public ConvertedResponse<QuickGOQuery> convert(OntologyResponse response) {
        ConvertedResponse<QuickGOQuery> convertedResponse = new ConvertedResponse<>();
        ConversionContext context = new ConversionContext();

        SlimmingConversionInfo conversionInfo = new SlimmingConversionInfo();

        QuickGOQuery filterEverything = QuickGOQuery.createAllQuery().not();
        if (response.getResults() != null) {
            Set<QuickGOQuery> queries = new HashSet<>();
            for (OntologyResponse.Result result : response.getResults()) {
                for (String desc : result.getDescendants()) {
                    queries.add(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc));
                    if (!conversionInfo.descendantToTermMap.containsKey(desc)) {
                        conversionInfo.descendantToTermMap.put(desc, new ArrayList<>());
                    }
                    conversionInfo.descendantToTermMap.get(desc).add(result.getId());
                }
            }

            context.put(SlimmingConversionInfo.class, conversionInfo);

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

    public static class SlimmingConversionInfo {
        private Map<String, List<String>> descendantToTermMap = new HashMap<>();

        public Map<String, List<String>> getInfo() {
            return descendantToTermMap;
        }
    }
}
