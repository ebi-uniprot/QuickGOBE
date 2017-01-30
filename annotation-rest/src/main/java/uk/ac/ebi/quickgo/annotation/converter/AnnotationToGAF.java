package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert an {@link Annotation}  to a String representation.
 * See http://geneontology.org/page/go-annotation-file-gaf-format-21}
 *
 * An except from a GAF file is below:
 * UniProtKB	Q4VCS5	AMOT		GO:0001570	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 * UniProtKB	Q4VCS5	AMOT		GO:0001701	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 * UniProtKB	Q4VCS5	AMOT		GO:0001702	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 * UniProtKB	Q4VCS5	AMOT		GO:0001725	PMID:16043488	IDA		C	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20051207	UniProt		UniProtKB:Q4VCS5-1
 * UniProtKB	Q4VCS5	AMOT		GO:0001726	PMID:11257124	IDA		C	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20091109	MGI
 * UniProtKB	Q4VCS5	AMOT		GO:0003365	GO_REF:0000107	IEA	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	P	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170107	Ensembl
 * UniProtKB	Q4VCS5	AMOT		GO:0004872	PMID:11257124	IDA		F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20091109	MGI
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:11257124	IPI	UniProtKB:P00747	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20051212	HGNC
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:16043488	IPI	UniProtKB:Q6RHR9-2	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20051207	UniProt		UniProtKB:Q4VCS5-1
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:19615732	IPI	UniProtKB:P35240	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170108	IntAct
 *
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:54
 * Created with IntelliJ IDEA.
 */
public class AnnotationToGAF extends AnnotationTo implements Function<Annotation, String>{
    static final String OUTPUT_DELIMITER = "\t";
    private static final String UNIPROT_KB = "UniProtKB";
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
         * Convert an {@link Annotation} to a String representation.
         * @param annotation instance
         * @return String TSV delimited representation of an annotation in GAF format.
         */
        @Override
        public String apply(Annotation annotation) {
            String[] idElements = idToComponents(annotation);
            StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
            return tsvJoiner.add(idElements[0])
                            .add(toCanonical(annotation.id))
                            .add(annotation.symbol)
                            .add(annotation.qualifier)
                            .add(idOrSlimmedId(annotation))
                            .add(annotation.reference)
                            .add(annotation.evidenceCode)
                            .add(withFromAsString(annotation.withFrom))
                            .add(Aspect.fromScientificName(annotation.goAspect).character)
                            .add("")   // name - in GP core optional not used
                            .add("")   //synonym, - in GP core  e.g. 'Nit79A3_0905' optional not used
                            .add(toGeneProductType(idElements[0]))
                            .add("taxon:" + annotation.taxonId)
                            .add(toYMD(annotation.date))
                            .add(annotation.assignedBy)
                            .add(extensionsAsString(annotation.extensions))
                            .add(UNIPROT_KB.equals(idElements[0]) ? String.format("%s:%s", UNIPROT_KB,
                                                                                   idElements[1]) : "").toString();
        }

    /**
     * Extract the canonical version of the id, removing the variation or isoform suffix if it exists.
     * @param id Annotation id, could had isoform or variant suffix.
     * @return canonical form of the id with the soform or variant suffix removed.
     */
    private String toCanonical(String id) {
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
        throw new IllegalArgumentException(String.format("Can not extract the canonical version of the id from %s",
                                                         id));
    }

    private String toGeneProductType(String idElement) {
        switch (idElement) {
            case "UniProtKB":
                return "protein";
            case "IntAct":
                return "complex";
            case "RNAcentral":
                return "miRNA";
        }
        throw new IllegalArgumentException("Cannot determine gene product type for based on DB of " + idElement);
    }

    public enum Aspect {
        BIOLOGICAL_PROCESS("biological_process", "P"),
        MOLECULAR_FUNCTION("molecular_function", "F"),
        CELLULAR_COMPONENT("cellular_component", "C");

        private final String scientificName;
        private final String character;

        Aspect(String scientificName, String character) {
            this.scientificName = scientificName;
            this.character = character;
        }

        public static Aspect fromScientificName(String scientificName) {
            for (Aspect aspect : Aspect.values()) {
                if (aspect.scientificName.equals(scientificName)) {
                    return aspect;
                }
            }
            throw new IllegalArgumentException("Unrecognized Aspect scientificName: " + scientificName);
        }
    }
}
