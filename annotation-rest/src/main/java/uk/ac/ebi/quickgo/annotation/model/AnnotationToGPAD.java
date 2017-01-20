package uk.ac.ebi.quickgo.annotation.model;

/**
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:24
 * Created with IntelliJ IDEA.

 *
 *      Column	Content	Required?	Cardinality	Example
 * 1 	DB	required	1	SGD
 * 2 	DB Object ID	required	1	P12345
 * 3 	Qualifier	required	1 or greater	enables
 * 4 	GO ID	required	1	GO:0019104
 * 5 	DB:Reference(s)	required	1 or greater	PMID:20727966
 * 6 	Evidence code	required	1	ECO:0000021
 * 7 	With (or) From	optional	0 or greater	Ensembl:ENSRNOP00000010579
 * 8 	Interacting taxon ID	optional	0 or 1	4896
 * 9 	Date	required	1	20130529
 * 10 	Assigned by	required	1	PomBase
 * 11 	Annotation Extension	optional	0 or greater	occurs_in(GO:0005739)
 * 12 	Annotation Properties	optional	0 or greater	annotation_identifier = 2113431320
 *
 * !gpa-version: 1.1
 * !Project_name: UniProt GO Annotation (UniProt-GOA)
 * !URL: http://www.ebi.ac.uk/GOA
 * !Contact Email: goa@ebi.ac.uk
 * !Date downloaded from the QuickGO browser: 20170117
 * !Filtering parameters selected to generate file: GAnnotation?count=25&protein=A0A000&select=normal&advanced=&termUse=ancestor&slimTypes=IPO%3D
 * UniProtKB	A0A000	enables	GO:0003824	GO_REF:0000002	ECO:0000256	InterPro:IPR015421|InterPro:IPR015422		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	enables	GO:0003870	GO_REF:0000002	ECO:0000256	InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	involved_in	GO:0009058	GO_REF:0000002	ECO:0000256	InterPro:IPR004839		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	enables	GO:0030170	GO_REF:0000002	ECO:0000256	InterPro:IPR004839|InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 * UniProtKB	A0A000	involved_in	GO:0033014	GO_REF:0000002	ECO:0000256	InterPro:IPR010961		20170107	InterPro		go_evidence=IEA
 *
 */
public class AnnotationToGPAD {

    private static final String ID_DELIMITER = ":";
    private static final String OUTPUT_DELIMITER = "\t";

    public String interactingTaxonId;

    public Annotation annotation;

    public String toTSV() {

        String[] idElements = annotation.id.split(ID_DELIMITER);

        return idElements[0] + OUTPUT_DELIMITER +
                idElements[1] + OUTPUT_DELIMITER +
                annotation.qualifier + OUTPUT_DELIMITER +
                annotation.goId + OUTPUT_DELIMITER +
                annotation.reference + OUTPUT_DELIMITER +
                annotation.evidenceCode + OUTPUT_DELIMITER +
                annotation.withFrom + OUTPUT_DELIMITER +
                interactingTaxonId + OUTPUT_DELIMITER +
                annotation.date + OUTPUT_DELIMITER +
                annotation.assignedBy + OUTPUT_DELIMITER +
                annotation.extensions + OUTPUT_DELIMITER + //Contains go evidence code only e.g. 'go_evidence=IEA'
                "goEvidence="+annotation.goEvidence;
    }
}
