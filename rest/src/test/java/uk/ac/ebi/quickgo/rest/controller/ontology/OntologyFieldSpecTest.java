package uk.ac.ebi.quickgo.rest.controller.ontology;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyFields;
import uk.ac.ebi.quickgo.rest.controller.search.OntologyFieldSpec;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 19/01/16
 * @author Edd
 */
public class OntologyFieldSpecTest {
    private static OntologyFieldSpec ontologyFieldSpec;

    @BeforeClass
    public static void setUpClass() {
        ontologyFieldSpec = new OntologyFieldSpec();
    }

    @Test
    public void allSearchableFieldsAreSearchable() {
        for (String field : OntologyFields.Searchable.VALUES) {
            assertThat(ontologyFieldSpec.isSearchable(field), is(true));
        }
    }

    @Test
    public void unknownFieldsAreNotSearchable() {
        assertThat(ontologyFieldSpec.isSearchable("barbie"), is(false));
    }
}