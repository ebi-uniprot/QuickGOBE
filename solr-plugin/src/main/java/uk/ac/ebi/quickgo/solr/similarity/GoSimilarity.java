package uk.ac.ebi.quickgo.solr.similarity;

import org.apache.lucene.search.similarities.ClassicSimilarity;

/**
 * Similarity scorer that ignores term frequencies. This effectively means that the amount of time a given term
 * appears within a document is not counted toward the document's final score.
 *
 * @author Ricardo Antunes
 */
public class GoSimilarity extends ClassicSimilarity {
    @Override public float tf(float freq) {
        if(freq > 0) {
            return 1F;
        } else {
            return 0F;
        }
    }
}
