package uk.ac.ebi.quickgo.ff.files.ontology;

import uk.ac.ebi.quickgo.ff.files.SourceFiles;

import java.io.File;

/**
 * class that defines the set of GO-specific source files
 *
 * @author tonys
 *
 */
public class GOSourceFiles extends OntologySourceFiles {
	public enum EGOTerm { GO_ID, CATEGORY, NAME, IS_OBSOLETE }
	public SourceFiles.TSVDataFile<EGOTerm> goTerms;

	public enum ETaxonUnion { UNION_ID, NAME, TAXA }
	public SourceFiles.TSVDataFile<ETaxonUnion> taxonUnions;

	public enum ETaxonConstraint { RULE_ID, GO_ID, NAME, RELATIONSHIP, TAX_ID_TYPE, TAX_ID, TAXON_NAME, SOURCES }
	public SourceFiles.TSVDataFile<ETaxonConstraint> taxonConstraints;

	public enum ETermTaxonConstraint { GO_ID, RULE_ID }
	public SourceFiles.TSVDataFile<ETermTaxonConstraint> termTaxonConstraints;

	public enum EAnnotationGuidelineInfo { GO_ID, TITLE, URL }
	public SourceFiles.TSVDataFile<EAnnotationGuidelineInfo> annotationGuidelines;

	public enum EPlannedGOChangeInfo { GO_ID, TITLE, URL }
	public SourceFiles.TSVDataFile<EPlannedGOChangeInfo> plannedGOChanges;

	public enum EAnnExtRelation { RELATION, USAGE, DOMAIN }
	public SourceFiles.TSVDataFile<EAnnExtRelation> annExtRelations;

	public enum EAnnExtRelRelation { CHILD, PARENT, RELATION_TYPE }
	public SourceFiles.TSVDataFile<EAnnExtRelRelation> aerRelations;

	public enum EAnnExtRelSecondary { RELATION, SECONDARY_ID }
	public SourceFiles.TSVDataFile<EAnnExtRelSecondary> aerSecondaries;

	public enum EAnnExtRelSubset { RELATION, SUBSET }
	public SourceFiles.TSVDataFile<EAnnExtRelSubset> aerSubsets;

	public enum EAnnExtRelDomain { RELATION, ENTITY, ENTITY_TYPE }
	public SourceFiles.TSVDataFile<EAnnExtRelDomain> aerDomains;

	public enum EAnnExtRelRange { RELATION, ENTITY, ENTITY_TYPE }
	public SourceFiles.TSVDataFile<EAnnExtRelRange> aerRanges;

	public enum EAnnExtRelRangeDefault { NAMESPACE, ID_SYNTAX, ENTITY_TYPE }
	public SourceFiles.TSVDataFile<EAnnExtRelRangeDefault> aerRangeDefaults;

	public enum EAnnExtRelEntitySyntax { ENTITY, ENTITY_TYPE, NAMESPACE, ID_SYNTAX }
	public SourceFiles.TSVDataFile<EAnnExtRelEntitySyntax> aerEntitySyntax;

	public enum EAnnBlacklistEntry { GO_ID, CATEGORY, ENTITY_TYPE, ENTITY_ID, TAXON_ID, ENTITY_NAME, ANCESTOR_GO_ID, REASON,  METHOD_ID }
	public SourceFiles.TSVDataFile<EAnnBlacklistEntry> blacklistForGoTerm;

	public GOSourceFiles(File directory) {
		super(directory);

		goTerms = new SourceFiles.TSVDataFile<>(directory, "TERMS");
		definitions = new SourceFiles.TSVDataFile<>(directory, "DEFINITIONS");
		synonyms = new SourceFiles.TSVDataFile<>(directory, "SYNONYMS");
		comments = new SourceFiles.TSVDataFile<>(directory, "COMMENTS");
		relations = new SourceFiles.TSVDataFile<>(directory, "RELATIONS");
		xrefs = new SourceFiles.TSVDataFile<>(directory, "GO_XREFS");
		definitionXrefs = new SourceFiles.TSVDataFile<>(directory, "DEFINITION_XREFS");
		crossOntologyRelations = new SourceFiles.TSVDataFile<>(directory, "CROSS_ONTOLOGY_RELATIONS");
		subsets = new SourceFiles.TSVDataFile<>(directory, "SUBSETS");
		history = new SourceFiles.TSVDataFile<>(directory, "TERM_HISTORY");
		credits = new SourceFiles.TSVDataFile<>(directory, "TERM_CREDITS");
		fundingBodies = new SourceFiles.TSVDataFile<>(directory, "FUNDING_BODIES");
		taxonUnions = new SourceFiles.TSVDataFile<>(directory, "TAXON_UNIONS");
		taxonConstraints = new SourceFiles.TSVDataFile<>(directory, "TAXON_CONSTRAINTS");
		termTaxonConstraints = new SourceFiles.TSVDataFile<>(directory, "TERM_TAXON_CONSTRAINTS");
		annotationGuidelines = new SourceFiles.TSVDataFile<>(directory, "ANNOTATION_GUIDELINES");
		plannedGOChanges = new SourceFiles.TSVDataFile<>(directory, "PLANNED_GO_CHANGES");
		annExtRelations = new SourceFiles.TSVDataFile<>(directory, "ANNOTATION_EXTENSION_RELATIONS");
		aerRelations = new SourceFiles.TSVDataFile<>(directory, "AER_RELATIONS");
		aerSecondaries = new SourceFiles.TSVDataFile<>(directory, "AER_SECONDARIES");
		aerSubsets = new SourceFiles.TSVDataFile<>(directory, "AER_SUBSETS");
		aerDomains = new SourceFiles.TSVDataFile<>(directory, "AER_DOMAINS");
		aerRanges = new SourceFiles.TSVDataFile<>(directory, "AER_RANGES");
		aerRangeDefaults = new SourceFiles.TSVDataFile<>(directory, "AER_RANGE_DEFAULTS");
		aerEntitySyntax = new SourceFiles.TSVDataFile<>(directory, "AER_ENTITY_SYNTAX");
		blacklistForGoTerm = new SourceFiles.TSVDataFile<>(directory, "TERM_BLACKLIST_ENTRIES");

	}

	@Override
	public SourceFiles.NamedFile[] requiredFiles() {
		return SourceFiles.holder(
				goTerms, definitions, synonyms, comments, relations, xrefs, definitionXrefs, subsets, history, credits,
				fundingBodies, taxonUnions, taxonConstraints, termTaxonConstraints,
				annotationGuidelines, plannedGOChanges,
				annExtRelations, aerRelations, aerSecondaries, aerSubsets, aerDomains, aerRanges, aerRangeDefaults, aerEntitySyntax
			);
	}
}
