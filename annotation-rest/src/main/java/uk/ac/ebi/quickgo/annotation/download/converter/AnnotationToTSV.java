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

    public static final String GENE_PRODUCT_ID_FIELD_NAME = "geneproductid";
    public static final String SYMBOL_FIELD_NAME = "symbol";
    public static final String QUALIFIER_FIELD_NAME = "qualifier";
    public static final String GO_TERM_FIELD_NAME = "goid";
    public static final String GO_NAME_FIELD_NAME = "goname";
    public static final String ECO_ID_FIELD_NAME = "evidencecode";
    public static final String GO_EVIDENCE_CODE_FIELD_NAME = "goevidence";
    public static final String REFERENCE_FIELD_NAME = "reference";
    public static final String WITH_FROM_FIELD_NAME = "withfrom";
    public static final String TAXON_ID_FIELD_NAME = "taxonid";
    public static final String ASSIGNED_BY_FIELD_NAME = "assignedby";
    public static final String ANNOTATION_EXTENSION_FIELD_NAME = "extensions";
    public static final String DATE_FIELD_NAME = "date";
    public static final String TAXON_NAME_FIELD_NAME = "taxonname";

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
        if (selectedFields.isEmpty() || selectedFields.contains(GENE_PRODUCT_ID_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.geneProductId));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(SYMBOL_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.symbol));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(QUALIFIER_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.qualifier));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GO_TERM_FIELD_NAME)) {
            if (Objects.nonNull(slimmedToGoId)) {
                tsvJoiner.add(slimmedToGoId);
            }
            tsvJoiner.add(nullToEmptyString.apply(annotation.goId));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GO_NAME_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.goName));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(ECO_ID_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.evidenceCode));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GO_EVIDENCE_CODE_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.goEvidence));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(REFERENCE_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.reference));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(WITH_FROM_FIELD_NAME)) {
            tsvJoiner.add(withFromAsString(annotation.withFrom));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(TAXON_ID_FIELD_NAME)) {
            tsvJoiner.add(annotation.taxonId == 0 ? "" :Integer.toString(annotation.taxonId));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(ASSIGNED_BY_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.assignedBy));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(ANNOTATION_EXTENSION_FIELD_NAME)) {
            tsvJoiner.add(extensionsAsString(annotation.extensions));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(DATE_FIELD_NAME)) {
            tsvJoiner.add(annotation.date == null ?"" : dateFormat.format(annotation.date));
        }
        if (selectedFields.isEmpty() || selectedFields.contains(TAXON_NAME_FIELD_NAME)) {
            tsvJoiner.add(nullToEmptyString.apply(annotation.taxonName));
        }
        return tsvJoiner.toString();
    }
}
