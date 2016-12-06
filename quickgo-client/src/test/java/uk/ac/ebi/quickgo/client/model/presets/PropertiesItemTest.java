package uk.ac.ebi.quickgo.client.model.presets;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

/**
 * Test creation of {@link PropertiesItem} instances.
 *
 * Created 06/12/16
 * @author Edd
 */
public class PropertiesItemTest {
    @Test
    public void canCreateWithValidId() {
        String id = "ID";
        PropertiesItem item = PropertiesItem.createWithId(id).build();
        assertThat(item.getId(), is(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullIdCausesException() {
        String id = null;
        PropertiesItem.createWithId(id).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyIdCausesException() {
        String id = "";
        PropertiesItem.createWithId(id).build();
    }

    @Test
    public void canCreateWithValidDynamicProperty() {
        String key = "key";
        String value = "value";

        PropertiesItem item = PropertiesItem.createWithId("ID").withProperty(key, value).build();

        assertThat(item.getProperties(), hasEntry(key, value));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullDynamicPropertyCausesException() {
        String key = null;
        PropertiesItem.createWithId("ID").withProperty(key, "value").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyDynamicPropertyCausesException() {
        String key = "";
        PropertiesItem.createWithId("ID").withProperty(key, "value").build();
    }
}