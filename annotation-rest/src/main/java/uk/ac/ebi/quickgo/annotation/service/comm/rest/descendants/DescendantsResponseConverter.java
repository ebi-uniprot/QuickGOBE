package uk.ac.ebi.quickgo.annotation.service.comm.rest.descendants;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.OntologyResponse;
import uk.ac.ebi.quickgo.rest.comm.ConversionContext;
import uk.ac.ebi.quickgo.rest.comm.ConvertedResponse;
import uk.ac.ebi.quickgo.rest.comm.ResponseConverter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Converts a response to a quickgoquery
 *
 * Created 09/08/16
 * @author Edd
 */
public class DescendantsResponseConverter implements ResponseConverter<OntologyResponse, QuickGOQuery> {
    @Override public ConvertedResponse<QuickGOQuery> convert(OntologyResponse response) {
        ConvertedResponse<QuickGOQuery> convertedResponse = new ConvertedResponse<>();
        ConversionContext context = new ConversionContext();

        QuickGOQuery filterEverything = QuickGOQuery.createAllQuery().not();
        if (response.getResults() != null) {
            QuickGOQuery quickGOQuery = response.getResults().stream()
                    .map(OntologyResponse.Result::getDescendants)
                    .flatMap(Collection::stream)
                    .map(descendant -> QuickGOQuery.createQuery(AnnotationFields.GO_ID, descendant))
                    .collect(Collectors.toSet())
                    .stream()
                    .reduce(QuickGOQuery::or)
                    .orElse(filterEverything);
            convertedResponse.setConvertedValue(quickGOQuery);
        } else {
            convertedResponse.setConvertedValue(filterEverything);
        }

        convertedResponse.setConversionContext(context);

        return convertedResponse;
    }
}
