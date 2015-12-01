package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created 24/11/15
 * @author Edd
 */
public class AbstractOntologyDocConverterTest {
    private GeneralODocConverter converter;

    // basic implementation that makes available protected methods which are to be tested
    private static class GeneralODocConverter extends AbstractODocConverter<OBOTerm> {
        @Override public OBOTerm convert(OntologyDocument ontologyDocument) {
            return null;
        }
    }

    @Before
    public void setup() {
        this.converter = new GeneralODocConverter();
    }

    /**
     * Check that all common OBO fields are converted
     */
    @Test
    public void convertsCommonFieldsWithoutError() {
        OntologyDocument goOntologyDoc = OntologyDocMocker.createGODoc("id1", "name1");
        OBOTerm oboTerm = new OBOTerm();
        converter.addCommonFields(goOntologyDoc, oboTerm);
        assertThat(oboTerm.id, is(equalTo("id1")));
        assertThat(oboTerm.name, is(equalTo("name1")));
        assertThat(oboTerm.ancestors, is(goOntologyDoc.ancestors));
        assertThat(oboTerm.children, is(goOntologyDoc.children));
        assertThat(oboTerm.comment, is(goOntologyDoc.comment));
        assertThat(oboTerm.definition, is(goOntologyDoc.definition));
        assertThat(oboTerm.replacedBy, is(goOntologyDoc.replacedBy));
        assertThat(oboTerm.isObsolete, is(goOntologyDoc.isObsolete));
        assertThat(oboTerm.subsets, is(goOntologyDoc.subsets));
        assertThat(oboTerm.synonyms.size(), is(equalTo(2)));
    }

    /**
     * Check that a partially populated document can be successfully converted in
     * to a corresponding OBOTerm
     */
    @Test
    public void documentWithNullFieldsCanBeConverted() {
        OntologyDocument doc = new OntologyDocument();
        doc.id = "id field";
        doc.ancestors = Arrays.asList("ancestor 0", "ancestor 1");
        OBOTerm term = new OBOTerm();
        converter.addCommonFields(doc, term);
        assertThat(term.id, is("id field"));
        assertThat(term.ancestors, containsInAnyOrder("ancestor 0", "ancestor 1"));
        assertThat(term.name, is(nullValue()));
    }

}