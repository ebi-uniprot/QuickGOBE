package uk.ac.ebi.quickgo.client.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created 05/09/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class PresetsControllerTest {
    @Test(expected = IllegalArgumentException.class)
    public void controllerInstantiationFailsOnNullPresets() {
        new PresetsController(null);
    }
}