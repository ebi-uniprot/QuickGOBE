package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.download.converter.helpers.Extensions;
import uk.ac.ebi.quickgo.annotation.download.converter.helpers.GeneProduct;
import uk.ac.ebi.quickgo.annotation.download.converter.helpers.WithFrom;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.*;
import java.util.function.BiFunction;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.DateConverter.toYYYYMMDD;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Helper.nullToEmptyString;

/**
 * Convert an Annotation to the GPAD format.
 *
 * See the format here: http://geneontology.org/page/gene-product-association-data-gpad-format}
 *
 * An excerpt from a GPAD file is below:
 <pre>
     UniProtKB	A0A000	enables	GO:0003824	GO_REF:0000002	ECO:0000256	InterPro:IPR015421|InterPro:IPR015422		20170107	InterPro		go_evidence=IEA
     UniProtKB	A0A000	enables	GO:0003870	GO_REF:0000002	ECO:0000256	InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
     UniProtKB	A0A000	involved_in	GO:0009058	GO_REF:0000002	ECO:0000256	InterPro:IPR004839		20170107	InterPro		go_evidence=IEA
     UniProtKB	A0A000	enables	GO:0030170	GO_REF:0000002	ECO:0000256	InterPro:IPR004839|InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
     UniProtKB	A0A000	involved_in	GO:0033014	GO_REF:0000002	ECO:0000256	InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 </pre>
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:24
 * Created with IntelliJ IDEA.
 */
public class AnnotationToGPAD implements BiFunction<Annotation, List<String>, List<String>> {

    private static final String GO_EVIDENCE = "goEvidence=";
    private static final String OUTPUT_DELIMITER = "\t";

    @Override
    public List<String> apply(Annotation annotation, List<String> selectedFields) {
        if (Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty()) {
            return Collections.singletonList(toOutputRecord(annotation, annotation.goId));
        } else {
            return annotation.slimmedIds.stream()
                                        .map(goId -> this.toOutputRecord(annotation, goId))
                                        .collect(toList());
        }
    }

    private String toOutputRecord(Annotation annotation, String goId) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        final GeneProduct geneProduct = GeneProduct.fromString(annotation.geneProductId);
        return tsvJoiner.add(geneProduct.db()).add(geneProduct.id())
                .add(nullToEmptyString(annotation.qualifier))
                .add(nullToEmptyString(goId))
                .add(nullToEmptyString(annotation.reference))
                .add(nullToEmptyString(annotation.evidenceCode))
                .add(WithFrom.nullOrEmptyListToString(annotation.withFrom))
                        .add(taxonIdAsString(annotation.interactingTaxonId))
                .add(ofNullable(annotation.date).map(toYYYYMMDD).orElse(""))
                .add(nullToEmptyString(annotation.assignedBy))
                .add(Extensions.asString(annotation.extensions))
                .add(GO_EVIDENCE + nullToEmptyString(annotation.goEvidence)).toString();
    }

    private static final int LOWEST_VALID_TAXON_ID = 1;

    private String taxonIdAsString(int taxonId) {
        return taxonId < LOWEST_VALID_TAXON_ID ? "" : Integer.toString(taxonId);
    }

}
