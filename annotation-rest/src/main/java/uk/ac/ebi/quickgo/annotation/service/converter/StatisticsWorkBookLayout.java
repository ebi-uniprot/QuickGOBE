package uk.ac.ebi.quickgo.annotation.service.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A Specification of format and content for the statistics download.
 * @author Tony Wardell
 * Date: 26/09/2017
 * Time: 10:52
 * Created with IntelliJ IDEA.
 */
class StatisticsWorkBookLayout {

    static final Map<String, StatisticsToWorkbook.SheetLayout> SHEET_LAYOUT_MAP = new HashMap<>();

    private static final String ANNOTATION = "annotation";
    private static final String GENE_PRODUCT = "geneProduct";

    static final String[] SECTION_TYPES = new String[]{ANNOTATION, GENE_PRODUCT};

    private static final AnnotationSectionLayout SL_ANNOTATION_REFERENCES =
            new AnnotationSectionLayout("References (by annotation)");
    private static final AnnotationSectionLayout SL_ANNOTATION_TAXON =
            new AnnotationSectionLayout("Taxon IDs (by annotation)");
    private static final AnnotationSectionLayout SL_ANNOTATION_ASSIGNED =
            new AnnotationSectionLayout("Sources (by annotation)");
    private static final AnnotationSectionLayout SL_ANNOTATION_GOID =
            new AnnotationSectionLayout("GO IDs (by annotation)");
    private static final AnnotationSectionLayout SL_ANNOTATION_ASPECT =
            new AnnotationSectionLayout("Aspects (by annotation)");
    private static final AnnotationSectionLayout SL_ANNOTATION_EVIDENCE_CODE =
            new AnnotationSectionLayout("Evidence Codes (by annotation)");

    private static final GeneProductSectionLayout SL_GENE_PRODUCT_GOID =
            new GeneProductSectionLayout("GO IDs (by protein)");
    private static final GeneProductSectionLayout SL_GENE_PRODUCT_ASPECT =
            new GeneProductSectionLayout("Aspects (by protein)");
    private static final GeneProductSectionLayout SL_GENE_PRODUCT_EVIDENCE_CODE =
            new GeneProductSectionLayout("Evidence Codes (by protein)");
    private static final GeneProductSectionLayout SL_GENE_PRODUCT_REFERENCES =
            new GeneProductSectionLayout("References (by protein)");
    private static final GeneProductSectionLayout SL_GENE_PRODUCT_TAXON =
            new GeneProductSectionLayout("Taxon IDs (by protein)");
    private static final GeneProductSectionLayout SL_GENE_PRODUCT_ASSIGNED =
            new GeneProductSectionLayout("Sources (by protein)");

    static {
        SHEET_LAYOUT_MAP.put("goId",
                             new StatisticsToWorkbook.SheetLayout("goid",
                                                                  Arrays.asList(SL_ANNOTATION_GOID,
                                                                                SL_GENE_PRODUCT_GOID)));

        SHEET_LAYOUT_MAP.put("aspect",
                             new StatisticsToWorkbook.SheetLayout("aspect",
                                                                  Arrays.asList(SL_ANNOTATION_ASPECT,
                                                                                SL_GENE_PRODUCT_ASPECT)));
        SHEET_LAYOUT_MAP.put("evidenceCode",
                             new StatisticsToWorkbook.SheetLayout("evidence",
                                                                  Arrays.asList(SL_ANNOTATION_EVIDENCE_CODE,
                                                                                SL_GENE_PRODUCT_EVIDENCE_CODE)));
        SHEET_LAYOUT_MAP.put("reference",
                             new StatisticsToWorkbook.SheetLayout("reference",
                                                                  Arrays.asList(SL_ANNOTATION_REFERENCES,
                                                                                SL_GENE_PRODUCT_REFERENCES)));
        SHEET_LAYOUT_MAP.put("taxonId",
                             new StatisticsToWorkbook.SheetLayout("taxon",
                                                                  Arrays.asList(SL_ANNOTATION_TAXON,
                                                                                SL_GENE_PRODUCT_TAXON)));
        SHEET_LAYOUT_MAP.put("assignedBy",
                             new StatisticsToWorkbook.SheetLayout("assigned",
                                                                  Arrays.asList(SL_ANNOTATION_ASSIGNED,
                                                                                SL_GENE_PRODUCT_ASSIGNED)));
    }



    static class AnnotationSectionLayout extends StatisticsToWorkbook.SectionLayout{
        private static final int BY_ANNOTATION_STARTING_COLUMN = 0;
        AnnotationSectionLayout(String header) {
            super(ANNOTATION, header, BY_ANNOTATION_STARTING_COLUMN);
        }
    }

    static class GeneProductSectionLayout extends StatisticsToWorkbook.SectionLayout{
        private static final int BY_PROTEIN_STARTING_COLUMN = 10;
        GeneProductSectionLayout(String header) {
            super(GENE_PRODUCT, header, BY_PROTEIN_STARTING_COLUMN);
        }
    }
}
