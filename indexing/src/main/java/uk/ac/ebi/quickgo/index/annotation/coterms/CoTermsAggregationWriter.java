package uk.ac.ebi.quickgo.index.annotation.coterms;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import com.google.common.base.Preconditions;
import org.springframework.batch.item.Chunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTerm.calculateProbabilityRatio;
import static uk.ac.ebi.quickgo.index.annotation.coterms.CoTerm.calculateSimilarityRatio;
import static uk.ac.ebi.quickgo.index.annotation.coterms.GeneProductBatch.buildBatch;

/**
 * Aggregates all the data need to calculate co-occurrence statistic data points.
 * AnnotationDocuments are passed to the write method for aggregation.
 * <B>*IMPORTANT* The AnnotationDocuments need to be passed to the write method in gene product order (asc) - the code
 * assumes this is the case in-order to carry out it batching.</B>
 * An optimization could be applied to this class.
 * Please see https://www.ebi.ac.uk/panda/jira/browse/GOA-2397 for details.
 *
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 */
public class CoTermsAggregationWriter extends AbstractItemStreamItemWriter<AnnotationDocument> {
    //A list of all unique geneProducts encountered - it exists so we can get a count of the total unique gene products.
    private final Set<String> geneProductList;

    //Determines which annotations get processed.
    private final Predicate<AnnotationDocument> toBeProcessed;
    private final CoTermMatrix coTerms;
    private final TermGPCount geneProductCountForTerms;
    private GeneProductBatch geneProductBatch;

