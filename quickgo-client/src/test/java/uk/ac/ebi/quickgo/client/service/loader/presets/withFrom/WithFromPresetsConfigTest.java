package uk.ac.ebi.quickgo.client.service.loader.presets.withFrom;

import org.junit.Before;

/**
 * @author Tony Wardell
 * Date: 03/08/2017
 * Time: 11:01
 * Created with IntelliJ IDEA.
 */
public class WithFromPresetsConfigTest {

    private WithFromPresetsConfig withFromPresetsConfig;

    @Before
    public void setup() {
        withFromPresetsConfig = new WithFromPresetsConfig();
    }
    //
    //    @Test
    //    public void preventDuplicates() throws Exception {
    //        final ItemProcessor<RawNamedPreset, RawNamedPreset> itemProcessor = withFromPresetsConfig
    // .duplicateChecker();
    //        RawNamedPreset rawNamedPreset1 = new RawNamedPreset();
    //        rawNamedPreset1.name = "AgBase";
    //        RawNamedPreset rawNamedPreset2 = new RawNamedPreset();
    //        rawNamedPreset2.name = "AspGD";
    //        RawNamedPreset rawNamedPreset3 = new RawNamedPreset();
    //        rawNamedPreset3.name = "ASPGD";
    //        RawNamedPreset rawNamedPreset4 = new RawNamedPreset();
    //        rawNamedPreset4.name = "Alzheimers_University_of_Toronto";
    //
    //        assertThat(itemProcessor.process(rawNamedPreset1), notNullValue());
    //        assertThat(itemProcessor.process(rawNamedPreset2), notNullValue());
    //        assertThat(itemProcessor.process(rawNamedPreset3), nullValue());
    //        assertThat(itemProcessor.process(rawNamedPreset4), notNullValue());
    //    }
}
