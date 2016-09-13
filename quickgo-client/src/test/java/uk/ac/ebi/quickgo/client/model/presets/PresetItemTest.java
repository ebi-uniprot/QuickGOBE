package uk.ac.ebi.quickgo.client.model.presets;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 12/09/16
 * @author Edd
 */
public class PresetItemTest {
    private final static String VALID_ID = "valid id";
    private final static String VALID_NAME = "valid name";
    private final static String VALID_DESCRIPTION = "valid description";
    private final static Integer VALID_RELEVANCY = 0;

    @Test(expected = IllegalArgumentException.class)
    public void nullIdCausesException() {
        new PresetItem(null, VALID_NAME, VALID_DESCRIPTION, VALID_RELEVANCY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullNameCausesException() {
        new PresetItem(VALID_ID, null, VALID_DESCRIPTION, VALID_RELEVANCY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDescriptionCausesException() {
        new PresetItem(VALID_ID, VALID_NAME, null, VALID_RELEVANCY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRelevancyCausesException() {
        new PresetItem(VALID_ID, VALID_NAME, VALID_DESCRIPTION, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullNameCausesExceptionIn2ParamConstructor() {
        new PresetItem(null, VALID_DESCRIPTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDescriptionCausesExceptionIn2ParamConstructor() {
        new PresetItem(VALID_NAME, null);
    }

    @Test
    public void canCreateValidPreset() {
        PresetItem presetItem = new PresetItem(VALID_ID, VALID_NAME, VALID_DESCRIPTION, VALID_RELEVANCY);
        assertThat(presetItem, is(notNullValue()));
    }

}