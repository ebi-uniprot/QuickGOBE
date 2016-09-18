package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.quickgo.index.annotation.Annotation;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 * Aggregates all the data need to calculate all co-occurrence stat data points.
 */
public class AnnotationCo_occurringTermsAggregator implements ItemProcessor<Annotation, Annotation> {

    private final Map<String, Map<String, HitCount>> termToTermOverlapMatrix;
    private final Map<String, HitCount> termGPCount;

    //A list of all unique geneProducts encountered - it exists so we can get a count of the total unique gene products.
    private final Set<String> geneProductList;
    private final Predicate<Annotation> toBeProcessed;
    //A set of all terms encountered for a Gene Product
    private Set<String> termBatch;
    //The input file has annotations in gene product order, so we use this value to note changes in gene product.
    private String currentGeneProduct;

    //Constructor
    public AnnotationCo_occurringTermsAggregator(Predicate<Annotation> toBeProcessed) {
        this.toBeProcessed = toBeProcessed;
        termBatch = new HashSet<>();
        termToTermOverlapMatrix = new TreeMap<>();
        geneProductList = new HashSet<>();
        termGPCount = new HashMap<>();

    }

    /**
     * For each row of the GP Association file, create a list of terms, against which there is a list of all the
     * other terms that that term share a referenced gene product with.
     * @param annotation input file containing annotations
     */
    @Override
    public Annotation process(Annotation annotation) throws Exception {

        if (!toBeProcessed.test(annotation)) {
            return annotation;
        }


        Preconditions.checkArgument(annotation!=null, "Null annotation passed to addRowToMatrix");

        refreshIfNewGeneProduct(annotation);
        updateTermBatchWithTermCount(annotation);
        geneProductList.add(annotation.dbObjectId);
        return annotation;
    }

    /**
     * Make it clear to the client this method needs calling to wrap up processing
     */
    public void finish() {
        updateCoTermsCount();
    }

    /**
     * Number of unique gene products processed from Annotations
     * @return unique gene product count
     */
    public long getTotalOfAnnotatedGeneProducts() {
        return geneProductList.size();
    }

    /**
     * This is the count of all unique gene products for terms encountered during processing. We hold this figure
     * separately as it is used many times.
     * @return map of GO terms to count of unique gene products for the term.
     */
    public Map<String, HitCount> getGeneProductCounts() {
        return termGPCount;
    }

    /**
     *  Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
     * @return map of processed terms to all co-occurring terms, together with count of how many times they have
     * co-occurred.
     */
    public Map<String, Map<String, HitCount>> getTermToTermOverlapMatrix() {
        return termToTermOverlapMatrix;
    }

    private void updateTermBatchWithTermCount(Annotation annotation) {
        termBatch.add(annotation.goId);
    }

    private void refreshIfNewGeneProduct(Annotation annotation) {
        if (currentGeneProduct != null && !annotation.dbObjectId.equals(currentGeneProduct)) {
            updateCoTermsCount();
            currentGeneProduct = annotation.dbObjectId;
            termBatch = new HashSet<>();
            return;
        }

        if (currentGeneProduct == null) {
            currentGeneProduct = annotation.dbObjectId;
        }
    }

    /**
     * Got to the end of the list of annotations for this gene product
     * Record which terms annotate the same gene products.
     */
    private void updateCoTermsCount() {

        for (String next : termBatch) {
            incrementCoTermsCount(next, termBatch);
            incrementCountForTerm(next);
        }
    }

    /**
     * Finally for every term, increment the count for this term
     */
    private void incrementCountForTerm(String term) {
        HitCount hitCount = termGPCount.get(term);
        if (hitCount == null) {
            hitCount = new HitCount();
            termGPCount.put(term, hitCount);
        }
        hitCount.hits++;
    }

    private void incrementCoTermsCount(String term, Set<String> co_occurringTerms) {

        //Get the co-stats for this term
        Map<String, HitCount> termCoTerms = termToTermOverlapMatrix.get(term);

        //Create if it doesn't exist.
        if (termCoTerms == null) {
            termCoTerms = new HashMap<>();
            termToTermOverlapMatrix.put(term, termCoTerms);
        }

        //Loop through all the terms we have encountered in this batch and update the quantities

        for (String co_occurringTerm : co_occurringTerms) {

            //Get 'permanent' record for this term/term permutation
            HitCount permutationHitCount = termCoTerms.get(co_occurringTerm);

            //Create if it doesn't exist.
            if (permutationHitCount == null) {
                permutationHitCount = new HitCount();
                termCoTerms.put(co_occurringTerm, permutationHitCount);
            }

            //Update it with a count of 'one' as this batch is for one gene protein and so the count must be one
            permutationHitCount.hits++;

        }
    }
}
