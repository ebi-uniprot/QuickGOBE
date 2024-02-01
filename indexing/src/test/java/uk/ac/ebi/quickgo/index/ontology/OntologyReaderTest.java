package uk.ac.ebi.quickgo.index.ontology;

import uk.ac.ebi.quickgo.model.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.model.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created 11/01/16
 * @author Edd
 */
class OntologyReaderTest {

    private OntologyReader ontologyReader;
    private int docCount;

    @AfterEach
    void after() {
        if (ontologyReader != null) {
            ontologyReader.close();
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

    @Nested
    class DocReaderAccessingValidOntologies {

        @BeforeEach
        void setUp() {
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
        void readsAllDocsWithoutError() {
            int count = 0;
            while (ontologyReader.read() != null) {
                count++;
            }
            assertThat(count, is(docCount));
        }
    }

    @Nested
    class DocReaderAccessingInvalidOntologies {
        private GeneOntology validGO;
        private EvidenceCodeOntology validECO;

        @BeforeEach
        void setUp() {
            validGO = mock(GeneOntology.class);
            when(validGO.getTerms()).thenReturn(createGOTerms());

            validECO = mock(EvidenceCodeOntology.class);
            when(validECO.getTerms()).thenReturn(createECOTerms());
        }

        @Test
        void openingEmptyGOWillCauseDocumentReaderException() {
            assertThrows(IllegalArgumentException.class, () -> ontologyReader = new OntologyReader(validGO, null));
        }

        @Test
        void openingEmptyECOWillCauseDocumentReaderException() {
            assertThrows(IllegalArgumentException.class, () -> ontologyReader = new OntologyReader(null, validECO));
        }

        @Test
        void opensSuccessfully() {
            ontologyReader = new OntologyReader(validGO, validECO);
            ontologyReader.open(new ExecutionContext());
            assertThat(ontologyReader, is(not(nullValue())));
        }
    }

    @Nested
    class DocReaderAccessingEmptyOntologies {
        private GeneOntology validGO;
        private EvidenceCodeOntology validECO;
        private GeneOntology emptyGO;
        private EvidenceCodeOntology emptyECO;

        @BeforeEach
        void setUp() {
            validGO = mock(GeneOntology.class);
            when(validGO.getTerms()).thenReturn(createGOTerms());

            validECO = mock(EvidenceCodeOntology.class);
            when(validECO.getTerms()).thenReturn(createECOTerms());

            emptyGO = new OntologyReader.EmptyGeneOntology();
            emptyECO = new OntologyReader.EmptyEvidenceCodeOntology();
        }

        @Test
        void openingEmptyGoOntologyIsSuccessful() {
            ontologyReader = new OntologyReader(emptyGO, validECO);
            ontologyReader.open(new ExecutionContext());
            assertThat(ontologyReader, is(not(nullValue())));
        }

        @Test
        void openingEmptyEcoOntologyIsSuccessful() {
            ontologyReader = new OntologyReader(validGO, emptyECO);
            ontologyReader.open(new ExecutionContext());
            assertThat(ontologyReader, is(not(nullValue())));
        }
    }
}
