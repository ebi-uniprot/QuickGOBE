package uk.ac.ebi.quickgo.annotation.common.document;

import java.util.Arrays;

/**
 * Class to create stubbed {@link AnnotationDocument} instances.
 *
 * Created 14/04/16
 * @author Edd
 */
public class AnnotationDocMocker {
    public static final int FLAT_FIELD_DEPTH = 0;

    private AnnotationDocMocker() {}

    public static AnnotationDocument createAnnotationDoc(String geneProductId) {
        AnnotationDocument doc = new AnnotationDocument();
        doc.geneProductId = geneProductId;

        doc.id = geneProductId + "-" + System.nanoTime();
        doc.goId = "GO:0003824";
        doc.ecoId = "ECO:0000256";
        doc.symbol = "moeA5";
        doc.qualifier = "enables";
        doc.goEvidence = "IEA";
        doc.reference = "GO_REF:0000002";
        doc.withFrom = Arrays.asList("InterPro:IPR015421", "InterPro:IPR015422");
        doc.taxonId = "35758";
        doc.assignedBy = "InterPro";
        doc.extension = "results_in_development_of(UBERON:0001675),acts_on_population_of(CL:0000032)";

        return doc;
    }

    /**
     * geneProductId
     symbol
     qualifier
     goId
     goEvidence
     ecoId
     reference
     withFrom
     taxonId
     assignedBy
     extension
     */
}