package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides template behaviour for converting an {@link OntologyDocument}
 * to an {@link OBOTerm}, using {@link FieldConverter} instances to convert fields into {@link OBOTerm}s.
 *
 * Created 24/11/15
 * @author Edd
 */
abstract class AbstractODocConverter<T extends OBOTerm> implements OntologyDocConverter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractODocConverter.class);

    private final static AnnotationGuideLineFieldConverter AG_FIELD_CONVERTER =
            new AnnotationGuideLineFieldConverter();
    private final static XORelationsFieldConverter XORELATIONS_FIELD_CONVERTER =
            new XORelationsFieldConverter();
    private final static TaxonConstraintsFieldConverter TAXON_CONSTRAINTS_FIELD_CONVERTER =
            new TaxonConstraintsFieldConverter();
    private final static SynonymsFieldConverter SYNONYMS_FIELD_CONVERTER =
            new SynonymsFieldConverter();
    private final static HistoryFieldConverter HISTORY_FIELD_CONVERTER =
            new HistoryFieldConverter();
    private final static XRefsFieldConverter XREFS_FIELD_CONVERTER =
            new XRefsFieldConverter();
    private final static DefinitionConverter DEFINITION_CONVERTER = new DefinitionConverter();
    private final static RelationConverter RELATION_FIELD_CONVERTER = new RelationConverter();
    private final static CreditsFieldConverter CREDITS_FIELD_CONVERTER = new CreditsFieldConverter();

    public abstract T convert(OntologyDocument ontologyDocument);

    protected void addCommonFields(OntologyDocument ontologyDocument, T term) {
        term.id = ontologyDocument.id;
        term.name = ontologyDocument.name;
        term.isObsolete = ontologyDocument.isObsolete;
        term.comment = ontologyDocument.comment;
        term.secondaryIds = ontologyDocument.secondaryIds;
        term.subsets = ontologyDocument.subsets;
        term.definition = DEFINITION_CONVERTER.apply(ontologyDocument);
        term.synonyms = SYNONYMS_FIELD_CONVERTER.convertFieldList(ontologyDocument.synonyms);
        term.history = HISTORY_FIELD_CONVERTER.convertFieldList(ontologyDocument.history);
        term.xRefs = XREFS_FIELD_CONVERTER.convertFieldList(ontologyDocument.xrefs);
        term.taxonConstraints = TAXON_CONSTRAINTS_FIELD_CONVERTER.convertFieldList(ontologyDocument.taxonConstraints);
        term.xRelations = XORELATIONS_FIELD_CONVERTER.convertFieldList(ontologyDocument.xRelations);
        term.annotationGuidelines = AG_FIELD_CONVERTER.convertFieldList(ontologyDocument.annotationGuidelines);
        term.credits = CREDITS_FIELD_CONVERTER.convertFieldList(ontologyDocument.credits);

        term.children = RELATION_FIELD_CONVERTER.convertFieldList(ontologyDocument.children);
        term.replaces = RELATION_FIELD_CONVERTER.convertFieldList(ontologyDocument.replaces);
        term.replacements = RELATION_FIELD_CONVERTER.convertFieldList(ontologyDocument.replacements);
    }
}