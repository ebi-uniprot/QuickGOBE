package uk.ac.ebi.quickgo.client.service.converter.ontology;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import com.google.common.base.Preconditions;

/**
 * This class provides template behaviour for converting an {@link OntologyDocument}
 * to an {@link OntologyTerm}, using {@link FieldConverter} instances to convert fields into {@link OntologyTerm}s.
 *
 * @author Ricardo Antunes
 */
abstract class AbstractDocConverter<T extends OntologyTerm> {
    public T convert(OntologyDocument doc) {
        Preconditions.checkArgument(doc != null, "Ontology document to convert is null");

        T term = createTerm();
        addCommonFields(doc, term);
        addOntologySpecificFields(doc, term);

        return term;
    }

    protected abstract T createTerm();

    protected abstract void addOntologySpecificFields(OntologyDocument doc, T term);

    private void addCommonFields(OntologyDocument doc, T term) {
        term.id = doc.id;
        term.name = doc.name;
        term.isObsolete = doc.isObsolete;
    }
}