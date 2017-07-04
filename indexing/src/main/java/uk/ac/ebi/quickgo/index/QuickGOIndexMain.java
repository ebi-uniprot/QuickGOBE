package uk.ac.ebi.quickgo.index;

import uk.ac.ebi.quickgo.index.annotation.AnnotationConfig;
import uk.ac.ebi.quickgo.index.geneproduct.GeneProductConfig;
import uk.ac.ebi.quickgo.index.ontology.OntologyConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
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
@Import({OntologyConfig.class, GeneProductConfig.class, AnnotationConfig.class})
@SpringBootApplication
public class QuickGOIndexMain {
    static <T> int run(Class<T> type, String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(type, args);
        return SpringApplication.exit(applicationContext);
    }

    public static void main(String[] args) {
        System.exit(QuickGOIndexMain.run(QuickGOIndexMain.class, args));
    }
}
