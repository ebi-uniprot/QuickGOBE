package uk.ac.ebi.quickgo.ff.files.ontology;

import uk.ac.ebi.quickgo.ff.files.SourceFiles;

import java.io.File;

import static uk.ac.ebi.quickgo.ff.files.SourceFiles.NamedFile;
import static uk.ac.ebi.quickgo.ff.files.SourceFiles.TSVDataFile;

/**
 * class that defines the set of ECO-specific source files
 * 
 * @author tonys
 *
 */
public class ECOSourceFiles extends OntologySourceFiles {
	public ECOSourceFiles(File directory) {
		super(directory);
		
		terms = new TSVDataFile<>(directory, "ECO_TERMS");
		definitions = new TSVDataFile<>(directory, "ECO_DEFINITIONS");
		synonyms = new TSVDataFile<>(directory, "ECO_SYNONYMS");
		comments = new TSVDataFile<>(directory, "ECO_COMMENTS");
		relations = new TSVDataFile<>(directory, "ECO_RELATIONS");
		xrefs = new TSVDataFile<>(directory, "ECO_XREFS");
		history = new TSVDataFile<>(directory, "ECO_TERM_HISTORY");
		credits = new TSVDataFile<>(directory, "ECO_TERM_CREDITS");
	}

	@Override
	public NamedFile[] requiredFiles() {
		return SourceFiles.holder(terms, definitions, synonyms, comments, relations, xrefs, history, credits);
	}
}
