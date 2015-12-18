package uk.ac.ebi.quickgo.repo.reader.converter;

import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created 14/12/15
 * @author Edd
 */
public class GOTermToODocConverterTest {
    @Test
    public void converts1Term() {
        GOSourceFiles sourceFiles = new GOSourceFiles(new File("/home/eddturner/working/quickgo-local/quickgo-data/ff"));
        GOLoader goLoader = new GOLoader(sourceFiles);
        Optional<GeneOntology> geneOntologyOptional = goLoader.load();
        assertThat(geneOntologyOptional.isPresent(), is(true));

        GeneOntology geneOntology = geneOntologyOptional.get();
        GOTermToODocConverter docConverter = new GOTermToODocConverter();

        List<GenericTerm> terms = geneOntology.getTerms();
        int max = 100;
        int count = 0;

//        for (GenericTerm term : terms) {
//            if (count++ >= max) {
//                break;
//            }
//
//            GOTerm goTerm = (GOTerm) term;
//            System.out.println(docConverter.apply(Optional.of(goTerm)));
//        }

//        System.out.println(docConverter.apply(Optional.of((GOTerm) geneOntology.getTerm("GO:0000215"))));
//        System.out.println(docConverter.apply(Optional.of((GOTerm) geneOntology.getTerm("GO:0003146"))));
        System.out.println(docConverter.apply(Optional.of((GOTerm) geneOntology.getTerm("GO:0000003"))));
    }
}