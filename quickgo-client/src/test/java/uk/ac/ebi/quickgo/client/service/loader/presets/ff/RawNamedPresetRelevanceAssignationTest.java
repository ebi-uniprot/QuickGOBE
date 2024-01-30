package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created 01/09/16
 * @author Edd
 */
class RawNamedPresetRelevanceAssignationTest {
    private static final String UNIPARC = "UniParc";
    private static final String UNIPROT = "UniProt";
    private final static List<String> UNIPROT_OR_UNIPARC = asList(UNIPROT, UNIPARC);
    private RawNamedPresetRelevanceAssignation relevanceChecker;

    @BeforeEach
    void setUp() {
        this.relevanceChecker = new RawNamedPresetRelevanceAssignation(UNIPROT_OR_UNIPARC);
    }

    @Test
    void invalidItemIsFiltered() throws Exception {
        assertThat(relevanceChecker.process(createAssignedBy("invalid")), is(nullValue()));
    }

    @Test
    void validItemIsNotFiltered() throws Exception {
        assertThat(relevanceChecker.process(createAssignedBy(UNIPROT)), is(not(nullValue())));
        assertThat(relevanceChecker.process(createAssignedBy(UNIPROT)).name, is(UNIPROT));
    }

    private RawNamedPreset createAssignedBy(String name) {
        RawNamedPreset preset = new RawNamedPreset();
        preset.name = name;
        return preset;
    }
}