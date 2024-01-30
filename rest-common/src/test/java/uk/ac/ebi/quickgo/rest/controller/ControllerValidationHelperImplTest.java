package uk.ac.ebi.quickgo.rest.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.rest.ParameterException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 18/04/16
 * @author Edd
 */
class ControllerValidationHelperImplTest {
    private static final int MAX_RESULTS = 10;
    private static final String ID_FORMAT = "id[0-9]+";
    private ControllerValidationHelperImpl controllerValidator;
    private ControllerValidationHelperImpl defaultControllerValidator;

    @BeforeEach
    void setUp() {
        this.defaultControllerValidator = new ControllerValidationHelperImpl();
        this.controllerValidator = new ControllerValidationHelperImpl(
                MAX_RESULTS,
                id -> id.matches(ID_FORMAT));
    }

    @Test
    void invalidIdAtStartProducesIllegalArgumentException() {
        assertThrows(ParameterException.class, () -> controllerValidator.validateCSVIds("wrongFormat"));
    }

    @Test
    void invalidIdAfterStartProducesIllegalArgumentException() {
        assertThrows(ParameterException.class, () -> controllerValidator.validateCSVIds("id1,wrongFormat"));
    }

    @Test
    void oneValidIdIsValidated() {
        assertThat(controllerValidator.validateCSVIds("id1").size(), is(1));
    }

    @Test
    void twoValidIdsAreValidated() {
        assertThat(controllerValidator.validateCSVIds("id1,id2").size(), is(2));
    }

    @Test
    void fiveValidIdsAreValidated() {
        assertThat(controllerValidator.validateCSVIds("id1,id2,id55,id100,id10000000").size(), is(5));
    }

    // ensure default id validation works
    @Test
    void oneValidIdIsValidatedByDefaultInstance() {
        assertThat(defaultControllerValidator.validateCSVIds("id1asdf").size(), is(1));
    }

    @Test
    void twoValidIdsAreValidatedByDefaultInstance() {
        assertThat(defaultControllerValidator.validateCSVIds("id1sdf,id2gdfgd").size(), is(2));
    }

    // result validation
    @Test
    void tooManyResultsIsInvalid() {
        assertThrows(ParameterException.class, () -> controllerValidator.validateRequestedResults(MAX_RESULTS + 1));
    }

    @Test
    void maxNumberOfResultsIsValidated() {
        // no exception thrown
        controllerValidator.validateRequestedResults(MAX_RESULTS);
    }

    @Test
    void permissibleNumberOfResultsIsValidated() {
        // no exception thrown
        controllerValidator.validateRequestedResults(1);
    }

    @Test
    void tooManyResultsIsInvalidForDefaultValidator() {
        assertThrows(ParameterException.class, () -> defaultControllerValidator.validateRequestedResults(ControllerValidationHelperImpl.MAX_PAGE_RESULTS + 1));
    }

    @Test
    void maxNumberOfResultsIsValidatedForDefaultValidator() {
        // no exception thrown
        defaultControllerValidator.validateRequestedResults(ControllerValidationHelperImpl.MAX_PAGE_RESULTS);
    }

    @Test
    void permissibleNumberOfResultsIsValidatedForDefaultValidator() {
        // no exception thrown
        defaultControllerValidator.validateRequestedResults(1);
    }

    @Test
    void createsListFromNullCSV() {
        assertThat(defaultControllerValidator.csvToList(null).size(), is(0));
    }

    @Test
    void createsListFromCSVForNoItems() {
        assertThat(defaultControllerValidator.csvToList("").size(), is(0));
    }

    @Test
    void createsListFromCSVForOneItem() {
        assertThat(defaultControllerValidator.csvToList("a").size(), is(1));
    }

    @Test
    void createsListFromCSVForTwoItems() {
        assertThat(defaultControllerValidator.csvToList("a,b").size(), is(2));
    }
}