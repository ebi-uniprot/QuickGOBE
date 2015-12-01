package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.ECOTerm;

/**
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
