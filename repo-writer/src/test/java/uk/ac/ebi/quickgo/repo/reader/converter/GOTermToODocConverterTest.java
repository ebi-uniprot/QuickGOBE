package uk.ac.ebi.quickgo.repo.reader.converter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.io.File;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created 14/12/15
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class GOTermToODocConverterTest {
    @Ignore
    @Test
    public void converts1Term() {
        GOSourceFiles sourceFiles = new GOSourceFiles(new File("/home/eddturner/working/quickgo-local/quickgo-data/ff"));
        GOLoader goLoader = new GOLoader(sourceFiles);
        Optional<GeneOntology> geneOntologyOptional = goLoader.load();
        assertThat(geneOntologyOptional.isPresent(), is(true));

        GeneOntology geneOntology = geneOntologyOptional.get();
        GOTermToODocConverter docConverter = new GOTermToODocConverter();

        System.out.println(docConverter.apply(Optional.of((GOTerm) geneOntology.getTerm("GO:0000003"))));
    }

    private static final String TERM_ID = "id1";

    @Mock
    public GOTerm term;

    private GOTermToODocConverter converter = new GOTermToODocConverter();

    @Before
    public void setup() {
        when(term.getId()).thenReturn(TERM_ID);
    }

    // annotation guidelines
    // children
    // taxon constraints
    // simple fields
}