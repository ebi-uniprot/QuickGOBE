package uk.ac.ebi.quickgo.indexer.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.indexer.file.GPAssociationFile;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.indexing.service.annotation.AnnotationIndexer;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.util.MemoryMonitor;

/**
 * Annotations indexing process
 * @author cbonill
 *
 */

public class QuickGOAnnotationIndexer extends Thread{

	/**
	 * Annotation indexer
	 */
	AnnotationIndexer annotationIndexer;

	// Log
	private final Logger logger = LoggerFactory.getLogger(QuickGOAnnotationIndexer.class);


	NamedFile file;
	GeneOntology ontology;
	EvidenceCodeOntology evidenceCodeOntology;

	Map<Integer, Miscellaneous> taxonomies;

	public void run() {
		int chunkSize = 150000; //TODO Increase this value to speed up the indexing process
		MemoryMonitor mm = new MemoryMonitor(true);
		try {
			// gp_association files
			logger.info("Indexing " + file.getName());

			//todo make gpAssociationFile of type GpaDataFile, once the later uses Generics
			GPAssociationFile gpAssociationFile = new GPAssociationFile(file, ontology.terms, evidenceCodeOntology.terms, taxonomies, chunkSize);
			int indexed = readAndIndexGPDataFileByChunks(gpAssociationFile, annotationIndexer, chunkSize);

			logger.info("indexAnnotations of file: " + file.getName() + " done: " + mm.end() + "  total indexed: " + indexed);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Given a GPData file, read it and index it by chunks of the specified size
	 *
	 * @param gpDataFile
	 *            File to read
	 * @param solrIndexer
	 *            Indexer to use
	 * @param chunkSize
	 *            Size of the chunk
	 * @throws Exception
	 */
	private int readAndIndexGPDataFileByChunks(GPAssociationFile gpDataFile, Indexer solrIndexer, int chunkSize) throws Exception {
		List<GOAnnotation> rows = new ArrayList<>();
		MemoryMonitor mm = new MemoryMonitor(true);
		logger.info("Load " + gpDataFile.getName());

		// read the records & index them
		gpDataFile.reader.open();
		int indexed = 0;
		int count = 0;
		String[] columns;
		while ((columns = gpDataFile.reader.readRecord()) != null) {
			rows.add(gpDataFile.calculateRow(columns));// Calculate next row and add it to the chunk
			count++;
			if (count == chunkSize) {// If the chunk size is reached, index it and reset the counters
				solrIndexer.index(rows);
				indexed = indexed + count;
				count = 0;
				rows = new ArrayList<>();
				// Set first row of chunk to true
				gpDataFile.setFirstRowOfChunk(true);
			}
		}

		// Index the rest
		if (rows.size() > 0) {
			solrIndexer.index(rows);
			indexed = indexed + rows.size();
		}

		gpDataFile.reader.close();
		logger.info("Load " + gpDataFile.getName() + " done - " + mm.end());

		return indexed;
	}

	public AnnotationIndexer getAnnotationIndexer() {
		return annotationIndexer;
	}

	public void setAnnotationIndexer(AnnotationIndexer annotationIndexer) {
		this.annotationIndexer = annotationIndexer;
	}

	public NamedFile getFile() {
		return file;
	}

	public void setFile(NamedFile file) {
		this.file = file;
	}

	public GeneOntology getOntology() {
		return ontology;
	}

	public void setOntology(GeneOntology ontology) {
		this.ontology = ontology;
	}

	public EvidenceCodeOntology getEvidenceCodeOntology() {
		return evidenceCodeOntology;
	}

	public void setEvidenceCodeOntology(EvidenceCodeOntology evidenceCodeOntology) {
		this.evidenceCodeOntology = evidenceCodeOntology;
	}

	public Map<Integer, Miscellaneous> getTaxonomies() {
		return taxonomies;
	}

	public void setTaxonomies(Map<Integer, Miscellaneous> taxonomies) {
		this.taxonomies = taxonomies;
	}
}
