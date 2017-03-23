package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.AnnotationREST;
import uk.ac.ebi.quickgo.annotation.coterms.*;
import uk.ac.ebi.quickgo.annotation.download.DownloadConfig;
import uk.ac.ebi.quickgo.annotation.metadata.MetaDataConfig;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.annotation.validation.loader.ValidationConfig;
import uk.ac.ebi.quickgo.rest.controller.SwaggerConfig;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Tony Wardell
 * Date: 07/10/2016
 * Time: 16:32
 * Created with IntelliJ IDEA.
 */
//@ActiveProfiles(profiles = {"failedToLoadCoTerms",})

@RunWith(SpringJUnit4ClassRunner.class)
//@ComponentScan({"uk.ac.ebi.quickgo.annotation.controller","uk.ac.ebi.quickgo.rest"})

@ContextConfiguration(name = "contextWithFakeBean")
@WebAppConfiguration
//@TestPropertySource(properties = {"coterm.source.manual=fu", "coterm.source.all=bar"})
@SpringApplicationConfiguration(classes = { CoTermControllerFailedLoadIT.CoTermConfig.class, AnnotationREST.class})
public class CoTermControllerFailedLoadIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoTermControllerFailedLoadIT.class);
    private static final String RESOURCE_URL = "/annotation/coterms";
    private static final String VALID_GO_TERM = "GO:7777771";

    @Autowired
    private WebApplicationContext webApplicationContext;

//    @Mock
//    private CoTermRepository coTermRepository;
//
//    @InjectMocks
//    CoTermController controllerUnderTest;

    private MockMvc mockMvc;

    @Before
    public void setup() {

        // this must be called for the @Mock annotations above to be processed
        // and for the mock service to be injected into the controller under
        // test.
//        MockitoAnnotations.initMocks(this);

        mockMvc = webAppContextSetup(webApplicationContext).build();


//        when(coTermRepository.findCoTerms("GO:7777771", CoTermSource.MANUAL )).thenThrow(new
//                                                                                                 IllegalStateException("Test no coterms loaded from file"));
//
//        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerUnderTest).build();
    }

    @Test //(expected = IllegalStateException.class)
    public void internalServerErrorIfCoTermFilesNotLoaded() throws Exception {
//        CoTermRepository mockRepository = Mockito.mock(CoTermRepository.class);
//                    when(mockRepository.findCoTerms("GO:7777771", CoTermSource.ALL )).thenThrow(new
//                                                                                                             IllegalStateException("Test no coterms loaded from file"));
//
//
//        CoTermController coTermController = new CoTermController(mockRepository);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(coTermController).build();
        ResultActions response = mockMvc.perform(get(buildPathToResource(VALID_GO_TERM)));
        response.andDo(print())
                .andExpect(status().isInternalServerError());
    }

    private String buildPathToResource(String id, String... args) {
        return RESOURCE_URL + "/" + id + Arrays.stream(args)
                                               .collect(Collectors.joining("&", "?", ""));
    }

//    /**
//     * Configure properties used by co-term generation, using test values.
//     */
////    @Profile("failedToLoadCoTerms")
    @Configuration
    @EnableWebMvc
    static class CoTermConfig {

//        @Bean
//        public CoTermRepository coTermRepository() throws Exception {
//            CoTermRepository coTermRepository = Mockito.mock(CoTermRepository.class);
//            when(coTermRepository.findCoTerms("GO:7777771", CoTermSource.MANUAL )).thenThrow(new
//                                                                                                     IllegalStateException("Test no coterms loaded from file"));
//
//           return coTermRepository;
//        }
//    }

    @Bean
    @Primary
    public CoTermRepository coTermRepository() {
        CoTermRepositorySimpleMap coTermRepository = null;

        try {
            coTermRepository = CoTermRepositorySimpleMap.createCoTermRepositorySimpleMap(new FileSystemResource
                                                                                                 ("fu"),
                                                                                         new FileSystemResource
                                                                                                 ("bar"),
                                                                                         1);
        } catch (Exception e) {

            LOGGER.error(e.getMessage());
        }
        return coTermRepository;
    }
    }

}
