package uk.ac.ebi.quickgo.annotation.coterms;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CoTermRepoTestConfig.class})
@ActiveProfiles(profiles = CoTermRepoTestConfig.SUCCESSFUL_RETRIEVAL)
public class CoTermRepositorySimpleMapSuccessfulRetrievalIT {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String GO_TERM_ID_ALL_ONLY = "GO:7777771";
    private static final String GO_TERM_ID_MANUAL_ONLY = "GO:8888881";

    @Autowired
    private CoTermRepository coTermRepository;

    @Test
    public void retrievalIsSuccessfulFromAll() {
        List<CoTerm> coTerms = coTermRepository.findCoTerms(GO_TERM_ID_ALL_ONLY, CoTermSource.ALL);
        assertThat(coTerms.get(0).getTarget(), is(GO_TERM_ID_ALL_ONLY));
        assertThat(coTerms.get(0).getComparedTerm(), is("GO:0003333"));
        assertThat(coTerms.get(0).getProbabilityRatio(), is(486.4f));
        assertThat(coTerms.get(0).getSimilarityRatio(), is(22.28f));
        assertThat(coTerms.get(0).getTogether(), is(8632L));
        assertThat(coTerms.get(0).getCompared(), is(5778L));
    }

    @Test
    public void retrievalIsSuccessfulFromManual() {
        List<CoTerm> coTerms = coTermRepository.findCoTerms(GO_TERM_ID_MANUAL_ONLY, CoTermSource.MANUAL);
        assertThat(coTerms.get(0).getTarget(), is(GO_TERM_ID_MANUAL_ONLY));
        assertThat(coTerms.get(0).getComparedTerm(), is("GO:0004444"));
        assertThat(coTerms.get(0).getProbabilityRatio(), is(302.4f));
        assertThat(coTerms.get(0).getSimilarityRatio(), is(78.28f));
        assertThat(coTerms.get(0).getTogether(), is(1933L));
        assertThat(coTerms.get(0).getCompared(), is(5219L));
    }

    @Test
    public void findCoTermsThrowsExceptionIfSearchIdIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The requested id is null.");
        coTermRepository.findCoTerms(null, CoTermSource.ALL);
    }
    @Test
    public void findCoTermsThrowsExceptionIfCoTermSourceIsNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The requested co-occurring source is null.");
        coTermRepository.findCoTerms(GO_TERM_ID_ALL_ONLY, null);
    }
}
