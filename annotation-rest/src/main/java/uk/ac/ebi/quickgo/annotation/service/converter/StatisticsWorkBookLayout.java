package uk.ac.ebi.quickgo.annotation.service.converter;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static uk.ac.ebi.quickgo.annotation.service.converter.SheetLayout.buildLayout;

/**
 * A specification of format and content for the statistics download.
 * @author Tony Wardell
 * Date: 26/09/2017
 * Time: 10:52
 * Created with IntelliJ IDEA.
 */
public class StatisticsWorkBookLayout {
    public static final LinkedHashSet<SheetLayout> SHEET_LAYOUT_SET = new LinkedHashSet<>();

    private static final String ANNOTATION = "annotation";
    private static final String GENE_PRODUCT = "geneProduct";

    //Annotation headers
    private static final AnnotationSectionLayout SL_ANNOTATION_GENE_PRODUCT =
            new AnnotationSectionLayout("Annotations (by gene product)");
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
    private static final GeneProductSectionLayout SL_GENE_PRODUCT_GO_ID =
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

    private static final String GO_ID_TYPE_NAME = "goId";
    private static final String ASPECT_TYPE_NAME = "aspect";
    private static final String EVIDENCE_CODE_TYPE_NAME = "evidenceCode";
    private static final String REFERENCE_TYPE_NAME = "reference";
    private static final String TAXON_ID_TYPE_NAME = "taxonId";
    private static final String ASSIGNED_BY_TYPE_NAME = "assignedBy";
    private static final String GENE_PRODUCT_TYPE_NAME = "geneProductId";

    private static final String GO_ID_SHEET_NAME = "goid";
    private static final String ASPECT_SHEET_NAME = "aspect";
    private static final String EVIDENCE_SHEET_NAME = "evidence";
    private static final String REFERENCE_SHEET_NAME = "reference";
    private static final String TAXON_SHEET_NAME = "taxon";
    private static final String ASSIGNED_BY_SHEET_NAME = "assigned";
    private static final String GENE_PRODUCT_SHEET_NAME = "gene product";

    //Add headers and layout
    static {
        SHEET_LAYOUT_SET.add(buildLayout(GENE_PRODUCT_TYPE_NAME, GENE_PRODUCT_SHEET_NAME,
                singletonList(SL_ANNOTATION_GENE_PRODUCT)));
        SHEET_LAYOUT_SET.add(buildLayout(GO_ID_TYPE_NAME, GO_ID_SHEET_NAME,
                asList(SL_ANNOTATION_GOID, SL_GENE_PRODUCT_GO_ID)));
        SHEET_LAYOUT_SET.add(buildLayout(ASPECT_TYPE_NAME, ASPECT_SHEET_NAME,
                asList(SL_ANNOTATION_ASPECT, SL_GENE_PRODUCT_ASPECT)));
        SHEET_LAYOUT_SET.add(buildLayout(EVIDENCE_CODE_TYPE_NAME, EVIDENCE_SHEET_NAME,
                asList(SL_ANNOTATION_EVIDENCE_CODE, SL_GENE_PRODUCT_EVIDENCE_CODE)));
        SHEET_LAYOUT_SET.add(buildLayout(REFERENCE_TYPE_NAME, REFERENCE_SHEET_NAME,
                asList(SL_ANNOTATION_REFERENCES, SL_GENE_PRODUCT_REFERENCES)));
        SHEET_LAYOUT_SET.add(buildLayout(TAXON_ID_TYPE_NAME, TAXON_SHEET_NAME,
                asList(SL_ANNOTATION_TAXON, SL_GENE_PRODUCT_TAXON)));
        SHEET_LAYOUT_SET.add(buildLayout(ASSIGNED_BY_TYPE_NAME, ASSIGNED_BY_SHEET_NAME,
                asList(SL_ANNOTATION_ASSIGNED, SL_GENE_PRODUCT_ASSIGNED)));
    }

    static class AnnotationSectionLayout extends SectionLayout {
        private static final int BY_ANNOTATION_STARTING_COLUMN = 0;

        AnnotationSectionLayout(String header) {
            super(ANNOTATION, header, BY_ANNOTATION_STARTING_COLUMN);
        }
    }

    static class GeneProductSectionLayout extends SectionLayout {
        private static final int BY_PROTEIN_STARTING_COLUMN = 10;

        GeneProductSectionLayout(String header) {
            super(GENE_PRODUCT, header, BY_PROTEIN_STARTING_COLUMN);
        }
    }
}
