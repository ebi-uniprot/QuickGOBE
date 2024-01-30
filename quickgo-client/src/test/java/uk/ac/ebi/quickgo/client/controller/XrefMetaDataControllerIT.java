package uk.ac.ebi.quickgo.client.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.client.QuickGOREST;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {QuickGOREST.class})
@WebAppConfiguration
class XrefMetaDataControllerIT {
  private MockMvc mockMvc;
  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }
  @Test
  void canRetrieveListOfXrefExternalMetaData() throws Exception {
    mockMvc.perform(get("/internal/xrefMetaData"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].database").exists())
      .andExpect(jsonPath("$[0].name").exists())
      .andExpect(jsonPath("$[0].entity_types").exists());
  }
}