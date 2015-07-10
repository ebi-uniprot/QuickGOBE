package uk.ac.ebi.quickgo.solr.model.miscellaneous;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Solr representation of miscellaneous data
 * 
 * @author cbonill
 * 
 */
public class SolrMiscellaneous {

	@Field
	String docType;
	// Taxonomies
	@Field
	public int taxonomyId;
	@Field
	public String taxonomyName;
	@Field("taxonomyClosure")
	public List<Integer> taxonomyClosures;

	// Co-Occurrence statistics
	@Field
	private String term;
	@Field
	private String comparedTerm;
	@Field
	private float together;
	@Field
	private float compared;
	@Field
	private float selected;
	@Field
	private float all;
	@Field
	private String statsType;//Non-IEA or All
	
	// Sequences
	@Field
	private String dbObjectID;
	@Field
	private String sequence;
	
	// Publications
	@Field
	private int publicationID;
	@Field
	private String publicationTitle;
	
	// Annotation guideline
	@Field
	private String guidelineTitle;
	@Field
	private String guidelineURL;
	
	// Annotation blacklist
	@Field
	private String backlistReason;
	@Field
	private String blacklistMethodID;
	@Field
	private String backlistCategory;
	@Field
	private String backlistEntryType;	
	
	// Annotation Extension Relations
	@Field
	private String aerName;
	@Field
	private String aerUsage;
	@Field
	private String aerDomain;

	@Field
	private List<String> aerParents;
	@Field
	private String aerRange;
	@Field
	private List<String> aerSecondaries;
	@Field
	private List<String> aerSubsets;
	
	// Xrefs Databases
	@Field
	private String xrefAbbreviation;
	@Field
	private String xrefDatabase;
	@Field
	private String xrefGenericURL;
	@Field
	private String xrefUrlSyntax;
		
	// Subsets count
	@Field
	private String subset;
	@Field
	private int subsetCount;
	
	// Evidences
	@Field
	private String evidenceCode;
	@Field
	private String evidenceName;
	
	// Post processing rule
	@Field
	private String pprRuleId;
	@Field
	private String pprAncestorGoId;
	@Field
	private String pprAncestorTerm;
	@Field
	private String pprRelationship;
	@Field
	private String pprTaxonName;
	@Field
	private String pprOriginalGoId;
	@Field
	private String pprOriginalTerm;
	@Field
	private String pprCleanupAction;
	@Field
	private String pprAffectedTaxGroup;
	@Field
	private String pprSubstitutedGoId;
	@Field
	private String pprSubstitutedTerm;
	@Field
	private String pprCuratorNotes;
	
	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public int getTaxonomyId() {
		return taxonomyId;
	}

	public void setTaxonomyId(int taxonomyId) {
		this.taxonomyId = taxonomyId;
	}

	public String getTaxonomyName() {
		return taxonomyName;
	}

	public void setTaxonomyName(String taxonomyName) {
		this.taxonomyName = taxonomyName;
	}

	public List<Integer> getTaxonomyClosures() {
		return taxonomyClosures;
	}

	public void setTaxonomyClosures(List<Integer> taxonomyClosures) {
		this.taxonomyClosures = taxonomyClosures;
	}
		
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getComparedTerm() {
		return comparedTerm;
	}

	public void setComparedTerm(String comparedTerm) {
		this.comparedTerm = comparedTerm;
	}

	public float getTogether() {
		return together;
	}

	public void setTogether(float together) {
		this.together = together;
	}

	public float getCompared() {
		return compared;
	}

	public void setCompared(float compared) {
		this.compared = compared;
	}

	public float getSelected() {
		return selected;
	}

	public void setSelected(float selected) {
		this.selected = selected;
	}

	public float getAll() {
		return all;
	}

	public void setAll(float all) {
		this.all = all;
	}

	public String getStatsType() {
		return statsType;
	}

	public void setStatsType(String statsType) {
		this.statsType = statsType;
	}

