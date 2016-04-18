package uk.ac.ebi.quickgo.rest.controller;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created 18/04/16
 * @author Edd
 */
public class ControllerValidationHelperImplTest {
    private static final int MAX_RESULTS = 10;
    private static final String ID_FORMAT = "id[0-9]+";
    private ControllerValidationHelperImpl controllerValidator;
    private ControllerValidationHelperImpl defaultControllerValidator;

    @Before
    public void setUp() {
        this.defaultControllerValidator = new ControllerValidationHelperImpl();
        this.controllerValidator = new ControllerValidationHelperImpl(
                MAX_RESULTS,
                id -> id.matches(ID_FORMAT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidIdAtStartProducesIllegalArgumentException() {
        controllerValidator.validateCSVIds("wrongFormat");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidIdAfterStartProducesIllegalArgumentException() {
        controllerValidator.validateCSVIds("id1,wrongFormat");
    }

    @Test
    public void oneValidIdIsValidated() {
        assertThat(controllerValidator.validateCSVIds("id1").size(), is(1));
    }

    @Test
    public void twoValidIdsAreValidated() {
        assertThat(controllerValidator.validateCSVIds("id1,id2").size(), is(2));
    }

    @Test
    public void fiveValidIdsAreValidated() {
        assertThat(controllerValidator.validateCSVIds("id1,id2,id55,id100,id10000000").size(), is(5));
    }

    // ensure default id validation works
    @Test
    public void oneValidIdIsValidatedByDefaultInstance() {
        assertThat(defaultControllerValidator.validateCSVIds("id1asdf").size(), is(1));
    }

    @Test
    public void twoValidIdsAreValidatedByDefaultInstance() {
        assertThat(defaultControllerValidator.validateCSVIds("id1sdf,id2gdfgd").size(), is(2));
    }

    // result validation
    @Test(expected = IllegalArgumentException.class)
    public void tooManyResultsIsInvalid() {
        controllerValidator.validateRequestedResults(MAX_RESULTS + 1);
    }

    @Test
    public void maxNumberOfResultsIsValidated() {
        // no exception thrown
        controllerValidator.validateRequestedResults(MAX_RESULTS);
    }

    @Test
    public void permissibleNumberOfResultsIsValidated() {
        // no exception thrown
        controllerValidator.validateRequestedResults(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooManyResultsIsInvalidForDefaultValidator() {
        defaultControllerValidator.validateRequestedResults(ControllerValidationHelperImpl.MAX_PAGE_RESULTS + 1);
    }

    @Test
    public void maxNumberOfResultsIsValidatedForDefaultValidator() {
        // no exception thrown
        defaultControllerValidator.validateRequestedResults(ControllerValidationHelperImpl.MAX_PAGE_RESULTS);
    }

    @Test
    public void permissibleNumberOfResultsIsValidatedForDefaultValidator() {
        // no exception thrown
        defaultControllerValidator.validateRequestedResults(1);
    }

    @Test
    public void createsListFromNullCSV() {
        assertThat(defaultControllerValidator.csvToList(null).size(), is(0));
    }

    @Test
    public void createsListFromCSVForNoItems() {
        assertThat(defaultControllerValidator.csvToList("").size(), is(0));
    }

    @Test
    public void createsListFromCSVForOneItem() {
        assertThat(defaultControllerValidator.csvToList("a").size(), is(1));
    }

    @Test
    public void createsListFromCSVForTwoItems() {
        assertThat(defaultControllerValidator.csvToList("a,b").size(), is(2));
    }
}