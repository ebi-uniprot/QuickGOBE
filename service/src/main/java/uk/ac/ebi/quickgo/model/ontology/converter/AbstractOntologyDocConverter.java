package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.FlatField;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.parseFlatFieldTree;

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

        return term;
    }

    protected List<OBOTerm.TaxonConstraint> retrieveTaxonConstraints(List<String> taxonConstraints) {
        if (taxonConstraints != null) {
            List<OBOTerm.TaxonConstraint> oboXrefs = new ArrayList<>();
            taxonConstraints.stream().forEach(
                    t -> {
                        // format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2..
                        OBOTerm.TaxonConstraint taxonConstraint = new OBOTerm.TaxonConstraint();

                        List<FlatField> fields = parseFlatFieldTree(t).getFields();
                        if (fields.size() == 7) {
                            taxonConstraint.ancestorId = fields.get(0).buildString().trim();
                            taxonConstraint.ancestorName = fields.get(1).buildString().trim();
                            taxonConstraint.relationship = fields.get(2).buildString().trim();
                            taxonConstraint.taxId = fields.get(3).buildString().trim();
                            taxonConstraint.taxIdType = fields.get(4).buildString().trim();
                            taxonConstraint.taxName = fields.get(5).buildString().trim();
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

                        List<FlatField> fields = parseFlatFieldTree(x).getFields();
                        if (fields.size() == 3) {
                            xref.dbCode = fields.get(0).buildString().trim();
                            xref.dbId = fields.get(1).buildString().trim();
                            xref.name = fields.get(2).buildString().trim();
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

                        List<FlatField> fields = parseFlatFieldTree(h).getFields();
                        if (fields.size() == 5) {
                            historicalInfo.name = fields.get(0).buildString().trim();
                            historicalInfo.timestamp = fields.get(1).buildString().trim();
                            historicalInfo.action = fields.get(2).buildString().trim();
                            historicalInfo.category = fields.get(3).buildString().trim();
                            historicalInfo.text = fields.get(4).buildString().trim();
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
                        List<FlatField> fields = parseFlatFieldTree(s).getFields();
                        if (fields.size() == 2) {
                            synonym.synonymName = fields.get(0).buildString().trim();
                            synonym.synonymType = fields.get(1).buildString().trim();
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
