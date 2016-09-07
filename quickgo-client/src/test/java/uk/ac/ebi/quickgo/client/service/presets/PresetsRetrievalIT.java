package uk.ac.ebi.quickgo.client.service.presets;

import uk.ac.ebi.quickgo.client.QuickGOREST;
import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;

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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests whether the {@link CompositePreset} instance is populated correctly at application startup.
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedBy").exists())
                .andExpect(jsonPath("$.assignedBy.presets.*", hasSize(1)));
    }
}
