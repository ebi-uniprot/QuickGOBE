package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.presets.read.assignedby.AssignedByRelevancyResponseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestOperations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Provides configurable properties, beans, etc., used during tests.
 *
 * Created 02/09/16
 * @author Edd
 */
@Configuration
public class MockPresetDataConfig {
    static final String DOI = "DOI";
    static final String ENSEMBL = "ENSEMBL";
    static final String REACTOME = "REACTOME";
    static final List<String> GO_REFS_FROM_RESOURCE =
            Stream.of(
                    "GO_REF:0000037",
                    "GO_REF:0000039",
                    "GO_REF:0000002",
                    "GO_REF:0000104"
            ).collect(Collectors.toList());
    static final String UNIPROT_KB = "UniProtKB";
    static final String SUCCESSFUL_FETCHING = "successfulFetching";
    static final String FAILED_FETCHING = "failedFetching";
    /*
     * Preset items information representing the most relevant, ECO:0000352 term.
     */
    static final PresetItem PRESET_ECO_32;
    static final PresetItem PRESET_DICTY_BASE;
    static final PresetItem PRESET_BHF_UCL;
    private static final AssignedByRelevancyResponseType DEFAULT_RELEVANT_ASSIGNED_BYS;

    static {
        DEFAULT_RELEVANT_ASSIGNED_BYS = new AssignedByRelevancyResponseType();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms = new AssignedByRelevancyResponseType.Terms();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy = new ArrayList<>();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add(UNIPROT_KB);
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add("1000");
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add(ENSEMBL);
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add("100");

        PRESET_ECO_32 = new PresetItem("ECO:0000352", "All manual codes", "evidence used in manual assertion", null, 1);
        PRESET_BHF_UCL = new PresetItem(null, "BHF-UCL", "The set of Cardiovascular-associated proteins being " +
                "prioritised for annotation by the Cardiovascular Gene Ontology Annotation Initiative located at " +
                "University College London", "http://www.ucl.ac.uk/cardiovasculargeneontology", 0);
        PRESET_DICTY_BASE = new PresetItem(null, "dictyBase", "dictyBase", null, 62);
    }

    @Bean @Profile(SUCCESSFUL_FETCHING)
    @SuppressWarnings("unchecked")
    public RestOperations restOperations() {
        RestOperations mockRestOperations = mock(RestOperations.class);

        when(mockRestOperations.getForObject(
                anyString(),
                isA(Class.class),
                any(HashMap.class)))
                .thenReturn(DEFAULT_RELEVANT_ASSIGNED_BYS);

        return mockRestOperations;
    }

    @Bean @Profile(FAILED_FETCHING)
    @SuppressWarnings("unchecked")
    public RestOperations badRestOperations() {
        RestOperations mockRestOperations = mock(RestOperations.class);
        doThrow(new RuntimeException())
                .when(mockRestOperations)
                .getForObject(
                        anyString(),
                        isA(Class.class),
                        any(HashMap.class));
        return mockRestOperations;
    }
}
