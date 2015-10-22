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

	private static final String ANNOTATION_HITS_GOTERM  = "ANNOTATION_HITS_GOTERM";
	private static final String GENEPRODUCT_HITS_GOTERM = "GENEPRODUCT_HITS_GOTERM";
	private static final String ANNOTATION_HITS_ASPECT  = "ANNOTATION_HITS_ASPECT";
	private static final String GENEPRODUCT_HITS_ASPECT_ = "GENEPRODUCT_HITS_ASPECT_";
	private static final String ANNOTATION_HITS_EVIDENCE  = "ANNOTATION_HITS_EVIDENCE";
	private static final String GENEPRODUCT_HITS_EVIDENCE = "GENEPRODUCT_HITS_EVIDENCE";
	private static final String ANNOTATION_HITS_REFERENCE  = "ANNOTATION_HITS_REFERENCE";
	private static final String GENEPRODUCT_HITS_REFERENCE = "GENEPRODUCT_HITS_REFERENCE";
	private static final String ANNOTATION_HITS_TAXON = "ANNOTATION_HITS_TAXON";
	private static final String GENEPRODUCT_HITS_TAXON = "GENEPRODUCT_HITS_TAXON";
	private static final String ANNOTATION_HITS_ASSIGNEDBY  = "ANNOTATION_HITS_ASSIGNEDBY";
	private static final String GENEPRODUCT_HITS_ASSIGNEDBY = "GENEPRODUCT_HITS_ASSIGNEDBY";
	private static final String ANNOTATION_HITS_SUMMARY  = "ANNOTATION_HITS_SUMMARY";
	private static final String GENEPRODUCT_HITS_SUMMARY = "GENEPRODUCT_HITS_SUMMARY";



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


	//Sorted Results

//	private StatisticTuple[] gotermAnnotationTopHits;
//	private StatisticTuple[] gotermGeneProductTopHits;
//	private StatisticTuple[] aspectAnnotationTopHits;
//	private StatisticTuple[] aspectGeneProductTopHits;
//	private StatisticTuple[] evidenceAnnotationTopHits;
//	private StatisticTuple[] evidenceGeneProductTopHits;
//	private StatisticTuple[] referenceAnnotationTopHits;
//	private StatisticTuple[] referenceGeneProductTopHits;
//	private StatisticTuple[] taxonAnnotationTopHits;
//	private StatisticTuple[] taxonGeneProductTopHits;
//	private StatisticTuple[] assignedByAnnotationTopHits;
//	private StatisticTuple[] assignedByGeneProductTopHits;
//	private StatisticTuple[] summaryAnnotationTopHits;
//	private StatisticTuple[] summaryGeneProductTopHits;



	public void addAnnotationToStatistics(GOAnnotation anno) {


		genericAnnotationCalculation(ANNOTATION_HITS_GOTERM, gotermAnnotationBucket, anno.getGoID());
		genericGeneProductCalculation(GENEPRODUCT_HITS_GOTERM, anno, gotermGeneProductBucket, anno.getGoID());

		genericAnnotationCalculation(ANNOTATION_HITS_ASPECT, aspectAnnotationBucket, anno.getGoAspect());
		genericGeneProductCalculation(GENEPRODUCT_HITS_ASPECT_, anno, aspectGeneProductBucket, anno.getGoAspect());

		genericAnnotationCalculation(ANNOTATION_HITS_EVIDENCE, evidenceAnnotationBucket, anno.getGoEvidence());
		genericGeneProductCalculation(GENEPRODUCT_HITS_EVIDENCE, anno, evidenceGeneProductBucket, anno.getGoEvidence());

		//Reference
		genericAnnotationCalculation(ANNOTATION_HITS_REFERENCE, referenceAnnotationBucket, anno.getReference());
		genericGeneProductCalculation(GENEPRODUCT_HITS_REFERENCE, anno, referenceGeneProductBucket, anno.getReference());

		//Taxon
		genericAnnotationCalculation(ANNOTATION_HITS_TAXON, taxonAnnotationBucket, Integer.toString(anno.getTaxonomyId()));
		genericGeneProductCalculation(GENEPRODUCT_HITS_TAXON, anno, taxonGeneProductBucket, Integer.toString(anno.getTaxonomyId()));

		//AssignedBy
		genericAnnotationCalculation(ANNOTATION_HITS_ASSIGNEDBY, assignedByAnnotationBucket, anno.getAssignedBy());
		genericGeneProductCalculation(GENEPRODUCT_HITS_ASSIGNEDBY, anno, assignedByGeneProductBucket, anno.getAssignedBy());

		//summary
		genericAnnotationCalculation(ANNOTATION_HITS_SUMMARY, summaryAnnotationBucket, "total-annotations");
		genericGeneProductCalculation(GENEPRODUCT_HITS_SUMMARY, anno, summaryGeneProductBucket, "total-unique-geneproducts");
	}


	/**
	 *
	 */
//	public void calculateTopHits(){
//		gotermAnnotationTopHits 	= topAnnotationsPerGOID();
//		gotermGeneProductTopHits 	= topGeneProductsPerGOID();
//		aspectAnnotationTopHits 	= topAnnotationsPerAspect();
//		aspectGeneProductTopHits 	= topGeneProductsPerAspect();
//		evidenceAnnotationTopHits 	= topAnnotationsPerEvidence();
//		evidenceGeneProductTopHits 	= topGeneProductsPerEvidence();
//		referenceAnnotationTopHits 	= topAnnotationsPerReference();
//		referenceGeneProductTopHits = topGeneProductsPerReference();
//		taxonAnnotationTopHits 		= topAnnotationsPerTaxon();
//		taxonGeneProductTopHits 	= topGeneProductsPerTaxon();
//		assignedByAnnotationTopHits = topAnnotationsPerAssignedBy();
//		assignedByGeneProductTopHits = topGeneProductsPerAssignedBy();
//		summaryAnnotationTopHits 	= topAnnotationsSummary();
//		summaryGeneProductTopHits 	= topGeneProductsSummary();
//	}

//	public StatisticTuple[] getGotermAnnotationTopHits() {
//		return gotermAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getGotermGeneProductTopHits() {
//		return gotermGeneProductTopHits;
//	}
//
//	public StatisticTuple[] getAspectAnnotationTopHits() {
//		return aspectAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getAspectGeneProductTopHits() {
//		return aspectGeneProductTopHits;
//	}
//
//	public StatisticTuple[] getEvidenceAnnotationTopHits() {
//		return evidenceAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getEvidenceGeneProductTopHits() {
//		return evidenceGeneProductTopHits;
//	}
//
//	public StatisticTuple[] getReferenceAnnotationTopHits() {
//		return referenceAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getReferenceGeneProductTopHits() {
//		return referenceGeneProductTopHits;
//	}
//
//	public StatisticTuple[] getTaxonAnnotationTopHits() {
//		return taxonAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getTaxonGeneProductTopHits() {
//		return taxonGeneProductTopHits;
//	}
//
//	public StatisticTuple[] getAssignedByAnnotationTopHits() {
//		return assignedByAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getAssignedByGeneProductTopHits() {
//		return assignedByGeneProductTopHits;
//	}
//
//	public StatisticTuple[] getSummaryAnnotationTopHits() {
//		return summaryAnnotationTopHits;
//	}
//
//	public StatisticTuple[] getSummaryGeneProductTopHits() {
//		return summaryGeneProductTopHits;
//	}

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
