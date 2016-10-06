package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermMocker.createMatrix;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermMocker.idFormat1;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermMocker.idFormat2;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermMocker.makeTermList;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermsAggregatorMocker.makeGpCountForTerm;

/**
 *
 * overlap = 2;  // Number of gene products annotated by both selected and compared terms
 * target  = 2;  // Number of gene products annotated by selected term
 * compared = 2; // Number of gene products annotated by compared terms
 * all  = 2;     // Total number of unique gene products annotated by selected term
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 16:26
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class StatisticsCalculatorTest {

    @Mock
    CoTermsAggregator aggregator;

    private static final String goTerm = "GO:0003824";

    @Test
    public void calculateStatisticsSingleGoTermComparedWithItself() {
        long geneProductCount = 2l;

        Map<String, Map<String, AtomicLong>> matrix = CoTermMocker.singleEntry();
        Map<String, AtomicLong> termGpCount = new HashMap<>();
        termGpCount.put(goTerm, new AtomicLong(geneProductCount));

        when(aggregator.getCoTerms()).thenReturn(matrix);
        when(aggregator.getGeneProductCounts()).thenReturn(termGpCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        StatisticsCalculator coTermsCalculator = new StatisticsCalculator(aggregator);
        List<CoTerm> results = coTermsCalculator.process(goTerm);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getTarget(), is(goTerm));
        assertThat(results.get(0).getComparedTerm(), is(goTerm));
        assertThat(results.get(0).getProbabilityRatio(), is(1f));
        assertThat(results.get(0).getSimilarityRatio(), is(100f));
        assertThat(results.get(0).getTogether(), is(geneProductCount));
        assertThat(results.get(0).getCompared(), is(geneProductCount));
    }

    @Test
    public void goTermHasCo_occurrenceWithOneOtherTerm() {
        int noOfCoHits = 1;
        int hitsPerTerm = 2;

        long geneProductCount = 10l;
        final String selected = "GO:0000001";
        final String compared = "GO:9000001";
        final List<String> selectedList = Arrays.asList(selected);
        final List<String> comparedList = Arrays.asList(compared);

        Map<String, AtomicLong> termGpCount = makeGpCountForTerm(hitsPerTerm, selectedList, comparedList);
        Map<String, Map<String, AtomicLong>> matrix = createMatrix(selectedList, comparedList, noOfCoHits);

        when(aggregator.getCoTerms()).thenReturn(matrix);
        when(aggregator.getGeneProductCounts()).thenReturn(termGpCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        StatisticsCalculator coTermsCalculator = new StatisticsCalculator(aggregator);
        List<CoTerm> results = coTermsCalculator.process(selected);
        assertThat(results, hasSize(1));

        assertThat(results.get(0).getTarget(), is(selected));
        assertThat(results.get(0).getComparedTerm(), is(compared));
        assertThat(results.get(0).getProbabilityRatio(), is(2.5f));
        assertThat(results.get(0).getSimilarityRatio(), is(33.33f));
        assertThat(results.get(0).getTogether(), is(1l));
        assertThat(results.get(0).getCompared(), is(2l));
    }

    @Test
    public void singleGoTermHasCooccurrenceWithTwoOtherTerms() {
        int selected = 1;
        int compared = 2;
        int noOfCoHits = 1;
        int hitsPerTerm = 2;

        long geneProductCount = 10l;
        final List<String> selectedList = makeTermList(selected, idFormat1);
        final List<String> comparedList = makeTermList(compared, idFormat2);

        Map<String, AtomicLong> termGpCount = makeGpCountForTerm(hitsPerTerm, selectedList, comparedList);
        Map<String, Map<String, AtomicLong>> matrix = createMatrix(selectedList, comparedList, noOfCoHits);

        when(aggregator.getCoTerms()).thenReturn(matrix);
        when(aggregator.getGeneProductCounts()).thenReturn(termGpCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        StatisticsCalculator coTermsCalculator = new StatisticsCalculator(aggregator);
        List<CoTerm> results = coTermsCalculator.process(selectedList.get(0));

        assertThat(results, hasSize(2));
        final float expectedProbabilityRatio = 2.5f;
        final float expectedSimiliarityRatio = 33.33f;
        final long expectedTogether = 1l;
        final long expectedCompared = 2l;

        assertThat(results.get(0).getTarget(), is(selectedList.get(0)));
        assertThat(results.get(0).getComparedTerm(), is(comparedList.get(0)));
        assertThat(results.get(0).getProbabilityRatio(), is(expectedProbabilityRatio));
        assertThat(results.get(0).getSimilarityRatio(), is(expectedSimiliarityRatio));
        assertThat(results.get(0).getTogether(), is(expectedTogether));
        assertThat(results.get(0).getCompared(), is(expectedCompared));

        assertThat(results.get(1).getTarget(), is(selectedList.get(0)));
        assertThat(results.get(1).getComparedTerm(), is(comparedList.get(1)));
        assertThat(results.get(1).getProbabilityRatio(), is(expectedProbabilityRatio));
        assertThat(results.get(1).getSimilarityRatio(), is(expectedSimiliarityRatio));
        assertThat(results.get(1).getTogether(), is(expectedTogether));
        assertThat(results.get(1).getCompared(), is(expectedCompared));
    }

    @Test(expected = IllegalArgumentException.class)
    public void processNullCausesException() {
        StatisticsCalculator calculator = new StatisticsCalculator(aggregator);
        calculator.process(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aggregatorIsNullCausesException() {
        final String goTerm = "GO:0003824";

        Map<String, Map<String, AtomicLong>> matrix = CoTermMocker.singleEntry();
        Map<String, AtomicLong> termGpCount = new HashMap<>();
        termGpCount.put(goTerm, new AtomicLong(2));
        new StatisticsCalculator(null);
    }

}
