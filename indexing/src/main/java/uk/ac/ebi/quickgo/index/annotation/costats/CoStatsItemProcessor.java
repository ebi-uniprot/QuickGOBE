package uk.ac.ebi.quickgo.index.annotation.costats;


import uk.ac.ebi.quickgo.common.costats.*;

import com.google.common.base.Preconditions;
import java.util.*;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Tony Wardell
 * Date: 26/11/2015
 * Time: 11:59
 * Created with IntelliJ IDEA.
 *
 * For the contents of the termToTermOverlapMatrix calculate co-occurrence statistics
 * A version of CoStatsSummarizer from Beta
 */
public class CoStatsItemProcessor implements ItemProcessor<String, List<CoOccurrenceStat>>{

	//This is the count of all gene products for the term. We hold this figure separately as it is used many times.
	private final Map<String, HitCount> termGPCount;

    //Holds a termN by termN matrix, each cell of which holds the count of gp this intersection of terms hold
    private final Map<String, Map<String, HitCount>> termToTermOverlapMatrix;

	//Total number of unique gene products that have annotations
	private final long geneProductCount;

	//Constructor
	public CoStatsItemProcessor(long geneProductCount, Map<String, HitCount> termGPCount, Map<String,
			Map<String, HitCount>> termToTermOverlapMatrix) {
		this.geneProductCount = geneProductCount;
		this.termGPCount = termGPCount;
        this.termToTermOverlapMatrix = termToTermOverlapMatrix;
	}


	/**
	 * Read each line in the term to term matrix for the selected term. For each calculate a CoStat instance.
	 *
	 */
	public List<CoOccurrenceStat> process(String goTerm){
		return resultsForOneGoTerm(calculateCoStatsForTerm(goTerm));
	}

	/**
	 * Create a CoStatsForTerm instance for each compared term, and calculate the COOccurrenceStatsTerm based on the values passed in.
	 * @param target The GO Term for which the co-occurrence statistics will be calculated.
	 * @return coStatsForTerm
	 */
	private CoOccurringTermsForSelectedTerm calculateCoStatsForTerm(String target) {
		Preconditions.checkArgument(null!=target, "Target passed to calculateCoStatsForTerm should not be null");

		Map<String, HitCount> coocurringTermsForTarget = termToTermOverlapMatrix.get(target);
		CoOccurringTermsForSelectedTerm
				coOccurringTermsForSelectedTerm = new CoOccurringTermsForSelectedTerm(target, geneProductCount, termGPCount.get(target).hits);

		for (String comparedTerm : coocurringTermsForTarget.keySet()) {

			final long comparedTermCoHitsWithTarget = coocurringTermsForTarget.get(comparedTerm).hits;
			final long comparedTermTotalHits = termGPCount.get(comparedTerm).hits;

			CoOccurringTerm coStatsTerm = new CoOccurringTerm(target, comparedTerm,	comparedTermTotalHits,
					comparedTermCoHitsWithTarget);

			coOccurringTermsForSelectedTerm.addAndCalculate(coStatsTerm);
		}
		return coOccurringTermsForSelectedTerm;

	}

	private List<CoOccurrenceStat> resultsForOneGoTerm(CoOccurringTermsForSelectedTerm coOccurringTermsForSelectedTerm) {

		List<CoOccurrenceStat> results = new ArrayList<>();

		//Get iterator of compared terms, ordered by significance ratio descending
		Iterator<CoOccurrenceStat> descendingIt = coOccurringTermsForSelectedTerm.highestSimilarity();

		while (descendingIt.hasNext()) {
			results.add(descendingIt.next());
		}

		return results;
	}

}
