package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.ECOTerm;

/**
 * Converts an {@link OntologyDocument} representing an ECO term,
 * to a {@link ECOTerm} instance.
 *
 * Created 23/11/15
 * @author Edd
 */
public class ECODocConverter extends AbstractODocConverter<ECOTerm> {
    @Override public ECOTerm convert(OntologyDocument ontologyDocument) {
        ECOTerm ecoTerm = new ECOTerm();
        addCommonFields(ontologyDocument, ecoTerm);
        return ecoTerm;
    }
}
