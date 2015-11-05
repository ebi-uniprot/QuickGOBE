package uk.ac.ebi.quickgo.statistic;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Save statistics information.
 * Map values from precalculated cache in solr to statistics information
 * @author twardell
 *
 */
public class StatisticsCalculation {


	public static final String ANNOTATION_HITS_GOTERM  = "ANNOTATION_HITS_GOTERM";
	public static final String GENEPRODUCT_HITS_GOTERM = "GENEPRODUCT_HITS_GOTERM";
	public static final String ANNOTATION_HITS_ASPECT  = "ANNOTATION_HITS_ASPECT";
	public static final String GENEPRODUCT_HITS_ASPECT_ = "GENEPRODUCT_HITS_ASPECT";
	public static final String ANNOTATION_HITS_EVIDENCE  = "ANNOTATION_HITS_EVIDENCE";
	public static final String GENEPRODUCT_HITS_EVIDENCE = "GENEPRODUCT_HITS_EVIDENCE";
	public static final String ANNOTATION_HITS_REFERENCE  = "ANNOTATION_HITS_REFERENCE";
	public static final String GENEPRODUCT_HITS_REFERENCE = "GENEPRODUCT_HITS_REFERENCE";
	public static final String ANNOTATION_HITS_TAXON = "ANNOTATION_HITS_TAXON";
	public static final String GENEPRODUCT_HITS_TAXON = "GENEPRODUCT_HITS_TAXON";
	public static final String ANNOTATION_HITS_ASSIGNEDBY  = "ANNOTATION_HITS_ASSIGNEDBY";
	public static final String GENEPRODUCT_HITS_ASSIGNEDBY = "GENEPRODUCT_HITS_ASSIGNEDBY";
	public static final String ANNOTATION_HITS_SUMMARY  = "ANNOTATION_HITS_SUMMARY";
	public static final String GENEPRODUCT_HITS_SUMMARY = "GENEPRODUCT_HITS_SUMMARY";



	private long totalNumberAnnotations;
	private long totalNumberGeneProducts;

	// GO ID
	private Set<StatsTerm> annotationsPerGOID = new TreeSet<StatsTerm>();
	private Set<StatsTerm> geneProductsPerGOID = new TreeSet<StatsTerm>();

	// Aspect
	private Set<StatsTerm> annotationsPerAspect = new TreeSet<StatsTerm>();
	private Set<StatsTerm> geneProductsPerAspect = new TreeSet<StatsTerm>();

	// Evidence
	private Set<StatsTerm> annotationsPerEvidence = new TreeSet<StatsTerm>();
	private Set<StatsTerm> geneProductsPerEvidence = new TreeSet<StatsTerm>();

	// Reference
	private Set<StatsTerm> annotationsPerReference = new TreeSet<StatsTerm>();
	private Set<StatsTerm> geneProductsPerReference = new TreeSet<StatsTerm>();

	// Taxon
	private Set<StatsTerm> annotationsPerTaxon = new TreeSet<StatsTerm>();
	private Set<StatsTerm> geneProductsPerTaxon = new TreeSet<StatsTerm>();

	// Assigned By
	private Set<StatsTerm> annotationsPerAssignedBy = new TreeSet<StatsTerm>();
	private Set<StatsTerm> geneProductsPerAssignedBy = new TreeSet<StatsTerm>();

	// Db Object ID
	private Set<StatsTerm> annotationsPerDBObjectID = new TreeSet<StatsTerm>();

	public StatisticsCalculation() {}

	public StatisticsCalculation(List<Miscellaneous> precalculatedStats) {
		for(Miscellaneous misc: precalculatedStats){

			//Go Terms
			if(ANNOTATION_HITS_GOTERM.equals(misc.getStatisticTupleType())){
				annotationsPerGOID.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}
			if(GENEPRODUCT_HITS_GOTERM.equals(misc.getStatisticTupleType())){
				geneProductsPerGOID.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}

			//Aspect
			if(ANNOTATION_HITS_ASPECT.equals(misc.getStatisticTupleType())){
				annotationsPerAspect.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}
			if(GENEPRODUCT_HITS_ASPECT_.equals(misc.getStatisticTupleType())){
				geneProductsPerAspect.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}

			//Evidence
			if(ANNOTATION_HITS_EVIDENCE.equals(misc.getStatisticTupleType())){
				annotationsPerEvidence.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}
			if(GENEPRODUCT_HITS_EVIDENCE.equals(misc.getStatisticTupleType())){
				geneProductsPerEvidence.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}

			//Reference
			if(ANNOTATION_HITS_REFERENCE.equals(misc.getStatisticTupleType())){
				annotationsPerReference.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}
			if(GENEPRODUCT_HITS_REFERENCE.equals(misc.getStatisticTupleType())){
				geneProductsPerReference.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}

			//Taxon
			if(ANNOTATION_HITS_TAXON.equals(misc.getStatisticTupleType())){
				annotationsPerTaxon.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}
			if(GENEPRODUCT_HITS_TAXON.equals(misc.getStatisticTupleType())){
				geneProductsPerTaxon.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}

			//Assigned By
			if(ANNOTATION_HITS_ASSIGNEDBY.equals(misc.getStatisticTupleType())){
				annotationsPerAssignedBy.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}
			if(GENEPRODUCT_HITS_ASSIGNEDBY.equals(misc.getStatisticTupleType())){
				geneProductsPerAssignedBy.add(new StatsTerm(misc.getStatisticTupleKey(), misc.getStatisticTupleName(),
						misc.getStatisticTuplePercentage(), misc.getStatisticTupleHits()));
				continue;
			}

			//Summary
			if(ANNOTATION_HITS_SUMMARY.equals(misc.getStatisticTupleType())){
				totalNumberAnnotations = misc.getStatisticTupleHits();
				continue;
			}
			if(GENEPRODUCT_HITS_SUMMARY.equals(misc.getStatisticTupleType())){
				totalNumberGeneProducts = misc.getStatisticTupleHits();
				continue;
			}

		}
	}

	public long getTotalNumberAnnotations() {
		return totalNumberAnnotations;
	}

	public long getTotalNumbergeneProducts() {
		return totalNumberGeneProducts;
	}

	public Set<StatsTerm> getAnnotationsPerGOID() {
		return annotationsPerGOID;
	}

	public Set<StatsTerm> getGeneProductsPerGOID() {
		return geneProductsPerGOID;
	}

	public Set<StatsTerm> getAnnotationsPerAspect() {
		return annotationsPerAspect;
	}

	public Set<StatsTerm> getGeneProductsPerAspect() {
		return geneProductsPerAspect;
	}

	public Set<StatsTerm> getAnnotationsPerEvidence() {
		return annotationsPerEvidence;
	}

	public Set<StatsTerm> getGeneProductsPerEvidence() {
		return geneProductsPerEvidence;
	}

	public Set<StatsTerm> getAnnotationsPerReference() {
		return annotationsPerReference;
	}

	public Set<StatsTerm> getGeneProductsPerReference() {
		return geneProductsPerReference;
	}

	public Set<StatsTerm> getAnnotationsPerTaxon() {
		return annotationsPerTaxon;
	}

	public Set<StatsTerm> getGeneProductsPerTaxon() {
		return geneProductsPerTaxon;
	}

	public Set<StatsTerm> getAnnotationsPerAssignedBy() {
		return annotationsPerAssignedBy;
	}

	public Set<StatsTerm> getGeneProductsPerAssignedBy() {
		return geneProductsPerAssignedBy;
	}

	public Set<StatsTerm> getAnnotationsPerDBObjectID() {
		return annotationsPerDBObjectID;
	}


}
