package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.io.IOException;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Provide a temporary location for files to be output during co-occurring terms calculation.
 * Created 12/10/16
 * @author Edd
 */
public class CoTermTemporaryDataStore extends ExternalResource {

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    public void before() throws IOException {
        temporaryFolder.create();
    }

    @Override
    public void after() {
        temporaryFolder.delete();
    }

    public static class Config {
        @Bean
        public Resource manualCoTermsResource() throws IOException {
            String manualCoTermsPath = temporaryFolder.newFile().getAbsolutePath();
            return new FileSystemResource(manualCoTermsPath);
        }

        @Bean
        public Resource allCoTermsResource() throws IOException {
            String allCoTermsLocation = temporaryFolder.newFile().getAbsolutePath();
            return new FileSystemResource(allCoTermsLocation);
        }
    }
}
