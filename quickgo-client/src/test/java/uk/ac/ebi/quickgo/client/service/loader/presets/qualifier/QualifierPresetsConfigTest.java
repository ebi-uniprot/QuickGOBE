package uk.ac.ebi.quickgo.client.service.loader.presets.qualifier;

import org.springframework.batch.item.Chunk;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;
import uk.ac.ebi.quickgo.client.service.loader.presets.ff.RawNamedPreset;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 02/03/2018
 * Time: 11:11
 * Created with IntelliJ IDEA.
 */
class QualifierPresetsConfigTest {

    private CompositePresetImpl presetBuilder;

    @BeforeEach
    void setUp() {
        presetBuilder = new CompositePresetImpl();
    }

    @Test
    void lowercaseNotBecomesUpperCaseNot() throws Exception {
        QualifierPresetsConfig config = new QualifierPresetsConfig();

        List<RawNamedPreset> rawNamedPresets = new ArrayList<>();
        final RawNamedPreset raw1 = new RawNamedPreset();
        raw1.name = "not|part_of";
        raw1.id = "not|part_of";
        raw1.relevancy = 1;
        rawNamedPresets.add(raw1);

        ItemWriter<RawNamedPreset> writer = config.rawPresetWriter(presetBuilder);
        writer.write(new Chunk<>(rawNamedPresets));

        final List<PresetItem> qualifiers = presetBuilder.getQualifiers();
        assertThat(qualifiers, hasSize(1));
        assertThat(qualifiers.get(0).getProperty(PresetItem.Property.NAME), is("NOT|part_of"));
        assertThat(qualifiers.get(0).getProperty(PresetItem.Property.ID), is("NOT|part_of"));
    }

    @Test
    void valuesWithoutNotAreUnaffected() throws Exception {
        QualifierPresetsConfig config = new QualifierPresetsConfig();

        List<RawNamedPreset> rawNamedPresets = new ArrayList<>();
        final RawNamedPreset raw1 = new RawNamedPreset();
        final String part_of = "part_of";
        raw1.name = part_of;
        raw1.relevancy = 1;
        rawNamedPresets.add(raw1);

        ItemWriter<RawNamedPreset> writer = config.rawPresetWriter(presetBuilder);
        writer.write(new Chunk<>(rawNamedPresets));

        final List<PresetItem> qualifiers = presetBuilder.getQualifiers();
        assertThat(qualifiers, hasSize(1));
        assertThat(qualifiers.get(0).getProperty(PresetItem.Property.NAME), is(part_of));
        assertThat(qualifiers.get(0).getProperty(PresetItem.Property.ID), is(part_of));
    }

    @Test
    void avoidsNullPointerExceptionIfNameIsNull() {
        QualifierPresetsConfig config = new QualifierPresetsConfig();

        List<RawNamedPreset> rawNamedPresets = new ArrayList<>();
        final RawNamedPreset raw1 = new RawNamedPreset();
        raw1.name = null;
        raw1.id = null;
        raw1.relevancy = 1;
        rawNamedPresets.add(raw1);

        ItemWriter<RawNamedPreset> writer = config.rawPresetWriter(presetBuilder);
        assertThrows(IllegalArgumentException.class, () -> writer.write(new Chunk<>(rawNamedPresets)));
    }
}
