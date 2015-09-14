package uk.ac.ebi.quickgo.indexer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.quickgo.data.SourceFiles;
import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.indexer.annotation.QuickGOAnnotationIndexer;
import uk.ac.ebi.quickgo.indexer.geneproduct.QuickGOGeneProductIndexer;
import uk.ac.ebi.quickgo.indexer.miscellaneous.QuickGOMiscellaneousIndexer;
import uk.ac.ebi.quickgo.indexer.ontology.QuickGOOntologyIndexer;
import uk.ac.ebi.quickgo.indexer.statistics.QuickGOCOOccurrenceStatsIndexer;
import uk.ac.ebi.quickgo.solr.indexing.service.annotation.AnnotationIndexer;
import uk.ac.ebi.quickgo.util.MemoryMonitor;

/**
 * The main controlling class for the whole QuickGO indexing process
 */
public class QuickGOIndexer {

	private SourceFiles sourceFiles;

	@Autowired
	QuickGOOntologyIndexer quickGOOntologyIndexer;

	@Autowired
	QuickGOMiscellaneousIndexer quickGOMiscellaneousIndexer;

	@Autowired
	QuickGOGeneProductIndexer quickGOGeneProductIndexer;

	@Autowired
	AnnotationIndexer annotationIndexer;

	@Autowired
	QuickGOCOOccurrenceStatsIndexer quickGOCOOccurrenceStatsIndexer;

	// Log
	private static final Logger logger = LoggerFactory.getLogger(QuickGOIndexer.class);

	// Path where data files required by the indexing process are located
	private String sourceFilesPath;

	// annotation data comes from gp_association files
	//This is the list of files to be read.
	private ArrayList<NamedFile> gpaList = new ArrayList<>();

	// gene product data (which may include cross-references) comes from gp_information files
	private ArrayList<NamedFile> gpiList = new ArrayList<>();
	private Properties properties;


	/**
	 * Main method for indexing go/eco terms, gene products, cross references
	 * and annotations in Solr
	 */
	public boolean index() {

		// Get data files
		logger.info("Getting data from source path {}", sourceFilesPath);
		sourceFiles = new SourceFiles(new File(sourceFilesPath));

		// Index data files
		boolean indexingResult = indexAll();
		if (indexingResult) {
			logger.info("Indexing completed successfully.");
		} else {
			logger.info("Indexing completed with errors.");
		}
		return indexingResult;
	}


	public void setSourceFilesPath(String sourceFilesPath) {
		this.sourceFilesPath = sourceFilesPath;
	}

	/**
	 * controller method to index everything that needs to be indexed
	 *
	 * @return flag indicating whether the indexing process was successful or
	 *         not
	 */
	private boolean indexAll() {

		// assume the worst
		MemoryMonitor mm = new MemoryMonitor(true);

		// Load GPI and GPA files
		loadFiles();

		try{
			// first index the GO data - this will also build an in-memory representation of the ontology, which will be used
			// later when indexing the annotation data
			logger.info("Indexing Ontologies");
			quickGOOntologyIndexer.setProperties(this.properties);
			quickGOOntologyIndexer.indexOntologies(sourceFiles);

			// index miscellaneous data
			logger.info("Indexing Miscellaneous");
			quickGOMiscellaneousIndexer.setProperties(this.properties);
 			quickGOMiscellaneousIndexer.index(sourceFiles, quickGOOntologyIndexer.getOntology());

			// index the gene products - this will also build a cache that will be used when indexing the annotations
			logger.info("Indexing Gene Products");
			quickGOGeneProductIndexer.setProperties(this.properties);
			quickGOGeneProductIndexer.indexGeneProducts(gpiList);

			// index any DB Xrefs - this augments the information indexed by indexGeneProducts
			logger.info("Indexing Gene Products DB Xrefs");
			quickGOGeneProductIndexer.indexDBXRefs(Arrays.asList(sourceFiles.getMappingFiles()));

			//now we can index the annotations themselves
			logger.info("Indexing Annotations");
			indexAnnotations();

			// Index Co-Occurrence stats
			logger.info("Indexing Co-occurence Stats");
			quickGOCOOccurrenceStatsIndexer.setProperties(this.properties);
			quickGOCOOccurrenceStatsIndexer.index();

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		// our work here is done...
		logger.info("indexAll done: " + mm.end());
		return true;
	}

	/**
	 * Index annotations creating a thread for each file
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void indexAnnotations()	throws SolrServerException, IOException, InterruptedException {


		annotationIndexer.deleteAll();


		//Iterate over list of files and create a thread to process each of them
		List<QuickGOAnnotationIndexer> goAnnotationIndexers = new ArrayList<>();
		for(NamedFile file : gpaList){
			goAnnotationIndexers.add(createAnnotationIndexer(file));
		}

		// This thread will wait for all the QuickGOAnnotationIndexer threads to finish to continue with the indexing process
		for(QuickGOAnnotationIndexer goAnnotationIndexer : goAnnotationIndexers){
			goAnnotationIndexer.join();
		}
	}

	private QuickGOAnnotationIndexer createAnnotationIndexer(NamedFile file) {
		QuickGOAnnotationIndexer quickGOAnnotationIndexer = new QuickGOAnnotationIndexer();
		quickGOAnnotationIndexer.setFile(file);
		quickGOAnnotationIndexer.setOntology(quickGOOntologyIndexer.getOntology());
		quickGOAnnotationIndexer.setEvidenceCodeOntology(quickGOOntologyIndexer.getEvidenceCodeOntology());
		quickGOAnnotationIndexer.setTaxonomies(quickGOMiscellaneousIndexer.getTaxonomiesMap());
		quickGOAnnotationIndexer.setAnnotationIndexer(annotationIndexer);
		quickGOAnnotationIndexer.setProperties(this.properties);
		quickGOAnnotationIndexer.start();
		return quickGOAnnotationIndexer;
	}


	/**
	 * Load GPA and GPI files
	 */
	private void loadFiles() {
		for (NamedFile f : sourceFiles.getGPDataFiles()) {
			String fn = f.getName();
			if (fn.startsWith("goa_uniprot")) {
				gpaList.add(f);
			} else if (fn.contains(".gpi")) {
				gpiList.add(new NamedFile(f.getDirectory(), f.getName()));
			}
		}
	}


	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
