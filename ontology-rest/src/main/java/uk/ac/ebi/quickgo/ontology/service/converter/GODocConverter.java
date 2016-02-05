package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.ontology.common.document.Aspect;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

/**
 * Converts an {@link OntologyDocument} representing a GO term,
 * to a {@link GOTerm} instance.
 *
 * Created 23/11/15
 * @author Edd
 */
public class GODocConverter extends AbstractODocConverter<GOTerm> {
    @Override public GOTerm convert(OntologyDocument ontologyDocument) {
        GOTerm goTerm = new GOTerm();
        addCommonFields(ontologyDocument, goTerm);
        goTerm.aspect = ontologyDocument.aspect != null ?
                Aspect.fromShortName(ontologyDocument.aspect) : null;
        goTerm.usage = ontologyDocument.usage != null ?
                GOTerm.Usage.fromFullName(ontologyDocument.usage): null;
        return goTerm;
    }
}
