package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created 24/11/15
 * @author Edd
 */
public abstract class AbstractOntologyDocConverter<T extends OBOTerm> implements OntologyDocConverter<T> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOntologyDocConverter.class);

    private static final String INTRA_ITEM_FIELD_SEPARATOR= "\\|";

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

        return term;
    }

    protected List<OBOTerm.Synonym> retrieveSynonyms(List<String> docSynonyms) {
        List<OBOTerm.Synonym> synonyms = new ArrayList<>();
        if (docSynonyms != null) {
            docSynonyms.stream().forEach(
                    s -> {
                        OBOTerm.Synonym synonym = new OBOTerm.Synonym();
                        String[] parts = s.split(INTRA_ITEM_FIELD_SEPARATOR);
                        if (parts.length == 2) {
                            synonym.synonymName = parts[0].trim();
                            synonym.synonymType = parts[1].trim();
                            synonyms.add(synonym);
                        } else {
                            LOGGER.warn("Could not parse flattened synonym: {}", s);
                        }
                    }
            );
        }
        return synonyms;
    }

}
