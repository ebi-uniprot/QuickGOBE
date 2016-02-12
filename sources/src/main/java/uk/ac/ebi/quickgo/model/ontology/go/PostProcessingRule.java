package uk.ac.ebi.quickgo.model.ontology.go;

public class PostProcessingRule {
	public String ruleId;
	public String ancestorGoId;
	public String ancestorTerm;
	public String relationship;
	public String taxonName;
	public String originalGoId;
	public String originalTerm;
	public String cleanupAction;
	public String affectedTaxGroup;
	public String substitutedGoId;
	public String substitutedTerm;
	public String curatorNotes;

	public PostProcessingRule(String ruleId, String ancestorGoId, String ancestorTerm, String relationship, String taxonName, String originalGoId, String originalTerm, String cleanupAction, String affectedTaxGroup, String substitutedGoId, String substitutedTerm, String curatorNotes) {
		this.ruleId = ruleId;
		this.ancestorGoId = ancestorGoId;
		this.ancestorTerm = ancestorTerm;
		this.relationship = relationship;
		this.taxonName = taxonName;
		this.originalGoId = originalGoId;
		this.originalTerm = originalTerm;
		this.cleanupAction = cleanupAction;
		this.affectedTaxGroup = affectedTaxGroup;
		this.substitutedGoId = substitutedGoId;
		this.substitutedTerm = substitutedTerm;
		this.curatorNotes = curatorNotes;
	}

	public boolean isTransform() {
		return "TRANSFORM".equalsIgnoreCase(cleanupAction);
	}

	public boolean isDelete() {
		return "DELETE".equalsIgnoreCase(cleanupAction);
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getAncestorGoId() {
		return ancestorGoId;
	}

	public void setAncestorGoId(String ancestorGoId) {
		this.ancestorGoId = ancestorGoId;
	}

	public String getAncestorTerm() {
		return ancestorTerm;
	}

	public void setAncestorTerm(String ancestorTerm) {
		this.ancestorTerm = ancestorTerm;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getTaxonName() {
		return taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	public String getOriginalGoId() {
		return originalGoId;
	}

	public void setOriginalGoId(String originalGoId) {
		this.originalGoId = originalGoId;
	}

	public String getOriginalTerm() {
		return originalTerm;
	}

	public void setOriginalTerm(String originalTerm) {
		this.originalTerm = originalTerm;
	}

	public String getCleanupAction() {
		return cleanupAction;
	}

	public void setCleanupAction(String cleanupAction) {
		this.cleanupAction = cleanupAction;
	}

	public String getAffectedTaxGroup() {
		return affectedTaxGroup;
	}

	public void setAffectedTaxGroup(String affectedTaxGroup) {
		this.affectedTaxGroup = affectedTaxGroup;
	}

	public String getSubstitutedGoId() {
		return substitutedGoId;
	}

	public void setSubstitutedGoId(String substitutedGoId) {
		this.substitutedGoId = substitutedGoId;
	}

	public String getSubstitutedTerm() {
		return substitutedTerm;
	}

	public void setSubstitutedTerm(String substitutedTerm) {
		this.substitutedTerm = substitutedTerm;
	}

	public String getCuratorNotes() {
		return curatorNotes;
	}

	public void setCuratorNotes(String curatorNotes) {
		this.curatorNotes = curatorNotes;
	}	
}
