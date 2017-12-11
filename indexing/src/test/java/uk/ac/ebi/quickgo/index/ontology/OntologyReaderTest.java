package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.model.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created 11/01/16
 * @author Edd
 */
@RunWith(HierarchicalContextRunner.class)
public class OntologyReaderTest {

    private OntologyReader ontologyReader;
    private int docCount;

    @After
    public void after() {
        if (ontologyReader != null) {
            ontologyReader.close();
        }
    }

    public class DocReaderAccessingValidOntologies {

        @Before
        public void setUp() {
            GeneOntology go = mock(GeneOntology.class);
            EvidenceCodeOntology eco = mock(EvidenceCodeOntology.class);

            when(go.getTerms()).thenReturn(createGOTerms());
            when(eco.getTerms()).thenReturn(createECOTerms());

            // set document count
            docCount += go.getTerms().size() + eco.getTerms().size();
            ontologyReader = new OntologyReader(go, eco);
            ontologyReader.open(new ExecutionContext());
        }

        @Test
        public void readsAllDocsWithoutError() throws Exception {
            int count = 0;
            while (ontologyReader.read() != null) {
                count++;
            }
            assertThat(count, is(docCount));
        }
    }

    public class DocReaderAccessingInvalidOntologies {
        private GeneOntology validGO;
        private EvidenceCodeOntology validECO;

        @Before
        public void setUp() {
            validGO = mock(GeneOntology.class);
            when(validGO.getTerms()).thenReturn(createGOTerms());

            validECO = mock(EvidenceCodeOntology.class);
            when(validECO.getTerms()).thenReturn(createECOTerms());
        }

        @Test(expected = IllegalArgumentException.class)
        public void openingEmptyGOWillCauseDocumentReaderException() {
            ontologyReader = new OntologyReader(validGO, null);
        }

        @Test(expected = IllegalArgumentException.class)
        public void openingEmptyECOWillCauseDocumentReaderException() {
            ontologyReader = new OntologyReader(null, validECO);
        }

        @Test
        public void opensSuccessfully() {
            ontologyReader = new OntologyReader(validGO, validECO);
            ontologyReader.open(new ExecutionContext());
            assertThat(ontologyReader, is(not(nullValue())));
        }
    }

    public class DocReaderAccessingEmptyOntologies {
        private GeneOntology validGO;
        private EvidenceCodeOntology validECO;
        private GeneOntology emptyGO;
        private EvidenceCodeOntology emptyECO;

        @Before
        public void setUp() {
            validGO = mock(GeneOntology.class);
            when(validGO.getTerms()).thenReturn(createGOTerms());

            validECO = mock(EvidenceCodeOntology.class);
            when(validECO.getTerms()).thenReturn(createECOTerms());

            emptyGO = new OntologyReader.EmptyGeneOntology();
            emptyECO = new OntologyReader.EmptyEvidenceCodeOntology();
        }

        @Test
        public void openingEmptyGoOntologyIsSuccessful() {
            ontologyReader = new OntologyReader(emptyGO, validECO);
            ontologyReader.open(new ExecutionContext());
            assertThat(ontologyReader, is(not(nullValue())));
        }

        @Test
        public void openingEmptyEcoOntologyIsSuccessful() {
            ontologyReader = new OntologyReader(validGO, emptyECO);
            ontologyReader.open(new ExecutionContext());
            assertThat(ontologyReader, is(not(nullValue())));
        }
    }

    private List<GenericTerm> createGOTerms() {
        GOTerm term = new GOTerm();
        term.setId("GO:0000001");
        return Collections.singletonList(term);
    }

    private List<GenericTerm> createECOTerms() {
        ECOTerm term1 = new ECOTerm();
        term1.setId("ECO:0000001");

        ECOTerm term2 = new ECOTerm();
        term2.setId("ECO:0000002");

        return Arrays.asList(term1, term2);
    }
}
