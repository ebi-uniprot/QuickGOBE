package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Turn a String holding a gene product id into it's component pieces.
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 09:34
 * Created with IntelliJ IDEA.
 */
public class GeneProductId {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductId.class);

    public final String db;
    public final String id;
    public final String withIsoFormOrVariant;

    private GeneProductId(String db, String id, String wthIsoFormOrVariant) {
        this.db = db;
        this.id = id;
        this.withIsoFormOrVariant = wthIsoFormOrVariant;
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
     * Extract the canonical version of the id, removing the variation or isoform suffix if it exists.
     * @param fullId Annotation id, could had isoform or variant suffix.
     * @return canonical form of the id with the isoform or variant suffix removed.
     */
    public static GeneProductId fromString(String fullId) {

        if (Objects.isNull(fullId) || fullId.isEmpty()) {
            return new GeneProductId(null, null, null);
        }

        Matcher uniprotMatcher = UNIPROT_CANONICAL_PATTERN.matcher(fullId);
        if (uniprotMatcher.matches()) {
            String db = "UniProtKB";
            String id = uniprotMatcher.group(CANONICAL_GROUP_NUMBER);
            String withIsoFormOrVariant = fullId.contains("-") ? fullId : null;
            return new GeneProductId(db, id, withIsoFormOrVariant);
        }

        Matcher rnaMatcher = RNA_CENTRAL_CANONICAL_PATTERN.matcher(fullId);
        if (rnaMatcher.matches()) {
            String db = "RNAcentral";
            String id = rnaMatcher.group(CANONICAL_GROUP_NUMBER);
            return new GeneProductId(db, id, null);
        }

        Matcher intactMatcher = INTACT_CANONICAL_PATTERN.matcher(fullId);
        if (intactMatcher.matches()) {
            String db = "IntAct";
            String id = intactMatcher.group(INTACT_ID_NUMBER);
            return new GeneProductId(db, id, null);
        }
        LOGGER.error(String.format("Cannot extract the canonical version of the id from \"%s\"", fullId));
        return new GeneProductId(null, null, null);
    }
}
