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
public class AnnotationCoStatsProcessorTest {

    @Mock
    CoStatsPermutations coStatsPermutations;

    @Mock
    Annotation annotation;

    @Test
    public void testAnnotationAddedToCoStatsIfPredicateAllows() throws Exception{
        Predicate<Annotation> toBeProcessed = t -> true;
        AnnotationCoStatsProcessor annotationCoStatsProcessor = new AnnotationCoStatsProcessor
                (coStatsPermutations, toBeProcessed);
        annotationCoStatsProcessor.process(annotation);
        verify(coStatsPermutations,times(1)).addRowToMatrix(annotation);
    }

    @Test
    public void testAnnotationNotAddedToCoStatsIfPredicateForbids() throws Exception{
        Predicate<Annotation> toBeProcessed = t -> false;
        AnnotationCoStatsProcessor annotationCoStatsProcessor = new AnnotationCoStatsProcessor
                (coStatsPermutations, toBeProcessed);
        annotationCoStatsProcessor.process(annotation);
        verify(coStatsPermutations,never()).addRowToMatrix(annotation);
    }
}
