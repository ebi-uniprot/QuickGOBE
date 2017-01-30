package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Convert an Annotation to the GPAD format.
 *
 * See the format here: http://geneontology.org/page/gene-product-association-data-gpad-format}
 *
 * An except from a GPAD file is below:
 * UniProtKB	A0A000	enables	GO:0003824	GO_REF:0000002	ECO:0000256	InterPro:IPR015421|InterPro:IPR015422		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	enables	GO:0003870	GO_REF:0000002	ECO:0000256	InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	involved_in	GO:0009058	GO_REF:0000002	ECO:0000256	InterPro:IPR004839		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	enables	GO:0030170	GO_REF:0000002	ECO:0000256	InterPro:IPR004839|InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	involved_in	GO:0033014	GO_REF:0000002	ECO:0000256	InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 *
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:24
 * Created with IntelliJ IDEA.
 */
public class AnnotationToGPAD extends AnnotationTo implements Function<Annotation, List<String>> {

    static final String OUTPUT_DELIMITER = "\t";

    @Override
    public List<String> apply(Annotation annotation) {
        if (Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty()) {
            return Collections.singletonList(toOutputRecord(annotation, annotation.id));
        } else {
            return annotation.slimmedIds.stream()
                                        .map(goId -> this.toOutputRecord(annotation, goId))
                                        .collect(toList());
        }
    }

    private String toOutputRecord(Annotation annotation, String id) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        String[] idElements = idToComponents(annotation);
        return tsvJoiner.add(idElements[0])
                        .add(idElements[1])
                        .add(annotation.qualifier)
                        .add(id)
                        .add(annotation.reference)
                        .add(annotation.evidenceCode)
                        .add(withFromAsString(annotation.withFrom))
                        .add(annotation.interactingTaxonId)
                        .add(toYMD(annotation.date))
                        .add(annotation.assignedBy)
                        .add(extensionsAsString(annotation.extensions))
                        .add("goEvidence=" + annotation.goEvidence).toString();
    }
}
