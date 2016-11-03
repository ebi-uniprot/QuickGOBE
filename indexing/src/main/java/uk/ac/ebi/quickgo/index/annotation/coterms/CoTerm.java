package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that represents the intersection between two GO terms, which have been used to annotate the
 * same gene products in an annotation data set. An instance of this class will hold statistics related to this
 * intersection.
 * The data in this class is used by the QuickGO and Protein2GO applications.
 * @author twardell
 *
 */
public class CoTerm {

    private static int logCount = 0;
    private final String target;
    private final String comparedTerm;
    private final long together;
    private final long compared;
    private final float similarityRatio;
    private final float probabilityRatio;
    private final long gpCount;
    private static final Logger logger = LoggerFactory.getLogger(CoTerm.class);

    /**
     * Create a permutation for the compared term.
     * @param target the id of the GO Term which is our compared 'from' GO Term.
     * @param comparedTerm the id of the GO Term which is our compared 'to' GO Term.
     * @param compared Count of proteins where compared term is annotated.
     * @param together Count of proteins where both target and compared terms are annotated.
     * @param probabilityRatio Probability of term here estimated as fraction of proteins annotated to term.
     * @param similarityRatio Ratio of probability of both terms to probability of either term.
     * @param gpCount the total number of unique gene products that have been annotated by the target term.
     */
    private CoTerm(String target, String comparedTerm, long compared, long together, float probabilityRatio,
            float similarityRatio, long gpCount) {
        this.target = target;
        this.comparedTerm = comparedTerm;
        this.compared = compared;
        this.together = together;
        this.probabilityRatio = probabilityRatio;
        this.similarityRatio = similarityRatio;
        this.gpCount = gpCount;

        if (!(compared > 0) || !(together > 0) || !(probabilityRatio > 0) || !(similarityRatio > 0)) {
            if (logCount++ < 100) {
                logger.info("The following coterm tuple may be incorrect: {}", this.toString());
            }
        }
    }

    /**
     * Probability similarity ratio
     * Ratio of probability of both terms to probability of either term
     * <code>=#together/(#selected+#compared-#together)</code>
     * Probability of term here estimated as fraction of proteins annotated to term.
     * @param selected Total count of gene products annotated to selected .
     * @param together Count of proteins where both selected and compared terms are annotated
     * @param compared Count of proteins where compared term is annotated
     */
    static float calculateSimilarityRatio(float selected, long together, long compared) {
        Preconditions
                .checkArgument(selected != 0, "CoTerm::calculateProbabilitySimilarityRatio The value for" +
                        " selected should not be zero");

        return 100 * ((together) / (selected + compared - together));
    }

    /**
     * Probability ratio
     * Ratio of probability of compared term given selected term to probability of compared term
     * <code>=(#together/selected)/(#compared/#all)</code>
     * Probability of term here estimated as fraction of proteins annotated to term.
     * @param selected Total count of gene products annotated to selected term
     * @param together Count of proteins where both selected and compared terms are annotated
     * @param all Total count of proteins
     * @param compared Count of proteins where compared term is annotated
     */
    static float calculateProbabilityRatio(float selected, long together, float all, long compared) {

        Preconditions.checkArgument(selected != 0, "CoTerm::calculateProbabilityRatio The value for selected" +
                " should not be zero");
        Preconditions.checkArgument(all != 0, "CoTerm::calculateProbabilityRatio The value for all" +
                " should not be zero");
        return (together / selected) / (compared / all);
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

    @Override public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (comparedTerm != null ? comparedTerm.hashCode() : 0);
        result = 31 * result + (int) (together ^ (together >>> 32));
        result = 31 * result + (int) (compared ^ (compared >>> 32));
        result = 31 * result + (similarityRatio != +0.0f ? Float.floatToIntBits(similarityRatio) : 0);
        result = 31 * result + (probabilityRatio != +0.0f ? Float.floatToIntBits(probabilityRatio) : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CoTerm that = (CoTerm) o;

        if (together != that.together) {
            return false;
        }
        if (compared != that.compared) {
            return false;
        }
        if (Float.compare(that.similarityRatio, similarityRatio) != 0) {
            return false;
        }
        if (Float.compare(that.probabilityRatio, probabilityRatio) != 0) {
            return false;
        }
        if (!target.equals(that.target)) {
            return false;
        }
        return comparedTerm.equals(that.comparedTerm);

    }

    @Override public String toString() {
        return "CoTerm{" +
                "target='" + target + '\'' +
                ", comparedTerm='" + comparedTerm + '\'' +
                ", together=" + together +
                ", compared=" + compared +
                ", similarityRatio=" + similarityRatio +
                ", probabilityRatio=" + probabilityRatio +
                ", gpCount=" + gpCount +
                '}';
    }

    public static class Builder {
        private String target;
        private String comparedTerm;
        private long compared;
        private long together;
        private float probabilityRatio;
        private float similarityRatio;
        private long gpCount;

        public Builder setTarget(String target) {
            Preconditions
                    .checkArgument(target != null && !target.trim().isEmpty(), "The parameter for 'setTarget' cannot " +
                            "be null or empty.");
            this.target = target;
            return this;
        }

        public Builder setComparedTerm(String comparedTerm) {
            Preconditions.checkArgument(comparedTerm != null && !comparedTerm.trim().isEmpty(), "The parameter for " +
                    "'setComparedTerm' cannot be null or empty.");
            this.comparedTerm = comparedTerm;
            return this;
        }

        public Builder setCompared(long compared) {
            this.compared = compared;
            return this;
        }

        public Builder setTogether(long together) {
            this.together = together;
            return this;
        }

        public Builder setProbabilityRatio(float probabilityRatio) {
            this.probabilityRatio = probabilityRatio;
            return this;
        }

        public Builder setSimilarityRatio(float similarityRatio) {
            this.similarityRatio = similarityRatio;
            return this;
        }

        public Builder setGpCount(long gpCount) {
            this.gpCount = gpCount;
            return this;
        }

        public CoTerm build() {
            return new CoTerm(target, comparedTerm, compared, together, probabilityRatio, similarityRatio, gpCount);
        }
    }
}


