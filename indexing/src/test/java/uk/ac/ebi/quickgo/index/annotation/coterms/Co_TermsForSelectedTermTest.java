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
public class Co_TermsForSelectedTermTest {

    @Mock
    CoTerm coTerm;

    @Test
    public void testCalculationCalledOnAllTerms(){

        String target = "GO:00003824";
        float totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        CoTermsForSelectedTerm cootfst = new CoTermsForSelectedTerm(totalNumberGeneProducts, selected);

        cootfst.addAndCalculate(coTerm);
        cootfst.addAndCalculate(coTerm);
        cootfst.addAndCalculate(coTerm);
        cootfst.addAndCalculate(coTerm);

        verify(coTerm, times(4)).calculateProbabilityRatio(2f,10f);
        verify(coTerm, times(4)).calculateProbabilitySimilarityRatio(2f);

       Iterator<CoTerm> it = cootfst.highestSimilarity();
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

        CoTermsForSelectedTerm cootfst = new CoTermsForSelectedTerm(totalNumberGeneProducts, selected);

        CoTerm mock1 = mock(CoTerm.class, "One");
        CoTerm mock2 = mock(CoTerm.class, "Two");
        CoTerm mock3 = mock(CoTerm.class, "Three");
        CoTerm mock4 = mock(CoTerm.class, "Four");

        cootfst.addAndCalculate(mock1);
        cootfst.addAndCalculate(mock2);
        cootfst.addAndCalculate(mock3);
        cootfst.addAndCalculate(mock4);

        when(mock1.getSimilarityRatio()).thenReturn(3f);
        when(mock2.getSimilarityRatio()).thenReturn(2f);
        when(mock3.getSimilarityRatio()).thenReturn(5f);
        when(mock4.getSimilarityRatio()).thenReturn(1f);

        Iterator<CoTerm> it = cootfst.highestSimilarity();

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

        CoTermsForSelectedTerm cootfst = new CoTermsForSelectedTerm(totalNumberGeneProducts, selected);

        cootfst.addAndCalculate(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingNullTargetToConstructorCausesException(){
        String target = null;
        float totalNumberGeneProducts = 10;
        long selected = 2;
        CoTermsForSelectedTerm cootfst = new CoTermsForSelectedTerm(totalNumberGeneProducts, selected);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingTotalNumberGeneProductsEqualToZeroToConstructorCausesException(){
        String target = "GO:00003824";
        float totalNumberGeneProducts = 0;
        long selected = 2;
        CoTermsForSelectedTerm cootfst = new CoTermsForSelectedTerm(totalNumberGeneProducts, selected);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingSelectedEqualToZeroToConstructorCausesException(){
        String target = "GO:00003824";
        float totalNumberGeneProducts = 10;
        long selected = 0;
        CoTermsForSelectedTerm cootfst = new CoTermsForSelectedTerm(totalNumberGeneProducts, selected);
    }
}
