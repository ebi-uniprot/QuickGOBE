package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

/**
 * Listener to be activated when all annotations have been passed to the co-occurring term aggregation code.
 *
 * @author Tony Wardell
 * Date: 12/09/2016
 * Time: 15:01
 * Created with IntelliJ IDEA.
 */
class CoTermsStepExecutionListener implements StepExecutionListener {

    private final CoTermsAggregationWriter all;
    private final CoTermsAggregationWriter manual;

    public CoTermsStepExecutionListener(CoTermsAggregationWriter all, CoTermsAggregationWriter manual) {

        Preconditions.checkArgument(null!=all, "The item writer instance for 'all' passed to " +
                "CoTermsStepExecutionListener was null, and not should be.");
        Preconditions.checkArgument(null!=manual, "The item writer instance for 'manual' passed to " +
                "CoTermsStepExecutionListener was null, and not should be.");

        this.all = all;
        this.manual = manual;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    /**
     * Call finish() on the aggregation instances, so the last accumulating buckets can be processed.
     * Call initialize on calculators so they have the data ready to calculate.
     * @param stepExecution
     * @return the ExitStatus passed to this method via the StepExecution argument.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        all.finish();
        manual.finish();
        return stepExecution.getExitStatus();
    }
}
