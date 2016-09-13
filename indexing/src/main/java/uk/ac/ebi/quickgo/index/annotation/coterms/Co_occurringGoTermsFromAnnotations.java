package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.index.annotation.Annotation;

import com.google.common.base.Preconditions;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Tony Wardell
 * Date: 06/09/2016
 * Time: 16:30
 * Created with IntelliJ IDEA.
 *
 * A version of ItemProcessor for adding details from an {@link uk.ac.ebi.quickgo.index.annotation.Annotation}
 * to a matrix of permutations where GOTerm A and GOTerm B both annotate the same Gene Product.
 *
 * Version of GPAFileToSummary from Beta
 */
public class Co_occurringGoTermsFromAnnotations implements ItemProcessor<Annotation, Annotation> {

    private final AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregator;
    private final Predicate<Annotation> toBeProcessed;

    public Co_occurringGoTermsFromAnnotations(AnnotationCo_occurringTermsAggregator annotationCoOccurringTermsAggregator,
            Predicate<Annotation> toBeProcessed) {
        Preconditions.checkArgument(annotationCoOccurringTermsAggregator != null);
        Preconditions.checkArgument(toBeProcessed != null);
        this.annotationCoOccurringTermsAggregator = annotationCoOccurringTermsAggregator;
        this.toBeProcessed = toBeProcessed;
    }

    @Override
    public Annotation process(Annotation annotation) throws Exception {

        if (toBeProcessed.test(annotation)) {
            annotationCoOccurringTermsAggregator.addRowToMatrix(annotation);
        }

        return annotation;
    }
}
