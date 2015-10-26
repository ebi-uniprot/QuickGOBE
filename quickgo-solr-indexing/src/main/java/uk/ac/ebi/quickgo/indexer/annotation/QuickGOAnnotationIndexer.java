package uk.ac.ebi.quickgo.indexer.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.indexer.file.GPAssociationFile;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.solr.indexing.service.annotation.AnnotationIndexer;
import uk.ac.ebi.quickgo.solr.indexing.service.miscellaneous.MiscellaneousIndexer;
import uk.ac.ebi.quickgo.solr.mapper.miscellaneous.StatisticTupleMapper;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;
import uk.ac.ebi.quickgo.util.CPUUtils;
import uk.ac.ebi.quickgo.util.MemoryMonitor;

/**
 * Annotations indexing process
 * @author cbonill
 *
 */
@Service("QuickGOAnnotationIndexer")
public class QuickGOAnnotationIndexer {

	/**
	 * Annotation indexer
	 */
	//AnnotationIndexer annotationIndexer
	//Set by spring
	private SolrServerProcessor solrServerProcessor;	//replaces annotationIndexer

	private MiscellaneousIndexer miscellaneousIndexer;

	private final Logger logger = LoggerFactory.getLogger(QuickGOAnnotationIndexer.class);
	private NamedFile file;
	private GeneOntology ontology;
	private EvidenceCodeOntology evidenceCodeOntology;
	private	Map<Integer, Miscellaneous> taxonomies;

	//TODO Increase this value to speed up the indexing process
	private long rowCreationTime;
	private long solrCallTime;
	private Properties properties;
	private int CHUNK_SIZE;

	public void start() {

		MemoryMonitor mm = new MemoryMonitor(true);
		GPAssociationFile gpAssociationFile = null;
		int indexed=0;
		try {

			// gp_association files
			logger.info("Indexing " + file.getName());

			//todo make gpAssociationFile of type GpaDataFile, once the later uses Generics - What EXACTLY does this mean?
			 gpAssociationFile = new GPAssociationFile(file, ontology.terms, evidenceCodeOntology.terms, taxonomies, CHUNK_SIZE);
			 indexed = readAndIndexGPDataFileByChunks(gpAssociationFile);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}finally{

			logger.info("Total row creation time was {}", rowCreationTime);
			logger.info("Total cache creation time was {}", gpAssociationFile!=null?gpAssociationFile.cacheCreationTime:null);
			logger.info("Total Solr indexing call time was {}", solrCallTime);
			logger.info("indexAnnotations of file: " + file.getName() + " done: " + mm.end() + "  total indexed: " + indexed);
		}
	}

	/**
	 * Given a GPData file, read it and index it by chunks of the specified size
	 *
	 * @param gpDataFile
	 *            File to read
	 *            Indexer to use
	 * @throws Exception
	 */
	private int readAndIndexGPDataFileByChunks(GPAssociationFile gpDataFile) throws Exception {
		List<GOAnnotation> rows = new ArrayList<>();
		MemoryMonitor mm = new MemoryMonitor(true);
		logger.info("Load " + gpDataFile.getName());
		StatisticsBucket statisticsBucket = new StatisticsBucket();

		// read the records & index them
		gpDataFile.reader.open();
		int indexed = 0;
		int count = 0;
		String[] columns;

		while ((columns = gpDataFile.reader.readRecord()) != null) {
			// Calculate next row and add it to the chunk
			long rowCreationStart = CPUUtils.getCpuTime();
			GOAnnotation annotation = gpDataFile.calculateRow(columns);
			annotation.setDocType(GOAnnotation.SolrAnnotationDocumentType.ANNOTATION.getValue());
			statisticsBucket.addAnnotationToStatistics(annotation);
			rows.add(annotation);
			rowCreationTime+=CPUUtils.getCpuTime()-rowCreationStart;

			count++;
			if (count == CHUNK_SIZE) {// If the chunk size is reached, index it and reset the counters
				long solrCallStart = CPUUtils.getCpuTime();
				//solrIndexer.index(rows);
				try {
					solrServerProcessor.indexBeansAutoCommit(rows);
				} catch (SolrServerException | IOException e) {
					logger.error(e.getMessage());
				}
				solrCallTime += CPUUtils.getCpuTime()-solrCallStart;
				indexed = indexed + count;
				count = 0;
				rows = new ArrayList<>();

				// Set first row of chunk to true
				gpDataFile.newChunk();
			}
		}

		// Index the rest
		if (rows.size() > 0) {
			//solrIndexer.index(rows);
			try {
				solrServerProcessor.indexBeansAutoCommit(rows);
			} catch (SolrServerException | IOException e) {
				logger.error(e.getMessage());
			}
			indexed = indexed + rows.size();
		}

		gpDataFile.reader.close();
		logger.info("Load " + gpDataFile.getName() + " done - " + mm.end());

		//Index statistics bucket
		StatisticTupleMapper statisticTupleMapper = new StatisticTupleMapper();
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsSummary(),		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsSummary(), 		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsPerAspect(), 		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsPerAspect(), 	statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsPerAssignedBy(), 	statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsPerAssignedBy(), statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsPerEvidence(),	statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsPerEvidence(), 	statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsPerGOID(), 		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsPerGOID(), 		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsPerTaxon(), 		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsPerTaxon(),		statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topAnnotationsPerReference(), 	statisticTupleMapper);
		miscellaneousIndexer.index(statisticsBucket.topGeneProductsPerReference(), 	statisticTupleMapper);

		return indexed;
	}


//	public void setAnnotationIndexer(AnnotationIndexer annotationIndexer) {
//		this.annotationIndexer = annotationIndexer;
//	}

	public void setSolrServerProcessor(SolrServerProcessor solrServerProcessor) {
		this.solrServerProcessor = solrServerProcessor;
	}

	public NamedFile getFile() {
		return file;
	}

	public void setFile(NamedFile file) {
		this.file = file;
	}

	public void setOntology(GeneOntology ontology) {
		this.ontology = ontology;
	}

	public void setEvidenceCodeOntology(EvidenceCodeOntology evidenceCodeOntology) {
		this.evidenceCodeOntology = evidenceCodeOntology;
	}

	public void setTaxonomies(Map<Integer, Miscellaneous> taxonomies) {
		this.taxonomies = taxonomies;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		CHUNK_SIZE =  Integer.parseInt(properties.getProperty("quickgo.index.annotation.chunksize"));
	}


	public void setMiscellaneousIndexer(MiscellaneousIndexer miscellaneousIndexer) {
		this.miscellaneousIndexer = miscellaneousIndexer;
	}
}
