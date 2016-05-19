package uk.ac.ebi.quickgo.ontology.traversal;

import org.springframework.batch.item.ItemProcessor;

/**
 * Created 18/05/16
 * @author Edd
 */
public class OntologyRelationshipValidator
        implements ItemProcessor<OntologyRelationshipTuple, OntologyRelationshipTuple> {
    @Override public OntologyRelationshipTuple process(OntologyRelationshipTuple ontologyRelationshipTuple)
            throws Exception {
        return ontologyRelationshipTuple;
    }
}
