package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

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
public class RawNamedPresetValidatorTest {
    private RawNamedPresetValidator validator;

    @Before
    public void setUp() {
        this.validator = new RawNamedPresetValidator();
    }

    @Test(expected = ValidationException.class)
    public void nullRawPresetIsInvalid() throws Exception {
        validator.process(null);
    }

    @Test(expected = ValidationException.class)
    public void nullNameIsInvalid() throws Exception {
        RawNamedPreset value = new RawNamedPreset();
        value.name = null;
        validator.process(value);
    }

    @Test(expected = ValidationException.class)
    public void emptyNameIsInvalid() throws Exception {
        RawNamedPreset value = new RawNamedPreset();
        value.name = "";
        validator.process(value);
    }

    @Test
    public void nonEmptyNameIsValid() throws Exception {
        RawNamedPreset value = new RawNamedPreset();
        value.name = "valid name";

        RawNamedPreset processedValue = validator.process(value);
        assertThat(processedValue, is(notNullValue()));
    }
}