package uk.ac.ebi.quickgo.annotation.validation.model;

import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tony Wardell
 * Date: 23/11/2016
 * Time: 10:44
 * Created with IntelliJ IDEA.
 */
class DBXRefEntityTest {

    private DBXRefEntity entity1;

    @BeforeEach
    void setup() throws Exception {

        entity1 = new DBXRefEntity();

        entity1.entityTypeName = "polypeptide region";
        entity1.entityType = "SO:0000839";
        entity1.databaseURL = "http://www.ebi.ac.uk/interpro/entry/[example_id]";
        entity1.idValidationPattern = Pattern.compile("IPR\\d{6}");
        entity1.database = "INTERPRO";
    }

    @Test
    void passes() {
        assertThat(entity1.test("IPR123456"), is(true));
    }

    @Test
    void fails() {
        assertThat(entity1.test("ZZZ123456"), is(false));
    }

    @Test
    void retrieveKeyValueSuccessfully(){
        assertThat(entity1.keyValue(), is(entity1.database));
    }

    @Test
    void ifPatternIsNullAllNonNullValuesAreValid(){
        entity1.idValidationPattern = null;
        assertThat(entity1.test("ZZZ123456"), is(true));
        assertThat(entity1.test(null), is(false));
    }
}
