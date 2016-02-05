package uk.ac.ebi.quickgo.client.service.converter.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.ECOTerm;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

/**
 * Class responsible for converting ECO specific fields from the {@link OntologyDocument} into the {@link ECOTerm}.
 *
 * @author Ricardo Antunes
 */
public class ECODocConverter extends AbstractDocConverter<ECOTerm> {
    @Override protected ECOTerm createTerm() {
        return new ECOTerm();
    }

    @Override protected void addOntologySpecificFields(OntologyDocument doc, ECOTerm term) {
        //There are no specific fields in an ECO term, so do nothing
    }
}
