package uk.ac.ebi.quickgo.indexer.annotation;

import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.model.statistics.StatisticTuple;
import uk.ac.ebi.quickgo.statistic.StatisticsCalculation;

import java.util.*;

/**
 * @Author Tony Wardell
 * Date: 15/10/2015
 * Time: 14:50
 * Created with IntelliJ IDEA.
 * Keep a count of distinct aspects of the annotation data
 * This information will be saved in solr and used on the statistics page
 */
public class StatisticsBucket {

	private static int STORED_HITS=80;


	//Go Terms
	private Map<String, StatisticTuple> gotermAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> gotermGeneProductBucket = new TreeMap<>();		//goterm, number of geneproducts

	private Map<String, StatisticTuple> aspectAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> aspectGeneProductBucket = new TreeMap<>();

	private Map<String, StatisticTuple> evidenceAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> evidenceGeneProductBucket = new TreeMap<>();

	private Map<String, StatisticTuple> referenceAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> referenceGeneProductBucket = new TreeMap<>();

	private Map<String, StatisticTuple> taxonAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> taxonGeneProductBucket = new TreeMap<>();

	private Map<String, StatisticTuple> assignedByAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> assignedByGeneProductBucket = new TreeMap<>();

	//private Map<String, StatisticTuple> summaryAnnotationBucket = new TreeMap<>();
	private long summaryAnnotationBucket = 0;
	private Map<String, StatisticTuple> summaryGeneProductBucket = new TreeMap<>();

	public void addAnnotationToStatistics(GOAnnotation anno) {

		genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_GOTERM, gotermAnnotationBucket, anno.getGoID());
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_GOTERM, anno, gotermGeneProductBucket, anno.getGoID());

		genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_ASPECT, aspectAnnotationBucket, anno.getGoAspect());
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_ASPECT_, anno, aspectGeneProductBucket, anno.getGoAspect());

		genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_EVIDENCE, evidenceAnnotationBucket, anno.getGoEvidence());
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_EVIDENCE, anno, evidenceGeneProductBucket, anno.getGoEvidence());

		//Reference
		genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_REFERENCE, referenceAnnotationBucket, anno.getReference());
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_REFERENCE, anno, referenceGeneProductBucket, anno.getReference());

		//Taxon
		genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_TAXON, taxonAnnotationBucket, Integer.toString(anno.getTaxonomyId()));
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_TAXON, anno, taxonGeneProductBucket, Integer.toString(anno.getTaxonomyId()));

		//AssignedBy
		genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_ASSIGNEDBY, assignedByAnnotationBucket, anno.getAssignedBy());
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_ASSIGNEDBY, anno, assignedByGeneProductBucket, anno.getAssignedBy());

		//summary
		//genericAnnotationCalculation(StatisticsCalculation.ANNOTATION_HITS_SUMMARY, summaryAnnotationBucket, "total-annotations");
		summaryAnnotationBucket++;
		genericGeneProductCalculation(StatisticsCalculation.GENEPRODUCT_HITS_SUMMARY, anno, summaryGeneProductBucket, "total-unique-geneproducts");
	}


	/**
	 * Generic processing of the requested element
	 * @param bucket
	 * @param key
	 */
	private void genericAnnotationCalculation(String type, Map<String, StatisticTuple> bucket, String key){
		StatisticTuple statsTuple = bucket.get(key);
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(type, key, 1);
			bucket.put(key, statsTuple);
		}else {
			statsTuple.addHit();
		}
	}


	private void genericGeneProductCalculation(String type, GOAnnotation anno, Map<String, StatisticTuple> bucket, String key){
		StatisticTuple statsTuple = bucket.get(key);
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(type, key, 0);
			statsTuple.uniqueHit(anno.getDbObjectID());
			bucket.put(key, statsTuple);
		} else {
			statsTuple.uniqueHit(anno.getDbObjectID());
		}
	}


	//##############################################################
	// The following section is where we sort the data

	public StatisticTuple[] topAnnotationsPerGOID() {
		Collection<StatisticTuple> values = gotermAnnotationBucket.values();
		return sortResults(values);
	}


	/**
	 * From a set of goids that meet the criteria entered by the user (as a filter)
	 * get the statistics for those goIDs, sort by number of annotations (for the annotations bucket)
	 * and sort by gene products (for that bucket) and return the list (with numbers) to the client.
	 *
	 * We only ever use an exact match for goids (children aren't taken into account).
	 *
	 */
	public StatisticTuple[] topGeneProductsPerGOID(){
		Collection<StatisticTuple> values = gotermGeneProductBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topAnnotationsPerAspect() {
		Collection<StatisticTuple> values = aspectAnnotationBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topGeneProductsPerAspect() {
		Collection<StatisticTuple> values = aspectGeneProductBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topAnnotationsPerEvidence() {
		Collection<StatisticTuple> values = evidenceAnnotationBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topGeneProductsPerEvidence() {
		Collection<StatisticTuple> values = evidenceGeneProductBucket.values();
		return sortResults(values);
	}



	public StatisticTuple[] topAnnotationsPerReference() {
		Collection<StatisticTuple> values = referenceAnnotationBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topGeneProductsPerReference() {
		Collection<StatisticTuple> values = referenceGeneProductBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topAnnotationsPerTaxon() {
		Collection<StatisticTuple> values = taxonAnnotationBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topGeneProductsPerTaxon() {
		Collection<StatisticTuple> values = taxonGeneProductBucket.values();
		return sortResults(values);
	}

	public StatisticTuple[] topAnnotationsPerAssignedBy() {
		Collection<StatisticTuple> values = assignedByAnnotationBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topGeneProductsPerAssignedBy() {
		Collection<StatisticTuple> values = assignedByGeneProductBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topAnnotationsSummary() {
		Collection<StatisticTuple> values = assignedByAnnotationBucket.values();
		return sortResults(values);
	}


	public StatisticTuple[] topGeneProductsSummary() {
		Collection<StatisticTuple> values = assignedByGeneProductBucket.values();
		return sortResults(values);
	}


	/**
	 * Sort the results, and take the top hits as a construct to index, after calculating hit percentages.
	 * @param values
	 * @return
	 */
	private StatisticTuple[] sortResults(Collection<StatisticTuple> values) {
		StatisticTuple[] valuesArr = values.toArray(new StatisticTuple[values.size()]);
		Arrays.sort(valuesArr, new CountComparator());
		StatisticTuple[] topHits = Arrays.copyOfRange(valuesArr, 0, STORED_HITS);

		//Calculate percentages
		for(StatisticTuple aTuple:topHits){
			if(aTuple==null)break;
			aTuple.calculateStatisticTuplePercentage(summaryAnnotationBucket);
		}

		return topHits;
	}



	/**
	 * Compare number of hits for each tuple. Will sort in a DESCENDING order in size.
	 * If the qty is the same then order by the key using natural String ordering (this helps for testing mainly)
	 */
	private class CountComparator implements Comparator<StatisticTuple>{

		@Override
		public int compare(StatisticTuple o1, StatisticTuple o2) {
			if(o1.getStatisticTupleHits()==o2.getStatisticTupleHits()){
				if(o1.getstatisticTupleKey().equals(o2.getstatisticTupleKey())){
					throw new IllegalArgumentException("The id for the statistics tuple is being compared with itself " + o1.getstatisticTupleKey());
				}else{
					return o1.getstatisticTupleKey().compareTo(o2.getstatisticTupleKey());
				}
			}
			return o1.getStatisticTupleHits()>o2.getStatisticTupleHits()?-1:1;
		}
	}
}
