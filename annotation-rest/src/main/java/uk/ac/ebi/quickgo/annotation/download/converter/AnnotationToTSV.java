package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger LOGGER = LoggerFactory.getLogger(AnnotationToTSV.class);

    private static final String YEAR_MONTH_DAY = "yyyyMMdd";

    @Override public List<String> apply(Annotation annotation) {
        if (Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty()) {
            return Collections.singletonList(toOutputRecord(annotation));
        } else {
            return annotation.slimmedIds.stream()
                                        .map(goId -> this.toSlimmedOutputRecord(annotation, goId))
                                        .collect(toList());
        }
    }

    private String toOutputRecord(Annotation annotation) {

        LOGGER.info("Converting annotation to TSV output record to download.");

        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        //todo replace annotation.date with annotation.dateTime,
        //todo then we can replace DateFormat with DateTimeFormatter from java 8
        //todo DateFormat is not thread safe so we cannot use an instance variable
        final SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY);
        //todo missing the following fields that come from other services yet to be plugged in
        //todo name, synonym, type from the gene product service
        return tsvJoiner.add(nullToEmptyString.apply(annotation.geneProductId))
                        .add(nullToEmptyString.apply(annotation.symbol))
                        .add(nullToEmptyString.apply(annotation.qualifier))
                        .add(nullToEmptyString.apply(annotation.goId))
                        .add(nullToEmptyString.apply(annotation.goName))
                        .add(determineEvidence(annotation))
                        .add(nullToEmptyString.apply(annotation.reference))
                        .add(withFromAsString(annotation.withFrom))
                        .add(annotation.taxonId == 0 ? "" : Integer.toString(annotation.taxonId))
                        .add(nullToEmptyString.apply(annotation.assignedBy))
                        .add(extensionsAsString(annotation.extensions))
                        .add(annotation.date == null? "":dateFormat.format(annotation.date))
                        .add(nullToEmptyString.apply(annotation.taxonName))
                        .toString();
    }

    private String determineEvidence(Annotation annotation) {
        StringBuilder evidenceBuilder = new StringBuilder();
        evidenceBuilder.append(nullToEmptyString.apply((annotation.evidenceCode)));
        if ((annotation.goEvidence != null) && !annotation.goEvidence.isEmpty()) {
            evidenceBuilder.append("(").append(annotation.goEvidence).append(")");
        }
        return evidenceBuilder.toString();
    }

    private String toSlimmedOutputRecord(Annotation annotation, String goId) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        //todo replace annotation.date with annotation.dateTime,
        //todo then we can replace DateFormat with DateTimeFormatter from java 8
        //todo DateFormat is not thread safe so we cannot use an instance variable
        final SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY);
        //todo missing the following fields that come from other services yet to be plugged in
        //todo name, synonym, type from the gene product service
        return tsvJoiner.add(nullToEmptyString.apply(annotation.geneProductId))
                        .add(nullToEmptyString.apply(annotation.symbol))
                        .add(nullToEmptyString.apply(annotation.qualifier))
                        .add(nullToEmptyString.apply(goId))
                        .add(nullToEmptyString.apply(annotation.goName))
                        .add(nullToEmptyString.apply(annotation.goId))
                        .add(nullToEmptyString.apply(annotation.evidenceCode + " (" + annotation.goEvidence + ")"))
                        .add(nullToEmptyString.apply(annotation.reference))
                        .add(withFromAsString(annotation.withFrom))
                        .add(Integer.toString(annotation.taxonId))
                        .add(nullToEmptyString.apply(annotation.assignedBy))
                        .add(extensionsAsString(annotation.extensions))
                        .add(nullToEmptyString.apply(dateFormat.format(annotation.date)))
                        .add(nullToEmptyString.apply(annotation.taxonName))
                        .toString();

    }

}
