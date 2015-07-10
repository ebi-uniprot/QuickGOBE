package uk.ac.ebi.quickgo.solr.model.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Class to represent Solr Term objects
 * 
 * @author cbonill
 * 
 */
public class SolrTerm {

	// Basic information
	@Field
	String docType;
	@Field
	String id;
	@Field
	String name;
	@Field
	String ontology;
	@Field
	String category;
	@Field
	boolean isObsolete;
	@Field("definition")
	List<String> definitions;
	@Field("definitionXref")
	List<String> definitionXref;
	@Field("comment")
	List<String> comments;
	@Field("secondaryId")
	List<String> secondaryIds;
	
	@Field
	String version;	
	@Field
	String usage;
	@Field("credit")
	List<String> credits;
	
	// Relations
	@Field
	String child;
	@Field
	String parent;
	@Field
	String relationType;

	// Synonyms
	@Field
	String synonymName;
	@Field
	String synonymType;

	// Taxon Constraints
	@Field
	String taxonConstraintRuleId;
	@Field
	String taxonConstraintAncestorId;
	@Field
	String taxonConstraintName;
	@Field
	String taxonConstraintRelationship;
	@Field
	String taxonConstraintTaxIdType;
	@Field
	String taxonConstraintTaxId;
	@Field
	String taxonConstraintTaxName;
	@Field("pubMedId")
	List<String> pubMedIds;

	// Cross References
	@Field
	String xrefDbCode;
	@Field
	String xrefDbId;
	@Field
	String xrefName;

	// Replaces
	@Field
	String obsoleteId;
	@Field
	String reason;

	// Annotation Guidelines
	@Field
	String annotationGuidelineTitle;
	@Field
	String annotationGuidelineUrl;

	// Cross-ontology relations
	@Field
	String crossOntologyRelation;
	@Field
	String crossOntologyOtherNamespace;
	@Field
	String crossOntologyForeignId;
	@Field
	String crossOntologyForeignTerm;
	@Field
	String crossOntologyUrl;
	
	// Change log
	@Field
	String historyName;
	@Field
	Date historyTimeStamp;
	@Field
	String historyAction;
	@Field
	String historyCategory;
	@Field
	String historyText;

	// OBO Fields
	@Field("subset")
	List<String> subsets;
 	
	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isObsolete() {
		return isObsolete;
	}

