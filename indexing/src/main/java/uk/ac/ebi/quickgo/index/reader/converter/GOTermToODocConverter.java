package uk.ac.ebi.quickgo.index.reader.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

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

            GOTerm goTerm = termOptional.get();
            OntologyDocument doc = ontologyDocument.get();

            doc.annotationGuidelines = extractAnnGuidelines(goTerm);
            doc.aspect = goTerm.getAspect() == null ?
                    null : goTerm.getAspect().text;
            doc.children = extractChildren(goTerm);
            doc.taxonConstraints = extractTaxonConstraints(goTerm);
            doc.usage = goTerm.getUsage() == null ?
                    null : goTerm.getUsage().getText();
            doc.blacklist = extractBlacklist(goTerm);

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
                            t -> FlatFieldBuilder.newFlatField()
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getTitle()))
                                    .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getUrl()))
                                    .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /*
     * format: ancestorId|ancestorName|relationship|taxId|taxIdType|taxName|pubMedId1&pubMedId2
     */
    protected List<String> extractTaxonConstraints(GOTerm goTerm) {
        if (!isEmpty(goTerm.getTaxonConstraints())) {
            return goTerm.getTaxonConstraints().stream()
                    .map(t -> {
                        FlatFieldBuilder pubmedsAsFlatField = FlatFieldBuilder.newFlatField();
                        t.getSourcesIds().stream().forEach(
                                s -> pubmedsAsFlatField.addField(FlatFieldLeaf.newFlatFieldLeaf(s))
                        );

                        return FlatFieldBuilder.newFlatField()
                                .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getGoId()))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getName()))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf(t.relationship()))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getTaxId()))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf(t.taxIdType()))
                                .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getTaxonName()))
                                .addField(pubmedsAsFlatField)
                                .buildString();
                    })
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    protected List<String> extractBlacklist(GOTerm goTerm) {
        if (!isEmpty(goTerm.getBlacklist())) {
            return goTerm.getBlacklist().stream()
                    .map(t -> FlatFieldBuilder.newFlatField()
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getGoId()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getCategory()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getEntityType()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getProteinAc()))       //entityID
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(Integer.toString(t.getTaxonId())))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getEntityName()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getAncestorGOID()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getReason()))
                            .addField(FlatFieldLeaf.newFlatFieldLeaf(t.getMethodId()))
                            .buildString())
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
