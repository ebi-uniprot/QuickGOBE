package uk.ac.ebi.quickgo.rest.controller.ontology;

import uk.ac.ebi.quickgo.rest.controller.search.OntologyField;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 19/01/16
 * @author Edd
 */
public class OntologyFieldTest {
    private static OntologyField ontologyQueryableField;

    @BeforeClass
    public static void setUpClass() {
        ontologyQueryableField = new OntologyField();
    }

    @Test
    public void allSearchableFieldsAreSearchable() {
        for (OntologyField.Search field : OntologyField.Search.values()) {
            assertThat(ontologyQueryableField.isSearchable(field.name()), is(true));
        }
    }

    @Test
    public void unknownFieldsAreNotSearchable() {
        assertThat(ontologyQueryableField.isSearchable("barbie"), is(false));
    }
}