package uk.ac.ebi.quickgo.ontology.traversal.read;

import uk.ac.ebi.quickgo.ontology.traversal.OntologyRelations;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * The purpose of this class is to validate that each {@link OntologyRelationship}
 * contains valid relationship information.
 *
 * Created 18/05/16
 * @author Edd
 */
class OntologyRelationshipValidator
        implements ItemProcessor<OntologyRelationship, OntologyRelationship> {

    private static final Logger LOGGER = getLogger(OntologyRelationshipValidator.class);

    private enum OntologyNameSpace {
        ECO, GO;

        private static final Map<String, OntologyNameSpace> nameToValueMap = new HashMap<>();

        static {
            for (OntologyNameSpace value : OntologyNameSpace.values()) {
                nameToValueMap.put(value.name(), value);
            }
        }

        public static OntologyNameSpace getName(String shortName) {
            if (nameToValueMap.containsKey(shortName)) {
                return nameToValueMap.get(shortName);
            } else {
                String errorMessage = "Unknown OntologyRelation: " + shortName;
                LOGGER.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    private static final Pattern NAME_SPACE_PATTERN = Pattern.compile("(.*):.*");

    @Override public OntologyRelationship process(OntologyRelationship ontologyRelationship)
            throws Exception {

        checkValidNameSpaces(ontologyRelationship);
        checkValidRelationship(ontologyRelationship.relationship);

        return ontologyRelationship;
    }

    void checkValidRelationship(String relationship) {
        try {
            OntologyRelations.getByShortName(relationship);
        } catch (IllegalArgumentException ie) {
            String errorMessage = "Could not find ontology relationship: " + relationship;
            LOGGER.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

    }

    void checkValidNameSpaces(OntologyRelationship ontologyRelationship) {
        String childNameSpace = nameSpaceOf(ontologyRelationship.child);
        String parentNameSpace = nameSpaceOf(ontologyRelationship.parent);
        if (!childNameSpace.equals(parentNameSpace)) {
            String errorMessage = "Ontology edge must traverse nodes from the same namespace: " +
                    ontologyRelationship;
            LOGGER.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        try {
            OntologyNameSpace.getName(childNameSpace);
            OntologyNameSpace.getName(parentNameSpace);
        } catch (IllegalArgumentException ie) {
            String errorMessage = "Could not find ontology namespace: " + childNameSpace;
            LOGGER.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    String nameSpaceOf(String term) {
        Matcher nameSpaceMatcher = NAME_SPACE_PATTERN.matcher(term);

        if (nameSpaceMatcher.matches()) {
            return nameSpaceMatcher.group(1);
        }

        String errorMessage = "Could not retrieve namespace for term: " + term;
        LOGGER.error(errorMessage);
        throw new ValidationException(errorMessage);
    }

}
