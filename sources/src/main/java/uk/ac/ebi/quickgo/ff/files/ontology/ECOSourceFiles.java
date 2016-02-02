package uk.ac.ebi.quickgo.ff.files.ontology;

import uk.ac.ebi.quickgo.ff.files.SourceFiles;

import java.io.File;

/**
 * class that defines the set of ECO-specific source files
 * 
 * @author tonys
 *
 */
public class ECOSourceFiles extends OntologySourceFiles {
	public ECOSourceFiles(File directory) {
		super(directory);
		
		terms = new SourceFiles.TSVDataFile<>(directory, "ECO_TERMS");
		definitions = new SourceFiles.TSVDataFile<>(directory, "ECO_DEFINITIONS");
		synonyms = new SourceFiles.TSVDataFile<>(directory, "ECO_SYNONYMS");
		comments = new SourceFiles.TSVDataFile<>(directory, "ECO_COMMENTS");
		relations = new SourceFiles.TSVDataFile<>(directory, "ECO_RELATIONS");
		xrefs = new SourceFiles.TSVDataFile<>(directory, "ECO_XREFS");
		history = new SourceFiles.TSVDataFile<>(directory, "ECO_TERM_HISTORY");
		credits = new SourceFiles.TSVDataFile<>(directory, "ECO_TERM_CREDITS");
	}

	@Override
	public SourceFiles.NamedFile[] requiredFiles() {
		return SourceFiles.holder(terms, definitions, synonyms, comments, relations, xrefs, history, credits);
	}
}
