package uk.ac.ebi.quickgo.document;

import java.util.List;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.newFlatField;

/**
 * Created 25/11/15
 * @author Edd
 */
public class FlatFieldBuilderTest {
    /**
     * Check you can create a flattened field object programmatically,
     * and that the resulting fields are as expected.
     */
    @Test
    public void createFlatFieldProgrammatically() {
        List<String> fields = newFlatField()
                .addField("first field")
                .addField("second field")
                .getFields();

        assertThat(fields.size(), is(2));
        assertThat(fields.get(0), is("first field"));
        assertThat(fields.get(1), is("second field"));
    }

    /**
     * Create a flattened field object programmatically, then store its
     * string representation. Check that this representation can be used
     * to create another flattened field object, whose fields are as expected.
     *
     * i.e., do a round-trip test.
     */
    @Test
    public void createFlatFieldFromString() {
        // the flattened field as a single string
        String flattenedField = newFlatField()
                .addField("first field")
                .addField("second field")
                .buildString();

        // use it to create the field list
        List<String> fields = newFlatField(flattenedField).getFields();

        // .. and check it contains what it should
        assertThat(fields.size(), is(2));
        assertThat(fields.get(0), is("first field"));
        assertThat(fields.get(1), is("second field"));
    }

}