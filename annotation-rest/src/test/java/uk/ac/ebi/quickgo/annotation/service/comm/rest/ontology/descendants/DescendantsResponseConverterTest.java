package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.DescendantsResponseConverter;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyResponse;
import uk.ac.ebi.quickgo.rest.comm.ConvertedResponse;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;

/**
 * Created 10/08/16
 * @author Edd
 */
public class DescendantsResponseConverterTest {
    private OntologyResponse response;
    private DescendantsResponseConverter converter;

    @Before
    public void setUp() {
        response = new OntologyResponse();
        response.setResults(new ArrayList<>());
        converter = new DescendantsResponseConverter();
    }

    @Test
    public void descendantsFromSingleResourceAreConvertedToQuickGOQuery() {
        String id1 = "id1";
        String desc1 = "desc1";

        addResponseDescendant(id1, desc1);
        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(convertedResponse.getConvertedValue(), is(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc1)));
    }

    @Test
    public void differentDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = "id1";
        String id2 = "id2";
        String desc1 = "desc1";
        String desc2 = "desc2";

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc2);

        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(convertedResponse.getConvertedValue(), is(
                QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc1)
                        .or(QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc2))));
    }

    @Test
    public void sameDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
        String id1 = "id1";
        String id2 = "id2";
        String desc1 = "desc1";

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id2, desc1);

        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(convertedResponse.getConvertedValue(), is(
                QuickGOQuery.createQuery(AnnotationFields.GO_ID, desc1)));
    }

    @Test
    public void nullResultsMeansFilterEverything() {
        response.setResults(null);
        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(convertedResponse.getConvertedValue(), is(QuickGOQuery.createAllQuery().not()));
    }

    @Test
    public void emptyResultsMeansFilterEverything() {
        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(convertedResponse.getConvertedValue(), is(QuickGOQuery.createAllQuery().not()));
    }

    @Test
    public void conversionContextContainsOneMapping() {
        String id1 = "id1";
        String desc1 = "desc1";

        addResponseDescendant(id1, desc1);
        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(extractContextProperties(convertedResponse), hasEntry(desc1, singletonList(id1)));
    }

    @Test
    public void conversionContextContainsTwoMappings() {
        String id1 = "id1";
        String id2 = "id2";
        String desc1 = "desc1";
        String desc2 = "desc2";
        String desc3 = "desc3";

        addResponseDescendant(id1, desc1);
        addResponseDescendant(id1, desc2);
        addResponseDescendant(id1, desc3);
        addResponseDescendant(id2, desc3);
        ConvertedResponse<QuickGOQuery> convertedResponse = converter.transform(response);

        assertThat(extractContextProperties(convertedResponse), hasEntry(desc1, singletonList(id1)));
        assertThat(extractContextProperties(convertedResponse), hasEntry(desc2, singletonList(id1)));
        assertThat(extractContextProperties(convertedResponse), hasEntry(desc3, asList(id1, id2)));
    }

    private Map<String, List<String>> extractContextProperties(ConvertedResponse<QuickGOQuery> convertedResponse) {
        SlimmingConversionInfo conversionInfo = convertedResponse.getQueryContext()
                .map(t -> t.get(SlimmingConversionInfo.class)
                        .orElse(new SlimmingConversionInfo()))
                .orElseThrow(IllegalStateException::new);

        return conversionInfo.getInfo();
    }

    private void addResponseDescendant(String termId, String descendantId) {
        for (OntologyResponse.Result result : response.getResults()) {
            if (result.getId().equals(termId)) {
                result.getDescendants().add(descendantId);
                return;
            }
        }

        OntologyResponse.Result newResult = new OntologyResponse.Result();
        newResult.setId(termId);
        List<String> descList = new ArrayList<>();
        descList.add(descendantId);
        newResult.setDescendants(descList);
        response.getResults().add(newResult);
    }
}