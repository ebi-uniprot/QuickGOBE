package uk.ac.ebi.quickgo.annotation.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
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
 * !Filtering parameters selected to generate file: GAnnotation?tax=261292&count=25&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0000166	GO_REF:0000038	IEA	UniProtKB-KW:KW-0547	F	5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0005524	GO_REF:0000038	IEA	UniProtKB-KW:KW-0067	F	5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0016874	GO_REF:0000038	IEA	UniProtKB-KW:KW-0436	F	5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0030272	GO_REF:0000003	IEA	EC:6.3.3.2	F	5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE8	Nit79A3_0905		GO:0046872	GO_REF:0000038	IEA	UniProtKB-KW:KW-0479	F	5-formyltetrahydrofolate cyclo-ligase	F8GCE8_NITSI|Nit79A3_0905	protein	taxon:261292	20170107	UniProt
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0003677	GO_REF:0000002	IEA	InterPro:IPR000445	F	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0003824	GO_REF:0000002	IEA	InterPro:IPR011257	F	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0006281	GO_REF:0000002	IEA	InterPro:IPR011257	P	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0006284	GO_REF:0000002	IEA	InterPro:IPR003265|InterPro:IPR005760	P	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 * UniProtKB	F8GCE9	Nit79A3_0906		GO:0016787	GO_REF:0000002	IEA	InterPro:IPR015797	F	A/G-specific adenine glycosylase	F8GCE9_NITSI|Nit79A3_0906	protein	taxon:261292	20170107	InterPro
 *
 */
public class AnnotationToGAF {
    private static final String ID_DELIMITER = ":";
    private static final String OUTPUT_DELIMITER = "\t";
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
    private static final String COMMA = ",";
    private static final String PIPE = "|";
    private static final DateFormat YYYYMMDD_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static String convert(Annotation annotation) {

        String[] idElements = annotation.id.split(ID_DELIMITER);

        return idElements[0] + OUTPUT_DELIMITER +
                toCanonical(annotation.id) + OUTPUT_DELIMITER +
                annotation.symbol + OUTPUT_DELIMITER +
                annotation.qualifier + OUTPUT_DELIMITER +
                annotation.goId + OUTPUT_DELIMITER +
                annotation.reference + OUTPUT_DELIMITER +
                annotation.evidenceCode + OUTPUT_DELIMITER +
                toWithFromList(annotation.withFrom) + OUTPUT_DELIMITER +
                Aspect.fromScientificName(annotation.goAspect).character + OUTPUT_DELIMITER +
                OUTPUT_DELIMITER +   // name - in GP core e.g. '5-formyltetrahydrofolate cyclo-ligase' optional not used
                OUTPUT_DELIMITER +   //synonym, - in GP core  e.g. 'Nit79A3_0905' optional not used
                annotation.geneProductType + OUTPUT_DELIMITER +        //Available in AnnotationDocument: Protein, miRNA etc
                "taxon:" + annotation.taxonId + OUTPUT_DELIMITER +
                toYMD(annotation.date) + OUTPUT_DELIMITER +
                annotation.assignedBy + OUTPUT_DELIMITER +
                toExtensionList(annotation.extensions) +
                (UNIPROT_KB.equals(idElements[0])?UNIPROT_KB+idElements[1]: ""); // Gene Product Form ID
    }

    private static String toYMD(Date date) {
        return YYYYMMDD_DATE_FORMAT.format(date);
    }

    /**
     * Extract the canonical version of the id, removing the variation or isoform suffix if it exists.
     * @param idCouldHaveVariationOrIsoform
     * @return
     */
    private static final String toCanonical(String idCouldHaveVariationOrIsoform){
        Matcher uniprotMatcher = UNIPROT_CANONICAL_PATTERN.matcher(idCouldHaveVariationOrIsoform);
        if(uniprotMatcher.matches()){
            return uniprotMatcher.group(CANONICAL_GROUP_NUMBER);
        }
        Matcher rnaMatcher = RNA_CENTRAL_CANONICAL_PATTERN.matcher(idCouldHaveVariationOrIsoform);
        if(rnaMatcher.matches()){
            return rnaMatcher.group(CANONICAL_GROUP_NUMBER);
        }

        Matcher intactMatcher = INTACT_CANONICAL_PATTERN.matcher(idCouldHaveVariationOrIsoform);
        if(intactMatcher.matches()){
            return intactMatcher.group(INTACT_GROUP_NUMBER);
        }
        throw new IllegalArgumentException(String.format("Can not extract the canonical version of the id from %s",
                                                         idCouldHaveVariationOrIsoform));
    }



