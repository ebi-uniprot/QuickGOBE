package uk.ac.ebi.quickgo.annotation.coterms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
@SpringBootTest(classes = {CoTermRepoTestConfig.class})
@ActiveProfiles(profiles = CoTermRepoTestConfig.SUCCESSFUL_RETRIEVAL)
class CoTermRepositorySimpleMapSuccessfulRetrievalIT {

    private static final String GO_TERM_ID_ALL_ONLY = "GO:7777771";
    private static final String GO_TERM_ID_MANUAL_ONLY = "GO:8888881";

    @Autowired
    private CoTermRepository coTermRepository;

    @Test
    void retrievalIsSuccessfulFromAll() {
        List<CoTerm> coTerms = coTermRepository.findCoTerms(GO_TERM_ID_ALL_ONLY, CoTermSource.ALL);
        assertThat(coTerms.get(0).getTarget(), is(GO_TERM_ID_ALL_ONLY));
        assertThat(coTerms.get(0).getComparedTerm(), is("GO:0003333"));
        assertThat(coTerms.get(0).getProbabilityRatio(), is(486.4f));
        assertThat(coTerms.get(0).getSimilarityRatio(), is(22.28f));
        assertThat(coTerms.get(0).getTogether(), is(8632L));
        assertThat(coTerms.get(0).getCompared(), is(5778L));
    }

    @Test
    void retrievalIsSuccessfulFromManual() {
        List<CoTerm> coTerms = coTermRepository.findCoTerms(GO_TERM_ID_MANUAL_ONLY, CoTermSource.MANUAL);
        assertThat(coTerms.get(0).getTarget(), is(GO_TERM_ID_MANUAL_ONLY));
        assertThat(coTerms.get(0).getComparedTerm(), is("GO:0004444"));
        assertThat(coTerms.get(0).getProbabilityRatio(), is(302.4f));
        assertThat(coTerms.get(0).getSimilarityRatio(), is(78.28f));
        assertThat(coTerms.get(0).getTogether(), is(1933L));
        assertThat(coTerms.get(0).getCompared(), is(5219L));
    }

    @Test
    void findCoTermsThrowsExceptionIfSearchIdIsNull() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> coTermRepository.findCoTerms(null, CoTermSource.ALL));
        assertTrue(exception.getMessage().contains("The requested id is null."));
    }
    @Test
    void findCoTermsThrowsExceptionIfCoTermSourceIsNull() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> coTermRepository.findCoTerms(GO_TERM_ID_ALL_ONLY, null));
        assertTrue(exception.getMessage().contains("The requested co-occurring source is null."));
    }
}
