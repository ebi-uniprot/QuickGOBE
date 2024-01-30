package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createGODoc;

/**
 * Created 24/11/15
 * @author Edd
 */
class GODocConverterTest {
    private GODocConverter converter;
    private OntologyDocument goDoc;
    private GOTerm goTerm;

    @BeforeEach
    void setup() {
        converter = new GODocConverter();
        goDoc = createGODoc("GO:0000001", "name1");
        goTerm = converter.convert(goDoc);
    }

    @Test
    void validateUsageWithoutError() {
        assertThat(goTerm.usage, is(notNullValue()));
        assertThat(goTerm.usage.getFullName(), is(equalTo(goDoc.usage)));
    }

    @Test
    void convertsAspectWithoutError() {
        assertThat(goTerm.aspect, is(notNullValue()));
        assertThat(goTerm.aspect.getShortName(), is(equalTo(goDoc.aspect)));
    }

    @Test
    void convertsSubsetsWithoutError() {
        assertThat(goTerm.subsets, is(notNullValue()));
        assertThat(goTerm.subsets, is(equalTo(goDoc.subsets)));
    }

    /**
     * Check that a partially populated document can be successfully converted in
     * to a corresponding OBOTerm
     */
    @Test
    void documentWithNullFieldsCanBeConverted() {
        OntologyDocument doc = new OntologyDocument();
        doc.id = "id field";
        doc.usage = "Electronic";
        GOTerm term = converter.convert(doc);
        assertThat(term.id, is("id field"));
        assertThat(term.usage, is(GOTerm.Usage.ELECTRONIC));
        assertThat(term.name, is(nullValue()));
    }

    @Test
    void convertsGoDiscussionsWithoutError() throws Exception {
        assertThat(goTerm.goDiscussions, hasSize(2));
    }
}