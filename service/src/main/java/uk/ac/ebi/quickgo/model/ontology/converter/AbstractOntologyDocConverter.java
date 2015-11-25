package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.newFlatField;

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

        return term;
    }

    protected List<OBOTerm.History> retrieveHistory(List<String> docHistory) {
        if (docHistory != null) {
            List<OBOTerm.History> history = new ArrayList<>();
            docHistory.stream().forEach(
                    h -> {
                        // format: name|timestamp|action|category|text
                        OBOTerm.History historicalInfo = new OBOTerm.History();

                        List<String> fields = newFlatField(h).getFields();
                        if (fields.size() == 5) {
                            historicalInfo.name = fields.get(0);
                            historicalInfo.timestamp = fields.get(1);
                            historicalInfo.action = fields.get(2);
                            historicalInfo.category = fields.get(3);
                            historicalInfo.text = fields.get(4);
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
                        List<String> fields = newFlatField(s).getFields();
                        if (fields.size() == 2) {
                            synonym.synonymName = fields.get(0).trim();
                            synonym.synonymType = fields.get(1).trim();
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
