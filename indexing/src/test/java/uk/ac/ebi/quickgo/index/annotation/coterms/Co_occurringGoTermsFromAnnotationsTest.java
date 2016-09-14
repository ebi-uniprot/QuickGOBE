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
public class Co_occurringGoTermsFromAnnotationsTest {

    @Mock
    AnnotationCo_occurringTermsAggregator aggregator;

    @Mock
    Annotation annotation;

    @Test
    public void annotationAddedToCoStatsIfPredicateAllows() throws Exception{
        Predicate<Annotation> toBeProcessed = t -> true;
        Co_occurringGoTermsFromAnnotations coTerms = new Co_occurringGoTermsFromAnnotations(aggregator, toBeProcessed);
        coTerms.process(annotation);
        verify(aggregator,times(1)).addRowToMatrix(annotation);
    }

    @Test
    public void annotationNotAddedToCoStatsIfPredicateForbids() throws Exception{
        Predicate<Annotation> toBeProcessed = t -> false;
        Co_occurringGoTermsFromAnnotations coTerms = new Co_occurringGoTermsFromAnnotations(aggregator, toBeProcessed);
        coTerms.process(annotation);
        verify(aggregator,never()).addRowToMatrix(annotation);
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionThrownIfNullAggregatorPassedToConstructor(){
        Predicate<Annotation> toBeProcessed = t -> true;
        new Co_occurringGoTermsFromAnnotations(null, toBeProcessed);
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionThrownIfNullPredicatePassedToConstructor(){
        new Co_occurringGoTermsFromAnnotations(aggregator, null);
    }
}
