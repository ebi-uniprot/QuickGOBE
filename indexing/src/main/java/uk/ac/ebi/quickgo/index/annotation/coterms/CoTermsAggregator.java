package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import org.springframework.batch.item.ItemWriter;

import static uk.ac.ebi.quickgo.index.annotation.coterms.GeneProductBatch.buildBatch;

/**
 * Aggregates all the data need to calculate co-occurrence statistic data points.
 *
 * An optimization could be applied to this class.
 * Please see https://www.ebi.ac.uk/panda/jira/browse/GOA-2397 for details.
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 */
class CoTermsAggregator implements ItemWriter<AnnotationDocument> {
    //A list of all unique geneProducts encountered - it exists so we can get a count of the total unique gene products.
    private final Set<String> geneProductList;

    //Determines which annotations get processed.
    private final Predicate<AnnotationDocument> toBeProcessed;
    private final CoTermMatrix coTerms;
    private final TermGPCount termGPCount;
    private GeneProductBatch geneProductBatch;

    CoTermsAggregator(Predicate<AnnotationDocument> toBeProcessed) {
        Preconditions
                .checkArgument(toBeProcessed != null, "Null predicate passed AnnotationCoOccurringTermsAggregator" +
                        " constructor");

        this.toBeProcessed = toBeProcessed;
        this.coTerms = new CoTermMatrix();
        geneProductList = new HashSet<>();
        termGPCount = new TermGPCount();
        geneProductBatch = new GeneProductBatch();
    }

    /**
     * Number of unique gene products processed from Annotations
     *
     * @return unique gene product count
     */
    long getTotalOfAnnotatedGeneProducts() {
        return geneProductList.size();
    }

    /**
     * This is the count of all unique gene products for terms encountered during processing. We hold this figure
     * separately as it is used many times.
     *
     * @return map of GO terms to count of unique gene products for the term.
     */
    Map<String, AtomicLong> getGeneProductCounts() {
        return termGPCount.termGPCount;
    }

    /**
     * Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
     *
     * @return map of processed terms to all co-occurring terms, together with count of how many times they have
     * co-occurred.
     */
    Map<String, Map<String, AtomicLong>> getCoTerms() {
        return coTerms.coTermMatrix;
    }

    /**
     * For each AnnotationDocument item passed to this method, check whether it passed the criteria for aggregating,
     * and if so add its data to the aggregated data.
     *
     * @param items a list of AnnotationDocuments.
     * @throws java.lang.Exception - if there are errors. The framework will catch the exception and convert or
     * rethrow it as appropriate.
     */
    @Override
    public void write(List<? extends AnnotationDocument> items) throws Exception {
        Preconditions.checkArgument(items != null, "Null annotation passed to process");
        items.stream()
                .filter(this.toBeProcessed::test)
                .forEach(this::addGOTermToAggregationForGeneProduct);
    }

    /**
     * The client must call finish() when all annotation documents have been processed by the write method to wrap up
     * processing.
     */
    void finish() {
        increaseCountsForTermsInBatch();
    }

    /**
     * Add the data in an AnnotationDocument instance to the aggregation.
     * The documents are processed by this class in the gene product order.
     * So the first thing to do is check if this doc has a previously unseen gene product id.
     * If it doesn't we use the existing aggregation object (newOrExistingBatch instance) to aggregate too.
     * If it is a new gene product the we have a new newOrExistingBatch instance created.
     * Add the data in this document to the target newOrExistingBatch.
     * Add the gene product id to the list of geneproduct ids that have been processed (we need a list of all gene
     * products processed for the statistics calculations at the end of the calculation.
     *
     * @param doc
     */
    private void addGOTermToAggregationForGeneProduct(AnnotationDocument doc) {
        if (geneProductBatch.geneProduct == null) {
            geneProductBatch.geneProduct = doc.geneProductId;
        }

        if (!doc.geneProductId.equals(geneProductBatch.geneProduct)) {
            increaseCountsForTermsInBatch();
            geneProductBatch = buildBatch(doc);
        } else {
            geneProductBatch.addTerm(doc.goId);
        }

        geneProductList.add(doc.geneProductId); //set so each gp is only added once.
    }

