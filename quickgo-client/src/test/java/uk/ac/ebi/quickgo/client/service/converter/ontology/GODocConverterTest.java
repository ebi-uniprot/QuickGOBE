package uk.ac.ebi.quickgo.client.service.converter.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.GOTerm;

import uk.ac.ebi.quickgo.common.model.Aspect;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter} class;
 */
public class GODocConverterTest {
    private GODocConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new GODocConverter();
    }

    @Test
        public void ontologyDocumentWithPopulatedAspectIsConvertedIntoGoTermWithPopulatedAspect() throws Exception {
        String aspect = Aspect.BIOLOGICAL_PROCESS.getScientificName();

        OntologyDocument doc = new OntologyDocument();
        doc.aspect = aspect;

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is(Aspect.BIOLOGICAL_PROCESS.getName()));
    }

    @Test
    public void ontologyDocumentWithNullAspectIsConvertedIntoGoTermWithNullAspect() throws Exception {
        OntologyDocument doc = new OntologyDocument();
        assertThat(doc.aspect, nullValue());

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is(nullValue()));
    }

    @Test
    public void ontologyDocumentWithUnknownAspectIsConvertedRegardless() throws Exception {
        OntologyDocument doc = new OntologyDocument();
        doc.aspect = "Dish_Washing";

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is("Dish_Washing"));
    }
}
