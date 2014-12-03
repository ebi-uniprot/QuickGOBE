package uk.ac.ebi.quickgo.miscellaneous;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * To represent the information that doesn't fit in gene products, annotations
 * or terms
 * 
 * @author cbonill
 * 
 */
public class Miscellaneous implements Serializable{
	
	private static final long serialVersionUID = -1002395703333033189L;
	
	// Taxonomies
	private int taxonomyId;
	private String taxonomyName;
	private List<Integer> taxonomyClosure;

	// Co-Occurrence stats	
	private String term;
	private String comparedTerm;
	private float together;
	private float compared;
	private float selected;
	private float all;
	private String statsType;//Non-IEA or All
	
	// Sequences
	private String dbObjectID;
	private String sequence;
	
	// Publications
	private int publicationID;	
	private String publicationTitle;
	
	// Annotation Guidelines	
	private String guidelineTitle;	
	private String guidelineURL;
	
	// Annotation Blacklist	
	private String backlistReason;	
	private String blacklistMethodID;	
	private String backlistCategory;	
	private String backlistEntryType;	
	
	// Annotation Extension Relations
	private String aerName;
	private String aerUsage;
	private String aerDomain;
	
	private List<String> aerParents = new ArrayList<>();
	private String aerRange;
	private List<String> aerSecondaries = new ArrayList<>();
	private List<String> aerSubsets = new ArrayList<>();

	// Xrefs Databases
	private String xrefAbbreviation;
	private String xrefDatabase;
	private String xrefGenericURL;
	private String xrefUrlSyntax;
	
	// Subsets count
	private String subset;
	private int subsetCount;
	
	//Evidence
	private String evidenceCode;
	private String evidenceName;
	
	// Post processing rule	
	private String pprRuleId;	
	private String pprAncestorGoId;	
	private String pprAncestorTerm;	
	private String pprRelationship;	
	private String pprTaxonName;	
	private String pprOriginalGoId;	
	private String pprOriginalTerm;	
	private String pprCleanupAction;	
	private String pprAffectedTaxGroup;	
	private String pprSubstitutedGoId;	
	private String pprSubstitutedTerm;	
	private String pprCuratorNotes;
	
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

	public List<Integer> getTaxonomyClosure() {
		return taxonomyClosure;
	}

	public void setTaxonomyClosure(List<Integer> taxonomyClosure) {
		this.taxonomyClosure = taxonomyClosure;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}