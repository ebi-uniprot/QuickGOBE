package uk.ac.ebi.quickgo.client.presets.read.assignedby;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
public class RawAssignedByPresetTopNTest {
    private static final String UNIPARC = "UniParc";
    private static final String UNIPROT = "UniProt";
    private final static Predicate<String> IS_UNIPROT_OR_UNIPARC = v -> v.equals(UNIPROT) || v.equals(UNIPARC);
    private RawAssignedByPresetTopN topN;

    @Before
    public void setUp() {
        this.topN = new RawAssignedByPresetTopN(IS_UNIPROT_OR_UNIPARC);
    }

    @Test
    public void invalidItemIsFiltered() throws Exception {
        assertThat(topN.process(assignedBy("invalid")), is(nullValue()));
    }

    @Test
    public void validItemIsNotFiltered() throws Exception {
        assertThat(topN.process(assignedBy(UNIPROT)), is(not(nullValue())));
        assertThat(topN.process(assignedBy(UNIPROT)).name, is(UNIPROT));
    }

    @Test
    public void processingMultipleItemsRetainsOrderOfValidItems() {
        List<RawAssignedByPreset> validAndInvalidAssignedBys = asList(
                assignedBy("invalid1"),
                assignedBy(UNIPROT),
                assignedBy("invalid2"),
                assignedBy(UNIPARC));

        List<String> validAssignedBys = validAndInvalidAssignedBys.stream()
                .map(assignedBy -> {
                    try {
                        return topN.process(assignedBy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .map(assignedBy -> assignedBy.name)
                .collect(Collectors.toList());

        assertThat(validAssignedBys, is(asList(UNIPROT, UNIPARC)));
    }

    private RawAssignedByPreset assignedBy(String name) {
        RawAssignedByPreset preset = new RawAssignedByPreset();
        preset.name = name;
        return preset;
    }
}