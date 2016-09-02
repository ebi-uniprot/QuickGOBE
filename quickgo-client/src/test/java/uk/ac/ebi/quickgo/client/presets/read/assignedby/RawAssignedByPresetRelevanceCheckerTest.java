package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created 01/09/16
 * @author Edd
 */
public class RawAssignedByPresetRelevanceCheckerTest {
    private static final String UNIPARC = "UniParc";
    private static final String UNIPROT = "UniProt";
    private final static List<String> UNIPROT_OR_UNIPARC = asList(UNIPROT, UNIPARC);
    private RawAssignedByPresetRelevanceChecker relevanceChecker;

    @Before
    public void setUp() {
        this.relevanceChecker = new RawAssignedByPresetRelevanceChecker(UNIPROT_OR_UNIPARC);
    }

    @Test
    public void invalidItemIsFiltered() throws Exception {
        assertThat(relevanceChecker.process(assignedBy("invalid")), is(nullValue()));
    }

    @Test
    public void validItemIsNotFiltered() throws Exception {
        assertThat(relevanceChecker.process(assignedBy(UNIPROT)), is(not(nullValue())));
        assertThat(relevanceChecker.process(assignedBy(UNIPROT)).name, is(UNIPROT));
    }

    private RawAssignedByPreset assignedBy(String name) {
        RawAssignedByPreset preset = new RawAssignedByPreset();
        preset.name = name;
        return preset;
    }
}