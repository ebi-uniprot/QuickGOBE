package uk.ac.ebi.quickgo.client.presets.read;

import org.springframework.batch.item.validator.ValidationException;

/**
 * Provides validation helper methods used whilst populating preset information.
 *
 * Created 05/09/16
 * @author Edd
 */
public class PresetsValidationHelper {
    public static void checkIsNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new ValidationException("Value cannot be null or empty");
        }
    }
}
