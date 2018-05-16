package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.download.converter.helpers.Extensions;
import uk.ac.ebi.quickgo.annotation.download.converter.helpers.WithFrom;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.common.model.Aspect;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.quickgo.annotation.download.TSVDownload.*;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.DateConverter.ISO_8601_FORMATTER;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Helper.nullToEmptyString;

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
public class AnnotationToTSV implements BiFunction<Annotation, List<String>, List<String>> {

    private static final Map<String, BiConsumer<OutputContent, StringJoiner>> selected2Content =
            initialiseContentMappings();
    static final String OUTPUT_DELIMITER = "\t";

    public AnnotationToTSV() {
    }

    @Override public List<String> apply(Annotation annotation, List<String> selectedFields) {
        final List<String> columns = whichColumnsWillWeShow(selectedFields);
        if (isSlimmedRequest(annotation)) {
            return Collections.singletonList(output(new OutputContent(annotation, columns, null)));
        } else {
            return annotation.slimmedIds.stream()
                    .map(goId -> output(new OutputContent(annotation, columns, goId)))
                    .collect(toList());
        }
    }

    private static Map<String, BiConsumer<OutputContent, StringJoiner>> initialiseContentMappings() {
        Map<String, BiConsumer<OutputContent, StringJoiner>> selected2Content = new HashMap<>();
        selected2Content.put(GENE_PRODUCT_FIELD_NAME,
                (c, j) -> {
                    String[] elements = nullToEmptyString(c.annotation.geneProductId).split(":");
                    if (elements.length == 2) {
                        j.add(elements[0]);
                        j.add(elements[1]);
                    } else {
                        j.add("");
                        j.add("");
                    }
                });
        selected2Content.put(SYMBOL_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.symbol)));
        selected2Content.put(QUALIFIER_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.qualifier)));
        selected2Content.put(GO_TERM_FIELD_NAME, (c, j) -> {
            if (Objects.nonNull(c.slimmedToGoId)) {
                j.add(c.slimmedToGoId);
            }
            j.add(nullToEmptyString(c.annotation.goId));
        });
        selected2Content.put(GO_ASPECT_FIELD_NAME,
                (c, j) -> j.add(Aspect.fromScientificName(c.annotation.goAspect)
                        .map(Aspect::getCharacter)
                        .orElse("")));
        selected2Content.put(GO_NAME_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.goName)));
        selected2Content.put(ECO_ID_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.evidenceCode)));
        selected2Content.put(GO_EVIDENCE_CODE_FIELD_NAME,
                (c, j) -> j.add(nullToEmptyString(c.annotation.goEvidence)));
        selected2Content.put(REFERENCE_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.reference)));
        selected2Content.put(WITH_FROM_FIELD_NAME, (c, j) -> j.add(WithFrom.nullOrEmptyListToString(c.annotation.withFrom)));
        selected2Content.put(TAXON_ID_FIELD_NAME,
                (c, j) -> j.add(c.annotation.taxonId == 0 ? "" : Integer.toString(c.annotation.taxonId)));
        selected2Content.put(ASSIGNED_BY_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.assignedBy)));
        selected2Content.put(ANNOTATION_EXTENSION_FIELD_NAME,
                (c, j) -> j.add(Extensions.asString(c.annotation.extensions)));
        selected2Content.put(DATE_FIELD_NAME,
                (c, j) -> j.add(ofNullable(c.annotation.date).map(ISO_8601_FORMATTER).orElse("")));
        selected2Content.put(TAXON_NAME_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.taxonName)));
        selected2Content.put(GENE_PRODUCT_NAME_FIELD_NAME, (c, j) -> j.add(nullToEmptyString(c.annotation.name)));
        selected2Content.put(GENE_PRODUCT_SYNONYMS_FIELD_NAME,
                (c, j) -> j.add(nullToEmptyString(c.annotation.synonyms)));
        selected2Content.put(GENE_PRODUCT_TYPE_FIELD_NAME, (c, j) -> j.add(c.annotation.getGeneProduct().type()));
        return selected2Content;
    }

    private boolean isSlimmedRequest(Annotation annotation) {
        return Objects.isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty();
    }

    private String output(OutputContent outputContent) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        for (String selectedField : outputContent.selectedFields) {
            selected2Content.get(selectedField).accept(outputContent, tsvJoiner);
        }
        return tsvJoiner.toString();
    }

    private static class OutputContent {
        final Annotation annotation;
        final List<String> selectedFields;
        final String slimmedToGoId;

        private OutputContent(Annotation annotation, List<String> selectedFields, String slimmedToGoId) {
            this.annotation = annotation;
            this.selectedFields = selectedFields;
            this.slimmedToGoId = slimmedToGoId;
        }
    }
}
