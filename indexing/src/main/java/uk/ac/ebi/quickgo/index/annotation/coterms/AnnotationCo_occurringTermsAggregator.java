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
    //Determines which annotations get processed.
    private final Predicate<Annotation> toBeProcessed;
    //A set of all terms encountered for a Gene Product. Therefore all these terms are co-occurring with each other.
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

    /**
     * For each row of the GP Association file, create a list of terms, against which there is a list of all the
     * other terms that that term share a referenced gene product with.
     *
     * @param annotation input file containing annotations
     */
    @Override
    public Annotation process(Annotation annotation) throws Exception {
        Preconditions.checkArgument(annotation != null, "Null annotation passed to process");
        if (!toBeProcessed.test(annotation)) {
            return annotation;
        }

        processTermBatchIfAnnotationGeneProductNotInCurrentBatch(annotation);
        termBatch.add(annotation.goId);
        geneProductList.add(annotation.dbObjectId); //set so each gp is only added once.
        return annotation;
    }

    /**
     * Make it clear to the client this method needs calling to wrap up processing
     */
    public void finish() {
        increaseCountsForTermsInBatch();
        termBatch = new HashSet<>(); //not required, however..
    }


    /**
     * Test to the gene product from the just read annotation contains the saved (for batch) gene product.
     * If it doesn't we have encountered annotations with a different gene product, so process the batch we have already
     * created.
     *
     * @param annotation annotation to process
     */
    private void processTermBatchIfAnnotationGeneProductNotInCurrentBatch(Annotation annotation) {
        if (currentGeneProduct != null) {
            if (!annotation.dbObjectId.equals(currentGeneProduct)) {
                increaseCountsForTermsInBatch();
                termBatch = new HashSet<>();
                currentGeneProduct = annotation.dbObjectId;
            }
        }else{
            currentGeneProduct = annotation.dbObjectId;
        }
    }

    /**
     * Got to the end of the list of annotations for this gene product
     * Record which terms annotate the same gene products.
     */
    private void increaseCountsForTermsInBatch() {

        for (String termId : termBatch) {
            incrementCountForCo_occurringTerms(termId);
            incrementGeneProductCountForTerm(termId);
        }
    }

    /**
     * For all terms encountered for gene product batch, add or increase hit count
     * @param termId single term from batch
     */
    private void incrementCountForCo_occurringTerms(String termId) {

        Map<String, HitCount> termCoTerms = getTermCoTerms(termId);

        //Loop through all the terms we have encountered in this batch and update the quantities
        for (String co_occurringTerm : termBatch) {

            //Get 'permanent' record for this termId/termId permutation
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

    /**
     * Get the co-stats for this termId
     *
     * @param termId
     * @return All terms that are co-occurring term to argument
     */
    private Map<String, HitCount> getTermCoTerms(String termId) {

        //look in the store
        Map<String, HitCount> termCoTerms = termToTermOverlapMatrix.get(termId);

        //Create if it doesn't exist.
        if (termCoTerms == null) {
            termCoTerms = new HashMap<>();
            termToTermOverlapMatrix.put(termId, termCoTerms);
        }
        return termCoTerms;
    }


    /**
     * For every term, increment by one the count of gene products for this term
     */
    private void incrementGeneProductCountForTerm(String term) {
        HitCount hitCount = termGPCount.get(term);
        if (hitCount == null) {
            hitCount = new HitCount();
            termGPCount.put(term, hitCount);
        }
        hitCount.hits++;
    }


}
