package uk.ac.ebi.quickgo.indexer.annotation;

import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;

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

	private Map<String, StatisticTuple> summaryAnnotationBucket = new TreeMap<>();
	private Map<String, StatisticTuple> summaryGeneProductBucket = new TreeMap<>();

	private long annotationsCounter = 0;


	public void addAnnotationToStatistics(GOAnnotation anno) {

		//Add to total annotations counter
		annotationsCounter++;

		gotermAnnotationCalculation(anno);
		genericGeneProductCalculation(anno, gotermGeneProductBucket, anno.getGoID());

		aspectAnnotationCalculation(anno);
		genericGeneProductCalculation(anno, aspectGeneProductBucket, anno.getGoAspect());

		evidenceAnnotationCalculation(anno);
		genericGeneProductCalculation(anno, evidenceGeneProductBucket, anno.getGoEvidence());

		//Reference
		genericAnnotationCalculation(referenceAnnotationBucket, anno.getReference());
		genericGeneProductCalculation(anno, referenceGeneProductBucket, anno.getReference());
	}



	private void gotermAnnotationCalculation(GOAnnotation anno) {
		StatisticTuple statsTuple = gotermAnnotationBucket.get(anno.getGoID());
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(anno.getGoID(), 1);
			gotermAnnotationBucket.put(anno.getGoID(), statsTuple);
		}else {
			statsTuple.addHit();
		}

	}

	/**
	 * Add to the count of proteins for this go term, if the protein in this annotation is unique
	 * to this go term, else fuggetaboutit.
	 * @param anno
	 */
	private void gotermGeneProductCalculation(GOAnnotation anno) {
		StatisticTuple statsTuple = gotermGeneProductBucket.get(anno.getGoID());
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(anno.getGoID(), 0);
			statsTuple.uniqueHit(anno.getDbObjectID());
			gotermGeneProductBucket.put(anno.getGoID(), statsTuple);
		} else {
			statsTuple.uniqueHit(anno.getDbObjectID());
		}
	}


	/**
	 * Annotations by Aspect
	 * @param anno
	 */
	private void aspectAnnotationCalculation(GOAnnotation anno){
		StatisticTuple statsTuple = aspectAnnotationBucket.get(anno.getGoAspect());
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(anno.getGoAspect(), 1);
			aspectAnnotationBucket.put(anno.getGoAspect(), statsTuple);
		}else {
			statsTuple.addHit();
		}
	}

	/**
	 * Gene Products by Aspect
	 * @param anno
	 */
	private void aspectGeneProductCalculation(GOAnnotation anno){
		StatisticTuple statsTuple = aspectGeneProductBucket.get(anno.getGoAspect());
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(anno.getGoAspect(), 0);
			statsTuple.uniqueHit(anno.getDbObjectID());
			aspectGeneProductBucket.put(anno.getGoAspect(), statsTuple);
		} else {
			statsTuple.uniqueHit(anno.getDbObjectID());
		}
	}


	/**
	 * Annotations by Evidence
	 * @param anno
	 */
	private void evidenceAnnotationCalculation(GOAnnotation anno){
		StatisticTuple statsTuple = evidenceAnnotationBucket.get(anno.getGoEvidence());
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(anno.getGoEvidence(), 1);
			evidenceAnnotationBucket.put(anno.getGoEvidence(), statsTuple);
		}else {
			statsTuple.addHit();
		}
	}

	/**
	 * Gene Products by Evidence
	 * @param anno
	 */
	private void evidenceGeneProductCalculation(GOAnnotation anno){
		StatisticTuple statsTuple = evidenceGeneProductBucket.get(anno.getGoEvidence());
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(anno.getGoEvidence(), 0);
			statsTuple.uniqueHit(anno.getDbObjectID());
			evidenceGeneProductBucket.put(anno.getGoEvidence(), statsTuple);
		} else {
			statsTuple.uniqueHit(anno.getDbObjectID());
		}
	}


	/**
	 * Generic processing of the requested element
	 * @param bucket
	 * @param key
	 */
	private void genericAnnotationCalculation(Map<String, StatisticTuple> bucket, String key){
		StatisticTuple statsTuple = bucket.get(key);
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(key, 1);
			bucket.put(key, statsTuple);
		}else {
			statsTuple.addHit();
		}
	}


	private void genericGeneProductCalculation(GOAnnotation anno, Map<String, StatisticTuple> bucket, String key){
		StatisticTuple statsTuple = bucket.get(key);
		if(statsTuple == null ){
			statsTuple = new StatisticTuple(key, 0);
			statsTuple.uniqueHit(anno.getDbObjectID());
			bucket.put(key, statsTuple);
		} else {
			statsTuple.uniqueHit(anno.getDbObjectID());
		}
	}


	//##############################################################
	// The following section is where we return the data

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


	/**
	 * sort the results
	 * @param values
	 * @return
	 */
	private StatisticTuple[] sortResults(Collection<StatisticTuple> values) {
		StatisticTuple[] valuesArr = values.toArray(new StatisticTuple[values.size()]);
		Arrays.sort(valuesArr, new CountComparator());
		return Arrays.copyOfRange(valuesArr, 0, STORED_HITS);
	}



	/**
	 * Compare number of hits for each tuple. Will sort in a DESCENDING order in size.
	 * If the qty is the same then order by the key using natural String ordering (this helps for testing mainly)
	 */
	private class CountComparator implements Comparator<StatisticTuple>{

		@Override
		public int compare(StatisticTuple o1, StatisticTuple o2) {
			if(o1.getHits()==o2.getHits()){
				if(o1.getKey().equals(o2.getKey())){
					throw new IllegalArgumentException("The id for the statistics tuple is being compared with itself " + o1.getKey());
				}else{
					return o1.getKey().compareTo(o2.getKey());
				}
			}
			return o1.getHits()>o2.getHits()?-1:1;
		}
	}
}
