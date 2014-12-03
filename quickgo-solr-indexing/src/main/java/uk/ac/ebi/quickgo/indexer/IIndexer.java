package uk.ac.ebi.quickgo.indexer;

import uk.ac.ebi.quickgo.data.SourceFiles;

public interface IIndexer {
	// TODO Review this interface
	/**
	 * Index all the information contained in source files
	 * @return true if the indexing was completed, false otherwise
	 */
	public boolean index();
}
