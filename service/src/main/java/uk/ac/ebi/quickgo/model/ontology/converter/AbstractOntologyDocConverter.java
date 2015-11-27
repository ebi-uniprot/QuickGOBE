package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.FlatField;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.parseFlatField;

/**
 * Created 24/11/15
 * @author Edd
 */
public abstract class AbstractOntologyDocConverter<T extends OBOTerm> implements OntologyDocConverter<T> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOntologyDocConverter.class);

    public abstract T convert(OntologyDocument ontologyDocument);

    protected T addCommonFields(OntologyDocument ontologyDocument, T term) {
        term.id = ontologyDocument.id;
        term.name = ontologyDocument.name;
        term.definition = ontologyDocument.definition;
        term.subsets = ontologyDocument.subsets;
        term.isObsolete = ontologyDocument.isObsolete;
        term.replacedBy = ontologyDocument.replacedBy;
        term.comment = ontologyDocument.comment;
        term.children = ontologyDocument.children;
        term.synonyms = retrieveSynonyms(ontologyDocument.synonyms);
        term.ancestors = ontologyDocument.ancestors;
        term.secondaryIds = ontologyDocument.secondaryIds;
        term.history = retrieveHistory(ontologyDocument.history);
        term.xrefs = retrieveXRefs(ontologyDocument.xrefs);
        term.taxonConstraints = retrieveTaxonConstraints(ontologyDocument.taxonConstraints);
        term.xRelations = retrieveXOntologyRelations(ontologyDocument.xRelations);
        term.blacklist = retrieveBlackListedItems(ontologyDocument.blacklist);
        term.annotationGuidelines = retrieveAnnotationGuideLines(ontologyDocument.annotationGuidelines);

        return term;
    }

    protected List<OBOTerm.AnnotationGuideLine> retrieveAnnotationGuideLines(List<String> annotationGuidelines) {
        if (annotationGuidelines != null) {
            List<OBOTerm.AnnotationGuideLine> ags = new ArrayList<>();
            annotationGuidelines.stream().forEach(
                    g -> {
                        // format: geneProductId|geneProductDB|reason|category|method
                        OBOTerm.AnnotationGuideLine ag = new OBOTerm.AnnotationGuideLine();

                        List<FlatField> fields = parseFlatField(g).getFields();
                        if (fields.size() == 2) {
                            ag.description = nullOrString(fields.get(0).buildString());
                            ag.url = nullOrString(fields.get(1).buildString());
                            ags.add(ag);
                        } else {
                            LOGGER.warn("Could not parse flattened annotationGuidelines: {}", g);
                        }
                    }
            );
            return ags;
        } else {
            return null;
        }
    }

    protected List<OBOTerm.BlacklistItem> retrieveBlackListedItems(List<String> blacklist) {
        if (blacklist != null) {
            List<OBOTerm.BlacklistItem> blacklistItems = new ArrayList<>();
            blacklist.stream().forEach(
                    b -> {
                        // format: geneProductId|geneProductDB|reason|category|method
                        OBOTerm.BlacklistItem blacklistItem = new OBOTerm.BlacklistItem();

                        List<FlatField> fields = parseFlatField(b).getFields();

                        if (fields.size() == 5) {
                            blacklistItem.geneProductId = nullOrString(fields.get(0).buildString());
                            blacklistItem.geneProductDb = nullOrString(fields.get(1).buildString());
                            blacklistItem.reason = nullOrString(fields.get(2).buildString());
                            blacklistItem.category = nullOrString(fields.get(3).buildString());
                            blacklistItem.method = nullOrString(fields.get(4).buildString());
                            blacklistItems.add(blacklistItem);
                        } else {
                            LOGGER.warn("Could not parse flattened blacklist: {}", b);
                        }
                    }
            );
            return blacklistItems;
        } else {
            return null;
        }
    }

    protected static String nullOrString(String convertedField) {
        String trimmed = convertedField.trim();
        return (trimmed.equals("")) ? null : trimmed;
    }

    protected List<OBOTerm.XORelation> retrieveXOntologyRelations(List<String> xrels) {
        if (xrels != null) {
            List<OBOTerm.XORelation> oboXORels = new ArrayList<>();
            xrels.stream().forEach(
                    x -> {
                        // format: id|term|namespace|url|relation
                        OBOTerm.XORelation xORel = new OBOTerm.XORelation();

                        List<FlatField> fields = parseFlatField(x).getFields();
                        if (fields.size() == 5) {
                            xORel.id = nullOrString(fields.get(0).buildString());
                            xORel.term = nullOrString(fields.get(1).buildString());
                            xORel.namespace = nullOrString(fields.get(2).buildString());
                            xORel.url = nullOrString(fields.get(3).buildString());
                            xORel.relation = nullOrString(fields.get(4).buildString());
                            oboXORels.add(xORel);
                        } else {
                            LOGGER.warn("Could not parse flattened xORel: {}", x);
                        }
                    }
            );
            return oboXORels;
        } else {
            return null;
        }
    }

    protected List<OBOTerm.TaxonConstraint> retrieveTaxonConstraints(List<String> taxonConstraints) {
        if (taxonConstraints != null) {
            List<OBOTerm.TaxonConstraint> oboXrefs = new ArrayList<>();
            taxonConstraints.stream().forEach(
                    t -> {
                        // format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2
                        // |blacklist
                        OBOTerm.TaxonConstraint taxonConstraint = new OBOTerm.TaxonConstraint();

                        List<FlatField> fields = parseFlatField(t).getFields();
                        if (fields.size() == 7) {
                            taxonConstraint.ancestorId = nullOrString(fields.get(0).buildString());
                            taxonConstraint.ancestorName = nullOrString(fields.get(1).buildString());
                            taxonConstraint.relationship = nullOrString(fields.get(2).buildString());
                            taxonConstraint.taxId = nullOrString(fields.get(3).buildString());
                            taxonConstraint.taxIdType = nullOrString(fields.get(4).buildString());
                            taxonConstraint.taxName = nullOrString(fields.get(5).buildString());
                            taxonConstraint.citations = new ArrayList<>();
                            fields.get(6).getFields().stream().forEach(
                                    rawLit -> {
                                        OBOTerm.Lit lit = new OBOTerm.Lit();
                                        lit.id = rawLit.buildString();
                                        taxonConstraint.citations.add(lit);
                                    }
                            );

                            oboXrefs.add(taxonConstraint);
                        } else {
                            LOGGER.warn("Could not parse flattened taxonConstraint: {}", t);
                        }
                    }
            );
            return oboXrefs;
        } else {
            return null;
        }
    }

    protected List<OBOTerm.XRef> retrieveXRefs(List<String> xrefs) {
        if (xrefs != null) {
            List<OBOTerm.XRef> oboXrefs = new ArrayList<>();
            xrefs.stream().forEach(
                    x -> {
                        // format: code|id|name
                        OBOTerm.XRef xref = new OBOTerm.XRef();

                        List<FlatField> fields = parseFlatField(x).getFields();
                        if (fields.size() == 3) {
                            xref.dbCode = nullOrString(fields.get(0).buildString());
                            xref.dbId = nullOrString(fields.get(1).buildString());
                            xref.name = nullOrString(fields.get(2).buildString());
                            oboXrefs.add(xref);
                        } else {
                            LOGGER.warn("Could not parse flattened xref: {}", x);
                        }
                    }
            );
            return oboXrefs;
        } else {
            return null;
        }
    }

    protected List<OBOTerm.History> retrieveHistory(List<String> docHistory) {
        if (docHistory != null) {
            List<OBOTerm.History> history = new ArrayList<>();
            docHistory.stream().forEach(
                    h -> {
                        // format: name|timestamp|action|category|text
                        OBOTerm.History historicalInfo = new OBOTerm.History();

                        List<FlatField> fields = parseFlatField(h).getFields();
                        if (fields.size() == 5) {
                            historicalInfo.name = nullOrString(fields.get(0).buildString());
                            historicalInfo.timestamp = nullOrString(fields.get(1).buildString());
                            historicalInfo.action = nullOrString(fields.get(2).buildString());
                            historicalInfo.category = nullOrString(fields.get(3).buildString());
                            historicalInfo.text = nullOrString(fields.get(4).buildString());
                            history.add(historicalInfo);
                        } else {
                            LOGGER.warn("Could not parse flattened history: {}", h);
                        }
                    }
            );
            return history;
        } else {
            return null;
        }
    }


    protected List<OBOTerm.Synonym> retrieveSynonyms(List<String> docSynonyms) {
        if (docSynonyms != null) {
            List<OBOTerm.Synonym> synonyms = new ArrayList<>();
            docSynonyms.stream().forEach(
                    s -> {
                        // format: name|type
                        OBOTerm.Synonym synonym = new OBOTerm.Synonym();
                        List<FlatField> fields = parseFlatField(s).getFields();
                        if (fields.size() == 2) {
                            synonym.synonymName = nullOrString(fields.get(0).buildString());
                            synonym.synonymType = nullOrString(fields.get(1).buildString());
                            synonyms.add(synonym);
                        } else {
                            LOGGER.warn("Could not parse flattened synonym: {}", s);
                        }
                    }
            );
            return synonyms;
        } else {
            return null;
        }
    }

}
