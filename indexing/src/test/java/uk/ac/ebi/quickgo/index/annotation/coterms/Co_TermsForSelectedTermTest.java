package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
        long totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts
        (totalNumberGeneProducts).setSelected(selected);

        cootfstBuilder.addCoTerm(coTerm);
        cootfstBuilder.addCoTerm(coTerm);
        cootfstBuilder.addCoTerm(coTerm);
        cootfstBuilder.addCoTerm(coTerm);
        CoTermsForSelectedTerm cootfst = cootfstBuilder.build();

        verify(coTerm, times(4)).calculateProbabilityRatio(2f,10f);
        verify(coTerm, times(4)).calculateProbabilitySimilarityRatio(2f);

       List<CoTerm> terms = cootfst.highestSimilarity();
        assertThat(terms, hasSize(equalTo(4)));
    }


    @Test
    public void testHighestSimilaritySortingWorks(){

        long totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts
                        (totalNumberGeneProducts).setSelected(selected);

        CoTerm mock1 = mock(CoTerm.class, "One");
        CoTerm mock2 = mock(CoTerm.class, "Two");
        CoTerm mock3 = mock(CoTerm.class, "Three");
        CoTerm mock4 = mock(CoTerm.class, "Four");

        cootfstBuilder.addCoTerm(mock1);
        cootfstBuilder.addCoTerm(mock2);
        cootfstBuilder.addCoTerm(mock3);
        cootfstBuilder.addCoTerm(mock4);

        when(mock1.getSimilarityRatio()).thenReturn(3f);
        when(mock2.getSimilarityRatio()).thenReturn(2f);
        when(mock3.getSimilarityRatio()).thenReturn(5f);
        when(mock4.getSimilarityRatio()).thenReturn(1f);
        CoTermsForSelectedTerm cootfst = cootfstBuilder.build();
        List<CoTerm> terms = cootfst.highestSimilarity();

        assertThat(terms.get(0).getSimilarityRatio(), is(5f));
        assertThat(terms.get(1).getSimilarityRatio(), is(3f));
        assertThat(terms.get(2).getSimilarityRatio(), is(2f));
        assertThat(terms.get(3).getSimilarityRatio(), is(1f));
    }



    @Test(expected=IllegalArgumentException.class)
    public void passingNullToAddAndCalculateCausesException(){

        long totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term
        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder().addCoTerm(null);
    }


    @Test(expected=IllegalArgumentException.class)
    public void passingTotalNumberGeneProductsEqualToZeroToConstructorCausesException(){
        long totalNumberGeneProducts = 0;
        long selected = 2;
        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts(totalNumberGeneProducts);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingSelectedEqualToZeroToConstructorCausesException(){
        long totalNumberGeneProducts = 10;
        long selected = 0;
        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder().setSelected(selected);
    }

    @Test(expected=IllegalArgumentException.class)
    public void buildingCoTermsForSelectedTermWithoutSpecifyingTotalNumberOfGeneProductsCausesException(){
        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder()
                .setSelected(4).addCoTerm(mock(CoTerm.class, "One"));
        cootfstBuilder.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void buildingCoTermsForSelectedTermWithoutSpecifyingSelectedCausesException(){
        CoTermsForSelectedTerm.Builder cootfstBuilder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts(4)
                .addCoTerm( mock(CoTerm.class, "One"));
        cootfstBuilder.build();
    }
}
