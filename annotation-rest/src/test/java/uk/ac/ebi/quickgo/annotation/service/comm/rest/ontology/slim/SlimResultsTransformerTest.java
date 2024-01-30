package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.slim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.SlimmingConversionInfo;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.annotation.SlimResultsTransformer;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * Created 11/08/16
 * @author Edd
 */
class SlimResultsTransformerTest {

    private SlimResultsTransformer transformer;
    private FilterContext context;
    private ArrayList<Annotation> results;
    private SlimmingConversionInfo conversionInfo;

    @BeforeEach
    void setUp() {
        conversionInfo = new SlimmingConversionInfo();
        transformer = new SlimResultsTransformer();
        context = new FilterContext();
        context.save(SlimmingConversionInfo.class, conversionInfo);
        results = new ArrayList<>();
    }

    @Test
    void emptyContextOnEmptyResultsCausesNoTransformation() {
        QueryResult<Annotation> emptyQueryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(emptyQueryResult, context);

        assertThat(transformedResults, is(emptyQueryResult));
    }

    @Test
    void emptyContextOnExistingResultsCausesNoTransformation() {
        addAnnotationToResults("goId1");

        QueryResult<Annotation> queryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(queryResult, context);

        assertThat(transformedResults.getResults(), hasSize(1));
        assertThat(transformedResults, is(queryResult));
    }

    @Test
    void oneResultThatMatchesOneSlimTermInContextIsTransformed() {
        String goId1 = "goId1";
        String slimmedId1 = "slimmedId1";

        addAnnotationToResults(goId1);
        addKnownMapping(goId1, slimmedId1);

        QueryResult<Annotation> queryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(queryResult, context);

        assertThat(transformedResults.getResults(), hasSize(1));
        Annotation transformedAnnotation = transformedResults.getResults().get(0);
        assertThat(transformedAnnotation.goId, is(goId1));
        assertThat(transformedAnnotation.slimmedIds, contains(slimmedId1));
    }

    @Test
    void oneResultThatMatchesTwoSlimTermsInContextIsTransformed() {
        String goId1 = "goId1";
        String slimmedId1 = "slimmedId1";
        String slimmedId2 = "slimmedId2";

        addAnnotationToResults(goId1);
        addKnownMapping(goId1, slimmedId1);
        addKnownMapping(goId1, slimmedId2);

        QueryResult<Annotation> queryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(queryResult, context);

        assertThat(transformedResults.getResults(), hasSize(1));
        Annotation transformedAnnotation = transformedResults.getResults().get(0);
        assertThat(transformedAnnotation.goId, is(goId1));
        assertThat(transformedAnnotation.slimmedIds, contains(slimmedId1, slimmedId2));
    }

    @Test
    void resultThatDoesNotMatchSlimTermInContextIsNotTransformed() {
        String goId1 = "goId1";
        addAnnotationToResults(goId1);

        String goId2 = "goId2";
        String slimmedId1 = "slimmedId1";
        addKnownMapping(goId2, slimmedId1);

        QueryResult<Annotation> queryResult = createQueryResult();
        QueryResult<Annotation> transformedResults = transformer.transform(queryResult, context);

        assertThat(transformedResults.getResults(), hasSize(1));

        Annotation nonTransformedAnnotation1 = transformedResults.getResults().get(0);
        assertThat(nonTransformedAnnotation1.goId, is(goId1));
        assertThat(nonTransformedAnnotation1.slimmedIds, is(nullValue()));
    }

    private void addAnnotationToResults(String goId) {
        Annotation annotation = new Annotation();
        annotation.goId = goId;
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
