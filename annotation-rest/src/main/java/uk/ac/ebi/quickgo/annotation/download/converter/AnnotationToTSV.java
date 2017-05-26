package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

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

    private static final List<String> FULL_FIELD_LIST = Arrays.asList(GENE_PRODUCT_ID_FIELD_NAME, SYMBOL_FIELD_NAME,
                                                                    QUALIFIER_FIELD_NAME, GO_TERM_FIELD_NAME,
                                                                    GO_NAME_FIELD_NAME, ECO_ID_FIELD_NAME,
                                                                    GO_EVIDENCE_CODE_FIELD_NAME,
                                                                    REFERENCE_FIELD_NAME, WITH_FROM_FIELD_NAME,
                                                                    TAXON_ID_FIELD_NAME, ASSIGNED_BY_FIELD_NAME,
                                                                    ANNOTATION_EXTENSION_FIELD_NAME, DATE_FIELD_NAME,
                                                                    TAXON_NAME_FIELD_NAME);

    private static final String YEAR_MONTH_DAY = "yyyyMMdd";

    private Map<String, BiConsumer<OutputContent, StringJoiner>> selected2Content;

    public AnnotationToTSV() {
        selected2Content = new HashMap<>();
        selected2Content.put(GENE_PRODUCT_ID_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation
                                                                                                         .geneProductId)));
        selected2Content.put(SYMBOL_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.symbol)));
        selected2Content.put(QUALIFIER_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.qualifier)));
        selected2Content.put(GO_TERM_FIELD_NAME, (c, j) -> {
            if (Objects.nonNull(c.slimmedToGoId)) {
                j.add(c.slimmedToGoId);
            }
            j.add(nullToEmptyString.apply(c.annotation.goId));
        });
        selected2Content.put(GO_NAME_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.goName)));
        selected2Content.put(ECO_ID_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.evidenceCode)));
        selected2Content.put(GO_EVIDENCE_CODE_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation
                                                                                                          .goEvidence)));
        selected2Content.put(REFERENCE_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.reference)));
        selected2Content.put(WITH_FROM_FIELD_NAME, (c, j) -> j.add(withFromAsString(c.annotation.withFrom)));
        selected2Content.put(TAXON_ID_FIELD_NAME, (c, j) -> j.add(c.annotation.taxonId == 0 ? "" : Integer.toString
                (c.annotation.taxonId)));
        selected2Content.put(ASSIGNED_BY_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation
                                                                                                     .assignedBy)));
        selected2Content.put(ANNOTATION_EXTENSION_FIELD_NAME, (c, j) -> j.add(extensionsAsString(c.annotation
                                                                                                         .extensions)));
        //todo replace annotation.date with annotation.dateTime,
        //todo then we can replace DateFormat with DateTimeFormatter from java 8
        //todo DateFormat is not thread safe so we cannot use an instance variable
        selected2Content.put(DATE_FIELD_NAME,
                             (c, j) -> {
                                 final SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY);
                                 j.add(c.annotation.date == null ? "" : dateFormat.format(c.annotation.date));
                             });
        selected2Content.put(TAXON_NAME_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.taxonName)));
        selected2Content.put(SYMBOL_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.symbol)));
        selected2Content.put(SYMBOL_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.symbol)));
        selected2Content.put(SYMBOL_FIELD_NAME, (c, j) -> j.add(nullToEmptyString.apply(c.annotation.symbol)));

    }

    @Override public List<String> apply(Annotation annotation, List<String> selectedFields) {
        final List<String> columns = whichColumnsWillWeShow(selectedFields);
        if (isSlimmedRequest(annotation)) {
            return Collections.singletonList(output(new OutputContent(annotation, columns,null)));
        } else {
            return annotation.slimmedIds.stream()
                                        .map(goId -> output(new OutputContent(annotation, columns, goId)))
                                        .collect(toList());
        }
    }

    private boolean isSlimmedRequest(Annotation annotation) {
        return Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty();
    }

    private List<String> whichColumnsWillWeShow(List<String> selectedFields) {
        return selectedFields.isEmpty() ? FULL_FIELD_LIST : selectedFields;
    }

    private String output(OutputContent outputContent) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        for (String selectedField : outputContent.selectedFields) {
            selected2Content.get(selectedField).accept(outputContent, tsvJoiner);
        }
        return tsvJoiner.toString();
    }

    private static class OutputContent{
        Annotation annotation;
        List<String> selectedFields;
        String slimmedToGoId;

        private OutputContent(Annotation annotation, List<String> selectedFields, String slimmedToGoId) {
            this.annotation = annotation;
            this.selectedFields = selectedFields;
            this.slimmedToGoId = slimmedToGoId;
        }
    }
}
