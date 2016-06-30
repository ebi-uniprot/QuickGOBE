package uk.ac.ebi.quickgo.client.service.converter.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link uk.ac.ebi.quickgo.client.service.converter.ontology.AbstractDocConverter} class
 */
public class AbstractDocConverterTest {
    private AbstractDocConverter<FakeOntologyTerm> converter;

    @Before
    public void setUp() throws Exception {
        converter = new FakeDocConverter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOntologyDocumentThrowsException() throws Exception {
        converter.convert(null);
    }

    @Test
    public void ontologyDocumentWithNullFieldsIsConvertedToOntologyTermWithNullFields() throws Exception {
        OntologyDocument emptyDoc = new OntologyDocument();

        FakeOntologyTerm term = converter.convert(emptyDoc);

        checkCommonFields(term, null, null, false);
    }

    @Test
    public void ontologyDocumentWithPopulatedFieldsIsConvertedToOntologyTermWithPopulatedFields() throws Exception {
        String id = "id";
        String name = "name";
        boolean isObsolete = true;

        OntologyDocument doc = new OntologyDocument();
        doc.id = id;
        doc.name = name;
        doc.isObsolete = isObsolete;

        FakeOntologyTerm term = converter.convert(doc);

        checkCommonFields(term, id, name, isObsolete);
    }

    private void checkCommonFields(OntologyTerm term, String id, String name, boolean isObsolete) {
        assertThat(term.id, is(id));
        assertThat(term.name, is(name));
        assertThat(term.isObsolete, is(isObsolete));
    }

    private static class FakeDocConverter extends AbstractDocConverter<FakeOntologyTerm> {
        @Override protected FakeOntologyTerm createTerm() {
            return new FakeOntologyTerm();
        }

        @Override protected void addOntologySpecificFields(OntologyDocument doc, FakeOntologyTerm term) {
            //does nothing
        }
    }

    private static class FakeOntologyTerm extends OntologyTerm {}
}