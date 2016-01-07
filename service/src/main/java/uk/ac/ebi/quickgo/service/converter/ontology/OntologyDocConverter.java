package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

/**
 * Converts an {@link OntologyDocument} into an instance of {@link OBOTerm}.
 *
 * Created 13/11/15
 * @author Edd
 */
public interface OntologyDocConverter<T extends OBOTerm>  {
    T convert(OntologyDocument ontologyDocument);
}
