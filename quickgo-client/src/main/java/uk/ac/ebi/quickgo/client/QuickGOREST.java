package uk.ac.ebi.quickgo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import uk.ac.ebi.quickgo.client.service.loader.presets.PresetsConfig;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.SwaggerConfig;

/**
 * Runnable class to start an embedded Jetty server to host the defined RESTful components.
 *
 * Created 16/11/15
 * @author Edd
 */
@SpringBootApplication
@ComponentScan({
        "uk.ac.ebi.quickgo.client.controller",
        "uk.ac.ebi.quickgo.rest"
})
@Import({SearchServiceConfig.class, SwaggerConfig.class, PresetsConfig.class})
public class QuickGOREST {

    public static void main(String[] args) {
        SpringApplication.run(QuickGOREST.class, args);
    }
}
