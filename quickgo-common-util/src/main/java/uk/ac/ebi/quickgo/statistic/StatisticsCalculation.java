package uk.ac.ebi.quickgo.statistic;

import java.util.Set;
import java.util.TreeSet;

/**
 * Bean to store statistics values depending on the applied filters
 * @author cbonill
 *
 */
public class StatisticsCalculation {

	// GO ID
	Set<StatsTerm> annotationsPerGOID = new TreeSet<StatsTerm>();
	Set<StatsTerm> proteinsPerGOID = new TreeSet<StatsTerm>();

	// Aspect
	Set<StatsTerm> annotationsPerAspect = new TreeSet<StatsTerm>();
	Set<StatsTerm> proteinsPerAspect = new TreeSet<StatsTerm>();

	// Evidence
	Set<StatsTerm> annotationsPerEvidence = new TreeSet<StatsTerm>();
	Set<StatsTerm> proteinsPerEvidence = new TreeSet<StatsTerm>();

	// Reference
	Set<StatsTerm> annotationsPerReference = new TreeSet<StatsTerm>();
	Set<StatsTerm> proteinsPerReference = new TreeSet<StatsTerm>();

	// Taxon
	Set<StatsTerm> annotationsPerTaxon = new TreeSet<StatsTerm>();
	Set<StatsTerm> proteinsPerTaxon = new TreeSet<StatsTerm>();

	// Assigned By
	Set<StatsTerm> annotationsPerAssignedBy = new TreeSet<StatsTerm>();
	Set<StatsTerm> proteinsPerAssignedBy = new TreeSet<StatsTerm>();

	// Db Object ID
	Set<StatsTerm> annotationsPerDBObjectID = new TreeSet<StatsTerm>();

	public Set<StatsTerm> getAnnotationsPerGOID() {
		return annotationsPerGOID;
	}

	public Set<StatsTerm> getProteinsPerGOID() {
		return proteinsPerGOID;
	}

	public Set<StatsTerm> getAnnotationsPerAspect() {
		return annotationsPerAspect;
	}

	public Set<StatsTerm> getProteinsPerAspect() {
		return proteinsPerAspect;
	}

	public Set<StatsTerm> getAnnotationsPerEvidence() {
		return annotationsPerEvidence;
	}

	public Set<StatsTerm> getProteinsPerEvidence() {
		return proteinsPerEvidence;
	}

	public Set<StatsTerm> getAnnotationsPerReference() {
		return annotationsPerReference;
	}

	public Set<StatsTerm> getProteinsPerReference() {
		return proteinsPerReference;
	}

	public Set<StatsTerm> getAnnotationsPerTaxon() {
		return annotationsPerTaxon;
	}

	public Set<StatsTerm> getProteinsPerTaxon() {
		return proteinsPerTaxon;
	}

	public Set<StatsTerm> getAnnotationsPerAssignedBy() {
		return annotationsPerAssignedBy;
	}

	public Set<StatsTerm> getProteinsPerAssignedBy() {
		return proteinsPerAssignedBy;
	}

	public Set<StatsTerm> getAnnotationsPerDBObjectID() {
		return annotationsPerDBObjectID;
	}

}
