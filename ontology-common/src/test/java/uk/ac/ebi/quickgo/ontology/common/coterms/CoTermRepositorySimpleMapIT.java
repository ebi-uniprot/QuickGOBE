package uk.ac.ebi.quickgo.ontology.common.coterms;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
public class CoTermRepositorySimpleMapIT {

    private static final String GO_TERM_ID_ALL_ONLY = "GO:7777771";
    private static final String GO_TERM_ID_MANUAL_ONLY = "GO:8888881";
    private CoTermRepositorySimpleMap coTermRepository;

    @Before
    public void setup() throws IOException {
        coTermRepository = CoTermRepositorySimpleMap
                .createCoTermRepositorySimpleMap(new ClassPathResource("CoTermsManual"),
                        new ClassPathResource("CoTermsAll"));
    }

    @Test
    public void retrievalIsSuccessfulFromAll() {
        List<CoTerm> coTerms = coTermRepository.findCoTerms(GO_TERM_ID_ALL_ONLY, CoTermSource.ALL, 1, t -> true);
        assertThat(coTerms.get(0).getId(), is(GO_TERM_ID_ALL_ONLY));
        assertThat(coTerms.get(0).getCompare(), is("GO:0003333"));
        assertThat(coTerms.get(0).getProbabilityRatio(), is(486.4f));
        assertThat(coTerms.get(0).getSignificance(), is(22.28f));
        assertThat(coTerms.get(0).getTogether(), is(8632L));
        assertThat(coTerms.get(0).getCompared(), is(5778L));
    }

    @Test
    public void retrievalIsSuccessfulFromManual() {
        List<CoTerm> coTerms = coTermRepository.findCoTerms(GO_TERM_ID_MANUAL_ONLY, CoTermSource.MANUAL, 1, t -> true);
        assertThat(coTerms.get(0).getId(), is(GO_TERM_ID_MANUAL_ONLY));
        assertThat(coTerms.get(0).getCompare(), is("GO:0004444"));
        assertThat(coTerms.get(0).getProbabilityRatio(), is(302.4f));
        assertThat(coTerms.get(0).getSignificance(), is(78.28f));
        assertThat(coTerms.get(0).getTogether(), is(1933L));
        assertThat(coTerms.get(0).getCompared(), is(5219L));
    }
}
