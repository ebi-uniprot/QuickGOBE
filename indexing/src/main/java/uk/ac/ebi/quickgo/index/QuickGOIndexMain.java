package uk.ac.ebi.quickgo.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Main class for populating the ontology Solr index.
 *
 * This class requires two properties to be set in the application.properties file:
 * 1. indexing.ontology.source
 *   used as the source directory containing the QuickGO ontology *.dat.gz files.
 *
 * 2. solr.host
 *   used as the address of the Solr instance, whose ontology index will be populated
 *   by this class. E.g., http://localhost:8082/solr/
 *
 * Once these properties are set, to populate the index, please run this class.
 *
 * Created 02/12/15
 * @author Edd
 */
@Import({IndexingJobConfig.class})
@SpringBootApplication
public class QuickGOIndexMain {
    public static void main(String[] args) {
        SpringApplication.run(QuickGOIndexMain.class, args);
    }
}
