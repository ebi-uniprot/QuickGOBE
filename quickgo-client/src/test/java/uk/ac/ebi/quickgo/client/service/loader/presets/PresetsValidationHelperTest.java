package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 05/09/16
 * @author Edd
 */
class PresetsValidationHelperTest {
    @Test
    void checkIsNullOrEmptyProducesExceptionOnNullValue() {
        assertThrows(ValidationException.class, () -> PresetsValidationHelper.checkIsNullOrEmpty(null));
    }

    @Test
    void checkIsNullOrEmptyProducesExceptionOnEmptyValue() {
        assertThrows(ValidationException.class, () -> PresetsValidationHelper.checkIsNullOrEmpty(""));
    }

    @Test
    void checkIsNullOrEmptyIsFalseForNonNullOrEmptyValue() {
        PresetsValidationHelper.checkIsNullOrEmpty("valid value");
    }
}