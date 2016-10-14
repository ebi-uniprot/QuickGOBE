package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;

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
public class CoTermsProcessorTest {

    @Mock
    private CoTermsAggregationWriter aggregator;

    @Mock
    CoTermsForSelectedTerm coTermsForSelectedTerm;

    private static final String GO_TERM = "GO:0003824";


    @Test
    public void singleGoTermHasCooccurrenceWithTwoOtherTerms() {

        List<CoTerm> returnList = Arrays.asList();
        when(aggregator.createCoTermsForSelectedTerm(GO_TERM)).thenReturn(coTermsForSelectedTerm);
        when(coTermsForSelectedTerm.highestSimilarity()).thenReturn(returnList);
        CoTermsProcessor coTermsCalculator = new CoTermsProcessor(aggregator);
        assertThat(coTermsCalculator.process(GO_TERM), is(returnList));
    }

    @Test(expected = IllegalArgumentException.class)
    public void processNullCausesException() {
        CoTermsProcessor calculator = new CoTermsProcessor(aggregator);
        calculator.process(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aggregatorIsNullCausesException() {
        new CoTermsProcessor(null);
    }

}
