package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 07/02/2017
 * Time: 11:00
 * Created with IntelliJ IDEA.
 */
class CoTermConfigTest {

    private static final String MANUAL_PATH = "foo";
    private static final String ALL_PATH = "bar";

    @Test
    void manualAndAllOutputPathsWhenDifferentWillNotCauseAIllegalStateException(){
        assertThrows(NullPointerException.class, () -> {
            CoTermsConfig coTermsConfig = new CoTermsConfig();
            CoTermsConfigProperties properties = new CoTermsConfigProperties();
            properties.setAll(ALL_PATH);
            properties.setManual(MANUAL_PATH);
            coTermsConfig.coTermAllSummarizationStep(properties);
            coTermsConfig.coTermManualSummarizationStep(properties);
        });
    }

    @Test
    void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsWhenCallingCoTermAllSummarizationStep(){
        assertThrows(IllegalStateException.class, () -> {
            CoTermsConfig coTermsConfig = new CoTermsConfig();
            CoTermsConfigProperties properties = new CoTermsConfigProperties();
            properties.setAll(MANUAL_PATH);
            properties.setManual(MANUAL_PATH);
            coTermsConfig.coTermAllSummarizationStep(properties);
        });
    }

    @Test
    void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsCoTermManualSummarizationStep(){
        assertThrows(IllegalStateException.class, () -> {
            CoTermsConfig coTermsConfig = new CoTermsConfig();
            CoTermsConfigProperties properties = new CoTermsConfigProperties();
            properties.setAll(ALL_PATH);
            properties.setManual(ALL_PATH);
            coTermsConfig.coTermAllSummarizationStep(properties);
        });
    }
}
