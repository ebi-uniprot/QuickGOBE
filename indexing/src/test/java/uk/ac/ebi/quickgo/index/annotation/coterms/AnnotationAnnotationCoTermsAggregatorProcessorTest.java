package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.index.annotation.Annotation;

import java.util.function.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 08/09/2016
 * Time: 13:59
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationAnnotationCoTermsAggregatorProcessorTest {

    @Mock
    AnnotationCoTermsAggregator annotationCoTermsAggregator;

    @Mock
    Annotation annotation;

    @Test
    public void testAnnotationAddedToCoStatsIfPredicateAllows() throws Exception{
        Predicate<Annotation> toBeProcessed = t -> true;
        CoOccuringGoTermsFromAnnotations coOccuringGoTermsFromAnnotations = new CoOccuringGoTermsFromAnnotations
                (annotationCoTermsAggregator, toBeProcessed);
        coOccuringGoTermsFromAnnotations.process(annotation);
        verify(annotationCoTermsAggregator,times(1)).addRowToMatrix(annotation);
    }

    @Test
    public void testAnnotationNotAddedToCoStatsIfPredicateForbids() throws Exception{
        Predicate<Annotation> toBeProcessed = t -> false;
        CoOccuringGoTermsFromAnnotations coOccuringGoTermsFromAnnotations = new CoOccuringGoTermsFromAnnotations
                (annotationCoTermsAggregator, toBeProcessed);
        coOccuringGoTermsFromAnnotations.process(annotation);
        verify(annotationCoTermsAggregator,never()).addRowToMatrix(annotation);
    }
}
