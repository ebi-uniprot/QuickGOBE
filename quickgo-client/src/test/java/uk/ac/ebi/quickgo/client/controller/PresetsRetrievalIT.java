package uk.ac.ebi.quickgo.client.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests whether the {@link CompositePresetImpl} instance is populated correctly at application startup.
 *
 * Created 05/09/16
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {QuickGOREST.class})
@WebAppConfiguration
public class PresetsRetrievalIT {
    private static final String RESOURCE_URL = "/internal/presets";
    private static final String FIELDS_PARAM = "fields";

    @Autowired private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void canRetrieveAssignedByPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedBy").exists());
    }

    @Test
    public void canRetrieveReferencePresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.references.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveEvidencePresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidences.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveWithFromPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.withFrom").exists());
    }

    @Test
    public void canRetrieveGeneProductPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.geneProducts.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveGOSlimSetsPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goSlimSets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveTaxonPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxons").exists());
    }

    @Test
    public void canRetrieveQualifierPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qualifiers").exists());
    }

    @Test
    public void canRetrieveAspectPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aspects.*", hasSize(3)));
    }

    @Test
    public void canRetrieveGeneProductTypesPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.geneProductTypes.*", hasSize(3)));
    }

    @Test
    public void canRetrieveAnnotationExtensionRelationsPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extRelations.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveAnnotationExtensionDatabasesPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.extDatabases.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveSingleDesiredPreset() throws Exception {
        mockMvc.perform(get(RESOURCE_URL).param(FIELDS_PARAM, "goSlimSets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedBy").doesNotExist())
                .andExpect(jsonPath("$.references").doesNotExist())
                .andExpect(jsonPath("$.evidences").doesNotExist())
                .andExpect(jsonPath("$.withFrom").doesNotExist())
                .andExpect(jsonPath("$.geneProducts").doesNotExist())
                .andExpect(jsonPath("$.goSlimSets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveMultipleDesiredPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL).param(FIELDS_PARAM, "goSlimSets,geneProducts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedBy").doesNotExist())
                .andExpect(jsonPath("$.references").doesNotExist())
                .andExpect(jsonPath("$.evidences").doesNotExist())
                .andExpect(jsonPath("$.withFrom").doesNotExist())
                .andExpect(jsonPath("$.geneProducts.*", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.goSlimSets.*", hasSize(greaterThan(0))));
    }
}
