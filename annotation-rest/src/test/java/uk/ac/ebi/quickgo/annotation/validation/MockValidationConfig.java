package uk.ac.ebi.quickgo.annotation.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Configuration to allow integration testing of validation.
 *
 * @author Tony Wardell
 * Date: 22/11/2016
 * Time: 09:44
 * Created with IntelliJ IDEA.
 */
@Configuration
public class MockValidationConfig {

    public static final String ID_SUCCEEDS = "PMID:123456";
    public static final String ID_FAILS = "xxx";

    @Bean
    public DBXRefEntityValidation dBXRefEntityValidation(){
        DBXRefEntityValidation dbxRefEntityValidation = mock(DBXRefEntityValidation.class);

        when(dbxRefEntityValidation.isValid(ID_SUCCEEDS)).thenReturn(true);
        when(dbxRefEntityValidation.isValid(ID_FAILS)).thenReturn(false);
        return dbxRefEntityValidation;
    }

    @Bean
    public ReferenceValuesValidation referenceValidator(){
        return new ReferenceValuesValidation();
    }

    @Bean
    public WithFromValuesValidation withFromValidator(){
        return new WithFromValuesValidation();
    }
}
