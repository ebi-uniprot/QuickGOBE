package uk.ac.ebi.quickgo.ontology.common.coterms;

/**
 * @author Tony Wardell
 * Date: 29/09/2016
 * Time: 11:30
 * Created with IntelliJ IDEA.
 *
 * todo will be replaced with  uk.ac.ebi.quickgo.index.annotation.coterms.CoTerm
 * todo although it will need to move from this location.
 */
public class CoTerm {

    private final String target;
    private final String comparedTerm;
    private final long together;
    private final long compared;
    private final float similarityRatio;
    private final float probabilityRatio;

    public CoTerm(String target, String comparedTerm, float probabilityRatio, float similarityRatio, long together,
            long compared) {
        this.target = target;
        this.comparedTerm = comparedTerm;
        this.probabilityRatio = probabilityRatio;
        this.similarityRatio = similarityRatio;
        this.together = together;
        this.compared = compared;
    }

    public float getSimilarityRatio() {
        return similarityRatio;
    }

    public float getProbabilityRatio() {
        return probabilityRatio;
    }

    public String getTarget() {
        return target;
    }

    public String getComparedTerm() {
        return comparedTerm;
    }

    public long getTogether() {
        return together;
    }

    public long getCompared() {
        return compared;
    }


}
