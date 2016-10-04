package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

/**
 * Created 05/09/16
 * @author Edd
 */
public class PresetsValidationHelperTest {
    @Test(expected = ValidationException.class)
    public void checkIsNullOrEmptyProducesExceptionOnNullValue() {
        PresetsValidationHelper.checkIsNullOrEmpty(null);
    }

    @Test(expected = ValidationException.class)
    public void checkIsNullOrEmptyProducesExceptionOnEmptyValue() {
        PresetsValidationHelper.checkIsNullOrEmpty("");
    }

    @Test
    public void checkIsNullOrEmptyIsFalseForNonNullOrEmptyValue() {
        PresetsValidationHelper.checkIsNullOrEmpty("valid value");
    }
}