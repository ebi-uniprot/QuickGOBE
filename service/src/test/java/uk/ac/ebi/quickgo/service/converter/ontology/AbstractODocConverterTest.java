package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

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
public class AbstractODocConverterTest {
    private MockODocConverter converter;
    private OntologyDocument validGODoc;
    private OBOTerm oboTermFromValidGODoc;

    // basic implementation that makes available protected methods which are to be tested
    private static class MockODocConverter extends AbstractODocConverter<OBOTerm> {
        @Override public OBOTerm convert(OntologyDocument ontologyDocument) {
            return null;
        }
    }

    @Before
    public void setup() {
        this.converter = new MockODocConverter();
        this.validGODoc = OntologyDocMocker.createGODoc("id1", "name1");
        this.oboTermFromValidGODoc = new OBOTerm();
        converter.addCommonFields(validGODoc, oboTermFromValidGODoc);
    }

    /**
     * ------------- Check common fields are common OBO fields are converted -------------
     */
    @Test
    public void convertsIdWithoutError() {
        assertThat(oboTermFromValidGODoc.id, is(equalTo("id1")));
    }

    @Test
    public void convertsNameWithoutError() {
        assertThat(oboTermFromValidGODoc.name, is(equalTo("name1")));
    }

    @Test
    public void convertsAncestorsWithoutError() {
        assertThat(oboTermFromValidGODoc.ancestors, is(validGODoc.ancestors));
    }

    @Test
    public void convertsChildrenWithoutError() {
        assertThat(oboTermFromValidGODoc.children, is(validGODoc.children));
    }

    @Test
    public void convertsCommentnWithoutError() {
        assertThat(oboTermFromValidGODoc.comment, is(validGODoc.comment));
    }

    @Test
    public void convertsDefinitionWithoutError() {
        assertThat(oboTermFromValidGODoc.definition, is(validGODoc.definition));
    }

    @Test
    public void convertsReplacedByWithoutError() {
        assertThat(oboTermFromValidGODoc.replacedBy, is(validGODoc.replacedBy));
    }

    @Test
    public void convertsIsObsoleteWithoutError() {
        assertThat(oboTermFromValidGODoc.isObsolete, is(validGODoc.isObsolete));
    }

    @Test
    public void convertsSubsetsWithoutError() {
        assertThat(oboTermFromValidGODoc.subsets, is(validGODoc.subsets));
    }

    @Test
    public void convertsSynonymsWithoutError() {
        assertThat(oboTermFromValidGODoc.synonyms.size(), is(equalTo(2)));
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