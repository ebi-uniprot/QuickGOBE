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

    //Annotation headers
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

    //Gene product headers
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

    private static final String GO_ID_SHEET_KEY = "goId";
    private static final String ASPECT_SHEET_KEY = "aspect";
    private static final String EVIDENCE_CODE_SHEET_KEY = "evidenceCode";
    private static final String REFERENCE_SHEET_KEY = "reference";
    private static final String TAXON_ID_SHEET_KEY = "taxonId";
    private static final String ASSIGNED_BY_SHEET_KEY = "assignedBy";

    private static final String GO_ID_SHEET_NAME = "goid";
    private static final String ASPECT_SHEET_NAME = "aspect";
    private static final String EVIDENCE_SHEET_NAME = "evidence";
    private static final String REFERENCE_SHEET_NAME = "reference";
    private static final String TAXON_SHEET_NAME = "taxon";
    private static final String ASSIGNED_SHEET_NAME = "assigned";

    //Add headers
    static {
        SHEET_LAYOUT_MAP.put(GO_ID_SHEET_KEY,
                new WorkbookFromStatisticsImpl.SheetLayout(GO_ID_SHEET_NAME,
                        Arrays.asList(SL_ANNOTATION_GOID,
                                SL_GENE_PRODUCT_GOID)));
        SHEET_LAYOUT_MAP.put(ASPECT_SHEET_KEY,
                new WorkbookFromStatisticsImpl.SheetLayout(ASPECT_SHEET_NAME,
                        Arrays.asList(SL_ANNOTATION_ASPECT,
                                SL_GENE_PRODUCT_ASPECT)));
        SHEET_LAYOUT_MAP.put(EVIDENCE_CODE_SHEET_KEY,
                new WorkbookFromStatisticsImpl.SheetLayout(EVIDENCE_SHEET_NAME,
                        Arrays.asList(SL_ANNOTATION_EVIDENCE_CODE,
                                SL_GENE_PRODUCT_EVIDENCE_CODE)));
        SHEET_LAYOUT_MAP.put(REFERENCE_SHEET_KEY,
                new WorkbookFromStatisticsImpl.SheetLayout(REFERENCE_SHEET_NAME,
                        Arrays.asList(SL_ANNOTATION_REFERENCES,
                                SL_GENE_PRODUCT_REFERENCES)));
        SHEET_LAYOUT_MAP.put(TAXON_ID_SHEET_KEY,
                new WorkbookFromStatisticsImpl.SheetLayout(TAXON_SHEET_NAME,
                        Arrays.asList(SL_ANNOTATION_TAXON,
                                SL_GENE_PRODUCT_TAXON)));
        SHEET_LAYOUT_MAP.put(ASSIGNED_BY_SHEET_KEY,
                new WorkbookFromStatisticsImpl.SheetLayout(ASSIGNED_SHEET_NAME,
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
