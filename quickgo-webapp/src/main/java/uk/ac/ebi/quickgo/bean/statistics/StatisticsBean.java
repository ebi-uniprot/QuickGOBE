package uk.ac.ebi.quickgo.bean.statistics;

import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.quickgo.service.statistic.type.StatsTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

/**
 * Bean to store statistics values depending on the applied filters
 * @author cbonill
 *
 */
public class StatisticsBean {

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

	public void setAnnotationsPerGOID(Set<StatsTerm> annotationsPerGOID) {
		this.annotationsPerGOID = annotationsPerGOID;
	}

	public Set<StatsTerm> getProteinsPerGOID() {
		return proteinsPerGOID;
	}

	public void setProteinsPerGOID(Set<StatsTerm> proteinsPerGOID) {
		this.proteinsPerGOID = proteinsPerGOID;
	}

	public Set<StatsTerm> getAnnotationsPerAspect() {
		return annotationsPerAspect;
	}

	public void setAnnotationsPerAspect(Set<StatsTerm> annotationsPerAspect) {
		this.annotationsPerAspect = annotationsPerAspect;
	}

	public Set<StatsTerm> getProteinsPerAspect() {
		return proteinsPerAspect;
	}

	public void setProteinsPerAspect(Set<StatsTerm> proteinsPerAspect) {
		this.proteinsPerAspect = proteinsPerAspect;
	}

	public Set<StatsTerm> getAnnotationsPerEvidence() {
		return annotationsPerEvidence;
	}

	public void setAnnotationsPerEvidence(Set<StatsTerm> annotationsPerEvidence) {
		this.annotationsPerEvidence = annotationsPerEvidence;
	}

	public Set<StatsTerm> getProteinsPerEvidence() {
		return proteinsPerEvidence;
	}

	public void setProteinsPerEvidence(Set<StatsTerm> proteinsPerEvidence) {
		this.proteinsPerEvidence = proteinsPerEvidence;
	}

	public Set<StatsTerm> getAnnotationsPerReference() {
		return annotationsPerReference;
	}

	public void setAnnotationsPerReference(
			Set<StatsTerm> annotationsPerReference) {
		this.annotationsPerReference = annotationsPerReference;
	}

	public Set<StatsTerm> getProteinsPerReference() {
		return proteinsPerReference;
	}

	public void setProteinsPerReference(Set<StatsTerm> proteinsPerReference) {
		this.proteinsPerReference = proteinsPerReference;
	}

	public Set<StatsTerm> getAnnotationsPerTaxon() {
		return annotationsPerTaxon;
	}

	public void setAnnotationsPerTaxon(Set<StatsTerm> annotationsPerTaxon) {
		this.annotationsPerTaxon = annotationsPerTaxon;
	}

	public Set<StatsTerm> getProteinsPerTaxon() {
		return proteinsPerTaxon;
	}

	public void setProteinsPerTaxon(Set<StatsTerm> proteinsPerTaxon) {
		this.proteinsPerTaxon = proteinsPerTaxon;
	}

	public Set<StatsTerm> getAnnotationsPerAssignedBy() {
		return annotationsPerAssignedBy;
	}

	public void setAnnotationsPerAssignedBy(
			Set<StatsTerm> annotationsPerAssignedBy) {
		this.annotationsPerAssignedBy = annotationsPerAssignedBy;
	}

	public Set<StatsTerm> getProteinsPerAssignedBy() {
		return proteinsPerAssignedBy;
	}

	public void setProteinsPerAssignedBy(Set<StatsTerm> proteinsPerAssignedBy) {
		this.proteinsPerAssignedBy = proteinsPerAssignedBy;
	}	
	
	public Set<StatsTerm> getAnnotationsPerDBObjectID() {
		return annotationsPerDBObjectID;
	}

	public void setAnnotationsPerDBObjectID(Set<StatsTerm> annotationsPerDBObjectID) {
		this.annotationsPerDBObjectID = annotationsPerDBObjectID;
	}

	public Set<StatsTerm> getStatsByProtein(AnnotationField annotationField){
		switch (annotationField){
			case GOID:
				for(StatsTerm statsTerm : this.getProteinsPerGOID()){
					String code = statsTerm.getCode(); 
					statsTerm.setCode("GO:" + code);
				}
				return this.getProteinsPerGOID();				
			case GOASPECT:
				return this.getProteinsPerAspect();
			case GOEVIDENCE:
				return this.getProteinsPerEvidence();
			case REFERENCE:
				return this.getProteinsPerReference();
			case TAXONOMYID:
				return this.getProteinsPerTaxon();
			case ASSIGNEDBY:
				return this.getProteinsPerAssignedBy();
		}
		return null;
	}
	
	public Set<StatsTerm> getStatsByAnnotation(AnnotationField annotationField){
		switch (annotationField){
			case GOID:
				for(StatsTerm statsTerm : this.getAnnotationsPerGOID()){
					String code = statsTerm.getCode(); 
					statsTerm.setCode("GO:" + code);
				}
				return this.getAnnotationsPerGOID();
			case GOASPECT:
				return this.getAnnotationsPerAspect();
			case GOEVIDENCE:
				return this.getAnnotationsPerEvidence();
			case REFERENCE:
				return this.getAnnotationsPerReference();
			case TAXONOMYID:
				return this.getAnnotationsPerTaxon();
			case ASSIGNEDBY:
				return this.getAnnotationsPerAssignedBy();
			case DBOBJECTID:
				for(StatsTerm statsTerm : this.getAnnotationsPerDBObjectID()){
					String code = statsTerm.getCode(); 
					statsTerm.setCode(code.toUpperCase());
				}
				return this.getAnnotationsPerDBObjectID();
		}
		return null;
	}
}