package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.common.model.Aspect;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Convert an {@link Annotation}  to a String representation.
 * See http://geneontology.org/page/go-annotation-file-gaf-format-21}
 *
 * An excerpt from a GAF file is below:
 <pre>
 UniProtKB	Q4VCS5	AMOT		GO:0001570	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P
 Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 UniProtKB	Q4VCS5	AMOT		GO:0001701	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P
 Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 UniProtKB	Q4VCS5	AMOT		GO:0001702	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P
 Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 UniProtKB	Q4VCS5	AMOT		GO:0001725	PMID:16043488	IDA		C	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071
 protein	taxon:9606	20051207	UniProt		UniProtKB:Q4VCS5-1
 UniProtKB	Q4VCS5	AMOT		GO:0001726	PMID:11257124	IDA		C	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071
 protein	taxon:9606	20091109	MGI
 UniProtKB	Q4VCS5	AMOT		GO:0003365	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P
 Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 UniProtKB	Q4VCS5	AMOT		GO:0004872	PMID:11257124	IDA		F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071
 protein	taxon:9606	20091109	MGI
 UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:11257124	IPI	UniProtKB:P00747	F	Angiomotin
 AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20051212	HGNC
 UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:16043488	IPI	UniProtKB:Q6RHR9-2	F	Angiomotin
 AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20051207	UniProt		UniProtKB:Q4VCS5-1
 UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:19615732	IPI	UniProtKB:P35240	F	Angiomotin
 AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170108	IntAct
 </pre>
 *
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:54
 * Created with IntelliJ IDEA.
 */
public class AnnotationToGAF extends AnnotationTo implements BiFunction<Annotation, List<String>, List<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationToGAF.class);
    private static final String UNIPROT_KB = "UniProtKB";
    private static final String TAXON = "taxon:";
    private static final Set<String> NOT_GAF_QUALIFIERS =
            new HashSet<>(asList("NOT|enables", "NOT|part_of", "NOT|involved_in"));
    private static final Set<String> VALID_GAF_QUALIFIERS =
            new HashSet<>(asList("contributes_to", "NOT|contributes_to", "colocalizes_with", "NOT|colocalizes_with"));
    private final Function<String, String> toCanonical = new IdCanonicaliser();
    private final Function<String, String> createCanonical = toCanonical.compose(nullToEmptyString);

    /**
     * Convert an {@link Annotation} to a String representation.
     * @param annotation instance
     * @param selectedFields ignore for GAF
     * @return String TSV delimited representation of an annotation in GAF format.
     *
     */
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
        String[] idElements = idToComponents(annotation.geneProductId);
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);

        return tsvJoiner.add(idElements[0])
                .add(createCanonical.apply(annotation.geneProductId))
                .add(nullToEmptyString.apply(annotation.symbol))
                .add(gafQualifierAsString(annotation.qualifier))
                .add(nullToEmptyString.apply(goId))
                .add(nullToEmptyString.apply(annotation.reference))
                .add(nullToEmptyString.apply(annotation.evidenceCode))
                .add(withFromAsString(annotation.withFrom))
                .add(aspectAsString(annotation.goAspect))
                .add("")   // name    - in GP core optional not used
                .add("")   // synonym - in GP core  e.g. 'Nit79A3_0905' optional not used
                .add(nullToEmptyString.apply(toGeneProductType(idElements[DB])))
                .add(TAXON + annotation.taxonId)
                .add(toYMD(annotation.date))
                .add(nullToEmptyString.apply(annotation.assignedBy))
                .add(extensionsAsString(annotation.extensions))
                .add(UNIPROT_KB.equals(idElements[0]) ? String.format("%s:%s", UNIPROT_KB,
                        idElements[1]) : "").toString();
    }

    private String gafQualifierAsString(String qualifier) {
        String annotationQualifier = nullToEmptyString.apply(qualifier);

        String gafQualifier;
        if (NOT_GAF_QUALIFIERS.contains(annotationQualifier)) {
            gafQualifier = "NOT";
        } else if (VALID_GAF_QUALIFIERS.contains(annotationQualifier)) {
            gafQualifier = annotationQualifier;
        } else {
            gafQualifier = "";
        }

        return gafQualifier;
    }

    private String aspectAsString(String goAspect) {
        return Aspect.fromScientificName(goAspect).map(Aspect::getCharacter).orElse("");
    }

    private static class IdCanonicaliser implements Function<String, String> {
        private static final int CANONICAL_GROUP_NUMBER = 2;
        private static final int INTACT_GROUP_NUMBER = 1;
        private static final String UNIPROT_CANONICAL_REGEX = "^(?:UniProtKB:)?(([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]" +
                "([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1})$";
        private static final Pattern UNIPROT_CANONICAL_PATTERN = Pattern.compile(UNIPROT_CANONICAL_REGEX);
        private static final String RNA_CENTRAL_REGEX = "^(?:RNAcentral:)?((URS[0-9A-F]{10})(_[0-9]+){0,1})$";
        private static final Pattern RNA_CENTRAL_CANONICAL_PATTERN = Pattern.compile(RNA_CENTRAL_REGEX);
        private static final String INTACT_CANONICAL_REGEX = "^(?:IntAct:)(EBI-[0-9]+)$";
        private static final Pattern INTACT_CANONICAL_PATTERN = Pattern.compile(INTACT_CANONICAL_REGEX);

        /**
         * Extract the canonical version of the id, removing the variation or isoform suffix if it exists.
         * @param id Annotation id, could had isoform or variant suffix.
         * @return canonical form of the id with the isoform or variant suffix removed.
         */
        @Override public String apply(String id) {
            Matcher uniprotMatcher = UNIPROT_CANONICAL_PATTERN.matcher(id);
            if (uniprotMatcher.matches()) {
                return uniprotMatcher.group(CANONICAL_GROUP_NUMBER);
            }
            Matcher rnaMatcher = RNA_CENTRAL_CANONICAL_PATTERN.matcher(id);
            if (rnaMatcher.matches()) {
                return rnaMatcher.group(CANONICAL_GROUP_NUMBER);
            }

            Matcher intactMatcher = INTACT_CANONICAL_PATTERN.matcher(id);
            if (intactMatcher.matches()) {
                return intactMatcher.group(INTACT_GROUP_NUMBER);
            }
            LOGGER.error(String.format("Cannot extract the canonical version of the id from \"%s\"", id));
            return "";
        }
    }
}
