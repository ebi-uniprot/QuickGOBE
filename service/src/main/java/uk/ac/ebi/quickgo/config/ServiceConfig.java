package uk.ac.ebi.quickgo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring configuration for the service layer, which depends on the repositories
 * made available by {@link RepoConfig}. Services to additionally make accessible
 * are defined in specified the {@link ComponentScan} packages.
 *
 * Created 19/11/15
 * @author Edd
 */
@Configuration
@Import({RepoConfig.class})
@ComponentScan({"uk.ac.ebi.quickgo.service"})
public class ServiceConfig {
}
