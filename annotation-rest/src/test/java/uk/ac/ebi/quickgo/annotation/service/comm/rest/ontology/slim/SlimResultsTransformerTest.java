package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.slim;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.SlimResultsTransformer;
import uk.ac.ebi.quickgo.rest.comm.QueryContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

/**
 * Created 11/08/16
 * @author Edd
 */
public class SlimResultsTransformerTest {

    private SlimResultsTransformer transformer;
    private QueryContext context;
    private ArrayList<Annotation> results;
    private SlimmingConversionInfo conversionInfo;

    @Before
    public void setUp() {
        conversionInfo = new SlimmingConversionInfo();
        transformer = new SlimResultsTransformer();
        context = new QueryContext();
        context.save(SlimmingConversionInfo.class, conversionInfo);
        results = new ArrayList<>();
    }

    @Test
    public void emptyContextOnEmptyResultsCausesNoTransformation() {
        QueryResult<Annotation> emptyQueryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(emptyQueryResult, context);

        assertThat(transformedResults, is(emptyQueryResult));
    }

    @Test
    public void emptyContextOnExistingResultsCausesNoTransformation() {
        addAnnotationToResults("goId1");

        QueryResult<Annotation> queryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(queryResult, context);

        assertThat(transformedResults.getResults().size(), is(1));
        assertThat(transformedResults, is(queryResult));
    }

    @Test
    public void existingResultsIsTransformed() {
        addAnnotationToResults("goId1");
        addKnownMapping("goId1", "slimmedId1");

        QueryResult<Annotation> queryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(queryResult, context);

        assertThat(transformedResults.getResults().size(), is(1));
        Annotation transformedAnnotation = transformedResults.getResults().get(0);
        assertThat(transformedAnnotation.goId, is("goId1"));
        assertThat(transformedAnnotation.slimmedGoIds, contains("slimmedId1"));
    }

    private void addAnnotationToResults(String goId, String... slimmedGOIds) {
        Annotation annotation = new Annotation();
        annotation.goId = goId;
        annotation.slimmedGoIds = Arrays.asList(slimmedGOIds);
        results.add(annotation);
    }

    private void addKnownMapping(String originalGOId, String... slimmedGOIds) {
        for (String slimmedGOId : slimmedGOIds) {
            conversionInfo.addOriginal2SlimmedGOIdMapping(originalGOId, slimmedGOId);
        }
    }

    private QueryResult<Annotation> createQueryResult() {
        return new QueryResult.Builder<>(results.size(), results).build();
    }
}