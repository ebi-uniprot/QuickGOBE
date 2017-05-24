package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 * Convert an {@link Annotation} to a String representation of the view seen in QuickGO front end.
 * DB           ID	    Symbol	Qualifier	GO ID       GO Name         Aspect	    Evidence    Reference
 * With	            Taxon	Date        Source  Splice
 * UniProtKB	Q4VCS5	AMOT	-	        GO:0005515	protein binding	Function	IPI	        PMID:11257124
 * UniProtKB:P00747	9606	20051212	HGNC	-
 * UniProtKB	Q4VCS5	AMOT	-	        GO:0005515	protein binding	Function	IPI	        PMID:16043488
 * UniProtKB:Q6RHR9-2	9606	20051207	UniProt	UniProtKB:Q4VCS5-1
 *
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 14:56
 * Created with IntelliJ IDEA.
 */
public class AnnotationToTSV extends AnnotationTo implements BiFunction<Annotation, List<String>, List<String>> {

    private static final String YEAR_MONTH_DAY = "yyyyMMdd";
    private Logger LOGGER = LoggerFactory.getLogger(AnnotationToTSV.class);

    @Override public List<String> apply(Annotation annotation, List<String> selectedFields) {
        if (Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty()) {
            return Collections.singletonList(toOutputRecord(annotation, selectedFields,null));
        } else {
            return annotation.slimmedIds.stream()
                                        .map(slimmedToGoId -> this.toOutputRecord(annotation, selectedFields, slimmedToGoId))
                                        .collect(toList());
        }

    }

    private String toOutputRecord(Annotation annotation, List<String> selectedFields, String slimmedToGoId) {

        LOGGER.info("Converting annotation to TSV output record to download.");

        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        //todo replace annotation.date with annotation.dateTime,
        //todo then we can replace DateFormat with DateTimeFormatter from java 8
        //todo DateFormat is not thread safe so we cannot use an instance variable
        final SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY);
        //todo missing the following fields that come from other services yet to be plugged in
        //todo name, synonym, type from the gene product service
        if (selectedFields.isEmpty() || selectedFields.contains("geneProductId")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.geneProductId));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("symbol")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.symbol));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("qualifier")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.qualifier));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("goId")) {
            if (Objects.nonNull(slimmedToGoId)) {
                tsvJoiner.add(slimmedToGoId);
            }
            tsvJoiner.add(nullToEmptyString.apply(annotation.goId));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("goName")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.goName));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("evidenceCode")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.evidenceCode));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("goEvidence")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.goEvidence));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("reference")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.reference));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("withFrom")) {
            tsvJoiner.add(withFromAsString(annotation.withFrom));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("taxonId")) {
            tsvJoiner.add(annotation.taxonId == 0 ? "" :Integer.toString(annotation.taxonId));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("assignedBy")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.assignedBy));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("extensions")) {
            tsvJoiner.add(extensionsAsString(annotation.extensions));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("date")) {
            tsvJoiner.add(annotation.date == null ?"" : dateFormat.format(annotation.date));
        }
        if (selectedFields.isEmpty() || selectedFields.contains("taxonName")) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.taxonName));
        }
        return tsvJoiner.toString();
    }
}
