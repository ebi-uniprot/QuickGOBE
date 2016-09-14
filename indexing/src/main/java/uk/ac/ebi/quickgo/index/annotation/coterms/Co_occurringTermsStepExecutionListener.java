package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * Listener to be activated when all annotations have been passed to the co-occurring term aggregation code.
 *
 * @author Tony Wardell
 * Date: 12/09/2016
 * Time: 15:01
 * Created with IntelliJ IDEA.
 */
public class Co_occurringTermsStepExecutionListener implements StepExecutionListener {

    private final AnnotationCo_occurringTermsAggregator all;
    private final AnnotationCo_occurringTermsAggregator manual;

    public Co_occurringTermsStepExecutionListener(AnnotationCo_occurringTermsAggregator all,
            AnnotationCo_occurringTermsAggregator manual) {
        this.all = all;
        this.manual = manual;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    /**
     * Call finish() on the aggregation instances, so the last accumulating buckets can be processed.
     * @param stepExecution
     * @return 'COMPLETED' once the aggregation code has finished processing.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        all.finish();
        manual.finish();
        return ExitStatus.COMPLETED;
    }
}
