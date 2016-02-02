package uk.ac.ebi.quickgo.ff.files.ontology;

import uk.ac.ebi.quickgo.ff.files.SourceFiles;

import java.io.File;

/**
 * class that defines the set of source files that are common to all (or, at least, most) ontologies
 * 
 * @author tonys
 *
 */
public abstract class OntologySourceFiles {
	public enum ETerm { TERM_ID, NAME, IS_OBSOLETE }
	public SourceFiles.TSVDataFile<ETerm> terms;

	public enum ETermDefinition { TERM_ID, DEFINITION }
	public SourceFiles.TSVDataFile<ETermDefinition> definitions;

	public enum ETermSynonym { TERM_ID, NAME, TYPE }
	public SourceFiles.TSVDataFile<ETermSynonym> synonyms;

	public enum ETermComment { TERM_ID, COMMENT_TEXT }
	public SourceFiles.TSVDataFile<ETermComment> comments;

	public enum ETermRelation { CHILD_ID, PARENT_ID, RELATION_TYPE }
	public SourceFiles.TSVDataFile<ETermRelation> relations;

	public enum ETermXref { TERM_ID, DB_CODE, DB_ID, NAME }
	public SourceFiles.TSVDataFile<ETermXref> xrefs;

	public enum ETermDefinitionXref { TERM_ID, DB_CODE, DB_ID }
	public SourceFiles.TSVDataFile<ETermDefinitionXref> definitionXrefs;

	public enum ECrossOntologyRelation { TERM_ID, RELATION, FOREIGN_NAMESPACE, FOREIGN_ID, FOREIGN_TERM, URL }
	public SourceFiles.TSVDataFile<ECrossOntologyRelation> crossOntologyRelations;

	public enum ETermSubset { TERM_ID, SUBSET, TYPE }
	public SourceFiles.TSVDataFile<ETermSubset> subsets;

	public enum ETermCredit { TERM_ID, CREDIT_CODE }
	public SourceFiles.TSVDataFile<ETermCredit> credits;

	public enum ETermHistory { TERM_ID, TIMESTAMP, ACTION, NAME, CATEGORY, TEXT }
	public SourceFiles.TSVDataFile<ETermHistory> history;

	public enum EFundingBody { CODE, DESCRIPTION, URL }
	public SourceFiles.TSVDataFile<EFundingBody> fundingBodies;

	protected File directory;

	public OntologySourceFiles(File directory) {
		this.directory = directory;
	}

	protected abstract SourceFiles.NamedFile[] requiredFiles();
}
