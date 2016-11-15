package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

/**
 * Converts an {@link OntologyDocument} into an instance of {@link OBOTerm}.
 *
 * Created 13/11/15
 * @author Edd
 */
public interface OntologyDocConverter<T extends OBOTerm>  {
    T convert(OntologyDocument ontologyDocument);
}