    /**
     * Got to the end of the list of annotations for this gene product
     * Record which terms annotate the same gene products.
     */
    private void increaseCountsForTermsInBatch() {
        for (String termId : geneProductBatch.terms) {
            coTerms.incrementCoTerms(termId, geneProductBatch.terms);
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
    String geneProduct;

    GeneProductBatch() {
        terms = new HashSet<>();
    }

    /**
     * Create a new GeneProductBatch to aggregate terms for the 'new' gene product id.
     * @param doc
     * @return
     */
    static GeneProductBatch buildBatch(AnnotationDocument doc) {
        GeneProductBatch geneProductBatch = new GeneProductBatch();
        geneProductBatch.geneProduct = doc.geneProductId;
        geneProductBatch.addTerm(doc.goId);
        return geneProductBatch;
    }

    /**
     * Add this term id to this list of term ids encountered for the gene product that is currently being read.
     * @param termId
     */
    void addTerm(String termId) {
        terms.add(termId);
    }
}

/**
 * This class represents a matrix of term to compared term, and its used to hold the number of permutation occurrences.
 */
class CoTermMatrix {

    // Key is the target term, the value is a map of all the GO terms that are used in annotations for the same gene
    // product. i.e.  Key =>target term, value=> map (key=>co-occurring term, value => AtomicLong For Co-occurrence)
    // For example key=>'GO:0003824', value=> map(entry 1 :: key=>'GO:0008152' value=>1346183 hits, entry 2
    // key=>'GO:0016740' value=>1043613 hits)
    final Map<String, Map<String, AtomicLong>> coTermMatrix;

    CoTermMatrix() {
        coTermMatrix = new TreeMap<>();
    }

    /**
     * For all terms encountered for gene product batch, increment its count. If this is a new {@code termId}, then
     * its count is initialised as 1.
     * @param termId single term from batch
     * @param termsInBatch a list of all terms encountered in annotations for a particular gene product.
     */
    void incrementCoTerms(String termId, Set<String> termsInBatch) {
        Map<String, AtomicLong> coTerms = getCoTerms(termId);

        //Loop through all the terms we have encountered in this batch and update the quantities
        for (String term : termsInBatch) {

            //Get 'permanent' record for this termId/termId permutation
            AtomicLong permutationCount = coTerms.get(term);

            //Create if it doesn't exist.
            if (permutationCount == null) {
                permutationCount = new AtomicLong();
                coTerms.put(term, permutationCount);
            }

            //Update it with a count of 'one' as this batch is for one gene protein and so the count must be one
            permutationCount.incrementAndGet();

        }
    }

    /**
     * Get the co-terms for this {@code termId}
     *
     * @param termId GO Term id to use for the lookup.
     * @return all terms that co-occur with the term specified as parameter.
     */
    private Map<String, AtomicLong> getCoTerms(String termId) {
        Map<String, AtomicLong> termCoTerms = coTermMatrix.get(termId);

        //Create if it doesn't exist.
        if (termCoTerms == null) {
            termCoTerms = new HashMap<>();
            coTermMatrix.put(termId, termCoTerms);
        }
        return termCoTerms;
    }

}

class TermGPCount {
    final Map<String, AtomicLong> termGPCount;

    TermGPCount() {
        this.termGPCount = new HashMap<>();
    }

    /**
     * For every term, increment by one the count of gene products for this term
     */
    void incrementGeneProductCountForTerm(String term) {
        AtomicLong count = termGPCount.get(term);
        if (count == null) {
            count = new AtomicLong();
            termGPCount.put(term, count);
        }
        count.incrementAndGet();
    }
}
