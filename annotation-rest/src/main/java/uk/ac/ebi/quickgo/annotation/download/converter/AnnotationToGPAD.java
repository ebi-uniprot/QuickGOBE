package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

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
public class AnnotationToGPAD extends AnnotationTo implements BiFunction<Annotation, List<String>, List<String>> {

    private static final String GO_EVIDENCE = "goEvidence=";

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
        String[] idElements = idToComponents(annotation.geneProductId);
        return tsvJoiner.add(idElements[0])
                        .add(idElements[1])
                        .add(nullToEmptyString.apply(annotation.qualifier))
                        .add(nullToEmptyString.apply(goId))
                        .add(nullToEmptyString.apply(annotation.reference))
                        .add(nullToEmptyString.apply(annotation.evidenceCode))
                        .add(withFromAsString(annotation.withFrom))
                        .add(taxonIdAsString(annotation.interactingTaxonId))
                        .add(toYMD(annotation.date))
                        .add(nullToEmptyString.apply(annotation.assignedBy))
                        .add(extensionsAsString(annotation.extensions))
                        .add(GO_EVIDENCE + nullToEmptyString.apply(annotation.goEvidence)).toString();
    }
}
