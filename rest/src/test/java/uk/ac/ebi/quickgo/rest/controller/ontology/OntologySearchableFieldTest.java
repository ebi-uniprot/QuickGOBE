package uk.ac.ebi.quickgo.rest.controller.ontology;

import uk.ac.ebi.quickgo.rest.controller.search.OntologySearchableField;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 19/01/16
 * @author Edd
 */
public class OntologySearchableFieldTest {
    private static OntologySearchableField ontologyQueryableField;

    @BeforeClass
    public static void setUpClass() {
        ontologyQueryableField = new OntologySearchableField();
    }

    @Test
    public void allSearchableFieldsAreSearchable() {
        for (OntologySearchableField.Search field : OntologySearchableField.Search.values()) {
            assertThat(ontologyQueryableField.isSearchable(field.name()), is(true));
        }
    }

    @Test
    public void unknownFieldsAreNotSearchable() {
        assertThat(ontologyQueryableField.isSearchable("barbie"), is(false));
    }
}