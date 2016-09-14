package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static uk.ac.ebi.quickgo.index.annotation.coterms.Co_occurringTermMocker.createMatrix;
import static uk.ac.ebi.quickgo.index.annotation.coterms.Co_occurringTermMocker.df1;
import static uk.ac.ebi.quickgo.index.annotation.coterms.Co_occurringTermMocker.df2;
import static uk.ac.ebi.quickgo.index.annotation.coterms.Co_occurringTermMocker.makeGpHitCountForTerm;
import static uk.ac.ebi.quickgo.index.annotation.coterms.Co_occurringTermMocker.makeTermList;


/**
 * @Author Tony Wardell
 * Date: 26/11/2015
 * Time: 16:26
 * Created with IntelliJ IDEA.
 *
 * Based on CoStatsSummarizerTest in Beta.
 *
 * overlap = 2;  // Number of gene products annotated by both selected and compared terms
 * target  = 2;  // Number of gene products annotated by selected term
 * compared = 2; // Number of gene products annotated by compared terms
 * all  = 2;     // Total number of unique gene products annotated by selected term
 */
@RunWith(MockitoJUnitRunner.class)
public class Co_occurringTermsStatsCalculatorTest {

    @Mock
    AnnotationCo_occurringTermsAggregator aggregator;

	@Test
	public void calculateStatisticsSingleGoTermComparedWithItself(){
        long geneProductCount = 2l;
        final String goTerm = "GO:0003824";

        Map<String, Map<String, HitCount>> matrix = Co_occurringTermMocker.singleEntry();
        Map<String, HitCount> termGpCount = new HashMap<>();
        termGpCount.put(goTerm, new HitCount(2));

        when(aggregator.getTermToTermOverlapMatrix()).thenReturn(matrix);
        when(aggregator.getGeneProductCounts()).thenReturn(termGpCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        Co_occurringTermsStatsCalculator coTermsCalculator = new Co_occurringTermsStatsCalculator(aggregator);
        coTermsCalculator.initialize();
        List<Co_occurringTerm> results = coTermsCalculator.process(goTerm);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getTarget(), is(goTerm));
        assertThat(results.get(0).getComparedTerm(), is(goTerm));
        assertThat(results.get(0).getProbabilityRatio(), is(1f));    //correct
        assertThat(results.get(0).getSimilarityRatio(), is(100f));      //correct
        assertThat(results.get(0).getTogether(), is(2l));            //correct
        assertThat(results.get(0).getCompared(), is(2l));            //correct
	}


    @Test
    public void goTermHasCo_occurrenceWithOneOtherTerm(){
        int noOfCoHits = 1;
        int hitsPerTerm = 2;

        long geneProductCount = 10l;
        final String selected = "GO:0000001";
        final String compared = "GO:9000001";
        final List<String> selectedList = Arrays.asList(selected);
        final List<String> comparedList = Arrays.asList(compared);

        Map<String, HitCount> termGpCount = makeGpHitCountForTerm(hitsPerTerm, selectedList, comparedList);
        Map<String, Map<String, HitCount>> matrix = createMatrix(selectedList, comparedList, noOfCoHits);

        when(aggregator.getTermToTermOverlapMatrix()).thenReturn(matrix);
        when(aggregator.getGeneProductCounts()).thenReturn(termGpCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        Co_occurringTermsStatsCalculator coTermsCalculator = new Co_occurringTermsStatsCalculator(aggregator);
        coTermsCalculator.initialize();
        List<Co_occurringTerm> results = coTermsCalculator.process(selected);
        assertThat(results, hasSize(1));

        assertThat(results.get(0).getTarget(), is("GO:0000001"));
        assertThat(results.get(0).getComparedTerm(), is("GO:9000001"));
        assertThat(results.get(0).getProbabilityRatio(), is(2.5f));      //correct
        assertThat(results.get(0).getSimilarityRatio(), is(33.33f));        //correct
        assertThat(results.get(0).getTogether(), is(1l));
        assertThat(results.get(0).getCompared(), is(2l));
    }


    @Test
    public void singleGoTermHasCooccurrenceWithTwoOtherTerms(){
        int selected = 1;
        int compared = 2;
        int noOfCoHits = 1;
        int hitsPerTerm = 2;

        long geneProductCount = 10l;
        final List<String> selectedList = makeTermList(selected, df1);
        final List<String> comparedList = makeTermList(compared, df2);

        Map<String, HitCount> termGpCount = makeGpHitCountForTerm(hitsPerTerm, selectedList, comparedList);
        Map<String, Map<String, HitCount>> matrix = createMatrix(selectedList, comparedList, noOfCoHits);

        when(aggregator.getTermToTermOverlapMatrix()).thenReturn(matrix);
        when(aggregator.getGeneProductCounts()).thenReturn(termGpCount);
        when(aggregator.getTotalOfAnnotatedGeneProducts()).thenReturn(geneProductCount);

        Co_occurringTermsStatsCalculator coTermsCalculator = new Co_occurringTermsStatsCalculator(aggregator);
        coTermsCalculator.initialize();
        List<Co_occurringTerm> results = coTermsCalculator.process(selectedList.get(0));

        assertThat(results, hasSize(2));

        assertThat(results.get(0).getTarget(), is(selectedList.get(0)));
        assertThat(results.get(0).getComparedTerm(), is(comparedList.get(0)));
        assertThat(results.get(0).getProbabilityRatio(), is(2.5f));
        assertThat(results.get(0).getSimilarityRatio(), is(33.33f));
        assertThat(results.get(0).getTogether(), is(1l));
        assertThat(results.get(0).getCompared(), is(2l));

        assertThat(results.get(1).getTarget(), is(selectedList.get(0)));
        assertThat(results.get(1).getComparedTerm(), is(comparedList.get(1)));
        assertThat(results.get(1).getProbabilityRatio(), is(2.5f));
        assertThat(results.get(1).getSimilarityRatio(), is(33.33f));
        assertThat(results.get(1).getTogether(), is(1l));
        assertThat(results.get(1).getCompared(), is(2l));
    }

    @Test(expected = IllegalArgumentException.class)
    public void targetStringIsNullCausesException(){
        long geneProductCount = 2l;
        final String goTerm = "GO:0003824";

        Map<String, Map<String, HitCount>> matrix = Co_occurringTermMocker.singleEntry();
        Map<String, HitCount> termGpCount = new HashMap<>();
        termGpCount.put(goTerm, new HitCount(2));

        Co_occurringTermsStatsCalculator
                coOccurringTermsStatsCalculator = new Co_occurringTermsStatsCalculator(aggregator);
        coOccurringTermsStatsCalculator.process(null);

    }

}
