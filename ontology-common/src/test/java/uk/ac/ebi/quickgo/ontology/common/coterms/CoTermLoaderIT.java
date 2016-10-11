package uk.ac.ebi.quickgo.ontology.common.coterms;

import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermLoader;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
public class CoTermLoaderIT {

    @Test
    public void allValuesShouldBeLoadedCorrectly(){

        Resource manualCoTermsFile = new ClassPathResource("CoTermsAll");
        Resource allCoTermsFile = new ClassPathResource("CoTermsManual");


        CoTermLoader coTermLoader = new CoTermLoader(manualCoTermsFile, allCoTermsFile);
        coTermLoader.load();
        assertThat(coTermLoader.coTermsAll.keySet(), hasSize(2));
        assertThat(coTermLoader.coTermsManual.keySet(), hasSize(2));

    }
}
