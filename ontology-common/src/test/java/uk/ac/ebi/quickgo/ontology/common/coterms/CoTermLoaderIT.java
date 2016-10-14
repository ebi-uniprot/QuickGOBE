package uk.ac.ebi.quickgo.ontology.common.coterms;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.hamcrest.CoreMatchers.is;
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

        Resource manualCoTermsFile = new ClassPathResource("CoTermsManual");
        Resource allCoTermsFile = new ClassPathResource("CoTermsAll");

        CoTermLoader coTermLoader = new CoTermLoader(manualCoTermsFile, allCoTermsFile);
        coTermLoader.load();
        assertThat(coTermLoader.coTermsAll.keySet(), hasSize(2));
        assertThat(coTermLoader.coTermsManual.keySet(), hasSize(2));

        //To a quick check that the columns are being loaded properly
        CoTerm coTerm = coTermLoader.coTermsAll.get("GO:0000001").get(1);
        assertThat(coTerm.getId(), is("GO:0000001"));
        assertThat(coTerm.getCompare(), is("GO:0034643"));
        assertThat(coTerm.getProbabilityRatio(), is(16446.73f));
        assertThat(coTerm.getSignificance(), is(7.56f));
        assertThat(coTerm.getTogether(), is(207l));
        assertThat(coTerm.getCompared(), is(208l));

    }
}
