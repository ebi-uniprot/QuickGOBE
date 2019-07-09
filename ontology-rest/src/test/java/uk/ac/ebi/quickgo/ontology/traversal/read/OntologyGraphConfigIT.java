package uk.ac.ebi.quickgo.ontology.traversal.read;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraph;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Check that the {@link OntologyGraphConfig} correctly sets up an instance of
 * {@link OntologyGraph}, by reading test data resources.
 *
 * Created 18/05/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OntologyGraphConfig.class, JobTestRunnerConfig.class})
@TestPropertySource(properties = "ontology.traversal.source=classpath:/relations/RELATIONS.dat.gz,classpath:/relations/ECO_RELATIONS.dat.gz")
public class OntologyGraphConfigIT {

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
                .map(rel -> rel.relationship.getShortName())
                .collect(Collectors.toSet());
        assertThat(edges,
                containsInAnyOrder(
                        "I", "P", "R", "CO", "UI"
                ));
    }
}