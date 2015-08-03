package uk.ac.ebi.quickgo.data;

import java.io.File;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.data.SourceFiles.TSVDataFile;


/**
 * class that defines the set of GO-specific source files
 * 
 * @author tonys
 *
 */
public class GOSourceFiles extends OntologySourceFiles {
	public enum EGOTerm { GO_ID, CATEGORY, NAME, IS_OBSOLETE }
	public TSVDataFile<EGOTerm> goTerms;

	public enum EProteinComplex { GO_ID, DB, DB_OBJECT_ID, DB_OBJECT_SYMBOL, DB_OBJECT_NAME }
	public TSVDataFile<EProteinComplex> proteinComplexes;

	public enum ETaxonUnion { UNION_ID, NAME, TAXA }
	public TSVDataFile<ETaxonUnion> taxonUnions;

	public enum ETaxonConstraint { RULE_ID, GO_ID, NAME, RELATIONSHIP, TAX_ID_TYPE, TAX_ID, TAXON_NAME, SOURCES }
	public TSVDataFile<ETaxonConstraint> taxonConstraints;

	public enum ETermTaxonConstraint { GO_ID, RULE_ID }
	public TSVDataFile<ETermTaxonConstraint> termTaxonConstraints;

	public enum EAnnotationGuidelineInfo { GO_ID, TITLE, URL }
	public TSVDataFile<EAnnotationGuidelineInfo> annotationGuidelines;

	public enum EPlannedGOChangeInfo { GO_ID, TITLE, URL }
	public TSVDataFile<EPlannedGOChangeInfo> plannedGOChanges;

	public enum EAnnExtRelation { RELATION, USAGE, DOMAIN }
	public TSVDataFile<EAnnExtRelation> annExtRelations;

	public enum EAnnExtRelRelation { CHILD, PARENT, RELATION_TYPE }
	public TSVDataFile<EAnnExtRelRelation> aerRelations;

	public enum EAnnExtRelSecondary { RELATION, SECONDARY_ID }
	public TSVDataFile<EAnnExtRelSecondary> aerSecondaries;

	public enum EAnnExtRelSubset { RELATION, SUBSET }
	public TSVDataFile<EAnnExtRelSubset> aerSubsets;

	public enum EAnnExtRelDomain { RELATION, ENTITY, ENTITY_TYPE }
	public TSVDataFile<EAnnExtRelDomain> aerDomains;

	public enum EAnnExtRelRange { RELATION, ENTITY, ENTITY_TYPE }
	public TSVDataFile<EAnnExtRelRange> aerRanges;

	public enum EAnnExtRelRangeDefault { NAMESPACE, ID_SYNTAX, ENTITY_TYPE }
	public TSVDataFile<EAnnExtRelRangeDefault> aerRangeDefaults;

	public enum EAnnExtRelEntitySyntax { ENTITY, ENTITY_TYPE, NAMESPACE, ID_SYNTAX }
	public TSVDataFile<EAnnExtRelEntitySyntax> aerEntitySyntax;
	
	public GOSourceFiles(File directory) {
		super(directory);
		
		goTerms = new TSVDataFile<>(directory, "TERMS");
		definitions = new TSVDataFile<>(directory, "DEFINITIONS");
		synonyms = new TSVDataFile<>(directory, "SYNONYMS");
		comments = new TSVDataFile<>(directory, "COMMENTS");
		relations = new TSVDataFile<>(directory, "RELATIONS");
		xrefs = new TSVDataFile<>(directory, "GO_XREFS");
		definitionXrefs = new TSVDataFile<>(directory, "DEFINITION_XREFS");
		crossOntologyRelations = new TSVDataFile<>(directory, "CROSS_ONTOLOGY_RELATIONS");
		subsets = new TSVDataFile<>(directory, "SUBSETS");
		history = new TSVDataFile<>(directory, "TERM_HISTORY");
		credits = new TSVDataFile<>(directory, "TERM_CREDITS");
		fundingBodies = new TSVDataFile<>(directory, "FUNDING_BODIES");
		proteinComplexes = new TSVDataFile<>(directory, "PROTEIN_COMPLEXES");
		taxonUnions = new TSVDataFile<>(directory, "TAXON_UNIONS");
		taxonConstraints = new TSVDataFile<>(directory, "TAXON_CONSTRAINTS");
		termTaxonConstraints = new TSVDataFile<>(directory, "TERM_TAXON_CONSTRAINTS");
		annotationGuidelines = new TSVDataFile<>(directory, "ANNOTATION_GUIDELINES");
		plannedGOChanges = new TSVDataFile<>(directory, "PLANNED_GO_CHANGES");
		annExtRelations = new TSVDataFile<>(directory, "ANNOTATION_EXTENSION_RELATIONS");
		aerRelations = new TSVDataFile<>(directory, "AER_RELATIONS");
		aerSecondaries = new TSVDataFile<>(directory, "AER_SECONDARIES");
		aerSubsets = new TSVDataFile<>(directory, "AER_SUBSETS");
		aerDomains = new TSVDataFile<>(directory, "AER_DOMAINS");
		aerRanges = new TSVDataFile<>(directory, "AER_RANGES");
		aerRangeDefaults = new TSVDataFile<>(directory, "AER_RANGE_DEFAULTS");
		aerEntitySyntax = new TSVDataFile<>(directory, "AER_ENTITY_SYNTAX");
	}
	
	@Override
	public NamedFile[] requiredFiles() {
		return SourceFiles.holder(
				goTerms, definitions, synonyms, comments, relations, xrefs, definitionXrefs, subsets, history, credits,
				fundingBodies, proteinComplexes, taxonUnions, taxonConstraints, termTaxonConstraints,
				annotationGuidelines, plannedGOChanges,
				annExtRelations, aerRelations, aerSecondaries, aerSubsets, aerDomains, aerRanges, aerRangeDefaults, aerEntitySyntax
			);
	}
}