    CoTermsAggregationWriter(Predicate<AnnotationDocument> toBeProcessed) {
        Preconditions
                .checkArgument(toBeProcessed != null, "Null predicate passed AnnotationCoOccurringTermsAggregator" +
                        " constructor.");

        this.toBeProcessed = toBeProcessed;
        this.coTerms = new CoTermMatrix();
        this.geneProductList = new HashSet<>();
        this.geneProductCountForTerms = new TermGPCount();
        this.geneProductBatch = new GeneProductBatch();
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
    public void write(Chunk<? extends AnnotationDocument> items) throws Exception {
        Preconditions.checkArgument(items != null, "Null annotation passed to process");
        items.stream()
                .filter(this.toBeProcessed::test)
                .forEach(this::addGOTermToAggregationForGeneProduct);
    }

    /**
     * When all annotation documents have been processed by the write method then an operation wrap up processing is
     * required.
     */
    @Override
    public void close() {
        increaseCountsForTermsInBatch();
    }

    /**
     * Provide an iteration of the all GO Terms with a co-occurrence (which will be all GO Terms annotated, since at
     * the very least a term is said to coincide with itself).
     * @return an iterator over all the GO Terms that have co-occurring terms.
     */
    Iterator<String> getCoTermsIterator() {
        return this.coTerms.coTermMatrix.keySet().iterator();
    }

    /**
     * Create a CoTermsForSelectedTerm instance for each compared term.
     * @param goTerm The GO Term for which the co-occurrence statistics will be calculated.
     * @return CoTermsForSelectedTerm instance with co-occurring statistics calculated for every
     * co-occurring term.
     */
    CoTermsForSelectedTerm createCoTermsForSelectedTerm(String goTerm) {

        Preconditions
                .checkArgument(null != goTerm, "Target GO term id passed to createCoTermsForSelectedTerm should not " +
                        "be null");

        final long selected = getCountOfGeneProductsForTerm(goTerm);
        if(selected == 0){
            return CoTermsForSelectedTerm.Builder.empty();
        }

        CoTermsForSelectedTerm.Builder coTermsBuilder = new CoTermsForSelectedTerm.Builder()
                .setTotalNumberOfGeneProducts(getGeneProductTotal())
                .setSelected(selected);

        for (String comparedTerm : getCoTermsAndCounts(goTerm).keySet()) {
            long together = getTogether(goTerm, comparedTerm);
            long compared = getCountOfGeneProductsForTerm(comparedTerm);

            coTermsBuilder.addCoTerm(new CoTerm.Builder()
                    .setTarget(goTerm)
                    .setComparedTerm(comparedTerm)
                    .setCompared(getCountOfGeneProductsForTerm(comparedTerm))
                    .setTogether(getTogether(goTerm, comparedTerm))
                    .setProbabilityRatio(calculateProbabilityRatio(selected, together, getGeneProductTotal(), compared))
                    .setSimilarityRatio(calculateSimilarityRatio(selected, together, compared))
                    .setGpCount(selected)
                    .build());
        }
        return coTermsBuilder.build();
    }

    /**
     * We hold a count of the number of unique gene products encountered during processing, for each term.
     * @param goTerm GO Term account for which we want the gene product count.
     * @return count of the number of unique gene products encountered during processing for the request term.
     */
    private long getCountOfGeneProductsForTerm(String goTerm) {
        final AtomicLong gpCountForTerm = geneProductCountForTerms.id2Count.get(goTerm);
        return gpCountForTerm != null ? gpCountForTerm.get():0L;
    }

    /**
     * Number of unique gene products processed from Annotations
     * @return unique gene product count
     */
    private long getGeneProductTotal() {
        return geneProductList.size();
    }

    /**
     * Get a map of all co-occurring terms to co-occurrence count for the target termId.
     * @param termId the termId for which the caller should receive all the co-occurring terms plus co-occurrence count.
     * @return map of co-occurring terms to co-occurrence count.
     */
    private Map<String, AtomicLong> getCoTermsAndCounts(String termId) {
        return coTerms.coTermMatrix.get(termId);
    }

    private long getTogether(String targetTerm, String comparedTerm) {
        return coTerms.coTermMatrix.get(targetTerm).get(comparedTerm).get();
    }

    /**
     * Add the data in an AnnotationDocument instance to the aggregation.
     * The documents are processed by this class in the gene product order.
     * So the first thing to do is check if this doc has a previously unseen gene product id.
     * If it doesn't we use the existing aggregation object (newOrExistingBatch instance) to aggregate too.
     * If it is a new gene product the we have a new newOrExistingBatch instance created.
     * Add the data in this document to the target newOrExistingBatch.
     * Add the gene product id to the list of gene product ids that have been processed (we need a list of all gene
     * products processed for the statistics calculations at the end of the calculation.
     *
     * @param doc an AnnotationDocument instance to be added to aggregation.
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
            geneProductCountForTerms.incrementGeneProductCountForTerm(termId);
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
     * @param doc an AnnotationDocument instance to be added to aggregation.
     * @return an instance of GeneProductBatch which will be used to aggregate co-occurring terms annotating the same
     * gene product.
     */
    static GeneProductBatch buildBatch(AnnotationDocument doc) {
        GeneProductBatch geneProductBatch = new GeneProductBatch();
        geneProductBatch.geneProduct = doc.geneProductId;
        geneProductBatch.addTerm(doc.goId);
        return geneProductBatch;
    }

    /**
     * Add this term id to this list of term ids encountered for the gene product that is currently being read.
     * @param termId GO Term processed.
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
        Map<String, AtomicLong> coTerms = coTermMatrix.computeIfAbsent(termId, k -> new HashMap<>());

        //Loop through all the terms we have encountered in this batch and update the quantities
        for (String term : termsInBatch) {

            //Get 'permanent' record for this termId/termId permutation
            AtomicLong permutationCount = coTerms.computeIfAbsent(term, k -> new AtomicLong());

            //Update it with a count of 'one' as this batch is for one gene protein and so the count must be one
            permutationCount.incrementAndGet();

        }
    }
}

/**
 * State for GO termId::count of gene products for it.
 */
class TermGPCount {
    final Map<String, AtomicLong> id2Count;

    TermGPCount() {
        this.id2Count = new HashMap<>();
    }

    /**
     * For every term, increment by one the count of gene products for this term
     */
    void incrementGeneProductCountForTerm(String term) {
        if (id2Count.putIfAbsent(term, new AtomicLong(1L)) != null) {
            id2Count.get(term).incrementAndGet();
        }
    }
}
