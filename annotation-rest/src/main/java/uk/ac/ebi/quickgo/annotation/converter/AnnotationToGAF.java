package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert an {@link Annotation}  to a String representation. See http://geneontology
 * .org/page/go-annotation-file-gaf-format-21}
 *
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:54
 * Created with IntelliJ IDEA.
 *
 * Column	Content	Required?	Cardinality	Example
 * 1	DB	required	1	UniProtKB
 * 2	DB Object ID	required	1	P12345
 * 3	DB Object Symbol	required	1	PHO3
 * 4	Qualifier	optional	0 or greater	NOT
 * 5	GO ID	required	1	GO:0003993
 * 6	DB:Reference (|DB:Reference)	required	1 or greater	PMID:2676709
 * 7	Evidence Code	required	1	IMP
 * 8	With (or) From	optional	0 or greater	GO:0000346
 * 9	Aspect	required	1	F
 * 10	DB Object Name	optional	0 or 1	Toll-like receptor 4
 * 11	DB Object Synonym (|Synonym)	optional	0 or greater	hToll|Tollbooth
 * 12	DB Object Type	required	1	protein
 * 13	Taxon(|taxon)	required	1 or 2	taxon:9606
 * 14	Date	required	1	20090118
 * 15	Assigned By	required	1	SGD
 * 16	Annotation Extension	optional	0 or greater	part_of(CL:0000576)
 * 17	Gene Product Form ID	optional	0 or 1	UniProtKB:P12345-2
 *
 *
 * !gaf-version: 2.1
 * !Project_name: UniProt GO Annotation (UniProt-GOA)
 * !URL: http://www.ebi.ac.uk/GOA
 * !Contact Email: goa@ebi.ac.uk
 * !Date downloaded from the QuickGO browser: 20170123
 * !Filtering parameters selected to generate file:
 * GAnnotation?count=25&protein=Q4VCS5&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
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
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:21187284	IPI	UniProtKB:P46937	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20120124	UniProt		UniProtKB:Q4VCS5-1
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:21481793	IPI	UniProtKB:P35240	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170108	IntAct		UniProtKB:Q4VCS5-1
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:21481793	IPI	UniProtKB:P35240	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170108	IntAct		UniProtKB:Q4VCS5-2
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:21481793	IPI	UniProtKB:P35240	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170108	IntAct
 * UniProtKB	Q4VCS5	AMOT		GO:0005515	PMID:21481793	IPI	UniProtKB:Q68EM7	F	Angiomotin	AMOT_HUMAN|AMOT|KIAA1071	protein	taxon:9606	20170108	IntAct
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
        public String apply(Annotation annotation) {
            String[] idElements = idToComponents(annotation);

            return idElements[0] + OUTPUT_DELIMITER +
                    toCanonical(annotation.id) + OUTPUT_DELIMITER +
                    annotation.symbol + OUTPUT_DELIMITER +
                    annotation.qualifier + OUTPUT_DELIMITER +
                    idOrSlimmedId(annotation) + OUTPUT_DELIMITER +
                    annotation.reference + OUTPUT_DELIMITER +
                    annotation.evidenceCode + OUTPUT_DELIMITER +
                    withFromAsString(annotation.withFrom) + OUTPUT_DELIMITER +
                    Aspect.fromScientificName(annotation.goAspect).character + OUTPUT_DELIMITER +
                    OUTPUT_DELIMITER +
                    // name - in GP core e.g. '5-formyltetrahydrofolate cyclo-ligase' optional not used
                    OUTPUT_DELIMITER +   //synonym, - in GP core  e.g. 'Nit79A3_0905' optional not used
                    toGeneProductType(idElements[0]) + OUTPUT_DELIMITER +
                    "taxon:" + annotation.taxonId + OUTPUT_DELIMITER +
                    toYMD(annotation.date) + OUTPUT_DELIMITER +
                    annotation.assignedBy + OUTPUT_DELIMITER +
                    extensionsAsString(annotation.extensions) + OUTPUT_DELIMITER +
                    (UNIPROT_KB.equals(idElements[0]) ? String.format("%s:%s", UNIPROT_KB, idElements[1]) : "");
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
        System.out.println(idElement);
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
