package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

/**
 * @author Tony Wardell
 * Date: 23/11/2016
 * Time: 11:37
 * Created with IntelliJ IDEA.
 */
public class ValidationEntitiesImplTest {

    private static final String INTERPRO = "INTERPRO";
    private ValidationEntities validationEntities;
    private ValidationEntity mockEntity;

    @Before
    public void setup(){
        ValidationEntitiesAggregator aggregator = new ValidationEntitiesAggregator();
        Map<String, List<ValidationEntity>> mappedEntities = new HashMap<>();
        mappedEntities.put("interpro", Arrays.asList(mockEntity,mockEntity));
        aggregator.mappedEntities = mappedEntities;

        validationEntities = new ValidationEntitiesImpl(aggregator);
        mockEntity = mock(ValidationEntity.class);
        when(mockEntity.keyValue()).thenReturn(INTERPRO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsConstructionIfConstructorArgumentIsNull(){
        new ValidationEntitiesImpl(null);
    }


    @Test
    public void checkTwoSuccessfulElements(){
        assertThat(validationEntities.get(INTERPRO.toLowerCase()), hasSize(equalTo(2)));
    }

}
