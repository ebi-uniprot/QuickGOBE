package uk.ac.ebi.quickgo.index.annotation;


import uk.ac.ebi.quickgo.index.annotation.costats.CoStatsPermutations;

import java.util.function.Predicate;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Tony Wardell
 * Date: 06/09/2016
 * Time: 16:30
 * Created with IntelliJ IDEA.
 *
 * Version of GPAFileToSummary
 */
public class AnnotationCoStatsSummarizer implements ItemProcessor<Annotation, Object> {

    private final CoStatsPermutations coStatsPermutations;
    private final Predicate<Annotation> toBeProcessed;

    public AnnotationCoStatsSummarizer(CoStatsPermutations coStatsPermutations,
            Predicate<Annotation> toBeProcessed) {
        this.coStatsPermutations = coStatsPermutations;
        this.toBeProcessed = toBeProcessed;
    }

    @Override public Object process(Annotation annotation) throws Exception {

        if(toBeProcessed.test(annotation)){
            coStatsPermutations.addRowToMatrix(annotation);
        }

        return null;
    }
}
