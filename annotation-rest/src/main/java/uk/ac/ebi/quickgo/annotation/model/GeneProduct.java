package uk.ac.ebi.quickgo.annotation.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.annotation.model.GeneProduct.GeneProductType.COMPLEX;
import static uk.ac.ebi.quickgo.annotation.model.GeneProduct.GeneProductType.MI_RNA;
import static uk.ac.ebi.quickgo.annotation.model.GeneProduct.GeneProductType.PROTEIN;

/**
 *  The state for GeneProduct information used for downloading, with logic to create.
 *
 * @author twardell
 */
public class GeneProduct {
    private static final int UNIPROT_CANONICAL_GROUP_NUMBER = 2;
    private static final int UNIPROT_NON_CANONICAL_GROUP_NUMBER = 4;
    private static final String UNIPROT_CANONICAL_REGEX = "^(?:UniProtKB:)?(([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]" +
            "([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])((-[0-9]+)|:PRO_[0-9]{10}|:VAR_[0-9]{6}){0,1})$";
    private static final Pattern UNIPROT_CANONICAL_PATTERN = Pattern.compile(UNIPROT_CANONICAL_REGEX);

    private static final int RNA_ID_GROUP = 1;
    private static final String RNA_CENTRAL_REGEX = "^(?:RNAcentral:)?((URS[0-9A-F]{10})(_[0-9]+){0,1})$";
    private static final Pattern RNA_CENTRAL_CANONICAL_PATTERN = Pattern.compile(RNA_CENTRAL_REGEX);

    private static final int COMPLEX_PORTAL_ID_NUMBER = 1;
    private static final String COMPLEX_PORTAL_CANONICAL_REGEX = "^(?:ComplexPortal:)?(CPX-[0-9]+)$";
    private static final Pattern COMPLEX_PORTAL_CANONICAL_PATTERN = Pattern.compile(COMPLEX_PORTAL_CANONICAL_REGEX);

    private final GeneProductId geneProductId;
    private final GeneProductType geneProductType;

    /**
     * Constructor
     * @param geneProductId a type to define gene product id
     * @param geneProductType a type to define gene product type
     */
    private GeneProduct(GeneProductId geneProductId, GeneProductType geneProductType) {
        this.geneProductId = geneProductId;
        this.geneProductType = geneProductType;
    }

    /**
     * Determine the correctness of the argument as a gene product id and if valid build a GeneProduct representation.
     * @param curieId Annotation id, could had isoform or variant suffix if it is a UniProt gene product.
     * @return a GeneProduct representation.
     */
    public static GeneProduct fromCurieId(String curieId) {

        if (Objects.isNull(curieId) || curieId.isEmpty()) {
            throw new IllegalStateException("Gene Product Id is null or empty");
        }

        Matcher uniprotMatcher = UNIPROT_CANONICAL_PATTERN.matcher(curieId);
        if (uniprotMatcher.matches()) {
            String db = "UniProtKB";
            String canonical = uniprotMatcher.group(UNIPROT_CANONICAL_GROUP_NUMBER);
            String nonDb = uniprotMatcher.group(UNIPROT_NON_CANONICAL_GROUP_NUMBER);
            String nonCanonical = canonical.equals(nonDb) ? null : nonDb;
            return new GeneProduct(new GeneProductId(db, canonical, curieId, nonCanonical), PROTEIN);
        }

        Matcher rnaMatcher = RNA_CENTRAL_CANONICAL_PATTERN.matcher(curieId);
        if (rnaMatcher.matches()) {
            String db = "RNAcentral";
            String id = rnaMatcher.group(RNA_ID_GROUP);
            return new GeneProduct(new GeneProductId(db, id, curieId, null), MI_RNA);
        }

        Matcher complexPortalMatcher = COMPLEX_PORTAL_CANONICAL_PATTERN.matcher(curieId);
        if (complexPortalMatcher.matches()) {
            String db = "ComplexPortal";
            String id = complexPortalMatcher.group(COMPLEX_PORTAL_ID_NUMBER);
            return new GeneProduct(new GeneProductId(db, id, curieId, null), COMPLEX);
        }
        throw new IllegalStateException(String.format("Gene Product Id %s is not valid", curieId));
    }

    public String canonicalId() {
        return  geneProductId != null ? geneProductId.id : null;
    }

    public String nonCanonicalId() {
        return geneProductId != null ? geneProductId.nonCanonical : null;
    }

    public String db() {
        return  geneProductId != null ? geneProductId.db : null;
    }

    public String fullId() {
        return geneProductId != null ? geneProductId.fullId : null;
    }

    public String type() {
        return  geneProductType != null ? geneProductType.getName() : null;
    }

    /**
     * A representation of the GeneProduct Id.
     */
    private static class GeneProductId {
        private final String db;
        private final String id;
        private final String fullId;
        private final String nonCanonical;

        private GeneProductId(String db, String canonical, String fullId, String nonCanonical) {
            assert nonNull(db);
            assert nonNull(canonical);
            assert nonNull(fullId);

            this.db = db;
            this.id = canonical;
            this.fullId = fullId;
            this.nonCanonical = nonCanonical;
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
