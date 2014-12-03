package uk.ac.ebi.quickgo.indexer.ontology;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.data.SourceFiles;
import uk.ac.ebi.quickgo.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.solr.indexing.service.ontology.TermIndexer;
import uk.ac.ebi.quickgo.util.MemoryMonitor;

/**
 * GO/ECO terms indexing process
 * @author cbonill
 *
 */
@Service("quickGOOntologyIndexer")
public class QuickGOOntologyIndexer {

	/**
	 * GO Terms indexer service
	 */
	@Autowired
	TermIndexer goTermIndexer;
	
	/**
	 * ECO Terms indexer service
	 */
	@Autowired
	TermIndexer ecoTermIndexer;
	
	// Log
	private static final Logger logger = Logger.getLogger(QuickGOOntologyIndexer.class);
	
	// cache of the GO data
	GeneOntology ontology = new GeneOntology();
	
	// ECO
	EvidenceCodeOntology evidenceCodeOntology = new EvidenceCodeOntology();
	
	/**
	 * index all the GO terms
	 * 
	 * @param sourceFiles
	 *            object containing references to all of the source files needed
	 *            by the indexing operation
	 * @return flag indicating whether the indexing was carried out successfully
	 * @throws Exception 
	 */
	public void indexOntologies(SourceFiles sourceFiles) throws Exception {
		MemoryMonitor mm = new MemoryMonitor(true);
	
		// read the ontology data from file, build an in-memory
		// representation and index data in Solr
		goTermIndexer.deleteAll();
		// Index GO
		indexGO(sourceFiles);
		// Index ECO
		indexECO(sourceFiles);	
		logger.info("index Ontologies done: " + mm.end());		
	}

	/**
	 * Index GO terms
	 * 
	 * @param sourceFiles
	 *            Source files
	 * @throws Exception
	 */
	private void indexGO(SourceFiles sourceFiles) throws Exception {
		MemoryMonitor mm = new MemoryMonitor(true);
		ontology.load(sourceFiles.goSourceFiles);		
		goTermIndexer.index(new ArrayList<GenericTerm>(ontology.terms.values()));
		logger.info("indexGO done: " + mm.end() + "  total indexed: "	+ ontology.terms.size());
	}

	/**
	 * Index ECO terms
	 * 
	 * @param sourceFiles
	 *            Source files
	 * @throws Exception
	 */
	private void indexECO(SourceFiles sourceFiles) throws Exception {
		MemoryMonitor mm = new MemoryMonitor(true);
		evidenceCodeOntology.load(sourceFiles.ecoSourceFiles, "root");
		ecoTermIndexer.index(new ArrayList<GenericTerm>(evidenceCodeOntology.terms.values()));
		logger.info("indexECO done: " + mm.end() + "  total indexed: "	+ evidenceCodeOntology.terms.size());
	}

	public GeneOntology getOntology() {
		return ontology;
	}

	public EvidenceCodeOntology getEvidenceCodeOntology() {
		return evidenceCodeOntology;
	}	
}
