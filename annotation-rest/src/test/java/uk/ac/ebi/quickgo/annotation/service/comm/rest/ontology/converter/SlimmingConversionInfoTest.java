package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 19/08/16
 * @author Edd
 */
class SlimmingConversionInfoTest {

    private SlimmingConversionInfo conversionInfo;

    @BeforeEach
    void setUp() {
        conversionInfo = new SlimmingConversionInfo();
    }

    @Test
    void cannotAddNullOriginalId() {
        String src = null;
        String dest = "dest";
        assertThrows(IllegalArgumentException.class, () -> conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest));
    }

    @Test
    void cannotAddEmptyOriginalId() {
        String src = "";
        String dest = "dest";
        assertThrows(IllegalArgumentException.class, () -> conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest));
    }

    @Test
    void cannotAddNullOriginal2SlimmedId() {
        String src = "src";
        String dest = null;
        assertThrows(IllegalArgumentException.class, () -> conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest));
    }

    @Test
    void cannotAddEmptyOriginal2SlimmedId() {
        String src = "src";
        String dest = "";
        assertThrows(IllegalArgumentException.class, () -> conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest));
    }

    @Test
    void canAddOneOriginal2SlimmedId() {
        String src = "src";
        String dest = "dest";
        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
        assertThat(conversionInfo.getInfo().size(), is(1));
        assertThat(conversionInfo.getInfo(), hasEntry(src, singletonList(dest)));
    }

    @Test
    void canAddTwoOriginal2SlimmedId() {
        String src = "src";
        String dest1 = "dest1";
        String dest2 = "dest2";
        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest1);
        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest2);
        assertThat(conversionInfo.getInfo().size(), is(1));
        assertThat(conversionInfo.getInfo(), hasEntry(src, asList(dest1, dest2)));
    }

    @Test
    void cannotModifyInfo() {
        String src = "src";
        String dest = "dest";

        conversionInfo.addOriginal2SlimmedGOIdMapping(src, dest);
        assertThat(conversionInfo.getInfo().size(), is(1));
        assertThrows(UnsupportedOperationException.class, () -> conversionInfo.getInfo().put("newKey", singletonList("newValue")));
    }
}