package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 13/09/16
 * @author Edd
 */
public class SourceColumnsFactoryTest {
    @Test
    public void canCreateValidInstanceForAllSources() {
        for (SourceColumnsFactory.Source source : SourceColumnsFactory.Source.values()) {
            RawNamedPresetColumns presetColumns = SourceColumnsFactory.createFor(source);
            assertThat(presetColumns, is(notNullValue()));
        }
    }
}