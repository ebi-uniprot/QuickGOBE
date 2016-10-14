package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTermMocker.*;

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
    private CoTermsAggregationWriter aggregator;

    private static final String GO_TERM = "GO:0003824";

    @Test
    public void calculateStatisticsSingleGoTermComparedWithItself() {
        long geneProductCount = 2L;

        when(aggregator.getCoTerms(GO_TERM)).thenReturn(singleCoTermMapping());
        when(aggregator.getGeneProductCountForGoTerm(GO_TERM)).thenReturn(geneProductCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        StatisticsCalculator coTermsCalculator = new StatisticsCalculator(aggregator);
        List<CoTerm> results = coTermsCalculator.process(GO_TERM);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getTarget(), is(GO_TERM));
        assertThat(results.get(0).getComparedTerm(), is(GO_TERM));
        assertThat(results.get(0).getProbabilityRatio(), is(1f));
        assertThat(results.get(0).getSimilarityRatio(), is(100f));
        assertThat(results.get(0).getTogether(), is(geneProductCount));
        assertThat(results.get(0).getCompared(), is(geneProductCount));
    }

    @Test
    public void goTermHasCo_occurrenceWithOneOtherTerm() {
        int noOfCoHits = 1;
        //int hitsPerTerm = 2;

        long geneProductCount = 10L;
        final String selected = "GO:0000001";
        final String compared = "GO:9000001";
        final List<String> selectedList = Collections.singletonList(selected);
        final List<String> comparedList = Collections.singletonList(compared);

        when(aggregator.getCoTerms("GO:0000001")).thenReturn(getCoTerms(compared, noOfCoHits));
        when(aggregator.getGeneProductCountForGoTerm(selectedList.get(0))).thenReturn(2L);
        when(aggregator.getGeneProductCountForGoTerm(comparedList.get(0))).thenReturn(2L);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        StatisticsCalculator coTermsCalculator = new StatisticsCalculator(aggregator);
        List<CoTerm> results = coTermsCalculator.process(selected);
        assertThat(results, hasSize(1));

        assertThat(results.get(0).getTarget(), is(selected));
        assertThat(results.get(0).getComparedTerm(), is(compared));
        assertThat(results.get(0).getProbabilityRatio(), is(2.5f));
        assertThat(results.get(0).getSimilarityRatio(), is(33.33f));
        assertThat(results.get(0).getTogether(), is(1L));
        assertThat(results.get(0).getCompared(), is(2L));
    }

    @Test
    public void singleGoTermHasCooccurrenceWithTwoOtherTerms() {
        int selected = 1;
        int compared = 2;
        int noOfCoHits = 1;

        long geneProductCount = 10L;
        final List<String> selectedList = makeTermList(selected, ID_FORMAT_1);
        final List<String> comparedList = makeTermList(compared, ID_FORMAT_2);

        when(aggregator.getCoTerms(selectedList.get(0))).thenReturn(getCoTerms(comparedList, noOfCoHits));
        when(aggregator.getGeneProductCountForGoTerm(selectedList.get(0))).thenReturn(2L);
        when(aggregator.getGeneProductCountForGoTerm(comparedList.get(0))).thenReturn(2L);
        when(aggregator.getGeneProductCountForGoTerm(comparedList.get(1))).thenReturn(2L);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        StatisticsCalculator coTermsCalculator = new StatisticsCalculator(aggregator);
        List<CoTerm> results = coTermsCalculator.process(selectedList.get(0));

        assertThat(results, hasSize(2));
        final float expectedProbabilityRatio = 2.5f;
        final float expectedSimilarityRatio = 33.33f;
        final long expectedTogether = 1L;
        final long expectedCompared = 2L;

        assertThat(results.get(0).getTarget(), is(selectedList.get(0)));
        assertThat(results.get(0).getComparedTerm(), is(comparedList.get(0)));
        assertThat(results.get(0).getProbabilityRatio(), is(expectedProbabilityRatio));
        assertThat(results.get(0).getSimilarityRatio(), is(expectedSimilarityRatio));
        assertThat(results.get(0).getTogether(), is(expectedTogether));
        assertThat(results.get(0).getCompared(), is(expectedCompared));

        assertThat(results.get(1).getTarget(), is(selectedList.get(0)));
        assertThat(results.get(1).getComparedTerm(), is(comparedList.get(1)));
        assertThat(results.get(1).getProbabilityRatio(), is(expectedProbabilityRatio));
        assertThat(results.get(1).getSimilarityRatio(), is(expectedSimilarityRatio));
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
        new StatisticsCalculator(null);
    }

}
