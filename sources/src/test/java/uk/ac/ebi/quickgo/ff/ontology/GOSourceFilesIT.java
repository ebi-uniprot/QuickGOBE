package uk.ac.ebi.quickgo.ff.ontology;

import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.io.File;
import org.junit.Test;

/**
 * Created 10/12/15
 * @author Edd
 */
public class GOSourceFilesIT {
    @Test
    public void loadsSourceFilesFromDir() throws Exception {
        SourceFiles sourceFiles = new SourceFiles(new File("/homes/eddturner/working/quickgo-local/quickgo-data/ff"));
        GeneOntology geneOntology = new GeneOntology();
        geneOntology.load(sourceFiles.goSourceFiles);
        System.out.println(geneOntology);
    }
}