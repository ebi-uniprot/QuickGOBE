package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
public class CoTermsForSelectedTermTest {

    @Mock
    CoTerm mockCoTerm;

    @Test
    public void testCalculationCalledOnAllTerms(){

        long totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        CoTermsForSelectedTerm.Builder builder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts
        (totalNumberGeneProducts).setSelected(selected);

        builder.addCoTerm(mockCoTerm);
        builder.addCoTerm(mockCoTerm);
        builder.addCoTerm(mockCoTerm);
        builder.addCoTerm(mockCoTerm);
        CoTermsForSelectedTerm coTermsForSelectedTerm = builder.build();

       List<CoTerm> terms = coTermsForSelectedTerm.highestSimilarity();
        assertThat(terms, hasSize(equalTo(4)));
    }


    @Test
    public void callingHighestSimilaritySortingWorks(){

        long totalNumberGeneProducts = 10;
        long selected = 2;  //Total count of proteins annotated to selected term

        CoTermsForSelectedTerm.Builder builder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts
                        (totalNumberGeneProducts).setSelected(selected);

        CoTerm mockCoTerm1 = mock(CoTerm.class);
        CoTerm mockCoTerm2 = mock(CoTerm.class);
        CoTerm mockCoTerm3 = mock(CoTerm.class);
        CoTerm mockCoTerm4 = mock(CoTerm.class);

        builder.addCoTerm(mockCoTerm1);
        builder.addCoTerm(mockCoTerm2);
        builder.addCoTerm(mockCoTerm3);
        builder.addCoTerm(mockCoTerm4);

        final float float3 = 3f;
        when(mockCoTerm1.getSimilarityRatio()).thenReturn(float3);
        final float float2 = 2f;
        when(mockCoTerm2.getSimilarityRatio()).thenReturn(float2);
        final float float5 = 5f;
        when(mockCoTerm3.getSimilarityRatio()).thenReturn(float5);
        final float float1 = 1f;
        when(mockCoTerm4.getSimilarityRatio()).thenReturn(float1);
        CoTermsForSelectedTerm coTermsForSelectedTerm = builder.build();
        List<CoTerm> terms = coTermsForSelectedTerm.highestSimilarity();

        assertThat(terms.get(0).getSimilarityRatio(), is(float5));
        assertThat(terms.get(1).getSimilarityRatio(), is(float3));
        assertThat(terms.get(2).getSimilarityRatio(), is(float2));
        assertThat(terms.get(3).getSimilarityRatio(), is(float1));
    }



    @Test(expected=IllegalArgumentException.class)
    public void passingNullToAddAndCalculateCausesException(){
        new CoTermsForSelectedTerm.Builder().addCoTerm(null);
    }


    @Test(expected=IllegalArgumentException.class)
    public void passingTotalNumberGeneProductsEqualToZeroToConstructorCausesException(){
        long totalNumberGeneProducts = 0;
        new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts(totalNumberGeneProducts);
    }

    @Test(expected=IllegalArgumentException.class)
    public void passingSelectedEqualToZeroToConstructorCausesException(){
        long selected = 0;
        new CoTermsForSelectedTerm.Builder().setSelected(selected);
    }

    @Test(expected=IllegalStateException.class)
    public void buildingCoTermsForSelectedTermWithoutSpecifyingTotalNumberOfGeneProductsCausesException(){
        CoTermsForSelectedTerm.Builder builder = new CoTermsForSelectedTerm.Builder()
                .setSelected(4).addCoTerm(mock(CoTerm.class, "One"));
        builder.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void buildingCoTermsForSelectedTermWithoutSpecifyingSelectedCausesException(){
        CoTermsForSelectedTerm.Builder builder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts(4)
                .addCoTerm( mock(CoTerm.class, "One"));
        builder.build();
    }
}
