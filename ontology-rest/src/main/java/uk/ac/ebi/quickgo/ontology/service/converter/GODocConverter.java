package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import static uk.ac.ebi.quickgo.common.model.Aspect.fromShortName;

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

    private final static GODiscussionConverter GO_DISCUSSION_CONVERTER =
            new GODiscussionConverter();

    private final static ExtendedXRefsFieldConverter PROTEIN_COMPLEX_CONVERTER =
            new ExtendedXRefsFieldConverter();

    @Override public GOTerm convert(OntologyDocument ontologyDocument) {
        GOTerm goTerm = new GOTerm();
        addCommonFields(ontologyDocument, goTerm);
        goTerm.aspect = fromShortName(ontologyDocument.aspect).orElse(null);
        goTerm.usage = ontologyDocument.usage != null ?
                GOTerm.Usage.fromFullName(ontologyDocument.usage) : null;
        goTerm.subsets = ontologyDocument.subsets;

        goTerm.blacklist = BLACKLIST_FIELD_CONVERTER.convertFieldList(ontologyDocument.blacklist);
        goTerm.goDiscussions = GO_DISCUSSION_CONVERTER.convertFieldList(ontologyDocument.goDiscussions);
        goTerm.proteinComplexes = PROTEIN_COMPLEX_CONVERTER.convertFieldList(ontologyDocument.proteinComplexes);

        return goTerm;
    }
}
