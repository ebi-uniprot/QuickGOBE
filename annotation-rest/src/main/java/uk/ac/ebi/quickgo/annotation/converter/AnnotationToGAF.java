package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert an {@link Annotation}  to a String representation. See http://geneontology
 * .org/page/go-annotation-file-gaf-format-21}
 *
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
 * !Date downloaded from the QuickGO browser: 20170118
 * !Filtering parameters selected to generate file:
 * GAnnotation?tax=261292&count=25&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0000166	GO_REF:0000038	IEA	UniProtKB-KW:KW-0547	F 5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0005524	GO_REF:0000038	IEA	UniProtKB-KW:KW-0067	F 5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0016874	GO_REF:0000038	IEA	UniProtKB-KW:KW-0436	F 5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0030272	GO_REF:0000003	IEA	EC:6.3.3.2	F	5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0046872	GO_REF:0000038	IEA	UniProtKB-KW:KW-0479	F 5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0003677	GO_REF:0000002	IEA	InterPro:IPR000445	F	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0003824	GO_REF:0000002	IEA	InterPro:IPR011257	F	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0006281	GO_REF:0000002	IEA	InterPro:IPR011257	P	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0006284	GO_REF:0000002	IEA	InterPro:IPR003265|InterPro:IPR005760 P	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0016787	GO_REF:0000002	IEA	InterPro:IPR015797	F	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 *
 */
public class AnnotationToGAF {
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

    private ConversionUtil conversionUtil;

    AnnotationToGAF(ConversionUtil conversionUtil) {
        this.conversionUtil = conversionUtil;
    }

    /**
     * Convert an {@link Annotation} to a String representation.
     * @param annotation
     * @return String TSV delimited representation of an annotation in GAF format.
     */
    public String convert(Annotation annotation) {
        String[] idElements = conversionUtil.idToComponents(annotation);

        return idElements[0] + OUTPUT_DELIMITER +
                toCanonical(annotation.id) + OUTPUT_DELIMITER +
                annotation.symbol + OUTPUT_DELIMITER +
                annotation.qualifier + OUTPUT_DELIMITER +
                annotation.goId + OUTPUT_DELIMITER +
                annotation.reference + OUTPUT_DELIMITER +
                annotation.evidenceCode + OUTPUT_DELIMITER +
                conversionUtil.withFromAsString(annotation.withFrom) + OUTPUT_DELIMITER +
                Aspect.fromScientificName(annotation.goAspect).character + OUTPUT_DELIMITER +
                OUTPUT_DELIMITER +   // name - in GP core e.g. '5-formyltetrahydrofolate cyclo-ligase' optional not used
                OUTPUT_DELIMITER +   //synonym, - in GP core  e.g. 'Nit79A3_0905' optional not used
                toGeneProductType(idElements[0]) + OUTPUT_DELIMITER +
                "taxon:" + annotation.taxonId + OUTPUT_DELIMITER +
                conversionUtil.toYMD(annotation.date) + OUTPUT_DELIMITER +
                annotation.assignedBy + OUTPUT_DELIMITER +
                conversionUtil.extensionsAsString(annotation.extensions) + OUTPUT_DELIMITER +
                (UNIPROT_KB.equals(idElements[0]) ? String.format("%s:%s", UNIPROT_KB, idElements[1]) : "");
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

    /**
     * Extract the canonical version of the id, removing the variation or isoform suffix if it exists.
     * @param idCouldHaveVariationOrIsoform
     * @return
     */
    private static String toCanonical(String idCouldHaveVariationOrIsoform) {
        Matcher uniprotMatcher = UNIPROT_CANONICAL_PATTERN.matcher(idCouldHaveVariationOrIsoform);
        if (uniprotMatcher.matches()) {
            return uniprotMatcher.group(CANONICAL_GROUP_NUMBER);
        }
        Matcher rnaMatcher = RNA_CENTRAL_CANONICAL_PATTERN.matcher(idCouldHaveVariationOrIsoform);
        if (rnaMatcher.matches()) {
            return rnaMatcher.group(CANONICAL_GROUP_NUMBER);
        }

        Matcher intactMatcher = INTACT_CANONICAL_PATTERN.matcher(idCouldHaveVariationOrIsoform);
        if (intactMatcher.matches()) {
            return intactMatcher.group(INTACT_GROUP_NUMBER);
        }
        throw new IllegalArgumentException(String.format("Can not extract the canonical version of the id from %s",
                                                         idCouldHaveVariationOrIsoform));
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
