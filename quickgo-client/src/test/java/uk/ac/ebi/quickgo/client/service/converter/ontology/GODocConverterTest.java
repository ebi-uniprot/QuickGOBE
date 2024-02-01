package uk.ac.ebi.quickgo.client.service.converter.ontology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.client.model.ontology.GOTerm;

import uk.ac.ebi.quickgo.common.model.Aspect;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter} class;
 */
class GODocConverterTest {
    private GODocConverter converter;

    @BeforeEach
    void setUp() {
        converter = new GODocConverter();
    }

    @Test
        void ontologyDocumentWithPopulatedAspectIsConvertedIntoGoTermWithPopulatedAspect() {
        String aspect = Aspect.BIOLOGICAL_PROCESS.getScientificName();

        OntologyDocument doc = new OntologyDocument();
        doc.aspect = aspect;

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is(Aspect.BIOLOGICAL_PROCESS.getName()));
    }

    @Test
    void ontologyDocumentWithNullAspectIsConvertedIntoGoTermWithNullAspect() {
        OntologyDocument doc = new OntologyDocument();
        assertThat(doc.aspect, nullValue());

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is(nullValue()));
    }

    @Test
    void ontologyDocumentWithUnknownAspectIsConvertedRegardless() {
        OntologyDocument doc = new OntologyDocument();
        doc.aspect = "Dish_Washing";

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is("Dish_Washing"));
    }
}
