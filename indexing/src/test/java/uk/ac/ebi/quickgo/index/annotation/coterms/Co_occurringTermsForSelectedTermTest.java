package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Iterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * @author Tony Wardell
 * Date: 09/09/2016
 * Time: 12:06
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class Co_occurringTermsForSelectedTermTest {

    @Mock
    Co_occurringTerm coOccurringTerm;

    @Test
    public void testCalculationCalledOnAllTerms(){

        String target = "GO:00003824";
        float totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        Co_occurringTermsForSelectedTerm cootfst = new Co_occurringTermsForSelectedTerm(target,
                totalNumberGeneProducts, selected);

        cootfst.addAndCalculate(coOccurringTerm);
        cootfst.addAndCalculate(coOccurringTerm);
        cootfst.addAndCalculate(coOccurringTerm);
        cootfst.addAndCalculate(coOccurringTerm);

        verify(coOccurringTerm, times(4)).calculateProbabilityRatio(2f,10f);
        verify(coOccurringTerm, times(4)).calculateProbabilitySimilarityRatio(2f);

       Iterator<Co_occurringTerm> it = cootfst.highestSimilarity();
        int itCounter = 0;
        while(it.hasNext()){
            it.next();
            itCounter++;
        }
        assertThat(itCounter, equalTo(4));
    }


    @Test
    public void testHighestSimilaritySortingWorks(){

        String target = "GO:00003824";
        float totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        Co_occurringTermsForSelectedTerm cootfst = new Co_occurringTermsForSelectedTerm(target,
                totalNumberGeneProducts, selected);

        Co_occurringTerm mock1 = mock(Co_occurringTerm.class, "One");
        Co_occurringTerm mock2 = mock(Co_occurringTerm.class, "Two");
        Co_occurringTerm mock3 = mock(Co_occurringTerm.class, "Three");
        Co_occurringTerm mock4 = mock(Co_occurringTerm.class, "Four");

        cootfst.addAndCalculate(mock1);
        cootfst.addAndCalculate(mock2);
        cootfst.addAndCalculate(mock3);
        cootfst.addAndCalculate(mock4);

        when(mock1.getSimilarityRatio()).thenReturn(3f);
        when(mock2.getSimilarityRatio()).thenReturn(2f);
        when(mock3.getSimilarityRatio()).thenReturn(5f);
        when(mock4.getSimilarityRatio()).thenReturn(1f);

        Iterator<Co_occurringTerm> it = cootfst.highestSimilarity();

        assertThat(it.next().getSimilarityRatio(), is(5f));
        assertThat(it.next().getSimilarityRatio(), is(3f));
        assertThat(it.next().getSimilarityRatio(), is(2f));
        assertThat(it.next().getSimilarityRatio(), is(1f));
    }



    @Test(expected=IllegalArgumentException.class)
    public void passingNullToAddAndCalculateCausesException(){

        String target = "GO:00003824";
        float totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        Co_occurringTermsForSelectedTerm cootfst = new Co_occurringTermsForSelectedTerm(target,
                totalNumberGeneProducts, selected);

        cootfst.addAndCalculate(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingNullTargetToConstructorCausesException(){
        String target = null;
        float totalNumberGeneProducts = 10;
        long selected = 2;
        Co_occurringTermsForSelectedTerm cootfst = new Co_occurringTermsForSelectedTerm(target,
                totalNumberGeneProducts, selected);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingTotalNumberGeneProductsEqualToZeroToConstructorCausesException(){
        String target = "GO:00003824";
        float totalNumberGeneProducts = 0;
        long selected = 2;
        Co_occurringTermsForSelectedTerm cootfst = new Co_occurringTermsForSelectedTerm(target,
                totalNumberGeneProducts, selected);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingSelectedEqualToZeroToConstructorCausesException(){
        String target = "GO:00003824";
        float totalNumberGeneProducts = 10;
        long selected = 0;
        Co_occurringTermsForSelectedTerm cootfst = new Co_occurringTermsForSelectedTerm(target,
                totalNumberGeneProducts, selected);
    }
}
