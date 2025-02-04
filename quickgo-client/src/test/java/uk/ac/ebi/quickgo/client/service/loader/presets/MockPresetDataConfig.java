package uk.ac.ebi.quickgo.client.service.loader.presets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestOperations;
import uk.ac.ebi.quickgo.client.model.presets.PresetItem;
import uk.ac.ebi.quickgo.client.model.presets.evidence.PresetEvidenceItem;
import uk.ac.ebi.quickgo.common.SearchableField;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static uk.ac.ebi.quickgo.client.service.loader.presets.assignedby.AssignedByPresetsConfig.ASSIGNED_BY;
import static uk.ac.ebi.quickgo.client.service.loader.presets.qualifier.QualifierPresetsConfig.QUALIFIER;
import static uk.ac.ebi.quickgo.client.service.loader.presets.taxon.TaxonPresetsConfig.TAXON_ID;

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

    //Profiles
    static final String SUCCESSFUL_FETCHING = "successfulFetching";
    static final String FAILED_FETCHING = "failedFetching";
    static final String NO_SEARCH_ATTRIBUTES = "noSearchAttributes";

    /*
     * Preset items information representing the most relevant, ECO:0000352 term.
     */
    static final PresetItem PRESET_ECO_32;
    static final PresetItem PRESET_BHF_UCL;
    static final PresetItem PRESET_GO_SLIM_ASPERGILLUS;
    static final PresetItem PRESET_GO_SLIM_METAGENOMICS;
    static final PresetItem PRESET_GO_SLIM_POMBE;
    static final PresetItem PRESET_GO_SLIM_SYNAPSE;
    static final PresetItem PRESET_TAXON_ARABIDOPSIS;
    static final PresetItem PRESET_TAXON_DROSOPHILA;
    static final String QUALIFIER_ENABLES = "enables";
    static final String QUALIFIER_INVOLVED_IN = "involved_in";
    private static final String TAXON_HUMAN = "9606";
    private static final String TAXON_BACTERIA = "2";
    private static final String SLIM_NAME = "name";
    private static final String SLIM_ASPECT = "aspect";
    private static final RelevancyResponseType DEFAULT_RELEVANT_ASSIGNED_BYS;
    private static final RelevancyResponseType DEFAULT_RELEVANT_TAXONS;
    private static final RelevancyResponseType DEFAULT_RELEVANT_QUALIFIERS;
    private static final RelevancyResponseType DEFAULT_RELEVANT_WITH_FROM;

    static {
        DEFAULT_RELEVANT_ASSIGNED_BYS = new RelevancyResponseType();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms = new RelevancyResponseType.Terms();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.relevancies = new ArrayList<>();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.relevancies.add(UNIPROT_KB);
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.relevancies.add("1000");
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.relevancies.add(ENSEMBL);
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.relevancies.add("100");

        DEFAULT_RELEVANT_TAXONS = new RelevancyResponseType();
        DEFAULT_RELEVANT_TAXONS.terms = new RelevancyResponseType.Terms();
        DEFAULT_RELEVANT_TAXONS.terms.relevancies = new ArrayList<>();
        DEFAULT_RELEVANT_TAXONS.terms.relevancies.add(TAXON_HUMAN);
        DEFAULT_RELEVANT_TAXONS.terms.relevancies.add("1000");
        DEFAULT_RELEVANT_TAXONS.terms.relevancies.add(TAXON_BACTERIA);
        DEFAULT_RELEVANT_TAXONS.terms.relevancies.add("100");

        DEFAULT_RELEVANT_QUALIFIERS = new RelevancyResponseType();
        DEFAULT_RELEVANT_QUALIFIERS.terms = new RelevancyResponseType.Terms();
        DEFAULT_RELEVANT_QUALIFIERS.terms.relevancies = new ArrayList<>();
        DEFAULT_RELEVANT_QUALIFIERS.terms.relevancies.add(QUALIFIER_ENABLES);
        DEFAULT_RELEVANT_QUALIFIERS.terms.relevancies.add("1000");
        DEFAULT_RELEVANT_QUALIFIERS.terms.relevancies.add(QUALIFIER_INVOLVED_IN);
        DEFAULT_RELEVANT_QUALIFIERS.terms.relevancies.add("100");

        DEFAULT_RELEVANT_WITH_FROM = new RelevancyResponseType();
        DEFAULT_RELEVANT_WITH_FROM.terms = new RelevancyResponseType.Terms();
        DEFAULT_RELEVANT_WITH_FROM.terms.relevancies = new ArrayList<>();
        DEFAULT_RELEVANT_WITH_FROM.terms.relevancies.add(UNIPROT_KB);
        DEFAULT_RELEVANT_WITH_FROM.terms.relevancies.add("1000");
        DEFAULT_RELEVANT_WITH_FROM.terms.relevancies.add(ENSEMBL);
        DEFAULT_RELEVANT_WITH_FROM.terms.relevancies.add("100");

        PRESET_ECO_32 = PresetItem
                .createWithName("evidence used in manual assertion")
                .withProperty(PresetItem.Property.ID, "ECO:0000352")
                .withProperty(PresetEvidenceItem.Property.GO_EVIDENCE.getKey(), "Any manual evidence")
                .withRelevancy(0)
                .build();

        PRESET_BHF_UCL = PresetItem
                .createWithName("BHF-UCL")
                .withProperty(PresetItem.Property.DESCRIPTION,
                        "The set of Cardiovascular-associated proteins being prioritised for annotation by the " +
                                "Cardiovascular Gene Ontology Annotation Initiative located at University College " +
                                "London")
                .withProperty(PresetItem.Property.URL, "http://www.ucl.ac.uk/cardiovasculargeneontology")
                .build();

        PRESET_GO_SLIM_ASPERGILLUS = createSlimSetPresetItem("goslim_aspergillus", "1535326", "Candida")
                .withAssociations(asList(
                        createPresetItem("GO:0005575", "cellular_component", "cellular_component"),
                        createPresetItem("GO:0005576", "extracellular region", "cellular_component"),
                        createPresetItem("GO:0000988", "transcription factor activity, protein binding",
                                "molecular_function")))
                .build();

        PRESET_GO_SLIM_METAGENOMICS = createSlimSetPresetItem("goslim_metagenomics", "408169", "Metagenomics")
                .withAssociations(asList(
                        createPresetItem("GO:0006259", "DNA metabolic process", "biological_process"),
                        createPresetItem("GO:0008233", "peptidase activity", "molecular_function"),
                        createPresetItem("GO:0016740", "transferase activity", "molecular_function")))
                .build();

        PRESET_GO_SLIM_POMBE = createSlimSetPresetItem("goslim_pombe", "1", "All organisms")
                .withAssociations(asList(
                        createPresetItem("GO:0002181", "cytoplasmic translation", "biological_process"),
                        createPresetItem("GO:0006355", "regulation of transcription, DNA-templated",
                                "biological_process")))
                .build();

        PRESET_GO_SLIM_SYNAPSE = createSlimSetPresetItem("goslim_synapse", "10090,10116,6239,7227,7955,9606", "SynGO")
                .withAssociations(singletonList(
                        createPresetItem("GO:0004444", "obsolete inositol-1,4,5-trisphosphate 1-phosphatase",
                                "cellular_component")))
                .build();

        PRESET_TAXON_ARABIDOPSIS = PresetItem
                .createWithName("Arabidopsis thaliana")
                .withProperty(PresetItem.Property.ID, "3702")
                .withRelevancy(1)
                .build();

        PRESET_TAXON_DROSOPHILA = PresetItem
                .createWithName("Drosophila melanogaster")
                .withProperty(PresetItem.Property.ID, "7227")
                .withRelevancy(2)
                .build();

    }

    @Bean(name = "restOperations") @Profile(SUCCESSFUL_FETCHING)
    @SuppressWarnings("unchecked")
    public RestOperations restOperations() {
        RestOperations mockRestOperations = mock(RestOperations.class);

        when(mockRestOperations.getForObject(
                anyStringContaining(ASSIGNED_BY),
                isA(Class.class),
                any(Map.class)))
                .thenReturn(DEFAULT_RELEVANT_ASSIGNED_BYS);
        when(mockRestOperations.getForObject(
                anyStringContaining(TAXON_ID),
                isA(Class.class),
                any(Map.class)))
                .thenReturn(DEFAULT_RELEVANT_TAXONS);
        when(mockRestOperations.getForObject(
                anyStringContaining(QUALIFIER),
                isA(Class.class),
                any(Map.class)))
                .thenReturn(DEFAULT_RELEVANT_QUALIFIERS);
        when(mockRestOperations.getForObject(anyStringContaining("withFrom"),
                isA(Class.class),
                any(Map.class)))
                .thenReturn(DEFAULT_RELEVANT_WITH_FROM);

        return mockRestOperations;
    }

    @Bean(name = "restOperations") @Profile(FAILED_FETCHING)
    @SuppressWarnings("unchecked")
    public RestOperations badRestOperations() {
        RestOperations mockRestOperations = mock(RestOperations.class);
        doThrow(new RetrievalException("Deliberately causing error in test"))
                .when(mockRestOperations)
                .getForObject(
                        anyString(),
                        isA(Class.class),
                        any(Map.class));
        return mockRestOperations;
    }

    @Bean @Profile(NO_SEARCH_ATTRIBUTES)
    public SearchableField searchableDocumentFields() {
        return new NoSearchablePresetDocumentFields();
    }

    private static PresetItem createPresetItem(String id, String name, String aspect) {
        return PresetItem
                .createWithName(id)
                .withProperty(PresetItem.Property.ID, id)
                .withProperty(SLIM_NAME, name)
                .withProperty(SLIM_ASPECT, aspect)
                .build();
    }

    private static PresetItem.Builder createSlimSetPresetItem(String name, String taxonIds, String shortLabel) {
        return PresetItem
          .createWithName(name)
          .withProperty("role", "Binning")
          .withProperty("taxIds", taxonIds)
          .withProperty("shortLabel", shortLabel);
    }

    private String anyStringContaining(String value) {
        return matches(".*" + value + ".*");
    }

    private static class NoSearchablePresetDocumentFields implements SearchableField {

        @Override public boolean isSearchable(String field) {
            return false;
        }

        @Override public Stream<String> searchableFields() {
            return Stream.empty();
        }

    }
}
