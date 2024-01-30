package uk.ac.ebi.quickgo.client.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 05/09/16
 * @author Edd
 */
class PresetsControllerTest {
    @Test
    void controllerInstantiationFailsOnNullPresets() {
        assertThrows(IllegalArgumentException.class, () -> new PresetsController(null));
    }
}