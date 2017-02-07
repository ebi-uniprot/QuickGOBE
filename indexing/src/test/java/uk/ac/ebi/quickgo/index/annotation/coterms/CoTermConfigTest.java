package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Test;

/**
 * @author Tony Wardell
 * Date: 07/02/2017
 * Time: 11:00
 * Created with IntelliJ IDEA.
 */
public class CoTermConfigTest {

    @Test(expected = NullPointerException.class)
    public void manualAndAllOutputPathsWhenDifferentDoesntCauseAIllegalStateException(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = "foo";
        coTermsConfig.allCoTermsPath = "bar";
        coTermsConfig.coTermAllSummarizationStep();
        coTermsConfig.coTermManualSummarizationStep();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsWhenCallingCoTermAllSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = "foo";
        coTermsConfig.allCoTermsPath = "foo";
        coTermsConfig.coTermAllSummarizationStep();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsCoTermManualSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = "foo";
        coTermsConfig.allCoTermsPath = "foo";
        coTermsConfig.coTermManualSummarizationStep();
    }

}
