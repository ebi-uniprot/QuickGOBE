package uk.ac.ebi.quickgo.repowriter.main;

import uk.ac.ebi.quickgo.repowriter.reader.ODocReader;
import uk.ac.ebi.quickgo.repowriter.write.IndexerProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created 12/01/16
 * @author Edd
 */
@Profile("QuickGOIndexOntologyMainIT")
@Configuration
public class QuickGOIndexOntologyMainITConfig {

    static final int STEP_SKIP_LIMIT = 2;

    @Bean
    @Primary
    public ODocReader reader() {
        return mock(ODocReader.class);
    }

    @Bean
    @Primary
    public IndexerProperties indexerProperties() {
        IndexerProperties mockIndexerProperties = mock(IndexerProperties.class);
        when(mockIndexerProperties.getOntologySkipLimit()).thenReturn(STEP_SKIP_LIMIT);
        return mockIndexerProperties;
    }
}
