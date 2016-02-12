package uk.ac.ebi.quickgo.client.service.converter.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.GOTerm;
import uk.ac.ebi.quickgo.client.service.converter.ontology.GODocConverter;
import uk.ac.ebi.quickgo.ontology.common.document.Aspect;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

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
    public void ontologyDocumentWithNullAspectIsConvertedIntoGoTermWithNullAspect() throws Exception {
        String aspect = Aspect.BIOLOGICAL_PROCESS.getShortName();

        OntologyDocument doc = new OntologyDocument();
        doc.aspect = aspect;

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is(Aspect.BIOLOGICAL_PROCESS.getName()));
    }

    @Test
    public void ontologyDocumentWithPopulatedAspectIsConvertedIntoGoTermWithPopulatedAspect() throws Exception {
        OntologyDocument doc = new OntologyDocument();

        GOTerm term = converter.convert(doc);

        assertThat(term.aspect, is(nullValue()));
    }
}
