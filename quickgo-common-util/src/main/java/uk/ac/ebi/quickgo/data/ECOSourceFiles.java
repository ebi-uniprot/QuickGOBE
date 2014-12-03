package uk.ac.ebi.quickgo.data;

import java.io.File;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.data.SourceFiles.TSVDataFile;

/**
 * class that defines the set of ECO-specific source files
 * 
 * @author tonys
 *
 */
public class ECOSourceFiles extends OntologySourceFiles {
	public ECOSourceFiles(File directory) {
		super(directory);
		
		terms = new TSVDataFile<ETerm>(directory, "ECO_TERMS");
		definitions = new TSVDataFile<ETermDefinition>(directory, "ECO_DEFINITIONS");
		synonyms = new TSVDataFile<ETermSynonym>(directory, "ECO_SYNONYMS");
		comments = new TSVDataFile<ETermComment>(directory, "ECO_COMMENTS");
		relations = new TSVDataFile<ETermRelation>(directory, "ECO_RELATIONS");
		xrefs = new TSVDataFile<ETermXref>(directory, "ECO_XREFS");
		history = new TSVDataFile<ETermHistory>(directory, "ECO_TERM_HISTORY");
		credits = new TSVDataFile<ETermCredit>(directory, "ECO_TERM_CREDITS");
	}

	@Override
	public NamedFile[] requiredFiles() {
		return SourceFiles.holder(terms, definitions, synonyms, comments, relations, xrefs, history, credits);
	}
}
