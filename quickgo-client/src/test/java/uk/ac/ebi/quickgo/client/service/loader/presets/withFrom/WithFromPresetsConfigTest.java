package uk.ac.ebi.quickgo.client.service.loader.presets.withFrom;

import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ItemWriter;

/**
 * @author Tony Wardell
 * Date: 03/08/2017
 * Time: 11:01
 * Created with IntelliJ IDEA.
 */
public class WithFromPresetsConfigTest {

    private CompositePresetImpl presetBuilder;

    @Before
    public void setUp() {
        presetBuilder = new CompositePresetImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void avoidsNullPointerExceptionIfNameIsNull() throws Exception {
        WithFromPresetsConfig config = new WithFromPresetsConfig();

        List<RawNamedPreset> rawNamedPresets = new ArrayList<>();
        final RawNamedPreset raw1 = new RawNamedPreset();
        raw1.name = null;
        raw1.id = null;
        raw1.relevancy = 1;
        rawNamedPresets.add(raw1);

        ItemWriter<RawNamedPreset> writer = config.rawPresetWriter(presetBuilder);
        writer.write(rawNamedPresets);
    }
}
