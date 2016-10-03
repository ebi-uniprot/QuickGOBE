package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
class Co_occurringTermsStepExecutionListener implements StepExecutionListener {

    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(Co_occurringTermsStepExecutionListener.class);

    private final AnnotationCoOccurringTermsAggregator all;
    private final AnnotationCoOccurringTermsAggregator manual;
    private final Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorManual;
    private final Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorAll;

    public Co_occurringTermsStepExecutionListener(AnnotationCoOccurringTermsAggregator all,
            AnnotationCoOccurringTermsAggregator manual,
            Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorManual,
            Co_occurringTermsStatsCalculator co_occurringTermsStatsCalculatorAll) {
        this.all = all;
        this.manual = manual;
        this.co_occurringTermsStatsCalculatorManual = co_occurringTermsStatsCalculatorManual;
        this.co_occurringTermsStatsCalculatorAll = co_occurringTermsStatsCalculatorAll;
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
        all.finish();
        manual.finish();
        co_occurringTermsStatsCalculatorManual.initialize();
        co_occurringTermsStatsCalculatorAll.initialize();
        return stepExecution.getExitStatus();
    }
}
