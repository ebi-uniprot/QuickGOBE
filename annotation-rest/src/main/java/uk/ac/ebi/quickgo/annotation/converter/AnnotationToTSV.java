package uk.ac.ebi.quickgo.annotation.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

/**
 * @author Tony Wardell
 * Date: 23/01/2017
 * Time: 11:48
 * Created with IntelliJ IDEA.
 */
public class AnnotationToTSV {

    /**
     * DB	ID	Symbol	Qualifier	GO ID	GO Name	Aspect	Evidence	Reference	With	Taxon	Date	Source	Splice
     * UniProtKB	Q4VCS5	AMOT	-	GO:0001570	vasculogenesis	Process	IEA	GO_REF:0000107	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	9606	20170107	Ensembl	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0001701	in utero embryonic development	Process	IEA	GO_REF:0000107
     * UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	9606	20170107	Ensembl	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0001702	gastrulation with mouth forming second	Process	IEA	GO_REF:0000107
     * UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	9606	20170107	Ensembl	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0001725	stress fiber	Component	IDA	PMID:16043488	-	9606	20051207	UniProt	UniProtKB:Q4VCS5-1
     * UniProtKB	Q4VCS5	AMOT	-	GO:0001726	ruffle	Component	IDA	PMID:11257124	-	9606	20091109	MGI	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0003365	establishment of cell polarity involved in ameboidal cell migration	Process	IEA	GO_REF:0000107	UniProtKB:Q8VHG2|ensembl:ENSMUSP00000108455	9606	20170107	Ensembl	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0004872	receptor activity	Function	IDA	PMID:11257124	-	9606	20091109	MGI	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:11257124	UniProtKB:P00747	9606	20051212	HGNC	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:16043488	UniProtKB:Q6RHR9-2	9606	20051207	UniProt	UniProtKB:Q4VCS5-1
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:19615732	UniProtKB:P35240	9606	20170108	IntAct	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:21187284	UniProtKB:P46937	9606	20120124	UniProt	UniProtKB:Q4VCS5-1
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:21481793	UniProtKB:P35240	9606	20170108	IntAct	UniProtKB:Q4VCS5-1
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:21481793	UniProtKB:P35240	9606	20170108	IntAct	UniProtKB:Q4VCS5-2
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:21481793	UniProtKB:P35240	9606	20170108	IntAct	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:21481793	UniProtKB:Q68EM7	9606	20170108	IntAct	-
     * UniProtKB	Q4VCS5	AMOT	-	GO:0005515	protein binding	Function	IPI	PMID:21481793	UniProtKB:Q68EM7	9606	20170108	IntAct	UniProtKB:Q4VCS5-2
     *
     */

//    static final String OUTPUT_DELIMITER = "\t";
//    private ConversionUtil conversionUtil;
//
//    AnnotationToTSV(ConversionUtil conversionUtil) {
//        this.conversionUtil = conversionUtil;
//    }


    /**
     * Convert an {@link Annotation} to a String representation.
     * @param annotation instance
     * @return String TSV delimited representation of an annotation in GAF format.
     */
    public String convert(Annotation annotation) {
//        String[] idElements = conversionUtil.idToComponents(annotation);
//
//        return annotation.id  + OUTPUT_DELIMITER +
//                annotation.symbol + OUTPUT_DELIMITER +
//                annotation.qualifier + OUTPUT_DELIMITER +
//                annotation.goId + OUTPUT_DELIMITER +
//                //annotation.goName + OUTPUT_DELIMITER +                todo not available
//                annotation.evidenceCode + OUTPUT_DELIMITER +
//                annotation.reference + OUTPUT_DELIMITER +
//                conversionUtil.withFromAsString(annotation.withFrom) + OUTPUT_DELIMITER +
//                annotation.taxonId + OUTPUT_DELIMITER +
//                annotation.assignedBy + OUTPUT_DELIMITER +
//                conversionUtil.extensionsAsString(annotation.extensions);
        return null;
    }

}
