package uk.ac.ebi.quickgo.ff.files.ontology;

import java.io.File;

import static uk.ac.ebi.quickgo.ff.files.SourceFiles.NamedFile;
import static uk.ac.ebi.quickgo.ff.files.SourceFiles.TSVDataFile;

/**
 * class that defines the set of source files that are common to all (or, at least, most) ontologies
 * 
 * @author tonys
 *
 */
public abstract class OntologySourceFiles {
	public enum ETerm { TERM_ID, NAME, IS_OBSOLETE }
	public TSVDataFile<ETerm> terms;

	public enum ETermDefinition { TERM_ID, DEFINITION }
	public TSVDataFile<ETermDefinition> definitions;

	public enum ETermSynonym { TERM_ID, NAME, TYPE }
	public TSVDataFile<ETermSynonym> synonyms;

	public enum ETermComment { TERM_ID, COMMENT_TEXT }
	public TSVDataFile<ETermComment> comments;

	public enum ETermRelation { CHILD_ID, PARENT_ID, RELATION_TYPE }
	public TSVDataFile<ETermRelation> relations;

	public enum ETermXref { TERM_ID, DB_CODE, DB_ID, NAME }
	public TSVDataFile<ETermXref> xrefs;

	public enum ETermDefinitionXref { TERM_ID, DB_CODE, DB_ID }
	public TSVDataFile<ETermDefinitionXref> definitionXrefs;

	public enum ECrossOntologyRelation { TERM_ID, RELATION, FOREIGN_NAMESPACE, FOREIGN_ID, FOREIGN_TERM, URL }
	public TSVDataFile<ECrossOntologyRelation> crossOntologyRelations;

	public enum ETermSubset { TERM_ID, SUBSET, TYPE }
	public TSVDataFile<ETermSubset> subsets;

	public enum ETermCredit { TERM_ID, CREDIT_CODE }
	public TSVDataFile<ETermCredit> credits;

	public enum ETermHistory { TERM_ID, TIMESTAMP, ACTION, NAME, CATEGORY, TEXT }
	public TSVDataFile<ETermHistory> history;

	public enum EFundingBody { CODE, DESCRIPTION, URL }
	public TSVDataFile<EFundingBody> fundingBodies;

	protected File directory;

	public OntologySourceFiles(File directory) {
		this.directory = directory;
	}

	protected abstract NamedFile[] requiredFiles();
}
