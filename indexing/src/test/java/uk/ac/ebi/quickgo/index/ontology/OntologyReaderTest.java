package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.model.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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

    public class DocReaderAccessingValidOntologies {

        @Before
        public void setUp() {
            GeneOntology go = mock(GeneOntology.class);
            EvidenceCodeOntology eco = mock(EvidenceCodeOntology.class);

            when(go.getTerms()).thenReturn(createGOTerms());
            when(eco.getTerms()).thenReturn(createECOTerms());

            // set document count
            docCount += go.getTerms().size() + eco.getTerms().size();

            Optional<GeneOntology> goOptional = Optional.of(go);
            Optional<EvidenceCodeOntology> ecoOptional = Optional.of(eco);
            ontologyReader = new OntologyReader(goOptional, ecoOptional);

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

        @Test(expected = DocumentReaderException.class)
        public void extractingEmptyOntologyDocumentThrowsDocumentReadException() throws DocumentReaderException {
            ontologyReader.extractOntologyDocument(Optional.empty());
        }

        @Test
        public void extractingNonEmptyOntologyDocumentSucceeds() throws DocumentReaderException {
            OntologyDocument ontologyDocument = ontologyReader.extractOntologyDocument(Optional.of(new OntologyDocument()));
            assertThat(ontologyDocument, is(not(nullValue())));
        }

    }

    public class DocReaderAccessingInvalidOntologies {
        private Optional<GeneOntology> validGOOptional;
        private Optional<EvidenceCodeOntology> validECOOptional;

        @Before
        public void setUp() {
            GeneOntology go = mock(GeneOntology.class);
            when(go.getTerms()).thenReturn(createGOTerms());

            EvidenceCodeOntology eco = mock(EvidenceCodeOntology.class);
            when(eco.getTerms()).thenReturn(createECOTerms());

            validGOOptional = Optional.of(go);
            validECOOptional = Optional.of(eco);
        }

        @Test(expected = DocumentReaderException.class)
        public void openingEmptyGOWillCauseDocumentReaderException() {
            ontologyReader = new OntologyReader(validGOOptional, Optional.empty());
            ontologyReader.open(new ExecutionContext());
        }

        @Test(expected = DocumentReaderException.class)
        public void openingEmptyECOWillCauseDocumentReaderException() {
            ontologyReader = new OntologyReader(Optional.empty(), validECOOptional);
            ontologyReader.open(new ExecutionContext());
        }

        @Test
        public void opensSuccessfully() {
            ontologyReader = new OntologyReader(validGOOptional, validECOOptional);
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

    @After
    public void after() {
        ontologyReader.close();
    }

}