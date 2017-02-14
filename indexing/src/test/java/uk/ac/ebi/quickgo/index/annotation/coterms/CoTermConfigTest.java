package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Test;

/**
 * @author Tony Wardell
 * Date: 07/02/2017
 * Time: 11:00
 * Created with IntelliJ IDEA.
 */
public class CoTermConfigTest {

    private static final String MANUAL_PATH = "foo";
    private static final String ALL_PATH = "bar";

    @Test(expected = NullPointerException.class)
    public void manualAndAllOutputPathsWhenDifferentWillNotCauseAIllegalStateException(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = MANUAL_PATH;
        coTermsConfig.allCoTermsPath = ALL_PATH;
        coTermsConfig.coTermAllSummarizationStep();
        coTermsConfig.coTermManualSummarizationStep();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsWhenCallingCoTermAllSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = MANUAL_PATH;
        coTermsConfig.allCoTermsPath = MANUAL_PATH;
        coTermsConfig.coTermAllSummarizationStep();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsCoTermManualSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = MANUAL_PATH;
        coTermsConfig.allCoTermsPath = MANUAL_PATH;
        coTermsConfig.coTermManualSummarizationStep();
    }
}
