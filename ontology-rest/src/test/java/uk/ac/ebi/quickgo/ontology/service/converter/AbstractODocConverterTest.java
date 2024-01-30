package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createGODoc;

/**
 * Created 24/11/15
 * @author Edd
 */
class AbstractODocConverterTest {
    private MockODocConverter converter;
    private OntologyDocument validGODoc;
    private OBOTerm oboTermFromValidGODoc;

    // basic implementation that makes available protected methods which are to be tested
    private static class MockODocConverter extends AbstractODocConverter<OBOTerm> {
        @Override public OBOTerm convert(OntologyDocument ontologyDocument) {
            return null;
        }
    }

    @BeforeEach
    void setup() {
        this.converter = new MockODocConverter();
        this.validGODoc = createGODoc("id1", "name1");
        this.oboTermFromValidGODoc = new OBOTerm();
        converter.addCommonFields(validGODoc, oboTermFromValidGODoc);
    }

    /*
     * Check common OBO fields are converted, which are not covered by other *Converter test
     * classes
     */
    @Test
    void convertsIdWithoutError() {
        assertThat(oboTermFromValidGODoc.id, is("id1"));
    }

    @Test
    void convertsNameWithoutError() {
        assertThat(oboTermFromValidGODoc.name, is("name1"));
    }

    @Test
    void convertsSecondaryIdsWithoutError() {
        assertThat(oboTermFromValidGODoc.secondaryIds, is(validGODoc.secondaryIds));
    }

    @Test
    void convertsCommentWithoutError() {
        assertThat(oboTermFromValidGODoc.comment, is(validGODoc.comment));
    }

    @Test
    void convertsReplacements() throws Exception {
        assertThat(oboTermFromValidGODoc.replacements, hasSize(validGODoc.replacements.size()));
    }

    @Test
    void convertsReplacedBy() throws Exception {
        assertThat(oboTermFromValidGODoc.replaces, hasSize(validGODoc.replaces.size()));
    }

    @Test
    void convertsIsObsoleteWithoutError() {
        assertThat(oboTermFromValidGODoc.isObsolete, is(validGODoc.isObsolete));
    }

    @Test
    void convertsSubsetsWithoutError() {
        assertThat(oboTermFromValidGODoc.subsets, is(validGODoc.subsets));
    }

    @Test
    void convertsSynonymsWithoutError() {
        assertThat(oboTermFromValidGODoc.synonyms.size(), is(validGODoc.synonyms.size()));
    }

    @Test
    void convertsCreditsWithoutError() {
        assertThat(oboTermFromValidGODoc.credits.size(), is(validGODoc.credits.size()));
    }

    /**
     * Check that a partially populated document can be successfully converted in
     * to a corresponding OBOTerm
     */
    @Test
    void documentWithNullFieldsCanBeConverted() {
        OntologyDocument doc = new OntologyDocument();
        doc.id = "id field";
        OBOTerm term = new OBOTerm();
        converter.addCommonFields(doc, term);
        assertThat(term.id, is("id field"));
        assertThat(term.name, is(nullValue()));
    }
}