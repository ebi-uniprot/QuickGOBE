package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.common.batch.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Check that the {@link OntologyGraphConfig} correctly sets up an instance of
 * {@link OntologyGraph}, by reading test data resources.
 *
 * Created 18/05/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {OntologyGraphConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
public class OntologyGraphConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OntologyGraph ontologyGraph;

    @Test
    public void runOntologyGraphLoading() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        BatchStatus status = jobExecution.getStatus();

        assertThat(status, is(BatchStatus.COMPLETED));

        assertThat(ontologyGraph.getVertices(),
                containsInAnyOrder(
                        "GO:0000001", "GO:0048308",
                        "ECO:0000205", "ECO:0000361", "ECO:0001149", "ECO:0000269"
                ));

        Set<String> edges = ontologyGraph.getEdges()
                .stream()
                .map(OntologyGraph.LabelledEdge::toString)
                .collect(Collectors.toSet());
        assertThat(edges,
                containsInAnyOrder(
                        "I", "P", "R", "CO", "UI"
                ));
    }
}