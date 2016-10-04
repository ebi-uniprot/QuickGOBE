package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.text.DecimalFormat;

/**
 * A class that represents the intersection between two GO terms, which have been used to annotate the
 * same gene products in an annotation data set. An instance of this class will hold statistics related to this
 * intersection.
 * The data in this class is used by the QuickGO and Protein2GO applications.
 * @author twardell
 *
 */
public class Co_occurringTerm {

    private final String target;
    private final String comparedTerm;
    private final long together;
    private final long compared;
    private float similarityRatio;
    private float probabilityRatio;

    /**
     * Create a permutation for the compared term.
     * @param target the id of the GO Term which is our compared 'from' GO Term
     * @param comparedTerm the id of the GO Term which is our compared 'to' GO Term
     * @param compared Count of proteins where compared term is annotated
     * @param together Count of proteins where both target and compared terms are annotated
     */
    private Co_occurringTerm(String target, String comparedTerm, long compared, long together) {
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
     */
    public void calculateProbabilitySimilarityRatio(float selected) {

        Preconditions
                .checkArgument(selected != 0, "Co_occurringTerm::calculateProbabilitySimilarityRatio The value for" +
                        " selected should not be zero");

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        float similarityRatio = 100 * ((this.together) / (selected + this.compared - this.together));
        float psRatio = Float.valueOf(twoDForm.format(similarityRatio));// Round it with 2 decimals
        if (Math.round(psRatio) == 100) {
            this.similarityRatio = 100;
        }
        this.similarityRatio = psRatio;
    }

    /**
     * Probability ratio
     * Ratio of probability of compared term given selected term to probability of compared term
     * <code>=(#together/selected)/(#compared/#all)</code>
     * Probability of term here estimated as fraction of proteins annotated to term.
     * @param selected Total count of gene products annotated to selected term
     */
    public float calculateProbabilityRatio(float selected, float all) {

        Preconditions.checkArgument(selected != 0, "Co_occurringTerm::calculateProbabilityRatio The value for selected" +
                " should not be zero");
        Preconditions.checkArgument(all != 0, "Co_occurringTerm::calculateProbabilityRatio The value for all" +
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

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Co_occurringTerm that = (Co_occurringTerm) o;

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

    @Override public int hashCode() {
        return target.hashCode();
    }
    public static class Co_occurringTermBuilder {
        private String target;
        private String comparedTerm;
        private long compared;
        private long together;

        public Co_occurringTermBuilder setTarget(String target) {
            this.target = target;
            return this;
        }

        public Co_occurringTermBuilder setComparedTerm(String comparedTerm) {
            this.comparedTerm = comparedTerm;
            return this;
        }

        public Co_occurringTermBuilder setCompared(long compared) {
            this.compared = compared;
            return this;
        }

        public Co_occurringTermBuilder setTogether(long together) {
            this.together = together;
            return this;
        }

        public Co_occurringTerm createCo_occurringTerm() {
            return new Co_occurringTerm(target, comparedTerm, compared, together);
        }
    }

}


