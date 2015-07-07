package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.util.NamedXRef;
import uk.ac.ebi.quickgo.util.XRef;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @Author Tony Wardell
 * Date: 03/02/2015
 * Time: 18:03
 * Created with IntelliJ IDEA.
 */
public class TermJson {
	private String termId = "";
	private String name = "";
	private boolean active;
	private boolean isGoTerm;
	private List<XRef> definitionXrefs;
	private String definition = "";
	private String aspectDescription = "";
	private GOTerm.ETermUsage usage;
	private String comment = "";
	private List<XRef> altIds;
	private String altIdsString;
	private List<TermCredit> credits;
	private List<Synonym> synonyms;
	private List<TaxonConstraint> taxonConstraints;
	private List<GOTerm.NamedURL> guidelines;
	private List<CrossOntologyRelation> crossOntologyRelations;
	private List<GenericTermSet> subsets;
	private List<NamedXRef> xrefs;
	private List<TermRelation> replaces;
	private List<TermRelation> replacements;
	private List<ChildTermRelationJson> childTermsRelations;
	private List<COOccurrenceStatsTerm> allCoOccurrenceStatsTerms;
	private List<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics;
	private TermOntologyHistory history;

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTermId() {
		return termId;
	}

	public String getName() {
		return name;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setIsGoTerm(boolean isGoTerm) {
		this.isGoTerm = isGoTerm;
	}

	public boolean isGoTerm() {
		return isGoTerm;
	}

	public void setDefinitionXrefs(List<XRef> tDefinitionXrefs) {
		this.definitionXrefs = tDefinitionXrefs;
	}

	public void setgetDefinition(String definition) {
		this.definition = definition;
	}

	public void setAspectDescription(String aspectDescription) {
		this.aspectDescription = aspectDescription;
	}

	public void setUsage(GOTerm.ETermUsage usage) {
		this.usage = usage;
	}

	public List<XRef> getDefinitionXrefs() {
		return definitionXrefs;
	}

	public String getDefinition() {
		return definition;
	}

	public String getAspectDescription() {
		return aspectDescription;
	}

	public GOTerm.ETermUsage getUsage() {
		return usage;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setAltIds(List<XRef> altIds) {
		this.altIds = altIds;
	}

	public List<XRef> getAltIds() {
		return altIds;
	}

	public void setAltIdsString(String altIdsString) {
		this.altIdsString = altIdsString;
	}

	public String getAltIdsString() {
		return altIdsString;
	}


	public void setCredits(List<TermCredit> credits) {
		this.credits = credits;
	}

	public List<TermCredit> getCredits() {
		return credits;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setTaxonConstraints(List<TaxonConstraint> taxonConstraints) {
		this.taxonConstraints = taxonConstraints;
	}

	public List<TaxonConstraint> getTaxonConstraints() {
		return taxonConstraints;
	}

	public void setGuidelines(List<GOTerm.NamedURL> guidelines) {
		this.guidelines = guidelines;
	}

	public List<GOTerm.NamedURL> getGuidelines() {
		return guidelines;
	}

	public void setCrossOntologyRelations(List<CrossOntologyRelation> crossOntologyRelations) {
		this.crossOntologyRelations = crossOntologyRelations;
	}

	public List<CrossOntologyRelation> getCrossOntologyRelations() {
		return crossOntologyRelations;
	}

	public void setSubsets(List<GenericTermSet> subsets) {
		this.subsets = subsets;
	}

	public List<GenericTermSet> getSubsets() {
		return subsets;
	}

	public void setXrefs(List<NamedXRef> xrefs) {
		this.xrefs = xrefs;
	}

	public List<NamedXRef> getXrefs() {
		return xrefs;
	}

	public void setReplaces(List<TermRelation> replaces) {
		this.replaces = replaces;
	}

	public List<TermRelation> getReplaces() {
		return replaces;
	}

	public void setReplacements(List<TermRelation> replacements) {
		this.replacements = replacements;
	}

	public List<TermRelation> getReplacements() {
		return replacements;
	}

	public void setChildTermsRelations(List<ChildTermRelationJson> childTermsRelations) {
		this.childTermsRelations = childTermsRelations;
	}

	public List<ChildTermRelationJson> getChildTermsRelations() {
		return childTermsRelations;
	}

	public void setAllCoOccurrenceStatsTerms(List<COOccurrenceStatsTerm> allCoOccurrenceStatsTerms) {
		this.allCoOccurrenceStatsTerms = allCoOccurrenceStatsTerms;
	}

	public List<COOccurrenceStatsTerm> getAllCoOccurrenceStatsTerms() {
		return allCoOccurrenceStatsTerms;
	}

	public void setNonIEACOOccurrenceStatistics(List<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics) {
		this.nonIEACOOccurrenceStatistics = nonIEACOOccurrenceStatistics;
	}

	public List<COOccurrenceStatsTerm> getNonIEACOOccurrenceStatistics() {
		return nonIEACOOccurrenceStatistics;
	}

	public void setHistory(TermOntologyHistory history) {
		this.history = history;
	}

	public TermOntologyHistory getHistory() {
		return history;
	}
}
