package uk.ac.ebi.quickgo.client.service.converter.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.GOTerm;
import uk.ac.ebi.quickgo.ontology.common.document.Aspect;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

/**
 * Class responsible for converting GO specific fields from the {@link OntologyDocument} into the {@link GOTerm}.
 *
 * @author Ricardo Antunes
 */
public class GODocConverter extends AbstractDocConverter<GOTerm> {
    @Override protected GOTerm createTerm() {
        return new GOTerm();
    }

    @Override protected void addOntologySpecificFields(OntologyDocument doc, GOTerm term) {
        assert doc != null : "Ontology document cannot be null";
        assert term != null : "GoTerm cannot be null";

        term.aspect = doc.aspect != null ? Aspect.fromShortName(doc.aspect).getName() : null;
    }
}
