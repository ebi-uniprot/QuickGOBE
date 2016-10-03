package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemWriter;

/**
 * Aggregates all the data need to calculate all co-occurrence stat data points.
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 */
public class AnnotationCoOccurringTermsAggregator implements ItemWriter<AnnotationDocument> {

    //A list of all unique geneProducts encountered - it exists so we can get a count of the total unique gene products.
    private final Set<String> geneProductList;

    //Determines which annotations get processed.
    private final Predicate<AnnotationDocument> toBeProcessed;

    private GeneProductBatch geneProductBatch;
    private final TermToTermOverlapMatrix overlapMatrix;
    private final TermGPCount termGPCount;

    public AnnotationCoOccurringTermsAggregator(Predicate<AnnotationDocument> toBeProcessed) {

        Preconditions
                .checkArgument(toBeProcessed != null, "Null predicate passed AnnotationCoOccurringTermsAggregator" +
                        " constructor");

        this.toBeProcessed = toBeProcessed;
        this.overlapMatrix = new TermToTermOverlapMatrix();
        geneProductList = new HashSet<>();
        termGPCount = new TermGPCount();
        geneProductBatch = new GeneProductBatch();
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
     * For each AnnotationDocument item passed to this method, check whether it passed the criteria for aggregating,
     * and if so add its data to the aggregated data.
     *
     * @param items a list of AnnotationDocuments.
     * @throws java.lang.Exception - if there are errors. The framework will catch the exception and convert or rethrow it as appropriate.
     */
    @Override
    public void write(List<? extends AnnotationDocument> items) throws Exception {

        Preconditions.checkArgument(items != null, "Null annotation passed to process");

        items.stream()
                .filter(this.toBeProcessed::test)
                .forEach(this::writeItem);
    }

    /**
     * Add the data in an AnnotationDocument instance to the aggregation.
     * The documents are processed by this class in the gene product order.
     * So the first thing to do is check if this doc has a previously unseen gene product id.
     * If it doesn't we use the existing aggregation object (provideBatch instance) to aggregate too.
     * If it is a new gene product the we have a new provideBatch instance created.
     * Add the data in this document to the target provideBatch.
     * Add the gene product id to the list of geneproduct ids that have been processed (we need a list of all gene
     * products processed for the statistics calculations at the end of the calculation.
     *
     * @param doc
     */
    private void writeItem(AnnotationDocument doc) {

        GeneProductBatch tb2 = geneProductBatch.provideBatch(doc);
        if (tb2 != geneProductBatch) {
            increaseCountsForTermsInBatch();
            geneProductBatch = tb2;
        }

        geneProductList.add(doc.geneProductId); //set so each gp is only added once.
    }

    /**
     * The client must call finish() when all annotation documents have been processed by the write method to wrap up
     * processing.
     */
    public void finish() {
        increaseCountsForTermsInBatch();
    }

    /**
     * Got to the end of the list of annotations for this gene product
     * Record which terms annotate the same gene products.
     */
    private void increaseCountsForTermsInBatch() {

        for (String termId : geneProductBatch.terms) {
            overlapMatrix.incrementCountForCo_occurringTerms(termId, geneProductBatch.terms);
            termGPCount.incrementGeneProductCountForTerm(termId);
        }
    }

}

/**
 * A data bucket for aggregating annotation document data. Each batch is created for all data with the same gene
 * product id.
 */
class GeneProductBatch {

    //A set of all terms encountered for a Gene Product. Therefore all these terms are co-occurring with each other.
    final Set<String> terms;

    //The input file has annotations in gene product order, so we use this value to note changes in gene product.
    private String geneProduct;

    public GeneProductBatch() {
        terms = new HashSet<>();
    }

    private void add(String goId) {
        terms.add(goId);
    }

    /**
     * For an instance of an annotation document, if the doc has the same gene product id as the existing aggregation
     * batch then re-use that batch. Otherwise create a new batch for the 'new' gene product id.
     * @param doc
     * @return
     */
    GeneProductBatch provideBatch(AnnotationDocument doc) {

        if (geneProduct == null) {
            geneProduct = doc.geneProductId;
        }

        if (!doc.geneProductId.equals(geneProduct)) {
            GeneProductBatch geneProductBatch = new GeneProductBatch();
            geneProductBatch.geneProduct = doc.geneProductId;
            geneProductBatch.add(doc.goId);
            return geneProductBatch;
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
