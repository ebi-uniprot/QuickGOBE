package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.common.validator.GeneProductDbXRefIDFormat;
import uk.ac.ebi.quickgo.common.validator.GeneProductDbXRefIDFormats;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author Tony Wardell
 * Date: 29/06/2016
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
public class AnnotationRequestConfig {
    private static final String EXAMPLE_REGEX="([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])" +
            "((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1}";

    public AnnotationRequestConfig() {}

    @Bean
    public GeneProductDbXRefIDFormats geneProductValidator() {

        List<GeneProductDbXRefIDFormat> entities = new ArrayList<>();
        GeneProductDbXRefIDFormat entity1 = new GeneProductDbXRefIDFormat("UniProtKB", "PR:000000001", "protein",
                EXAMPLE_REGEX, "http://www.uniprot.org/uniprot/[example_id]/");
        entities.add(entity1);

        GeneProductDbXRefIDFormats
                dbXrefEntities = GeneProductDbXRefIDFormats.createWithData(entities);

        return dbXrefEntities;
    }


    @Bean
    public Validator validator(){
        return new LocalValidatorFactoryBean();
    }

}
