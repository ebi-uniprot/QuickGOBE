package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
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
        validationEntities = new ValidationEntitiesImpl();
        mockEntity = mock(ValidationEntity.class);
        when(mockEntity.keyValue()).thenReturn(INTERPRO);
    }

    @Test
    public void nothingReturnedIfNotWrittenTo(){
        assertThat(validationEntities.get(INTERPRO), is(equalTo(null)));
    }


    @Test
    public void checkTwoSuccessfulElements(){
        validationEntities.addEntities( Arrays.asList(mockEntity,mockEntity));
        assertThat(validationEntities.get(INTERPRO.toLowerCase()), hasSize(equalTo(2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addingNullThrowsException(){
        validationEntities.addEntities(null);
    }

}
