package uk.ac.ebi.quickgo.repowriter.reader;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
public class ODocReaderTest {

    private ODocReader oDocReader;

    public class DocReaderAccessingValidOntologies {

        @Before
        public void setUp() {
            GeneOntology go = mock(GeneOntology.class);
            EvidenceCodeOntology eco = mock(EvidenceCodeOntology.class);

            when(go.getTerms()).thenReturn(createGOTerms());
            when(eco.getTerms()).thenReturn(createECOTerms());

            Optional<GeneOntology> goOptional = Optional.of(go);
            Optional<EvidenceCodeOntology> ecoOptional = Optional.of(eco);
            oDocReader = new ODocReader(goOptional, ecoOptional);

            oDocReader.open(new ExecutionContext());
        }

        @Test
        public void readsAllDocsWithoutError() throws Exception {
            int docCount = 0;
            while (oDocReader.read() != null) {
                docCount++;
            }
            assertThat(docCount, is(3));
        }

        @Test(expected = DocumentReaderException.class)
        public void extractingEmptyOntologyDocumentThrowsDocumentReadException() throws DocumentReaderException {
            oDocReader.extractOntologyDocument(Optional.empty());
        }

        @Test
        public void extractingNonEmptyOntologyDocumentSucceeds() throws DocumentReaderException {
            OntologyDocument ontologyDocument = oDocReader.extractOntologyDocument(Optional.of(new OntologyDocument()));
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
            oDocReader = new ODocReader(validGOOptional, Optional.empty());
            oDocReader.open(new ExecutionContext());
        }

        @Test(expected = DocumentReaderException.class)
        public void openingEmptyECOWillCauseDocumentReaderException() {
            oDocReader = new ODocReader(Optional.empty(), validECOOptional);
            oDocReader.open(new ExecutionContext());
        }

        @Test
        public void opensSuccessfully() {
            oDocReader = new ODocReader(validGOOptional, validECOOptional);
            oDocReader.open(new ExecutionContext());
            assertThat(oDocReader, is(not(nullValue())));
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
        oDocReader.close();
    }

}