	public String getDbObjectID() {
		return dbObjectID;
	}

	public void setDbObjectID(String dbObjectID) {
		this.dbObjectID = dbObjectID;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}	
	
	public int getPublicationID() {
		return publicationID;
	}

	public void setPublicationID(int publicationID) {
		this.publicationID = publicationID;
	}

	public String getPublicationTitle() {
		return publicationTitle;
	}

	public void setPublicationTitle(String publicationTitle) {
		this.publicationTitle = publicationTitle;
	}	
	
	public String getGuidelineTitle() {
		return guidelineTitle;
	}

	public void setGuidelineTitle(String guidelineTitle) {
		this.guidelineTitle = guidelineTitle;
	}

	public String getGuidelineURL() {
		return guidelineURL;
	}

	public void setGuidelineURL(String guidelineURL) {
		this.guidelineURL = guidelineURL;
	}	
	
	public String getBacklistReason() {
		return backlistReason;
	}

	public void setBacklistReason(String backlistReason) {
		this.backlistReason = backlistReason;
	}

	public String getBlacklistMethodID() {
		return blacklistMethodID;
	}

	public void setBlacklistMethodID(String blacklistMethodID) {
		this.blacklistMethodID = blacklistMethodID;
	}

	public String getBacklistCategory() {
		return backlistCategory;
	}

	public void setBacklistCategory(String backlistCategory) {
		this.backlistCategory = backlistCategory;
	}

	public String getBacklistEntryType() {
		return backlistEntryType;
	}

	public void setBacklistEntryType(String backlistEntryType) {
		this.backlistEntryType = backlistEntryType;
	}	
	
	public String getAerName() {
		return aerName;
	}

	public void setAerName(String aerName) {
		this.aerName = aerName;
	}

	public String getAerUsage() {
		return aerUsage;
	}

	public void setAerUsage(String aerUsage) {
		this.aerUsage = aerUsage;
	}

	public String getAerDomain() {
		return aerDomain;
	}

	public void setAerDomain(String aerDomain) {
		this.aerDomain = aerDomain;
	}

	public List<String> getAerParents() {
		return aerParents;
	}

	public void setAerParents(List<String> aerParents) {
		this.aerParents = aerParents;
	}

	public List<String> getAerSecondaries() {
		return aerSecondaries;
	}

	public void setAerSecondaries(List<String> aerSecondaries) {
		this.aerSecondaries = aerSecondaries;
	}

	public List<String> getAerSubsets() {
		return aerSubsets;
	}

	public void setAerSubsets(List<String> aerSubsets) {
		this.aerSubsets = aerSubsets;
	}	

	public String getAerRange() {
		return aerRange;
	}

	public void setAerRange(String aerRange) {
		this.aerRange = aerRange;
	}	

	public String getXrefAbbreviation() {
		return xrefAbbreviation;
	}

	public void setXrefAbbreviation(String xrefAbbreviation) {
		this.xrefAbbreviation = xrefAbbreviation;
	}

	public String getXrefDatabase() {
		return xrefDatabase;
	}

	public void setXrefDatabase(String xrefDatabase) {
		this.xrefDatabase = xrefDatabase;
	}

	public String getXrefGenericURL() {
		return xrefGenericURL;
	}

	public void setXrefGenericURL(String xrefGenericURL) {
		this.xrefGenericURL = xrefGenericURL;
	}

	public String getXrefUrlSyntax() {
		return xrefUrlSyntax;
	}

	public void setXrefUrlSyntax(String xrefUrlSyntax) {
		this.xrefUrlSyntax = xrefUrlSyntax;
	}	

	public String getSubset() {
		return subset;
	}

	public void setSubset(String subset) {
		this.subset = subset;
	}

	public int getSubsetCount() {
		return subsetCount;
	}

	public void setSubsetCount(int subsetCount) {
		this.subsetCount = subsetCount;
	}	

