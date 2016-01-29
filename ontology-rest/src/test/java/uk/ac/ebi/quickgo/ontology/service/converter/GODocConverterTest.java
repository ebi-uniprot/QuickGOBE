package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createGODoc;

/**
 * Created 24/11/15
 * @author Edd
 */
public class GODocConverterTest {
    private GODocConverter converter;

    @Before
    public void setup() {
        converter = new GODocConverter();
    }

    @Test
    public void validateGOOnlyFields() {
        OntologyDocument goDoc = createGODoc("GO:0000001", "name1");
        GOTerm goTerm = converter.convert(goDoc);
        assertThat(goTerm.usage, is(notNullValue()));
        assertThat(goTerm.usage.getFullName(), is(equalTo(goDoc.usage)));
        assertThat(goTerm.aspect, is(notNullValue()));
        assertThat(goTerm.aspect.getShortName(), is(equalTo(goDoc.aspect)));
    }
}