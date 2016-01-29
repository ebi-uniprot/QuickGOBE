package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.service.model.ontology.GOTerm;

/**
 * Converts an {@link OntologyDocument} representing a GO term,
 * to a {@link GOTerm} instance.
 *
 * Created 23/11/15
 * @author Edd
 */
public class GODocConverter extends AbstractODocConverter<GOTerm> {
    private final static BlackListFieldConverter BLACKLIST_FIELD_CONVERTER =
            new BlackListFieldConverter();

    @Override public GOTerm convert(OntologyDocument ontologyDocument) {
        GOTerm goTerm = new GOTerm();
        addCommonFields(ontologyDocument, goTerm);
        goTerm.aspect = ontologyDocument.aspect != null ?
                GOTerm.Aspect.fromShortName(ontologyDocument.aspect) : null;
        goTerm.usage = ontologyDocument.usage != null ?
                GOTerm.Usage.fromFullName(ontologyDocument.usage): null;

        goTerm.blacklist = BLACKLIST_FIELD_CONVERTER.convertFieldList(ontologyDocument.blacklist);
        return goTerm;
    }
}
