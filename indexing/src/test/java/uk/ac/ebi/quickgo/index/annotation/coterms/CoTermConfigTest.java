package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

import static org.mockito.Mockito.mock;

/**
 * @author Tony Wardell
 * Date: 07/02/2017
 * Time: 11:00
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class CoTermConfigTest {

    Resource manualPath;
    Resource allPath;

    @Before
    public void setup(){
        manualPath = mock(Resource.class);
        allPath = mock(Resource.class);
    }

    @Test(expected = NullPointerException.class)
    public void manualAndAllOutputPathsWhenDifferentWillNotCauseAIllegalStateException(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = manualPath;
        coTermsConfig.allCoTermsPath = allPath;
        coTermsConfig.coTermAllSummarizationStep();
        coTermsConfig.coTermManualSummarizationStep();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsWhenCallingCoTermAllSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = manualPath;
        coTermsConfig.allCoTermsPath = manualPath;
        coTermsConfig.coTermAllSummarizationStep();
    }

    @Test(expected = IllegalStateException.class)
    public void cannotHaveBothManualAndAllOutputPathsTheSameForCoTermsCoTermManualSummarizationStep(){
        CoTermsConfig coTermsConfig = new CoTermsConfig();
        coTermsConfig.manualCoTermsPath = manualPath;
        coTermsConfig.allCoTermsPath = manualPath;
        coTermsConfig.coTermManualSummarizationStep();
    }

}
