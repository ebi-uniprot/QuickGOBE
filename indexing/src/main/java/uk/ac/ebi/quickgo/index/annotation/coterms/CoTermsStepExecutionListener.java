package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final ItemWriter<AnnotationDocument> all;
    private final ItemWriter<AnnotationDocument> manual;

    public CoTermsStepExecutionListener(ItemWriter<AnnotationDocument> all, ItemWriter<AnnotationDocument> manual) {

        Preconditions.checkArgument(null!=all, "The item writer instance for 'all' passed to " +
                "CoTermsStepExecutionListener was null, and should be.");
        Preconditions.checkArgument(null!=manual, "The item writer instance for 'all' passed to " +
                "CoTermsStepExecutionListener was null, and should be.");

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
     * @return 'COMPLETED' once the aggregation code has finished processing.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ((CoTermsAggregator)all).finish();
        ((CoTermsAggregator)manual).finish();
        return stepExecution.getExitStatus();
    }
}
