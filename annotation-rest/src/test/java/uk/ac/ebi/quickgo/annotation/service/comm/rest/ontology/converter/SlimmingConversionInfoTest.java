package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;

/**
 * Created 19/08/16
 * @author Edd
 */
public class SlimmingConversionInfoTest {

    private SlimmingConversionInfo conversionInfo;

    @Before
    public void setUp() {
        conversionInfo = new SlimmingConversionInfo();
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullOriginalId() {
        String src = null;
        String dest = "dest";

        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddEmptyOriginalId() {
        String src = "";
        String dest = "dest";

        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullOriginal2SlimmedId() {
        String src = "src";
        String dest = null;

        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotAddEmptyOriginal2SlimmedId() {
        String src = "src";
        String dest = "";

        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
    }

    @Test
    public void canAddOneOriginal2SlimmedId() {
        String src = "src";
        String dest = "dest";
        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
        assertThat(conversionInfo.getInfo().size(), is(1));
        assertThat(conversionInfo.getInfo(), hasEntry(src, singletonList(dest)));
    }

    @Test
    public void canAddTwoOriginal2SlimmedId() {
        String src = "src";
        String dest1 = "dest1";
        String dest2 = "dest2";
        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest1);
        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest2);
        assertThat(conversionInfo.getInfo().size(), is(1));
        assertThat(conversionInfo.getInfo(), hasEntry(src, asList(dest1, dest2)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifyInfo() {
        String src = "src";
        String dest = "dest";

        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
        assertThat(conversionInfo.getInfo().size(), is(1));

        conversionInfo.getInfo().put("newKey", singletonList("newValue"));
    }
}