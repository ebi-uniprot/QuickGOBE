package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 05/09/16
 * @author Edd
 */
public class RawAssignedByPresetValidatorTest {
    private RawAssignedByPresetValidator validator;

    @Before
    public void setUp() {
        this.validator = new RawAssignedByPresetValidator();
    }

    @Test(expected = ValidationException.class)
    public void nullRawPresetIsInvalid() throws Exception {
        validator.process(null);
    }

    @Test(expected = ValidationException.class)
    public void nullNameIsInvalid() throws Exception {
        RawAssignedByPreset value = new RawAssignedByPreset();
        value.name = null;
        validator.process(value);
    }

    @Test(expected = ValidationException.class)
    public void emptyNameIsInvalid() throws Exception {
        RawAssignedByPreset value = new RawAssignedByPreset();
        value.name = "";
        validator.process(value);
    }

    @Test
    public void nonEmptyNameIsValid() throws Exception {
        RawAssignedByPreset value = new RawAssignedByPreset();
        value.name = "valid name";

        RawAssignedByPreset processedValue = validator.process(value);
        assertThat(processedValue, is(notNullValue()));
    }
}