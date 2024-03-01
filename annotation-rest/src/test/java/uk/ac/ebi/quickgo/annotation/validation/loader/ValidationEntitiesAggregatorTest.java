package uk.ac.ebi.quickgo.annotation.validation.loader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import uk.ac.ebi.quickgo.annotation.validation.model.ValidationEntity;
import uk.ac.ebi.quickgo.annotation.validation.service.ValidationEntityChecker;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 16:46
 * Created with IntelliJ IDEA.
 */
class ValidationEntitiesAggregatorTest {

    private ValidationEntitiesAggregator aggregator;
    private ValidationEntity mockEntityInterpro;
    private ValidationEntity mockEntityPim;
    private ValidationEntityChecker mockValidationEntityChecker;

    @BeforeEach
    void setup(){
        mockValidationEntityChecker = mock(ValidationEntityChecker.class);
        aggregator = new ValidationEntitiesAggregator(mockValidationEntityChecker);
        mockEntityInterpro = mock(ValidationEntity.class);
        mockEntityPim = mock(ValidationEntity.class);
    }

    @Test
    void writeNullEntitiesThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> aggregator.write(null));
    }

    @Test
    void writeEntitiesPassedOn() throws Exception {
        final List<ValidationEntity> items = Arrays.asList(mockEntityInterpro, mockEntityPim);
        aggregator.write(new Chunk<>(items));
        verify(mockValidationEntityChecker, times(1)).addEntities(items);
    }

}