	public String getEvidenceCode() {
		return evidenceCode;
	}

	public void setEvidenceCode(String evidenceCode) {
		this.evidenceCode = evidenceCode;
	}

	public String getEvidenceName() {
		return evidenceName;
	}

	public void setEvidenceName(String evidenceName) {
		this.evidenceName = evidenceName;
	}
	
	public String getPprRuleId() {
		return pprRuleId;
	}

	public void setPprRuleId(String pprRuleId) {
		this.pprRuleId = pprRuleId;
	}

	public String getPprAncestorGoId() {
		return pprAncestorGoId;
	}

	public void setPprAncestorGoId(String pprAncestorGoId) {
		this.pprAncestorGoId = pprAncestorGoId;
	}

	public String getPprAncestorTerm() {
		return pprAncestorTerm;
	}

	public void setPprAncestorTerm(String pprAncestorTerm) {
		this.pprAncestorTerm = pprAncestorTerm;
	}

	public String getPprRelationship() {
		return pprRelationship;
	}

	public void setPprRelationship(String pprRelationship) {
		this.pprRelationship = pprRelationship;
	}

	public String getPprTaxonName() {
		return pprTaxonName;
	}

	public void setPprTaxonName(String pprTaxonName) {
		this.pprTaxonName = pprTaxonName;
	}

	public String getPprOriginalGoId() {
		return pprOriginalGoId;
	}

	public void setPprOriginalGoId(String pprOriginalGoId) {
		this.pprOriginalGoId = pprOriginalGoId;
	}

	public String getPprOriginalTerm() {
		return pprOriginalTerm;
	}

	public void setPprOriginalTerm(String pprOriginalTerm) {
		this.pprOriginalTerm = pprOriginalTerm;
	}

	public String getPprCleanupAction() {
		return pprCleanupAction;
	}

	public void setPprCleanupAction(String pprCleanupAction) {
		this.pprCleanupAction = pprCleanupAction;
	}

	public String getPprAffectedTaxGroup() {
		return pprAffectedTaxGroup;
	}

	public void setPprAffectedTaxGroup(String pprAffectedTaxGroup) {
		this.pprAffectedTaxGroup = pprAffectedTaxGroup;
	}

	public String getPprSubstitutedGoId() {
		return pprSubstitutedGoId;
	}

	public void setPprSubstitutedGoId(String pprSubstitutedGoId) {
		this.pprSubstitutedGoId = pprSubstitutedGoId;
	}

	public String getPprSubstitutedTerm() {
		return pprSubstitutedTerm;
	}

	public void setPprSubstitutedTerm(String pprSubstitutedTerm) {
		this.pprSubstitutedTerm = pprSubstitutedTerm;
	}

	public String getPprCuratorNotes() {
		return pprCuratorNotes;
	}

	public void setPprCuratorNotes(String pprCuratorNotes) {
		this.pprCuratorNotes = pprCuratorNotes;
	}

	/**
	 * Gene products documents types
	 * 
	 * @author cbonill
	 * 
	 */
	public enum SolrMiscellaneousDocumentType implements SolrDocumentType {

		TAXONOMY("taxonomy"),
		STATS("stats"),
		SEQUENCE("sequence"),
		PUBLICATION("publication"),
		GUIDELINE("guideline"),
		BLACKLIST("blacklist"),
		EXTENSION("extension"),
		XREFDB("xrefdb"),
		SUBSETCOUNT("subsetcount"),
		EVIDENCE("evidence"),
		POSTPROCESSINGRULE("postprocessingrule");

		String value;

		private SolrMiscellaneousDocumentType(String value) {
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
		 * @param solrMiscellaneousDocumentType
		 *            Value to convert
		 * @return Value as SolrDocumentType object
		 */
		public static SolrDocumentType getAsInterface(SolrMiscellaneousDocumentType solrMiscellaneousDocumentType) {
			return solrMiscellaneousDocumentType;
		}
	}
}