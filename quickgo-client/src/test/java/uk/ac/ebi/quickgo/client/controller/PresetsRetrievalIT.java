package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.client.model.presets.impl.CompositePresetImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
@SpringApplicationConfiguration(classes = {QuickGOREST.class})
@WebAppConfiguration
public class PresetsRetrievalIT {
    private static final String RESOURCE_URL = "/QuickGO/internal/presets";

    @Autowired
    protected WebApplicationContext webApplicationContext;

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
                .andExpect(jsonPath("$.assignedBy").exists())
                .andExpect(jsonPath("$.assignedBy.presets.*", hasSize(1)));
    }

    @Test
    public void canRetrieveReferencePresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.references").exists())
                .andExpect(jsonPath("$.references.presets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveEvidencePresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidences").exists())
                .andExpect(jsonPath("$.evidences.presets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveWithFromPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.withFrom").exists())
                .andExpect(jsonPath("$.withFrom.presets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveGeneProductPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.geneProducts").exists())
                .andExpect(jsonPath("$.geneProducts.presets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveGOSlimSetsPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goSlimSets").exists())
                .andExpect(jsonPath("$.goSlimSets.presets.*", hasSize(greaterThan(0))));
    }

    @Test
    public void canRetrieveTaxonPresets() throws Exception {
        mockMvc.perform(get(RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxons").exists())
                .andExpect(jsonPath("$.taxons.presets").exists());
    }
}
