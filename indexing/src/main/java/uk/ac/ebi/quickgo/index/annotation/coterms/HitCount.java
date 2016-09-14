package uk.ac.ebi.quickgo.index.annotation.coterms;

/**
 * A value object for the number of occurrences of a 'thing'.
 *
 * @author Tony Wardell
 * Date: 20/07/2016
 * Time: 15:35
 * Created with IntelliJ IDEA.
 */
public class HitCount {

    public long hits;

    public HitCount(int i) {
        hits = i;
    }

    public HitCount() {
    }
}
