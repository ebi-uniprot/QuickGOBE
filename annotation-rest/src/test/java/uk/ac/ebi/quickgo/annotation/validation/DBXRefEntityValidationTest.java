package uk.ac.ebi.quickgo.annotation.validation;

import uk.ac.ebi.quickgo.annotation.validation.model.DBXRefEntity;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Tony Wardell
 * Date: 09/11/2016
 * Time: 17:27
 * Created with IntelliJ IDEA.
 */
public class DBXRefEntityValidationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private DBXRefEntityValidation validation;
    private DBXRefEntityValidation.DBXRefEntityAggregator aggregator;

    @Before
    public void setup() throws Exception {

        validation = new DBXRefEntityValidation();
        aggregator = new DBXRefEntityValidation.DBXRefEntityAggregator();

        DBXRefEntity entity1 = new DBXRefEntity();
        DBXRefEntity entity2 = new DBXRefEntity();
        DBXRefEntity entity3 = new DBXRefEntity();

        entity1.entityTypeName = "polypeptide region";
        entity1.entityType = "SO:0000839";
        entity1.databaseURL = "http://www.ebi.ac.uk/interpro/entry/[example_id]";
        entity1.idValidationPattern = Pattern.compile("IPR\\d{6}");
        entity1.database = "INTERPRO";

        entity2.entityTypeName = "polypeptide region";
        entity2.entityType = "SO:0000839";
        entity2.databaseURL = "http://www.ebi.ac.uk/interpro/entry/[example_id]";
        entity2.idValidationPattern = Pattern.compile("ZZZ\\d{6}");
        entity2.database = "INTERPRO";

        entity3.entityTypeName = "entity";
        entity3.entityType = "BET:0000000";
        entity3.databaseURL = "http://www.uniprot.org/unirule/[example_id]";
        entity3.idValidationPattern = Pattern.compile("UR[0-9]{9}");
        entity3.database = "UniRule";

        List<DBXRefEntity> entities;
        entities = Arrays.asList(entity1, entity2, entity3);
        aggregator.write(entities);
    }

    @Test
    public void idWithDatabaseIsValidatedSuccessfully() {
        String[] idList = {"InterPro:IPR123456"};
        assertThat(validation.isValid(idList, null), is(true));
    }

    @Test
    public void idWithoutDatabaseIsValidatedSuccessfully() {
        String[] idList = {"xxx"};
        assertThat(validation.isValid(idList, null), is(true));
    }

    @Test
    public void idWithKnownDatabaseButNoIdPartIsNotVerified() {
        String[] idList = {"InterPro:"};
        assertThat(validation.isValid(idList, null), is(false));
    }

    @Test
    public void idWithUnknownDatabaseButNoIdPartIsNotVerified() {
        String[] idList = {"Dell:"};
        assertThat(validation.isValid(idList, null), is(false));
    }

    @Test
    public void idWithUnknownDatabaseAndIdPartIsNotVerified() {
        String[] idList = {"Dell:12345"};
        assertThat(validation.isValid(idList, null), is(false));
    }

    @Test
    public void idContainsOnlyColonIsNotVerified() {
        String[] idList = {"Dell:12345"};
        assertThat(validation.isValid(idList, null), is(false));
    }

    @Test
    public void multipleIdsWithOneInvalidIsNotVerified() {
        String[] idList = {"InterPro:IPR123456", "InterPro:YYY123456", "InterPro:IPR123456"};
        assertThat(validation.isValid(idList, null), is(false));
    }

    @Test
    public void verificationUsingFlattenedDatabaseNamesIsSuccessful() {
        String[] idList = {"InterPro:IPR123456", "InterPro:ZZZ123456", "InterPro:IPR123456"};
        assertThat(validation.isValid(idList, null), is(true));
    }

    @Test
    public void verificationUsingMixtureOfDatabaseNamesIsSuccessful() {
        String[] idList = {"InterPro:IPR123456", "UniRule:UR123456789", "InterPro:IPR123456"};
        assertThat(validation.isValid(idList, null), is(true));
    }

    @Test
    public void nullListOfIdsToBeValidationThrowsException() {
        assertThat(validation.isValid(null, null), is(true));
}

    @Test
    public void nullListOfEntitiesWrittenToDBXRefEntityValidationCannotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        aggregator.write(null);
    }

    @Test
    public void writeEntitiesIsSuccessfulEvenIfContainsNull() {
        aggregator.write(Arrays.asList(new DBXRefEntity(), null, new DBXRefEntity()));
    }
}
