package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.io.File;
import java.util.Optional;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by edd on 11/12/2015.
 */
public class GOLoaderIT {

    @Test
    public void canLoadGOInstance() {
        GOSourceFiles sourceFiles = new GOSourceFiles(new File("/home/eddturner/working/quickgo-local/quickgo-data/ff"));
        GOLoader goLoader = new GOLoader(sourceFiles);
        Optional<GeneOntology> geneOntologyOptional = goLoader.load();
        assertThat(geneOntologyOptional.isPresent(), is(true));

        GeneOntology geneOntology = geneOntologyOptional.get();
        GenericTerm genericTerm = geneOntology.getTerms().get(1);
    }

}