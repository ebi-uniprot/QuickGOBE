package uk.ac.ebi.quickgo.geneproduct.model;

import uk.ac.ebi.quickgo.geneproduct.GeneProductREST;
import uk.ac.ebi.quickgo.geneproduct.service.ServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * @author Tony Wardell
 * Date: 02/06/2016
 * Time: 09:59
 * Created with IntelliJ IDEA.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GeneProductREST.class})
//@WebAppConfiguration
public class GeneProductDbXRefIDFormatsIT {

    //private GeneProductDbXRefIDFormats gpFormats;
    //ServiceConfig serviceConfig = new ServiceConfig();

    @Autowired
    private ControllerValidationHelper validator;

    @Before
    public void setup(){
//        serviceConfig.xrefValidationRegexFile = "src/test/resources/DB_XREFS_ENTITIES.dat.gz";
//        //gpFormats = GeneProductDbXRefIDFormats.createWithData()
//        validator = serviceConfig.geneProductValidator();
    }

    @Test
    public void successfullyValidateRNACentralID(){

       assertThat(validator.validateCSVIds("EBI-11166735"),contains("EBI-11166735"));


    }
}
