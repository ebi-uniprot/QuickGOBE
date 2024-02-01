package uk.ac.ebi.quickgo.index.ontology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created 04/04/17
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class SiteMapStepListenerTest {
    @Mock
    private MockableWebSitemapGenerator webSitemapGenerator;

    private SiteMapStepListener listener;

    @BeforeEach
    void setUp() {
        listener = new SiteMapStepListener(webSitemapGenerator);
    }

    @Test
    void canCreateValidInstance() {
        assertThat(listener, is(notNullValue()));
    }

    @Test
    void nullSiteMapGeneratorCausesException() {
        assertThrows(IllegalArgumentException.class, () -> new SiteMapStepListener(null));
    }

    @Test
    void afterStepCausesInvokesMapWriting() {
        ExitStatus exitStatus = new ExitStatus("MyRandomExitStatus");
        StepExecution stepExecution = new StepExecution("PretendStepName", null);
        stepExecution.setExitStatus(exitStatus);

        ExitStatus exitStatusAfterStep = listener.afterStep(stepExecution);
        assertThat(exitStatusAfterStep, is(exitStatus));

        verify(webSitemapGenerator, times(1)).write();
        verify(webSitemapGenerator, times(1)).writeSitemapsWithIndex();
    }
}