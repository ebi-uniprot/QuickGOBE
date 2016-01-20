package uk.ac.ebi.quickgo.repo.solr.similarity;

import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Similarity scorer that ignores term frequencies. This effectively means that the amount of time a given term
 * appears within a document is not counted toward the document's final score.
 *
 * @author Ricardo Antunes
 */
public class GoSimilarity extends DefaultSimilarity {
    @Override public float tf(float freq) {
        if(freq > 0) {
            return 1F;
        } else {
            return 0F;
        }
    }
}
