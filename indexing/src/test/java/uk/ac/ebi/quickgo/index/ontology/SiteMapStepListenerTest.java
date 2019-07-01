package uk.ac.ebi.quickgo.index.ontology;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created 04/04/17
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SiteMapStepListenerTest {
    @Mock
    private MockableWebSitemapGenerator webSitemapGenerator;

    private SiteMapStepListener listener;

    @Before
    public void setUp() {
        listener = new SiteMapStepListener(webSitemapGenerator);
    }

    @Test
    public void canCreateValidInstance() {
        assertThat(listener, is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSiteMapGeneratorCausesException() {
        new SiteMapStepListener(null);
    }

    @Test
    public void afterStepCausesInvokesMapWriting() {
        ExitStatus exitStatus = new ExitStatus("MyRandomExitStatus");
        StepExecution stepExecution = new StepExecution("PretendStepName", null);
        stepExecution.setExitStatus(exitStatus);

        ExitStatus exitStatusAfterStep = listener.afterStep(stepExecution);
        assertThat(exitStatusAfterStep, is(exitStatus));

        verify(webSitemapGenerator, times(1)).write();
        verify(webSitemapGenerator, times(1)).writeSitemapsWithIndex();
    }
}