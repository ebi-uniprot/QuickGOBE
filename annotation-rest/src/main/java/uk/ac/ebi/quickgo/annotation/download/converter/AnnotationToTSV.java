package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Convert an {@link Annotation} to a String representation of the view seen in QuickGO front end.
 * DB           ID	    Symbol	Qualifier	GO ID       GO Name         Aspect	    Evidence    Reference       With	            Taxon	Date        Source  Splice
 * UniProtKB	Q4VCS5	AMOT	-	        GO:0005515	protein binding	Function	IPI	        PMID:11257124	UniProtKB:P00747	9606	20051212	HGNC	-
 * UniProtKB	Q4VCS5	AMOT	-	        GO:0005515	protein binding	Function	IPI	        PMID:16043488	UniProtKB:Q6RHR9-2	9606	20051207	UniProt	UniProtKB:Q4VCS5-1
 *
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 14:56
 * Created with IntelliJ IDEA.
 */
public class AnnotationToTSV extends AnnotationTo implements Function<Annotation, List<String>> {

    @Override public List<String> apply(Annotation annotation) {
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
        return tsvJoiner.add(annotation.geneProductId)
                        .add(nullToEmptyString.apply(annotation.symbol))
                        .add(nullToEmptyString.apply(annotation.qualifier))
                        .add(nullToEmptyString.apply(goId))
                        .add(nullToEmptyString.apply(annotation.goName))
                        .add(nullToEmptyString.apply(annotation.evidenceCode + " (" + annotation.goEvidence + ")"))
                        .add(nullToEmptyString.apply(annotation.reference))
                        .add(withFromAsString(annotation.withFrom))
                        .add(Integer.toString(annotation.taxonId))
                        .add(nullToEmptyString.apply(annotation.assignedBy))
                        .add(extensionsAsString(annotation.extensions)).toString();
    }

}
