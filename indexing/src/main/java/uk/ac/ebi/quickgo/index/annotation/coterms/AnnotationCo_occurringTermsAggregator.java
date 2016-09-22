package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemWriter;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 * Aggregates all the data need to calculate all co-occurrence stat data points.
 */
public class AnnotationCo_occurringTermsAggregator implements ItemWriter<AnnotationDocument> {

    //A list of all unique geneProducts encountered - it exists so we can get a count of the total unique gene products.
    private final Set<String> geneProductList;

    //Determines which annotations get processed.
    private final Predicate<AnnotationDocument> toBeProcessed;

    private TermBatch termBatch;
    private TermToTermOverlapMatrix overlapMatrix;
    private TermGPCount termGPCount;

    //Constructor
    public AnnotationCo_occurringTermsAggregator(Predicate<AnnotationDocument> toBeProcessed) {

        Preconditions
                .checkArgument(toBeProcessed != null, "Null predicate passed AnnotationCo_occurringTermsAggregator" +
                        " constructor");

        this.toBeProcessed = toBeProcessed;
        this.overlapMatrix = new TermToTermOverlapMatrix();
        geneProductList = new HashSet<>();
        termGPCount = new TermGPCount();
        termBatch = new TermBatch();
    }

    /**
     * Number of unique gene products processed from Annotations
     *
     * @return unique gene product count
     */
    public long getTotalOfAnnotatedGeneProducts() {
        return geneProductList.size();
    }

    /**
     * This is the count of all unique gene products for terms encountered during processing. We hold this figure
     * separately as it is used many times.
     *
     * @return map of GO terms to count of unique gene products for the term.
     */
    public Map<String, HitCount> getGeneProductCounts() {
        return termGPCount.termGPCount;
    }

    /**
     * Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
     *
     * @return map of processed terms to all co-occurring terms, together with count of how many times they have
     * co-occurred.
     */
    public Map<String, Map<String, HitCount>> getTermToTermOverlapMatrix() {
        return overlapMatrix.termToTermOverlapMatrix;
    }

    /**
     * For each row of the GP Association file, create a list of terms, against which there is a list of all the
     * other terms that that term share a referenced gene product with.
     *
     * @param items
     */
    @Override
    public void write(List<? extends AnnotationDocument> items) throws Exception {

        Preconditions.checkArgument(items != null, "Null annotation passed to process");

        items.stream()
                .filter(i -> this.toBeProcessed.test(i))
                .forEach(i -> writeItem(i));
    }

    private void writeItem(AnnotationDocument doc) {

        TermBatch tb2 = termBatch.termBatch(doc);
        if (tb2 != termBatch) {
            increaseCountsForTermsInBatch();
            termBatch = tb2;
        }

        geneProductList.add(doc.geneProductId); //set so each gp is only added once.
    }

    /**
     * Make it clear to the client this method needs calling to wrap up processing
     */
    public void finish() {
        increaseCountsForTermsInBatch();
    }

    /**
     * Got to the end of the list of annotations for this gene product
     * Record which terms annotate the same gene products.
     */
    private void increaseCountsForTermsInBatch() {

        for (String termId : termBatch.termsInBatch) {
            overlapMatrix.incrementCountForCo_occurringTerms(termId, termBatch.termsInBatch);
            termGPCount.incrementGeneProductCountForTerm(termId);
        }
    }

}

class TermBatch {

    //A set of all terms encountered for a Gene Product. Therefore all these terms are co-occurring with each other.
    Set<String> termsInBatch;

    //The input file has annotations in gene product order, so we use this value to note changes in gene product.
    String currentGeneProduct;

    public TermBatch() {
        termsInBatch = new HashSet<>();
    }

    private void add(String goId) {
        termsInBatch.add(goId);
    }

    uk.ac.ebi.quickgo.index.annotation.coterms.TermBatch termBatch(AnnotationDocument doc) {

        if (currentGeneProduct == null) {
            currentGeneProduct = doc.geneProductId;
        }

        if (!doc.geneProductId.equals(currentGeneProduct)) {
            TermBatch termBatch = new TermBatch();
            termBatch.currentGeneProduct = doc.geneProductId;
            termBatch.add(doc.goId);
            return termBatch;
        }
        this.add(doc.goId);
        return this;

    }
}

class TermToTermOverlapMatrix {

    //Key =>target term, value=> map (key=>co-occurring term, value => HitCountForCo-occurrence
    final Map<String, Map<String, HitCount>> termToTermOverlapMatrix;

    public TermToTermOverlapMatrix() {
        termToTermOverlapMatrix = new TreeMap<>();
    }

    /**
     * For all terms encountered for gene product batch, add or increase hit count
     * @param termId single term from batch
     */
    void incrementCountForCo_occurringTerms(String termId, Set<String> termsInBatch) {

        Map<String, HitCount> co_occurringTerms = getCo_occurringTerms(termId);

        //Loop through all the terms we have encountered in this batch and update the quantities
        for (String co_occurringTerm : termsInBatch) {

            //Get 'permanent' record for this termId/termId permutation
            HitCount permutationHitCount = co_occurringTerms.get(co_occurringTerm);

            //Create if it doesn't exist.
            if (permutationHitCount == null) {
                permutationHitCount = new HitCount();
                co_occurringTerms.put(co_occurringTerm, permutationHitCount);
            }

            //Update it with a count of 'one' as this batch is for one gene protein and so the count must be one
            permutationHitCount.hits++;

        }
    }

    /**
     * Get the co-stats for this termId
     *
     * @param termId
     * @return All terms that are co-occurring term to argument
     */
    private Map<String, HitCount> getCo_occurringTerms(String termId) {

        //look in the store
        Map<String, HitCount> termCoTerms = termToTermOverlapMatrix.get(termId);

        //Create if it doesn't exist.
        if (termCoTerms == null) {
            termCoTerms = new HashMap<>();
            termToTermOverlapMatrix.put(termId, termCoTerms);
        }
        return termCoTerms;
    }

}

class TermGPCount {
    final Map<String, HitCount> termGPCount;

    public TermGPCount() {
        this.termGPCount = new HashMap<>();
    }

    /**
     * For every term, increment by one the count of gene products for this term
     */
    void incrementGeneProductCountForTerm(String term) {
        HitCount hitCount = termGPCount.get(term);
        if (hitCount == null) {
            hitCount = new HitCount();
            termGPCount.put(term, hitCount);
        }
        hitCount.hits++;
    }
}