	public void setObsolete(boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	public List<String> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<String> definitions) {
		this.definitions = definitions;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public List<String> getSecondaryIds() {
		return secondaryIds;
	}

	public void setSecondaryIds(List<String> secondaryIds) {
		this.secondaryIds = secondaryIds;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getChild() {
		return child;
	}

	public void setChild(String child) {
		this.child = child;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getSynonymName() {
		return synonymName;
	}

	public void setSynonymName(String synonymName) {
		this.synonymName = synonymName;
	}

	public String getSynonymType() {
		return synonymType;
	}

	public void setSynonymType(String synonymType) {
		this.synonymType = synonymType;
	}

	public String getTaxonConstraintRuleId() {
		return taxonConstraintRuleId;
	}	

	public String getTaxonConstraintAncestorId() {
		return taxonConstraintAncestorId;
	}

	public void setTaxonConstraintAncestorId(String taxonConstraintAncestorId) {
		this.taxonConstraintAncestorId = taxonConstraintAncestorId;
	}

	public void setTaxonConstraintRuleId(String taxonConstraintRuleId) {
		this.taxonConstraintRuleId = taxonConstraintRuleId;
	}

	public String getTaxonConstraintName() {
		return taxonConstraintName;
	}

	public void setTaxonConstraintName(String taxonConstraintName) {
		this.taxonConstraintName = taxonConstraintName;
	}

	public String getTaxonConstraintRelationship() {
		return taxonConstraintRelationship;
	}

	public void setTaxonConstraintRelationship(
			String taxonConstraintRelationship) {
		this.taxonConstraintRelationship = taxonConstraintRelationship;
	}

	public String getTaxonConstraintTaxIdType() {
		return taxonConstraintTaxIdType;
	}

	public void setTaxonConstraintTaxIdType(String taxonConstraintTaxIdType) {
		this.taxonConstraintTaxIdType = taxonConstraintTaxIdType;
	}

	public String getTaxonConstraintTaxId() {
		return taxonConstraintTaxId;
	}

	public void setTaxonConstraintTaxId(String taxonConstraintTaxId) {
		this.taxonConstraintTaxId = taxonConstraintTaxId;
	}

	public String getTaxonConstraintTaxName() {
		return taxonConstraintTaxName;
	}

	public void setTaxonConstraintTaxName(String taxonConstraintTaxName) {
		this.taxonConstraintTaxName = taxonConstraintTaxName;
	}

	public String getXrefDbCode() {
		return xrefDbCode;
	}

	public void setXrefDbCode(String xrefDbCode) {
		this.xrefDbCode = xrefDbCode;
	}

	public String getXrefDbId() {
		return xrefDbId;
	}

	public void setXrefDbId(String xrefDbId) {
		this.xrefDbId = xrefDbId;
	}	
	
	public String getXrefName() {
		return xrefName;
	}

	public void setXrefName(String xrefName) {
		this.xrefName = xrefName;
	}

	public String getObsoleteId() {
		return obsoleteId;
	}

	public void setObsoleteId(String obsoleteId) {
		this.obsoleteId = obsoleteId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAnnotationGuidelineTitle() {
		return annotationGuidelineTitle;
	}

	public void setAnnotationGuidelineTitle(String annotationGuidelineTitle) {
		this.annotationGuidelineTitle = annotationGuidelineTitle;
	}

	public String getAnnotationGuidelineUrl() {
		return annotationGuidelineUrl;
	}

	public void setAnnotationGuidelineUrl(String annotationGuidelineUrl) {
		this.annotationGuidelineUrl = annotationGuidelineUrl;
	}

	public String getHistoryName() {
		return historyName;
	}

	public void setHistoryName(String historyName) {
		this.historyName = historyName;
	}

	public Date getHistoryTimeStamp() {
		return historyTimeStamp;
	}

	public void setHistoryTimeStamp(Date historyTimeStamp) {
		this.historyTimeStamp = historyTimeStamp;
	}

	public String getHistoryAction() {
		return historyAction;
	}

	public void setHistoryAction(String historyAction) {
		this.historyAction = historyAction;
	}

	public String getHistoryCategory() {
		return historyCategory;
	}

	public void setHistoryCategory(String historyCategory) {
		this.historyCategory = historyCategory;
	}

	public String getHistoryText() {
		return historyText;
	}

	public void setHistoryText(String historyText) {
		this.historyText = historyText;
	}	
	
	public List<String> getPubMedIds() {
		return pubMedIds;
	}

	public void setPubMedIds(List<String> pubMedIds) {
		this.pubMedIds = pubMedIds;
	}	

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public List<String> getSubsets() {
		return subsets;
	}

	public void setSubsets(List<String> subsets) {
		this.subsets = subsets;
	}

	public List<String> getDefinitionXref() {
		return definitionXref;
	}

	public void setDefinitionXref(List<String> definitionXref) {
		this.definitionXref = definitionXref;
	}

	public List<String> getCredits() {
		return credits;
	}

	public void setCredits(List<String> credits) {
		this.credits = credits;
	}	

	public String getCrossOntologyRelation() {
		return crossOntologyRelation;
	}

	public void setCrossOntologyRelation(String crossOntologyRelation) {
		this.crossOntologyRelation = crossOntologyRelation;
	}

	public String getCrossOntologyOtherNamespace() {
		return crossOntologyOtherNamespace;
	}

	public void setCrossOntologyOtherNamespace(String crossOntologyOtherNamespace) {
		this.crossOntologyOtherNamespace = crossOntologyOtherNamespace;
	}

	public String getCrossOntologyForeignId() {
		return crossOntologyForeignId;
	}

	public void setCrossOntologyForeignId(String crossOntologyForeignId) {
		this.crossOntologyForeignId = crossOntologyForeignId;
	}

	public String getCrossOntologyForeignTerm() {
		return crossOntologyForeignTerm;
	}

	public void setCrossOntologyForeignTerm(String crossOntologyForeignTerm) {
		this.crossOntologyForeignTerm = crossOntologyForeignTerm;
	}	

	public String getCrossOntologyUrl() {
		return crossOntologyUrl;
	}

	public void setCrossOntologyUrl(String crossOntologyUrl) {
		this.crossOntologyUrl = crossOntologyUrl;
	}

	/**
	 * Terms documents types
	 * @author cbonill
	 *
	 */
	public enum SolrTermDocumentType implements SolrDocumentType {
		TERM("term"),
		SYNONYM("synonym"),
		RELATION("relation"),
		CONSTRAINT("constraint"),
		HISTORY("history"),		
		XREF("xref"),
		GUIDELINE("guideline"),
		REPLACE("replace"),
		ONTOLOGY("ontology"),
		ONTOLOGYRELATION("ontologyrelation");

		String value;
		
		SolrTermDocumentType(String value){
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
		 * @return Enum values as SolrDocumentType objects
		 */
		public static List<SolrDocumentType> getAsInterfaces() {
			List<SolrDocumentType> documentTypes = new ArrayList<>();
			documentTypes.addAll(Arrays.asList(values()));
			return documentTypes;
		}
	}
}