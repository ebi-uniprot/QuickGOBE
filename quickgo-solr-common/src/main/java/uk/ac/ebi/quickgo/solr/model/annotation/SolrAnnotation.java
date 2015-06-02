package uk.ac.ebi.quickgo.solr.model.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Class to represent Solr annotations
 *
 * @author cbonill
 *
 */
public class SolrAnnotation {

	@Field
	private String id;
	@Field
	private String goEvidence;
	@Field
	private String docType;
	@Field
	private String db;
	@Field
	private String dbObjectID;
	@Field
	private String dbObjectSymbol;
	@Field
	private String dbObjectName;
	@Field
	private String dbObjectType;
	@Field
	private List<String> dbObjectSynonyms;
	@Field
	private String targetSet;
	@Field("qualifier")
	private String qualifier;
	@Field
	private String goID;
	@Field
	private String goAspect;
	@Field
	private String ecoID;
	@Field
	private String termName;
	@Field
	private String interactingTaxID;
	@Field
	private String date;
	@Field
	private String assignedBy;
	@Field("extension")
	private List<String> extensions;
	@Field("fullExtension")
	private String fullExtension;
	@Field
	private String properties;
	@Field("dbXref")
	private String dbXref;
	@Field
	private List<String> with;
	@Field
	private String fullWith;

	// Ancestors
	@Field
	private List<String> ancestorsI;
	@Field
	private List<String> ancestorsIPO;
	@Field
	private List<String> ancestorsIPOR;

	// ECO Ancestors
	@Field
	private List<String> ecoAncestorsI;

	// Extra fields
	@Field
	private int taxonomyId;
	@Field
	private String taxonomyName;
	@Field("taxonomyClosure")
	private List<Integer> taxonomyClosures;
	@Field
	private int sequenceLength;
	@Field("gp2protein")
	private List<String> gp2proteinList;
	@Field("subSet")
	private List<String> subSets;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoEvidence() {
		return goEvidence;
	}

	public void setGoEvidence(String goEvidence) {
		this.goEvidence = goEvidence;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
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

	public String getDbObjectType() {
		return dbObjectType;
	}

	public void setDbObjectType(String dbObjectType) {
		this.dbObjectType = dbObjectType;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
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

	public String getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(String assignedBy) {
		this.assignedBy = assignedBy;
	}

	public List<String> getExtensions() {
		return extensions;
	}

	public String getFullExtension() {
		return fullExtension;
	}


	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getTargetSet() {
		return targetSet;
	}

	public void setTargetSet(String targetSet) {
		this.targetSet = targetSet;
	}

	public String getGoAspect() {
		return goAspect;
	}

	public void setGoAspect(String goAspect) {
		this.goAspect = goAspect;
	}

	public String getDbXref() {
		return dbXref;
	}

	public void setDbXref(String dbXref) {
		this.dbXref = dbXref;
	}

	public List<String> getWith() {
		return with;
	}

	public void setWith(List<String> with) {
		this.with = with;
	}

	public String getFullWith(){return fullWith;}

	public void setFullWith(String fullWith){
		this.fullWith = fullWith;
	}

	public int getTaxonomyId() {
		return taxonomyId;
	}

	public void setTaxonomyId(int taxonomyId) {
		this.taxonomyId = taxonomyId;
	}

	public List<Integer> getTaxonomyClosures() {
		return taxonomyClosures;
	}

	public void setTaxonomyClosures(List<Integer> taxonomyClosures) {
		this.taxonomyClosures = taxonomyClosures;
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

	public int getSequenceLength() {
		return sequenceLength;
	}

	public void setSequenceLength(int sequenceLength) {
		this.sequenceLength = sequenceLength;
	}

	public List<String> getGp2proteinList() {
		return gp2proteinList;
	}

	public void setGp2proteinList(List<String> gp2proteinList) {
		this.gp2proteinList = gp2proteinList;
	}

	public List<String> getSubSets() {
		return subSets;
	}

	public void setSubSets(List<String> subSets) {
		this.subSets = subSets;
	}

	public List<String> getEcoAncestorsI() {
		return ecoAncestorsI;
	}

	public void setEcoAncestorsI(List<String> ecoAncestorsI) {
		this.ecoAncestorsI = ecoAncestorsI;
	}

	public void setFullExtension(String fullExtension) {
		this.fullExtension = fullExtension;
	}


	public enum SolrAnnotationDocumentType implements SolrDocumentType {

		ANNOTATION("annotation");

		String value;

		private SolrAnnotationDocumentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Get values as SolrDocumentType objects
		 *
		 * @return Enum values as SolrDocumentType objects
		 */
		public static List<SolrDocumentType> getAsInterfaces() {
			List<SolrDocumentType> documentTypes = new ArrayList<SolrDocumentType>();
			for (SolrDocumentType solrDocumentType : values()) {
				documentTypes.add(solrDocumentType);
			}
			return documentTypes;
		}

		/**
		 * Get value as SolrDocumentType object
		 *
		 * @param solrAnnotationDocumentType
		 *            Value to convert
		 * @return Value as SolrDocumentType object
		 */
		public static SolrDocumentType getAsInterface(
				SolrAnnotationDocumentType solrAnnotationDocumentType) {
			SolrDocumentType documentType = solrAnnotationDocumentType;
			return documentType;
		}
	}
}
