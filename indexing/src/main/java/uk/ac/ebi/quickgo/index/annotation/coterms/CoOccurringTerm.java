package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.text.DecimalFormat;

/**
 * A co-occurrence statistics entity. Represents two goTerms which are being used to annotate the same gene product.
 * This class only holds the compared to term however, as instances of this class are held in a collection for the
 * selected (target) goTerm.
 * @author twardell
 *
 */
public class CoOccurringTerm implements Comparable<CoOccurringTerm> {

    private final String target;
    private final String comparedTerm;
    private final long together;
    private final long compared;
    private float similarityRatio;
    private float probabilityRatio;

    /**
     * Create a permutation for the compared term.
     * @param target the id of the GO Term against which we will make co-occurring terms
     * @param comparedTerm term id for this statistics
     * @param compared count of gene products where compared term is annotated
     * @param together count of gene products where both selected and compared terms are annotated
     */
    public CoOccurringTerm(String target, String comparedTerm, long compared, long together) {
        this.target = target;
        this.comparedTerm = comparedTerm;
        this.compared = compared;
        this.together = together;
    }

    /**
     * Probability similarity ratio
     * Ratio of probability of both terms to probability of either term
     * <code>=#together/(#selected+#compared-#together)</code>
     * Probability of term here estimated as fraction of proteins annotated to term.
     * @param selected Total count of gene products annotated to selected term
     * @return the calculated similarity ratio
     */
    public float calculateProbabilitySimilarityRatio(float selected) {

        Preconditions
                .checkArgument(selected != 0, "CoOccurringTerm::calculateProbabilitySimilarityRatio The value for" +
                        " selected should not be zero");

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        float similarityRatio = 100 * ((this.together) / (selected + this.compared - this.together));
        float psRatio = Float.valueOf(twoDForm.format(similarityRatio));// Round it with 2 decimals
        if (Math.round(psRatio) == 100) {
            this.similarityRatio = 100;
        }
        this.similarityRatio = psRatio;
        return this.similarityRatio;
    }

    /**
     * Probability ratio
     * Ratio of probability of compared term given selected term to probability of compared term
     * <code>=(#together/selected)/(#compared/#all)</code>
     * Probability of term here estimated as fraction of proteins annotated to term.
     * @param selected Total count of gene products annotated to selected term
     * @return the calculated probability ratio
     */
    public float calculateProbabilityRatio(float selected, float all) {

        Preconditions.checkArgument(selected != 0, "CoOccurringTerm::calculateProbabilityRatio The value for selected" +
                " should not be zero");
        Preconditions.checkArgument(all != 0, "CoOccurringTerm::calculateProbabilityRatio The value for all" +
                " should not be zero");

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        probabilityRatio = (this.together / selected) / (this.compared / all);
        probabilityRatio = Float.valueOf(twoDForm.format(probabilityRatio));// Round it with 2 decimals
        return probabilityRatio;
    }

    /**
     * Ratio of probability of both terms to probability of either term
     * @return similarityRatio
     */
    public float getSimilarityRatio() {
        return similarityRatio;
    }

    /**
     * Ratio of probability of compared term given selected term to probability of compared term
     * @return probabilityRatio
     */
    public float getProbabilityRatio() {
        return probabilityRatio;
    }

    /**
     *
     * @return the term with which this co-occurring term co-occurs.
     */
    public String getTarget() {
        return target;
    }

    /**
     * The co-occurring term
     * @return the GOTerm used as the compared too term
     */
    public String getComparedTerm() {
        return comparedTerm;
    }

    /**
     * Count of gene products where both selected and compared terms are annotated
     * @return count
     */
    public long getTogether() {
        return together;
    }

    /**
     * Count of gene products where compared term is annotated
     * @return count
     */
    public long getCompared() {
        return compared;
    }

    @Override
    public int compareTo(CoOccurringTerm o) {
        if (this.getSimilarityRatio() == o.getSimilarityRatio()) {
            return 0;
        }
        return this.getSimilarityRatio() > o.getSimilarityRatio() ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CoOccurringTerm that = (CoOccurringTerm) o;

        if (Float.compare(that.together, together) != 0) {
            return false;
        }
        if (Float.compare(that.compared, compared) != 0) {
            return false;
        }
        return comparedTerm.equals(that.comparedTerm);

    }

    @Override
    public int hashCode() {
        int result = comparedTerm.hashCode();
        result = 31 * result + (together != +0.0f ? Float.floatToIntBits(together) : 0);
        result = 31 * result + (compared != +0.0f ? Float.floatToIntBits(compared) : 0);
        return result;
    }

    @Override public String toString() {
        return "CoOccurringTerm{" +
                "target='" + target + '\'' +
                ", comparedTerm='" + comparedTerm + '\'' +
                ", together=" + together +
                ", compared=" + compared +
                ", similarityRatio=" + similarityRatio +
                ", probabilityRatio=" + probabilityRatio +
                '}';
    }
}
