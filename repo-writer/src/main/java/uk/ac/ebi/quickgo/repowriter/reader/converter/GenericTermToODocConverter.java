package uk.ac.ebi.quickgo.repowriter.reader.converter;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.Synonym;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Converts a {@link GenericTerm} instance into a corresponding {@link OntologyDocument} instance.
 * This class uses {@link FlatFieldBuilder} to serialize nested field information into
 * single fields in the {@link OntologyDocument}.
 *
 * Created 14/12/15
 * @author Edd
 */
public class GenericTermToODocConverter implements Function<Optional<? extends GenericTerm>,
        Optional<OntologyDocument>> {

    @Override public Optional<OntologyDocument> apply(Optional<? extends GenericTerm> termOptional) {
        if (termOptional.isPresent()) {
            OntologyDocument doc = new OntologyDocument();
            GenericTerm term = termOptional.get();
            doc.ancestors = extractAncestors(term);
            doc.considers = extractConsidersAsList(term);
            doc.id = term.getId();
            doc.isObsolete = term.isObsolete();
            doc.comment = term.getComment();
            doc.definition = term.getDefinition();
            doc.history = extractHistory(term);
            doc.name = term.getName();
            doc.ontologyType = term.getOntologyType();
            doc.secondaryIds = extractSecondaries(term);
            doc.subsets = extractSubsets(term);
            doc.synonyms = extractSynonyms(term);
            doc.synonymNames = extractSynonymNames(term);
            doc.xrefs = extractXRefs(term);
            doc.xRelations = extractXRelationsAsList(term);

            ArrayList<GenericTerm> replacedBy = term.replacedBy();
            if (replacedBy != null && replacedBy.size() > 0) {
                doc.replacedBy = replacedBy.get(0).getId();
            }

            return Optional.of(doc);
        } else {
            return Optional.empty();
        }

    }

    private List<String> extractSubsets(GenericTerm term) {
        if (!isEmpty(term.getSubsetsNames())) {
            return term.getSubsetsNames();
        } else {
            return null;
        }
    }

    private List<String> extractSecondaries(GenericTerm term) {
        String secondaries = term.secondaries();
        if (secondaries != null && secondaries.trim().length() != 0) {
            String[] secondariesArr = secondaries.split(",");
            if (secondariesArr.length > 0) {
                return Arrays.asList(secondariesArr);
            }
        }

        return null;
    }

    protected List<String> extractConsidersAsList(GenericTerm term) {
        if (!isEmpty(term.consider())) {
            return term.consider().stream()
                    .map(GenericTerm::getId)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: id|term|namespace|url|relation
     */
    protected List<String> extractXRelationsAsList(GenericTerm term) {
        if (!isEmpty(term.getCrossOntologyRelations())) {
            return term.getCrossOntologyRelations().stream()
                    .map(
                            c -> newFlatField()
                                    .addField(newFlatFieldLeaf(c.getForeignID()))
                                    .addField(newFlatFieldLeaf(c.getForeignTerm()))
                                    .addField(newFlatFieldLeaf(c.getOtherNamespace()))
                                    .addField(newFlatFieldLeaf(c.getUrl()))
                                    .addField(newFlatFieldLeaf(c.getRelation()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: code|id|name
     */
    protected List<String> extractXRefs(GenericTerm term) {
        if (!isEmpty(term.getXrefs())) {
            return term.getXrefs().stream()
                    .map(
                            g -> newFlatField()
                                    .addField(newFlatFieldLeaf(g.getDb()))
                                    .addField(newFlatFieldLeaf(g.getId()))
                                    .addField(newFlatFieldLeaf(g.getName()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    protected List<String> extractAncestors(GenericTerm term) {
        if (!isEmpty(term.getAncestors())) {
            return term.getAncestors()
                    .stream() // ancestors is a list of parent ids for this term?
                    .map(
                            a -> a.getParent().getId())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: name|timestamp|action|category|text
     */
    protected List<String> extractHistory(GenericTerm term) {
        if (term.getHistory() != null && !isEmpty(term.getHistory().getHistoryAll())) {
            return term.getHistory().getHistoryAll().stream()
                    .map(
                            h -> newFlatField()
                                    .addField(newFlatFieldLeaf(h.getTermName()))
                                    .addField(newFlatFieldLeaf(h.getTimestamp()))
                                    .addField(newFlatFieldLeaf(h.getAction().description))
                                    .addField(newFlatFieldLeaf(h.getCategory().description))
                                    .addField(newFlatFieldLeaf(h.getText()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    protected List<String> extractSynonymNames(GenericTerm term) {
        if (!isEmpty(term.getSynonyms())) {
            return term.getSynonyms().stream()

                    .map(Synonym::getName).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: synonymName|synonymType
     */
    protected List<String> extractSynonyms(GenericTerm term) {
        if (!isEmpty(term.getSynonyms())) {
            return term.getSynonyms().stream()
                    .map(
                            s -> newFlatField()
                                    .addField(newFlatFieldLeaf(s.getName()))
                                    .addField(newFlatFieldLeaf(s.getType()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
