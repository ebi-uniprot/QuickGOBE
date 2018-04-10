package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.GeneProduct.GeneProductType.COMPLEX;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.GeneProduct.GeneProductType.PROTEIN;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.GeneProduct.GeneProductType.MI_RNA;

/**
 *  The state for GeneProduct information used for downloading, with logic to create.
 */
public class GeneProduct {

    private static final int RNA_ID_GROUP = 1;
    private final GeneProductId geneProductId;
    private final GeneProductType geneProductType;

    private GeneProduct(GeneProductId geneProductId, GeneProductType geneProductType) {
        this.geneProductId = geneProductId;
        this.geneProductType = geneProductType;
    }

    private static final int CANONICAL_GROUP_NUMBER = 2;
    private static final int INTACT_ID_NUMBER = 1;
    private static final String UNIPROT_CANONICAL_REGEX = "^(?:UniProtKB:)?(([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]" +
            "([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1})$";
    private static final Pattern UNIPROT_CANONICAL_PATTERN = Pattern.compile(UNIPROT_CANONICAL_REGEX);
    private static final String RNA_CENTRAL_REGEX = "^(?:RNAcentral:)?((URS[0-9A-F]{10})(_[0-9]+){0,1})$";
    private static final Pattern RNA_CENTRAL_CANONICAL_PATTERN = Pattern.compile(RNA_CENTRAL_REGEX);
    private static final String INTACT_CANONICAL_REGEX = "^(?:IntAct:)(EBI-[0-9]+)$";
    private static final Pattern INTACT_CANONICAL_PATTERN = Pattern.compile(INTACT_CANONICAL_REGEX);

    /**
     * Determine the correctness of the argument as a gene product id and if valid build a GeneProduct representation.
     * @param fullId Annotation id, could had isoform or variant suffix if it is a UniProt gene product.
     * @return a GeneProduct representation.
     */
    public static GeneProduct fromString(String fullId) {

        if (Objects.isNull(fullId) || fullId.isEmpty()) {
            return nullObject();
        }

        Matcher uniprotMatcher = UNIPROT_CANONICAL_PATTERN.matcher(fullId);
        if (uniprotMatcher.matches()) {
            String db = "UniProtKB";
            String id = uniprotMatcher.group(CANONICAL_GROUP_NUMBER);
            String withIsoFormOrVariant = fullId.contains("-") ? fullId : null;
            return new GeneProduct(new GeneProductId(db, id, withIsoFormOrVariant), PROTEIN);
        }

        Matcher rnaMatcher = RNA_CENTRAL_CANONICAL_PATTERN.matcher(fullId);
        if (rnaMatcher.matches()) {
            String db = "RNAcentral";
            String id = rnaMatcher.group(RNA_ID_GROUP);
            return new GeneProduct(new GeneProductId(db, id, null), MI_RNA);
        }

        Matcher intactMatcher = INTACT_CANONICAL_PATTERN.matcher(fullId);
        if (intactMatcher.matches()) {
            String db = "IntAct";
            String id = intactMatcher.group(INTACT_ID_NUMBER);
            return new GeneProduct(new GeneProductId(db, id, null), COMPLEX);
        }
        return nullObject();
    }

    public String id() {
        return  geneProductId != null ? geneProductId.id : null;
    }

    public String db() {
        return  geneProductId != null ? geneProductId.db : null;
    }

    public String withIsoformOrVariant() {
        return  geneProductId != null ? geneProductId.withIsoFormOrVariant : null;
    }

    public String type() {
        return  geneProductType != null ? geneProductType.getName() : null;
    }

    //Create an empty version of the gene product
    private static GeneProduct nullObject() {
        return new GeneProduct(new GeneProductId(null, null, null), null);
    }

    /**
     * A representation of the GeneProduct Id.
     */
    private static class GeneProductId {
        private final String db;
        private final String id;
        private final String withIsoFormOrVariant;

        private GeneProductId(String db, String id, String wthIsoFormOrVariant) {
            this.db = db;
            this.id = id;
            this.withIsoFormOrVariant = wthIsoFormOrVariant;
        }
    }

    /**
     * A representation of the GeneProduct type.
     */
    public enum GeneProductType {
        COMPLEX("complex"),
        PROTEIN("protein"),
        MI_RNA("miRNA");

        private String name;

        GeneProductType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
