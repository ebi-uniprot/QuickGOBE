package uk.ac.ebi.quickgo.annotation.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.coterms.CoTermRepository;
import uk.ac.ebi.quickgo.annotation.coterms.CoTermRepositorySimpleMap;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Expect an internal server error to be returned if the coterms files exist but contain no content.
 *
 * @author Tony Wardell
 * Date: 07/10/2016
 * Time: 16:32
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AnnotationREST.class, CoTermControllerFailedLoadIT.CoTermConfig.class})
@WebAppConfiguration
public class CoTermControllerFailedLoadIT {

    private static final String RESOURCE_URL = "/annotation/coterms";
    private static final String VALID_GO_TERM = "GO:7777771";

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void internalServerErrorIfCoTermFilesNotLoaded() throws Exception {
        ResultActions response = mockMvc.perform(get(buildPathToResource(VALID_GO_TERM)));
        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }

    private String buildPathToResource(String id, String... args) {
        return RESOURCE_URL + "/" + id + Arrays.stream(args)
                                               .collect(Collectors.joining("&", "?", ""));
    }
    @Configuration
    static class CoTermConfig {

        /**
         * An instance of CoTermRepositorySimpleMap is used rather than a mock so that is tested too.
         * Real files are used for the empty coterm files so that the constructor checking in SimpleMap succeeds (the
         * files exist), but contain no content, which prompts the error when a look up is tried.
         * @return CoTermRepository instance
         */

        @Bean
        @Primary
        public CoTermRepository coTermRepository() {
            CoTermRepositorySimpleMap coTermRepository = null;
            try {
                final Resource manual = new ClassPathResource("/coterms/CoTermsManualEmpty");
                final Resource all = new ClassPathResource("/coterms/CoTermsAllEmpty");
                coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(manual, all,1);
            } catch (Exception e) {
                System.out.printf(e.getMessage());
            }
            return coTermRepository;
        }
    }
}
