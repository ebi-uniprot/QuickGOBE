package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

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
}