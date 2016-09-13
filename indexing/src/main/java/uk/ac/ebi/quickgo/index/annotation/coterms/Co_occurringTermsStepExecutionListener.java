package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
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

    @Override public void beforeStep(StepExecution stepExecution) {

    }

    @Override public ExitStatus afterStep(StepExecution stepExecution) {
        all.finish();
        manual.finish();
        return ExitStatus.COMPLETED;
    }
}
