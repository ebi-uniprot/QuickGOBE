package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Converts a {@link GOTerm} instance into an {@link OntologyDocument} instance.
 *
 * Created 14/12/15
 * @author Edd
 */
public class GOTermToODocConverter implements Function<Optional<GOTerm>, Optional<OntologyDocument>> {

    private final static GenericTermToODocConverter GENERIC_TERM_TO_DOC_CONVERTER = new GenericTermToODocConverter();

    @Override public Optional<OntologyDocument> apply(Optional<GOTerm> termOptional) {
        Optional<OntologyDocument> ontologyDocument = GENERIC_TERM_TO_DOC_CONVERTER.apply(termOptional);

        if (termOptional.isPresent() && ontologyDocument.isPresent()) {

            GOTerm term = termOptional.get();
            OntologyDocument doc = ontologyDocument.get();

            doc.annotationGuidelines = extractAnnGuidelines(term);
            doc.aspect = term.getAspect() == null?
                    null : term.getAspect().text;
            doc.children = extractChildren(term);
            doc.taxonConstraints = extractTaxonConstraints(term);
            doc.usage = term.getUsage() == null?
                    null : term.getUsage().getText();
            doc.blacklist = extractBlacklist(term);

            return Optional.of(doc);
        } else {
            return Optional.empty();
        }
    }

    protected List<String> extractChildren(GOTerm goTerm) {
        if (!isEmpty(goTerm.getChildren())) {
            return goTerm.getChildren().stream()
                    .map(
                            t -> t.getChild().getId())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: description|url
     */
    protected List<String> extractAnnGuidelines(GOTerm goTerm) {
        if (!isEmpty(goTerm.getGuidelines())) {
            return goTerm.getGuidelines().stream()
                    .map(
                            t -> newFlatField()
                                    .addField(newFlatFieldLeaf(t.getTitle()))
                                    .addField(newFlatFieldLeaf(t.getUrl()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else return null;
    }

    /*
     * format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2
     */
    protected List<String> extractTaxonConstraints(GOTerm goTerm) {
        if (!isEmpty(goTerm.getTaxonConstraints())) {
            return goTerm.getTaxonConstraints().stream()
                    .map(t -> {
                        FlatFieldBuilder pubmedsAsFlatField = newFlatField();
                        t.getSourcesIds().stream().forEach(
                                s -> pubmedsAsFlatField.addField(newFlatFieldLeaf(s))
                        );

                        return newFlatField()
                                .addField(newFlatFieldLeaf(t.getGoId()))
                                .addField(newFlatFieldLeaf(t.getName()))
                                .addField(newFlatFieldLeaf(t.relationship()))
                                .addField(newFlatFieldLeaf(t.getTaxId()))
                                .addField(newFlatFieldLeaf(t.taxIdType()))
                                .addField(newFlatFieldLeaf(t.getTaxonName()))
                                .addField(pubmedsAsFlatField)
                                .buildString();
                    })
                    .collect(Collectors.toList());
        } else return null;
    }

    /*
     * format: goId|category|entityType|entityId|taxonId|ancestorGoId|reason|predictedBy
     */
    protected List<String> extractBlacklist(GOTerm goTerm){
        if (!isEmpty(goTerm.getBlacklist())) {
            return goTerm.getBlacklist().stream()
                    .map(
                            t -> newFlatField()
                                    .addField(newFlatFieldLeaf(t.getGoId()))
                                    .addField(newFlatFieldLeaf(t.getCategory()))
                                    .addField(newFlatFieldLeaf(t.getEntityType()))
                                    .addField(newFlatFieldLeaf(t.getProteinAc()))
                                    .addField(newFlatFieldLeaf(Integer.toString(t.getTaxonId())))
                                    .addField(newFlatFieldLeaf(t.getEntityName()))
                                    .addField(newFlatFieldLeaf(t.getAncestorGOID()))
                                    .addField(newFlatFieldLeaf(t.getReason()))
                                    .addField(newFlatFieldLeaf(t.getMethodId()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else return null;
    }
}
