package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Iterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
