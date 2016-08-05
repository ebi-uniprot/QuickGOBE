package uk.ac.ebi.quickgo.ontology.controller.validation;

import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.common.converter.HelpfulConverter.toCSV;
import static uk.ac.ebi.quickgo.ontology.model.OntologyRelationType.DEFAULT_TRAVERSAL_TYPES;

/**
 * Created 25/05/16
 * @author Edd
 */
public class OBOControllerValidationHelperImplTest {

    private static final Predicate<String> FAKE_ID_VALIDATION_PREDICATE = id -> true;
    private static final int FAKE_MAX_PAGE_RESULTS = 100;
    private OBOControllerValidationHelperImpl validator;
    private ArrayList<OntologyRelationType> invalidRelationships;

    @Before
    public void setUp() {
        this.validator = new OBOControllerValidationHelperImpl(FAKE_MAX_PAGE_RESULTS, FAKE_ID_VALIDATION_PREDICATE);

        invalidRelationships = new ArrayList<>(asList(OntologyRelationType.values()));
        invalidRelationships.removeAll(DEFAULT_TRAVERSAL_TYPES);
    }

    @Test
    public void checkValidTraversalRelationshipTypes() {
        for (OntologyRelationType validRelationship : DEFAULT_TRAVERSAL_TYPES) {
            validator.checkValidTraversalRelationType(validRelationship);
        }
    }

    @Test
    public void checkInvalidTraversalRelationshipTypes() {
        List<IllegalArgumentException> exceptions = new ArrayList<>();
        for (OntologyRelationType relationship : OntologyRelationType.values()) {
            try {
                validator.checkValidTraversalRelationType(relationship);
            } catch (IllegalArgumentException exception) {
                exceptions.add(exception);
            }
        }
        assertThat(exceptions.size(), is(invalidRelationships.size()));
    }

    @Test
    public void checkValidationWorksFor1ValidRelation() {
        OntologyRelationType validRelation = OntologyRelationType.DEFAULT_TRAVERSAL_TYPES.get(0);
        List<OntologyRelationType> validRelations = validator.validateRelationTypes(validRelation.getLongName());
        assertThat(validRelations, contains(validRelation));
    }

    @Test
    public void checkValidationWorksFor2ValidRelations() {
        OntologyRelationType validRelation0 = OntologyRelationType.DEFAULT_TRAVERSAL_TYPES.get(0);
        OntologyRelationType validRelation1 = OntologyRelationType.DEFAULT_TRAVERSAL_TYPES.get(1);

//        List<OntologyRelationType> validRelations = validator.validateRelationTypes(
//                relationsToCSV(validRelation0, validRelation1));
        List<OntologyRelationType> validRelations = validator.validateRelationTypes(toCSV
                        (validRelation0.getLongName(), validRelation1.getLongName()));

        assertThat(validRelations, containsInAnyOrder(validRelation0, validRelation1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidationWorksFor1InvalidRelation() {
        validator.validateRelationTypes(invalidRelationships.get(0).getLongName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidationWorksFor2InvalidRelations() {
        validator.validateRelationTypes(
                toCSV(invalidRelationships.get(0).getLongName(), invalidRelationships.get(1)
                        .getLongName()));
    }

}
