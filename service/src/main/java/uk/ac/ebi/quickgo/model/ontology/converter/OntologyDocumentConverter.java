package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.GOTerm;

import org.springframework.core.convert.converter.Converter;

/**
 * Created 13/11/15
 * @author Edd
 */
public class OntologyDocumentConverter implements Converter<OntologyDocument, GOTerm> {
    @Override public GOTerm convert(OntologyDocument ontologyDocument) {
        return null;
    }
}
