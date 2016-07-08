package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.common.validator.DbXRefEntity;
import uk.ac.ebi.quickgo.common.validator.EntityValidation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;

import javax.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configuration class mimics {@link uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig} used for testing.
 *
 * @author Tony Wardell
 * Date: 29/06/2016
 * Time: 14:24
 * Created with IntelliJ IDEA.
 */
public class AnnotationRequestConfig {
    private static final String UNIPROTKB_GENE_PRODUCT_ID_VALIDATING_REGEX="([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]" +
            "([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])" +
            "((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1}";

    public AnnotationRequestConfig() {}

    /**
     * Create test validator
     * @return A hardcoded example of a GeneProductDbXRefIDFormats containing a validating regular expression for
     * UniProtKB.
     */
    @Bean
    public EntityValidation geneProductValidator() {

        List<DbXRefEntity> entities = new ArrayList<>();
        DbXRefEntity entity1 = new DbXRefEntity("UniProtKB", "PR:000000001", "protein",
                UNIPROTKB_GENE_PRODUCT_ID_VALIDATING_REGEX, "http://www.uniprot.org/uniprot/[example_id]/");
        entities.add(entity1);

        return EntityValidation.createWithData(entities);
    }

    /**
     *
     * @return instance used to run validation against the validated class.
     */
    @Bean
    public Validator validator(){
        return new LocalValidatorFactoryBean();
    }

}
