package uk.ac.ebi.quickgo.repo.reader.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.Synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatFieldFromDepth;
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

    // the base depth of a field in a document
    static final int DEPTH_OF_NESTED_DOC_FIELD = 2;

    @Override public Optional<OntologyDocument> apply(Optional<? extends GenericTerm> termOptional) {
        if (termOptional.isPresent()) {
            OntologyDocument doc = new OntologyDocument();
            GenericTerm goTerm = termOptional.get();

            doc.ancestors = extractAncestors(goTerm);
            // TODO: blacklist -- where is this info?
            doc.considers = extractConsidersAsList(goTerm);
            doc.id = goTerm.getId();
            doc.isObsolete = goTerm.isObsolete();
            doc.comment = goTerm.getComment();
            doc.definition = goTerm.getDefinition();
            doc.history = extractHistory(goTerm);
            doc.name = goTerm.getName();
            doc.ontologyType = goTerm.getOntologyType();
            doc.secondaryIds = extractSecondaries(goTerm);
            doc.synonyms = extractSynonyms(goTerm);
            doc.synonymNames = extractSynonymNames(goTerm);
            doc.xrefs = extractXRefs(goTerm);
            doc.xRelations = extractXRelationsAsList(goTerm);

            ArrayList<GenericTerm> replacedBy = goTerm.replacedBy();
            if (replacedBy != null && replacedBy.size() > 0) {
                doc.replacedBy = replacedBy.get(0).getId();
            }

            return Optional.of(doc);
        } else {
            return Optional.empty();
        }

    }

    private List<String> extractSecondaries(GenericTerm goTerm) {
        String secondaries = goTerm.secondaries();
        if (secondaries != null && secondaries.trim().length() != 0) {
            String[] secondariesArr = secondaries.split(",");
            if (secondariesArr.length > 0) {
                return Arrays.asList(secondariesArr);
            }
        }

        return null;
    }

    protected List<String> extractConsidersAsList(GenericTerm goTerm) {
        if (!isEmpty(goTerm.consider())) {
            return goTerm.consider().stream()
                    .map(GenericTerm::getId)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: id|term|namespace|url|relation
     */
    protected List<String> extractXRelationsAsList(GenericTerm goTerm) {
        if (!isEmpty(goTerm.getCrossOntologyRelations())) {
            return goTerm.getCrossOntologyRelations().stream()
                    .map(
                            c -> newFlatFieldFromDepth(DEPTH_OF_NESTED_DOC_FIELD)
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
    protected List<String> extractXRefs(GenericTerm goTerm) {
        if (!isEmpty(goTerm.getXrefs())) {
            return goTerm.getXrefs().stream()
                    .map(
                            g -> newFlatFieldFromDepth(DEPTH_OF_NESTED_DOC_FIELD)
                                    .addField(newFlatFieldLeaf(g.getDb()))
                                    .addField(newFlatFieldLeaf(g.getId()))
                                    .addField(newFlatFieldLeaf(g.getName()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    protected List<String> extractAncestors(GenericTerm goTerm) {
        if (!isEmpty(goTerm.getAncestors())) {
            return goTerm.getAncestors()
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
    protected List<String> extractHistory(GenericTerm goTerm) {
        if (goTerm.getHistory() != null && !isEmpty(goTerm.getHistory().getHistoryAll())) {
            return goTerm.getHistory().getHistoryAll().stream()
                    .map(
                            h -> newFlatFieldFromDepth(DEPTH_OF_NESTED_DOC_FIELD)
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

    protected List<String> extractSynonymNames(GenericTerm goTerm) {
        if (!isEmpty(goTerm.getSynonyms())) {
            return goTerm.getSynonyms().stream()

                    .map(Synonym::getName).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: synonymName|synonymType
     */
    protected List<String> extractSynonyms(GenericTerm goTerm) {
        if (!isEmpty(goTerm.getSynonyms())) {
            return goTerm.getSynonyms().stream()
                    .map(
                            s -> newFlatFieldFromDepth(DEPTH_OF_NESTED_DOC_FIELD)
                                    .addField(newFlatFieldLeaf(s.getName()))
                                    .addField(newFlatFieldLeaf(s.getType()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
