package uk.ac.ebi.quickgo.index.annotation.costats;

import uk.ac.ebi.quickgo.common.costats.HitCount;
import uk.ac.ebi.quickgo.index.annotation.Annotation;
import java.util.*;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 * Holds all the data need to calculate a co-occurrence stat data point
 */
public class CoStatsPermutations {

	//Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
	private final Map<String, Map<String, HitCount>> termToTermOverlapMatrix;

	//This is the count of all gene products for the term. We hold this figure separately as it is used many times.
	private final Map<String, HitCount> termGPCount;

	//A unique list of all geneProducts - it exists so we can get a count of the total unique gene products
	private final Set<String> geneProductList;

	//A set of all terms encountered for a Gene Product
	private Set<String> termBatch;

	private String currentGeneProduct;

	//Constructor
	CoStatsPermutations() {
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
	public void addRowToMatrix(Annotation annotation){

		refreshIfNewGeneProduct(annotation);
		updateTermBatchWithTermCount(annotation);
		geneProductList.add(annotation.dbObjectId);

	}

	private void updateTermBatchWithTermCount(Annotation annotation) {
		termBatch.add(annotation.goId);
	}

	/**
	 * Make it clear to the client this method needs calling to wrap up processing
     */
	public void finish(){
		updateCoTermsCount();
	}

	private void refreshIfNewGeneProduct(Annotation annotation) {
		if(currentGeneProduct!=null && !annotation.dbObjectId.equals(currentGeneProduct)){
			updateCoTermsCount();
            currentGeneProduct =  annotation.dbObjectId;
			termBatch = new HashSet<>();
			return;
		}

		if(currentGeneProduct==null){
			currentGeneProduct =  annotation.dbObjectId;
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

	//Finally for every term, increment the count for this term
	private void incrementCountForTerm(String term) {
		HitCount hitCount = termGPCount.get(term);
		if(hitCount == null){
            hitCount = new HitCount();
            termGPCount.put(term, hitCount);
        }
		hitCount.hits++;
	}

	public Map<String, Map<String, HitCount>> getTermToTermOverlapMatrix() {
		return termToTermOverlapMatrix;
	}

	private void incrementCoTermsCount(String term, Set<String> coocurringTerms) {

		//Get the co-stats for this term
		Map<String, HitCount> termCoTerms = termToTermOverlapMatrix.get(term);

		//Create if it doesn't exist.
		if (termCoTerms == null) {
			termCoTerms = new HashMap<>();
			termToTermOverlapMatrix.put(term, termCoTerms);
		}

		//Loop through all the terms we have encountered in this batch and update the quantities

        for (String coocurringTerm : coocurringTerms) {

            //Get 'permanent' record for this term/term permutation
            HitCount permutationHitCount = termCoTerms.get(coocurringTerm);

            //Create if it doesn't exist.
            if (permutationHitCount == null) {
                permutationHitCount = new HitCount();
                termCoTerms.put(coocurringTerm, permutationHitCount);
            }

            //Update it with a count of 'one' as this batch is for one gene protein and so the count must be one
            permutationHitCount.hits++;

        }

	}

	public long totalOfAnnotatedGeneProducts() {
		return geneProductList.size();
	}

	public Map<String, HitCount> termGPCount() {
		return termGPCount;
	}

	public Map<String, Map<String, HitCount>> termToTermOverlapMatrix() {
		return termToTermOverlapMatrix;
	}
}
