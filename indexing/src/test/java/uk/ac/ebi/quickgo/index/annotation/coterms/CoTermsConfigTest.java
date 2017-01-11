package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Test;

/**
 * @author Tony Wardell
 * Date: 11/01/2017
 * Time: 09:16
 * Created with IntelliJ IDEA.
 */
public class CoTermsConfigTest {

    public CoTermsConfigTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void allAndManualCoTermsOutputPathsCannotBeTheSame(){
        CoTermsConfig config = new CoTermsConfig();
        config.outputPath("foo", "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void manualOutputPathCannotBeNull(){
        CoTermsConfig config = new CoTermsConfig();
        config.outputPath(null, "foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void allOutputPathCannotBeNull(){
        CoTermsConfig config = new CoTermsConfig();
        config.outputPath("foo", null);
    }

    @Test
    public void differentValuesForManualAndAllPathsIsOK(){
        CoTermsConfig config = new CoTermsConfig();
        config.outputPath("foo", "bar");
    }
}
