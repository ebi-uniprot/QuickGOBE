package uk.ac.ebi.quickgo.annotation.validation.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tony Wardell
 * Date: 24/11/2016
 * Time: 10:51
 * Created with IntelliJ IDEA.
 */

@SpringBootTest
class ValidationEntityCheckerIT {

    @Autowired
    private ValidationEntityChecker validator;

    @Test
    void validationSucceedsIfKnownDb(){
        assertThat(validator.isValid("PMID:123456"), is(true));
        assertThat(validator.isValid("IntAct:EBI-10043081"), is(true));
        assertThat(validator.isValid("ComplexPortal:CPX-101"), is(true));
    }
}
