package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepExecution;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 13/10/2016
 * Time: 13:52
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class CoTermsStepExecutionListenerTest {

    @Mock
    CoTermsAggregator mockAllAggregator;

    @Mock
    CoTermsAggregator mockManualAggregator;

    @Mock
    StepExecution mockExecution;

    @Test
    public void callingFinishOnAggregatorsIsSuccessful(){
        CoTermsStepExecutionListener listener = new CoTermsStepExecutionListener(mockAllAggregator, mockManualAggregator);
        listener.afterStep(mockExecution);
        verify(mockAllAggregator).finish();
        verify(mockManualAggregator).finish();
        verify(mockExecution).getExitStatus();
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingNullAllAggregatorToConstructorThrowsException(){
        new CoTermsStepExecutionListener(null, mockManualAggregator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void passingNullManualAggregatorToConstructorThrowsException(){
        new CoTermsStepExecutionListener(mockAllAggregator, null);
    }
}
