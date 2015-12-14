package uk.ac.ebi.quickgo.repo.reader.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.Synonym;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatFieldFromDepth;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Converts a {@link GenericTerm} instance into a corresponding {@link OntologyDocument} instance.
 *
 * Created 14/12/15
 * @author Edd
 */
public class GenericTermToDocConverter implements Function<Optional<? extends GenericTerm>,
        Optional<OntologyDocument>> {

    @Override public Optional<OntologyDocument> apply(Optional<? extends GenericTerm> termOptional) {
        if (termOptional.isPresent()) {
            OntologyDocument doc = new OntologyDocument();
            GenericTerm goTerm = termOptional.get();

            doc.id = goTerm.getId();
            doc.ontologyType = goTerm.getOntologyType();
            doc.name = goTerm.getName();
            doc.isObsolete = goTerm.isObsolete;
            doc.definition = goTerm.getDefinition();
            doc.comment = goTerm.getComment();

            // synonyms
            doc.synonyms = goTerm.getSynonyms().stream()
                    .map(
                            s -> newFlatFieldFromDepth(2)
                                    .addField(newFlatFieldLeaf(s.getName()))
                                    .addField(newFlatFieldLeaf(s.getType()))
                                    .buildString())
                    .collect(Collectors.toList());
            doc.synonymNames = goTerm.getSynonyms().stream()
                    .map(Synonym::getName).collect(Collectors.toList());

            // history
            // format: name|timestamp|action|category|text
            doc.history = goTerm.getHistory().getHistoryAll().stream().map(
                    h -> newFlatFieldFromDepth(2)
                            .addField(newFlatFieldLeaf(h.getTermName()))
                            .addField(newFlatFieldLeaf(h.getTimestamp()))
                            .addField(newFlatFieldLeaf(h.getAction().description))
                            .addField(newFlatFieldLeaf(h.getCategory().description))
                            .addField(newFlatFieldLeaf(h.getText()))
                            .buildString()
            ).collect(Collectors.toList());

            doc.secondaryIds = Arrays.asList(goTerm.secondaries().split(","));

            doc.ancestors = goTerm.getAncestors().stream() // ancestors is a list of parent ids for this term?
                    .map(a -> a.getParent().getId())
                    .collect(Collectors.toList());

            // xrefs
            // format: code|id|name
            doc.xrefs = goTerm.getXrefs().stream().map(g -> newFlatFieldFromDepth(2)
                    .addField(newFlatFieldLeaf(g.getDb()))
                    .addField(newFlatFieldLeaf(g.getId()))
                    .addField(newFlatFieldLeaf(g.getName()))
                    .buildString()
            ).collect(Collectors.toList());

            // xrelations
            // format: id|term|namespace|url|relation
            doc.xRelations = goTerm.getCrossOntologyRelations().stream().map(c -> newFlatFieldFromDepth(2)
                    .addField(newFlatFieldLeaf(c.getForeignID()))
                    .addField(newFlatFieldLeaf(c.getForeignTerm()))
                    .addField(newFlatFieldLeaf(c.getOtherNamespace()))
                    .addField(newFlatFieldLeaf(c.getUrl()))
                    .addField(newFlatFieldLeaf(c.getRelation()))
                    .buildString()
            ).collect(Collectors.toList());

            // considers
            // TODO: check if correct
            doc.considers = goTerm.consider().stream().map(GenericTerm::getId).collect(Collectors.toList());

            // replacedBy
            // TODO: check if correct
            ArrayList<GenericTerm> replacedBy = goTerm.replacedBy();
            if (replacedBy != null && replacedBy.size() > 0) {
                doc.replacedBy = replacedBy.get(0).getId();
            }

            // blacklist
            // TODO: get this info from somewhere!

            return Optional.of(doc);
        } else {
            return Optional.empty();
        }

    }
}
