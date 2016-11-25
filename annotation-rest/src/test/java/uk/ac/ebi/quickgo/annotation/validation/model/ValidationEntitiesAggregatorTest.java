package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 16:46
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesAggregatorTest {

    private static final String INTERPRO = "INTERPRO";
    private static final String PIM = "PIM";
    private ValidationEntitiesAggregator  aggregator;
    private ValidationEntity mockEntityInterpro;
    private ValidationEntity mockEntityPim;

    @Before
    public void setup(){
        aggregator = new ValidationEntitiesAggregator();
        mockEntityInterpro = mock(ValidationEntity.class);
        when(mockEntityInterpro.keyValue()).thenReturn(INTERPRO);
        mockEntityPim = mock(ValidationEntity.class);
        when(mockEntityPim.keyValue()).thenReturn(PIM);
    }

    @Test
    public void loadTwoEntitiesWithTheSameName(){
        aggregator.write(Arrays.asList(mockEntityInterpro, mockEntityInterpro));
        assertThat(aggregator.mappedEntities.keySet(), hasSize(equalTo(1)));
        assertThat(aggregator.mappedEntities.get(INTERPRO.toLowerCase()), hasSize(equalTo(2)));
    }

    @Test
    public void loadTwoEntitiesWithTheDifferentNames(){
        aggregator.write(Arrays.asList(mockEntityInterpro, mockEntityPim));
        assertThat(aggregator.mappedEntities.keySet(), hasSize(equalTo(2)));
        assertThat(aggregator.mappedEntities.get(INTERPRO.toLowerCase()), hasSize(equalTo(1)));
        assertThat(aggregator.mappedEntities.get(PIM.toLowerCase()), hasSize(equalTo(1)));
    }

        @Test
        public void loadTwoSuccessfulElementsIgnoreNull(){
            aggregator.write(Arrays.asList(mockEntityInterpro, mockEntityInterpro, null));
            assertThat(aggregator.mappedEntities.keySet(), hasSize(equalTo(1)));
            assertThat(aggregator.mappedEntities.get(INTERPRO.toLowerCase()), hasSize(equalTo(2)));
        }

        @Test
        public void loadTwoSuccessfulElementsIgnoreKeyValueIsNull(){
            ValidationEntity mockEntityKeyIsNull = mock(ValidationEntity.class);
            when(mockEntityKeyIsNull.keyValue()).thenReturn(null);
            aggregator.write(Arrays.asList(mockEntityInterpro, mockEntityInterpro, mockEntityKeyIsNull));
            assertThat(aggregator.mappedEntities.keySet(), hasSize(equalTo(1)));
            assertThat(aggregator.mappedEntities.get(INTERPRO.toLowerCase()), hasSize(equalTo(2)));
        }
}
