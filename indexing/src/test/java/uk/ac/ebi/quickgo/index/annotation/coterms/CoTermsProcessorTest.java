package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
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
@ExtendWith(MockitoExtension.class)
class CoTermsProcessorTest {

    private static final String GO_TERM = "GO:0003824";
    @Mock
    private CoTermsForSelectedTerm coTermsForSelectedTerm;
    @Mock
    private CoTermsAggregationWriter aggregator;

    @Test
    void singleGoTermHasCooccurrenceWithTwoOtherTerms() {

        CoTerm mockTermA = mock(CoTerm.class);
        CoTerm mockTermB = mock(CoTerm.class);
        CoTerm mockTermC = mock(CoTerm.class);

        List<CoTerm> returnList = Arrays.asList(mockTermA, mockTermB, mockTermC);

        when(aggregator.createCoTermsForSelectedTerm(GO_TERM)).thenReturn(coTermsForSelectedTerm);
        when(coTermsForSelectedTerm.highestSimilarity()).thenReturn(returnList);
        CoTermsProcessor coTermsCalculator = new CoTermsProcessor(aggregator);
        assertThat(coTermsCalculator.process(GO_TERM), is(returnList));
    }

    @Test
    void processNullCausesException() {
        assertThrows(IllegalArgumentException.class, () -> {
            CoTermsProcessor calculator = new CoTermsProcessor(aggregator);
            calculator.process(null);
        });
    }

    @Test
    void aggregatorIsNullCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new CoTermsProcessor(null));
    }

}
