package uk.ac.ebi.quickgo.annotation.coterms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 15:57
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {CoTermRepoTestConfig.class})
@ActiveProfiles(profiles = CoTermRepoTestConfig.FAILED_RETRIEVAL)
public class CoTermRepositorySimpleMapFailedRetrievalIT {

    private static final String GO_TERM = "GO:7777771";

    @Autowired
    private CoTermRepository failedCoTermLoading;

    @Test(expected = IllegalStateException.class)
    public void cannotLoadAllFromCoTermRepositoryAsFileIsEmpty() {
        failedCoTermLoading.findCoTerms(GO_TERM, CoTermSource.ALL);

    }

    @Test(expected = IllegalStateException.class)
    public void cannotLoadManualFromCoTermRepositoryAsFileIsEmpty() {
        failedCoTermLoading.findCoTerms(GO_TERM, CoTermSource.MANUAL);

    }
}
