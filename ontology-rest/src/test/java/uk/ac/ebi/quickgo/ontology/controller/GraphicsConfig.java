package uk.ac.ebi.quickgo.ontology.controller;

import uk.ac.ebi.quickgo.graphics.service.GraphImageService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Bean definitions to use within tests and override the ones created in the usual application context.
 *
 * Created 27/09/16
 * @author Edd
 */
@Configuration class GraphicsConfig {
    @Bean @Primary
    public GraphImageService graphImageService() {
        return mock(GraphImageService.class);
    }
}
