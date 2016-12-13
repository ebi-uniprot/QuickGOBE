package uk.ac.ebi.quickgo.annotation.validation.loader;

import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;
import uk.ac.ebi.quickgo.annotation.validation.service.ValidationEntityChecker;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 16:46
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesAggregatorTest {

    private ValidationEntitiesAggregator aggregator;
    private ValidationEntity mockEntityInterpro;
    private ValidationEntity mockEntityPim;
    private ValidationEntityChecker mockValidationEntityChecker;

    @Before
    public void setup(){
        mockValidationEntityChecker = mock(ValidationEntityChecker.class);
        aggregator = new ValidationEntitiesAggregator(mockValidationEntityChecker);
        mockEntityInterpro = mock(ValidationEntity.class);
        mockEntityPim = mock(ValidationEntity.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeNullEntitiesThrowsException(){
        aggregator.write(null);
    }

    @Test
    public void writeEntitiesPassedOn(){
        final List<ValidationEntity> items = Arrays.asList(mockEntityInterpro, mockEntityPim);
        aggregator.write(items);
        verify(mockValidationEntityChecker, times(1)).addEntities(items);
    }

}
