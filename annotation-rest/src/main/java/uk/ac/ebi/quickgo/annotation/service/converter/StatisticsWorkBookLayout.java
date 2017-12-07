package uk.ac.ebi.quickgo.annotation.service.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A specification of format and content for the statistics download.
 * @author Tony Wardell
 * Date: 26/09/2017
 * Time: 10:52
 * Created with IntelliJ IDEA.
 */
public class StatisticsWorkBookLayout {
    public static final Map<String, WorkbookFromStatisticsImpl.SheetLayout> SHEET_LAYOUT_MAP = new HashMap<>();
    static final String ANNOTATION = "annotation";
    static final String GENE_PRODUCT = "geneProduct";

    public static final String[] SECTION_TYPES = new String[]{ANNOTATION, GENE_PRODUCT};

    //Annotation stats
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

    //Gene product stats
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
                new WorkbookFromStatisticsImpl.SheetLayout("goid",
                        Arrays.asList(SL_ANNOTATION_GOID,
                                SL_GENE_PRODUCT_GOID)));
        SHEET_LAYOUT_MAP.put("aspect",
                new WorkbookFromStatisticsImpl.SheetLayout("aspect",
                        Arrays.asList(SL_ANNOTATION_ASPECT,
                                SL_GENE_PRODUCT_ASPECT)));
        SHEET_LAYOUT_MAP.put("evidenceCode",
                new WorkbookFromStatisticsImpl.SheetLayout("evidence",
                        Arrays.asList(SL_ANNOTATION_EVIDENCE_CODE,
                                SL_GENE_PRODUCT_EVIDENCE_CODE)));
        SHEET_LAYOUT_MAP.put("reference",
                new WorkbookFromStatisticsImpl.SheetLayout("reference",
                        Arrays.asList(SL_ANNOTATION_REFERENCES,
                                SL_GENE_PRODUCT_REFERENCES)));
        SHEET_LAYOUT_MAP.put("taxonId",
                new WorkbookFromStatisticsImpl.SheetLayout("taxon",
                        Arrays.asList(SL_ANNOTATION_TAXON,
                                SL_GENE_PRODUCT_TAXON)));
        SHEET_LAYOUT_MAP.put("assignedBy",
                new WorkbookFromStatisticsImpl.SheetLayout("assigned",
                        Arrays.asList(SL_ANNOTATION_ASSIGNED,
                                SL_GENE_PRODUCT_ASSIGNED)));
    }

    static class AnnotationSectionLayout extends WorkbookFromStatisticsImpl.SectionLayout {
        private static final int BY_ANNOTATION_STARTING_COLUMN = 0;

        AnnotationSectionLayout(String header) {
            super(ANNOTATION, header, BY_ANNOTATION_STARTING_COLUMN);
        }
    }

    static class GeneProductSectionLayout extends WorkbookFromStatisticsImpl.SectionLayout {
        private static final int BY_PROTEIN_STARTING_COLUMN = 10;

        GeneProductSectionLayout(String header) {
            super(GENE_PRODUCT, header, BY_PROTEIN_STARTING_COLUMN);
        }
    }
}
