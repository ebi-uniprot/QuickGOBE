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
        CoTermsConfigProperties properties = new CoTermsConfigProperties();
        properties.setAll(ALL_PATH);
        properties.setManual(MANUAL_PATH);
        coTermsConfig.coTermAllSummarizationStep(properties);
        coTermsConfig.coTermManualSummarizationStep(properties);
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsWhenCallingCoTermAllSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        CoTermsConfigProperties properties = new CoTermsConfigProperties();
        properties.setAll(MANUAL_PATH);
        properties.setManual(MANUAL_PATH);
        coTermsConfig.coTermAllSummarizationStep(properties);
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsCoTermManualSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        CoTermsConfigProperties properties = new CoTermsConfigProperties();
        properties.setAll(ALL_PATH);
        properties.setManual(ALL_PATH);
        coTermsConfig.coTermAllSummarizationStep(properties);
    }
}
