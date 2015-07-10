/**
 *
 */
package uk.ac.ebi.quickgo.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds the data for one row read from a gp_association (GPAD) file
 * (i.e., a single GO annotation)
 *
 * @author tonys
 *
 */
@Deprecated
public class Annotation implements Serializable{

	private static final long serialVersionUID = 2592634776573970012L;

	private String goEvidence;
	private String db;
	private String dbObjectID;
	private String dbObjectSymbol;
	private String dbObjectName;
	private String dbObjectType;
	private List<String> dbObjectSynonyms = new ArrayList<>();
	private String goID;
	private String ecoID;
	private String termName;
	private String assignedBy;
	private String reference;
	private List<String> with;
	private String fullWith;
	private String qualifier;
	private String interactingTaxID;
	private String date;
	private List<String> extensions;
	private String fullExtension;
	private String properties;

	// Ancestors
	private List<String> ancestorsI;
	private List<String> ancestorsIPO;
	private List<String> ancestorsIPOR;

	// ECO Ancestors
	private List<String> ecoAncestorsI;

	// Extra fields
	private String targetSet;
	private String goAspect;
	private int taxonomyId;
	private String taxonomyName;
	private List<Integer> taxonomyClosure;
	private int sequenceLength;
	private List<String> gp2protein;
	private List<String> subset;

	public Annotation() {
	}

	public Annotation(String goEvidence, String db,
			String dbObjectID, String goID, String ecoID, String termName, String assignedBy,
			String reference, List<String> with, String fullWith,
			String qualifier, String interactingTaxID, String date,
			List<String> extensions, String fullExtension, String properties) {

		this.goEvidence = goEvidence;
		this.db = db;
		this.dbObjectID = dbObjectID;
		this.goID = goID;
		this.ecoID = ecoID;
		this.termName = termName;
		this.assignedBy = assignedBy;
		this.reference = reference;
		this.with = with;
		this.fullWith = fullWith;
		this.qualifier = qualifier;
		this.interactingTaxID = interactingTaxID;
		this.date = date;
		this.extensions = extensions;
		this.fullExtension=fullExtension;
		this.properties = properties;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getDbObjectID() {
		return dbObjectID;
	}

	public void setDbObjectID(String dbObjectID) {
		this.dbObjectID = dbObjectID;
	}

	public String getGoID() {
		return goID;
	}

	public void setGoID(String goID) {
		this.goID = goID;
	}

	public String getEcoID() {
		return ecoID;
	}

	public void setEcoID(String ecoID) {
		this.ecoID = ecoID;
	}

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public List<String> getWith() {
		return with;
	}

	public void setWith(List<String> with) {
		this.with = with;
	}

	public String getFullWith() {
		return fullWith;
	}

	public void setFullWith(String fullWith) {
		this.fullWith = fullWith;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public  String getQualifier() {
		return qualifier;
	}

	public void setQualifier( String qualifier) {
		this.qualifier = qualifier;
	}

	public String getInteractingTaxID() {
		return interactingTaxID;
	}

	public void setInteractingTaxID(String interactingTaxID) {
		this.interactingTaxID = interactingTaxID;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}

	public String getFullExtension() {	return fullExtension;	}

	public void setFullExtension(String fullExtension) {
		this.fullExtension = fullExtension;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getGoEvidence() {
		return goEvidence;
	}

	public void setGoEvidence(String goEvidence) {
		this.goEvidence = goEvidence;
	}

	public String getGoAspect() {
		return goAspect;
	}

	public void setGoAspect(String goAspect) {
		this.goAspect = goAspect;
	}

	public int getTaxonomyId() {
		return taxonomyId;
	}

	public void setTaxonomyId(int taxonomyId) {
		this.taxonomyId = taxonomyId;
	}

	public List<Integer> getTaxonomyClosure() {
		return taxonomyClosure;
	}

	public void setTaxonomyClosure(List<Integer> taxonomyClosure) {
		this.taxonomyClosure = taxonomyClosure;
	}

	public List<String> getAncestorsI() {
		return ancestorsI;
	}

	public void setAncestorsI(List<String> ancestorsI) {
		this.ancestorsI = ancestorsI;
	}

	public List<String> getAncestorsIPO() {
		return ancestorsIPO;
	}

	public void setAncestorsIPO(List<String> ancestorsIPO) {
		this.ancestorsIPO = ancestorsIPO;
	}

	public List<String> getAncestorsIPOR() {
		return ancestorsIPOR;
	}

	public void setAncestorsIPOR(List<String> ancestorsIPOR) {
		this.ancestorsIPOR = ancestorsIPOR;
	}

	public String getTargetSet() {
		return targetSet;
	}

	public void setTargetSet(String targetSet) {
		this.targetSet = targetSet;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getDbObjectSymbol() {
		return dbObjectSymbol;
	}

	public void setDbObjectSymbol(String dbObjectSymbol) {
		this.dbObjectSymbol = dbObjectSymbol;
	}

	public String getDbObjectName() {
		return dbObjectName;
	}

	public void setDbObjectName(String dbObjectName) {
		this.dbObjectName = dbObjectName;
	}

	public String getTaxonomyName() {
		return taxonomyName;
	}

	public void setTaxonomyName(String taxonomyName) {
		this.taxonomyName = taxonomyName;
	}

	public List<String> getDbObjectSynonyms() {
		return dbObjectSynonyms;
	}

	public void setDbObjectSynonyms(List<String> dbObjectSynonyms) {
		this.dbObjectSynonyms = dbObjectSynonyms;
	}

	public String getDbObjectType() {
		return dbObjectType;
	}

	public void setDbObjectType(String dbObjectType) {
		this.dbObjectType = dbObjectType;
	}

	public int getSequenceLength() {
		return sequenceLength;
	}

	public void setSequenceLength(int sequenceLength) {
		this.sequenceLength = sequenceLength;
	}

	public List<String> getGp2protein() {
		return gp2protein;
	}

	public void setGp2protein(List<String> gp2protein) {
		this.gp2protein = gp2protein;
	}

	public List<String> getSubset() {
		return subset;
	}

	public void setSubset(List<String> subset) {
		this.subset = subset;
	}

	public List<String> getEcoAncestorsI() {
		return ecoAncestorsI;
	}

	public void setEcoAncestorsI(List<String> ecoAncestorsI) {
		this.ecoAncestorsI = ecoAncestorsI;
	}


	/*public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}*/
}
