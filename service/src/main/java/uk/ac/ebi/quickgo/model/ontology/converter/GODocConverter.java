package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.GOTerm;

/**
 * Created 23/11/15
 * @author Edd
 */
public class GODocConverter extends AbstractODocConverter<GOTerm> {
    @Override public GOTerm convert(OntologyDocument ontologyDocument) {
        GOTerm goTerm = new GOTerm();
        addCommonFields(ontologyDocument, goTerm);
        goTerm.aspect = ontologyDocument.aspect;
        goTerm.usage = ontologyDocument.usage;
        return goTerm;
    }
}
