package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.Synonym;
import uk.ac.ebi.quickgo.model.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Converts a {@link GenericTerm} instance into a corresponding {@link OntologyDocument} instance.
 * This class uses {@link FlatFieldBuilder} to serialize nested field information into
 * single fields in the {@link OntologyDocument}.
 *
 * Created 14/12/15
 * @author Edd
 */
public class GenericTermToODocConverter implements Function<GenericTerm,
        OntologyDocument> {

    @Override public OntologyDocument apply(GenericTerm term) {
        Preconditions.checkArgument(Objects.nonNull(term), "The Generic Term instance applied to " +
                "GenericTermToODocConverter cannot be null");
            OntologyDocument doc = new OntologyDocument();
            doc.id = term.getId();
            doc.isObsolete = term.isObsolete();
            doc.comment = term.getComment();
            doc.definition = term.getDefinition();
            doc.definitionXrefs = extractDefinitionXrefs(term);
            doc.history = extractHistory(term);
            doc.name = term.getName();
            doc.ontologyType = term.getOntologyType();
            doc.secondaryIds = extractSecondaries(term);
            doc.subsets = extractSubsets(term);
            doc.synonyms = extractSynonyms(term);
            doc.synonymNames = extractSynonymNames(term);
            doc.xrefs = extractXRefs(term);
            doc.xRelations = extractXRelationsAsList(term);
            doc.replaces = extractReplaces(term);
            doc.replacements = extractReplacements(term);
            doc.credits = extractCredits(term);
            return doc;
    }

    private List<String> extractCredits(GenericTerm term) {
        List<String> extractedCredits = null;

        if (!isEmpty(term.getCredits())) {
            extractedCredits = term.getCredits().stream()
                    .map(credit -> FlatFieldBuilder.newFlatField()
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(credit.getCode()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(credit.getUrl()))
                            .buildString())
                    .collect(Collectors.toList());
        }

        return extractedCredits;
    }

    private List<String> extractDefinitionXrefs(GenericTerm term) {
        return term.getDefinitionXrefs().stream()
                .map(xref -> FlatFieldBuilder.newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(xref.getDb()))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(xref.getId()))
                        .buildString())
                .collect(Collectors.toList());
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

    private List<String> extractReplacements(GenericTerm term) {
        return extractReplaceElementsFromRelations(term.getReplacements(),
                (TermRelation relation) -> relation.getParent().getId());
    }

    private List<String> extractReplaces(GenericTerm term) {
        return extractReplaceElementsFromRelations(term.getReplaces(),
                (TermRelation relation) -> relation.getChild().getId());
    }

    private List<String> extractReplaceElementsFromRelations(Collection<TermRelation> replaceList,
            Function<TermRelation, String> replaceIdExtractor) {
        if (!isEmpty(replaceList)) {
            return replaceList.stream()
                    .map(replace -> FlatFieldBuilder.newFlatField()
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(replaceIdExtractor.apply(replace)))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(replace.getTypeof().getFormalCode()))
                            .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: id|term|namespace|url|relation
     */
    private List<String> extractXRelationsAsList(GenericTerm term) {
        if (!isEmpty(term.getCrossOntologyRelations())) {
            return term.getCrossOntologyRelations().stream()
                    .map(
                            c -> FlatFieldBuilder.newFlatField()
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(c.getForeignID()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(c.getForeignTerm()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(c.getOtherNamespace()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(c.getUrl()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(c.getRelation()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: code|id|name
     */
    private List<String> extractXRefs(GenericTerm term) {
        if (!isEmpty(term.getXrefs())) {
            return term.getXrefs().stream()
                    .map(
                            g -> FlatFieldBuilder.newFlatField()
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(g.getDb()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(g.getId()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(g.getName()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: name|timestamp|action|category|text
     */
    private List<String> extractHistory(GenericTerm term) {
        if (term.getHistory() != null && !isEmpty(term.getHistory().getHistoryAll())) {
            return term.getHistory().getHistoryAll().stream()
                    .map(
                            h -> FlatFieldBuilder.newFlatField()
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(h.getTermName()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(h.getTimestamp()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(h.getAction().description))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(h.getCategory().description))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(h.getText()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private List<String> extractSynonymNames(GenericTerm term) {
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
    private List<String> extractSynonyms(GenericTerm term) {
        if (!isEmpty(term.getSynonyms())) {
            return term.getSynonyms().stream()
                    .map(
                            s -> FlatFieldBuilder.newFlatField()
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(s.getName()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(s.getType()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
