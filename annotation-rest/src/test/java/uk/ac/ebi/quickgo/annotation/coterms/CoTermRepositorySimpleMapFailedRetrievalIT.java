package uk.ac.ebi.quickgo.annotation.coterms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
@SpringBootTest(classes = {CoTermRepoTestConfig.class})
@ActiveProfiles(profiles = CoTermRepoTestConfig.FAILED_RETRIEVAL)
class CoTermRepositorySimpleMapFailedRetrievalIT {

    private static final String GO_TERM = "GO:7777771";

    @Autowired
    private CoTermRepository failedCoTermLoading;

    @Test
    void cannotLoadAllFromCoTermRepositoryAsFileIsEmpty() {
        assertThrows(IllegalStateException.class, () -> failedCoTermLoading.findCoTerms(GO_TERM, CoTermSource.ALL));

    }

    @Test
    void cannotLoadManualFromCoTermRepositoryAsFileIsEmpty() {
        assertThrows(IllegalStateException.class, () -> failedCoTermLoading.findCoTerms(GO_TERM, CoTermSource.MANUAL));

    }
}
