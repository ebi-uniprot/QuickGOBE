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

    private String id;
    private String compare;
    private float probabilityRatio;
    private float significance;
    private long together;
    private long compared;

    public CoTerm(String id, String compare, float probabilityRatio, float significance, long together,
            long compared) {
        this.id = id;
        this.compare = compare;
        this.probabilityRatio = probabilityRatio;
        this.significance = significance;
        this.together = together;
        this.compared = compared;
    }

    public String getId() {
        return id;
    }

    public String getCompare() {
        return compare;
    }

    public float getProbabilityRatio() {
        return probabilityRatio;
    }

    public float getSignificance() {
        return significance;
    }

    public long getTogether() {
        return together;
    }

    public long getCompared() {
        return compared;
    }

}
