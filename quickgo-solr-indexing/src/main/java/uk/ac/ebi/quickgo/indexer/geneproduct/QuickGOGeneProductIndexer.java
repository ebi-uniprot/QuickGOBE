package uk.ac.ebi.quickgo.indexer.geneproduct;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.indexer.file.GPDataFile;
import uk.ac.ebi.quickgo.indexer.file.GPInformationFile;
import uk.ac.ebi.quickgo.solr.indexing.Indexer;
import uk.ac.ebi.quickgo.solr.indexing.service.geneproduct.GeneProductIndexer;
import uk.ac.ebi.quickgo.solr.indexing.service.geneproduct.mapping.GeneProductIdMapping;
import uk.ac.ebi.quickgo.util.MemoryMonitor;

/**
 * GeneProducts mapping indexing process
 * @author cbonill
 *
 */

@Service("quickGOGeneProductIndexer")
public class QuickGOGeneProductIndexer {

	/**
	 * Gene products indexer
	 */
	@Autowired
	GeneProductIndexer geneProductIndexer;

	/**
	 * GP2Protein mapping
	 */
	@Autowired
	GeneProductIdMapping productIdMapping;

	// Log
	private static final Logger logger = LoggerFactory.getLogger(QuickGOGeneProductIndexer.class);

	/**
	 * index all gene products
	 *
	 * @param gpiList
	 *            list of gp_information files that contain the metadata for the
	 *            gene products to be indexed
	 * @return number of gene products indexed
	 * @throws Exception
	 */
	public void indexGeneProducts(List<NamedFile> gpiList) throws Exception {
		int indexed = 0;
		MemoryMonitor mm = new MemoryMonitor(true);

		// gp_information files
		geneProductIndexer.deleteAll();
		for (NamedFile f : gpiList) {
			logger.info("Indexing " + f.getName());
			GPInformationFile gpInformationFile = new GPInformationFile(f);
			indexed = readAndIndexGPDataFileByChunks(gpInformationFile,	geneProductIndexer, 15000);
		}
		logger.info("indexGeneProducts done: " + mm.end() + "  total indexed: "	+ indexed);
	}

	/**
	 * index gene product cross-references
	 *
	 * @param gp2List
	 *            list of gp2protein files
	 * @return True if the indexing finished correctly, False otherwise
	 */
	public boolean indexDBXRefs(List<NamedFile> gp2proteinList) {
		boolean bOK = false;
		MemoryMonitor mm = new MemoryMonitor(true);
		int count = 0;
		try {
			// gp2protein files
			for (NamedFile gp2proteinFile : gp2proteinList) {
				logger.info("Indexing " + gp2proteinFile.getName());
				productIdMapping.readAndIndexGPMappings(gp2proteinFile.file());
			}
			bOK = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		logger.info("indexDBXRefs done: " + mm.end() + "  total indexed: " + count);
		return bOK;
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
	private int readAndIndexGPDataFileByChunks(GPDataFile gpDataFile,
			Indexer solrIndexer, int chunkSize) throws Exception {

		List rows = new ArrayList();
		MemoryMonitor mm = new MemoryMonitor(true);
		logger.info("Load " + gpDataFile.getName());

		// make sure we're dealing with a file that's in the expected format
		gpDataFile.checkVersion();

		// read the records & index them
		gpDataFile.reader.open();
		int indexed = 0;
		int count = 0;
		String[] columns;
		while ((columns = gpDataFile.reader.readRecord()) != null) {
			GeneProduct geneProduct = (GeneProduct) gpDataFile.calculateRow(columns);// Calculate next row
			if (geneProduct != null){
				rows.add(geneProduct);// Add it to the chunk
				count++;
				if (count == chunkSize) {// If the chunk size is reached, index it and reset the counters
					solrIndexer.index(rows);
					indexed = indexed + count;
					count = 0;
					rows = new ArrayList<>();
				}
			}
		}

		// Index the rest
		solrIndexer.index(rows);
		indexed = indexed + rows.size();
		rows = new ArrayList<>();

		gpDataFile.reader.close();
		logger.info("Load " + gpDataFile.getName() + " done - " + mm.end());

		return indexed;
	}
}
