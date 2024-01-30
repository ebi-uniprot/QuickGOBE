package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created 13/09/16
 * @author Edd
 */
class SourceColumnsFactoryTest {
    @Test
    void canCreateValidInstanceForAllSources() {
        for (SourceColumnsFactory.Source source : SourceColumnsFactory.Source.values()) {
            RawNamedPresetColumns presetColumns = SourceColumnsFactory.createFor(source);
            assertThat(presetColumns, is(notNullValue()));
        }
    }
}