    private static String toExtensionList(List<Annotation.ConnectedXRefs> connectedXRefs){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < connectedXRefs.size(); i++) {
            Annotation.ConnectedXRefs xRefs =  connectedXRefs.get(i);
            List<Annotation.QualifiedXref> qualifiedXrefs = xRefs.getConnectedXrefs();
            for(Annotation.QualifiedXref qualifiedXref : qualifiedXrefs){
                sb.append(qualifiedXref.asXref());
            }
        }
        return sb.toString();
    }

//    private static String toWithFromList(List<Annotation.ConnectedXRefs> connectedXRefs){
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < connectedXRefs.size(); i++) {
//            Annotation.ConnectedXRefs xRefs =  connectedXRefs.get(i);
//            List<Annotation.SimpleXRef> simpleXRefs = xRefs.getConnectedXrefs();
//            sb.append(andSimpleRefs(simpleXRefs));
//            if(i<connectedXRefs.size()) sb.append(PIPE);
//        }
//        return sb.toString();
//    }

    private static String toWithFromList(List<Annotation.ConnectedXRefs> connectedXRefs){
            return connectedXRefs.stream()
                          .map(itemList -> simpleRefAndToString(itemList))
                          .collect(Collectors.joining(PIPE));
    }

//    private static String simpleRefAndToString(Annotation.ConnectedXRefs itemList) {
//        return itemList.getConnectedXrefs()
//                       .stream()
//                        .map(cr -> ((Annotation.SimpleXRef) cr).asXref())
//                        .collect(Collectors.joining(COMMA));
//    }

    private static String simpleRefAndToString(Annotation.ConnectedXRefs itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(cr -> {
                                                            Annotation.SimpleXRef sr = ((Annotation.SimpleXRef) cr);
                                                            return sr.asXref();
                                                                })
                .collect(Collectors.joining(COMMA)).toString();


    }

    private static String andSimpleRefs(List<Annotation.SimpleXRef> simpleXRefs) {
        return simpleXRefs.stream()
                   .map(sr -> sr.asXref())
                   .collect(Collectors.joining(COMMA));
    }

    //    private String toExtensionString(List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedXRefs){
//        return connectedXRefs
//                .stream()
//                .map( cx -> cx.getConnectedXrefs())
//                .flatMap(List::stream)
//                .map(this::xrefAsString)
//                .collect(Collectors.joining());
//
//    }
//
//    private String xrefAsString(Annotation.ConnectedXRefs xRefs){
//        List<Annotation.QualifiedXref> qualifiedXrefs = (List<Annotation.QualifiedXref>)xRefs;
//        return qualifiedXrefs.stream().map(Annotation.QualifiedXref::asXref).collect(Collectors.joining(","));
//    }
//
//
//    private static final String toExtensionString(List<Annotation.ConnectedXRefs> connectedXRefs){
//
//        connectedXRefs.forEach();
//    }


    private static <T extends Annotation.AbstractXref> List<Annotation.ConnectedXRefs<T>> connectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream().map(itemList -> {
                                      Annotation.ConnectedXRefs<T> xrefs = new Annotation.ConnectedXRefs<>();
                                      itemList.stream().map(Supplier::get).forEach(xrefs::addXref);
                                      return xrefs;
                                  }
        ).collect(Collectors.toList());
    }

    private static <T extends Annotation.AbstractXref> List<String> stringsForConnectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream()
                    .map(itemList ->
                                 itemList.stream()
                                         .map(Supplier::toString).collect(Collectors.joining(COMMA))
                    ).collect(Collectors.toList());
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
