/**
 *
 */
package uk.ac.ebi.quickgo.indexer.file;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.cache.query.service.CacheRetrievalImpl;
import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.indexer.IIndexer;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.geneproduct.enums.GeneProductField;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.service.geneproduct.GeneProductRetrieval;
import uk.ac.ebi.quickgo.solr.query.service.miscellaneous.MiscellaneousRetrieval;

/**
 * class to represent (and read on a row-by-row basis) a gp_association (GPAD)
 * file
 *
 * @author tonys
 *
 */
public class GPAssociationFile extends GPDataFile {

	// Log
	//private static final Logger logger = Logger.getLogger(GPAssociationFile.class);

	// columns in GPAD 1.1 format files:
	//
	// 1 DB
	private static final int COLUMN_DB = 0;
	// 2 DB_Object_ID
	private static final int COLUMN_DB_OBJECT_ID = 1;
	// 3 Qualifier
	private static final int COLUMN_QUALIFIER = 2;
	// 4 GO ID
	private static final int COLUMN_GO_ID = 3;
	// 5 DB:Reference(s)
	private static final int COLUMN_REFERENCE = 4;
	// 6 Evidence code
	private static final int COLUMN_EVIDENCE = 5;
	// 7 With / From
	private static final int COLUMN_WITH = 6;
	// 8 Interacting taxon ID
	private static final int COLUMN_INTERACTING_TAXID = 7;
	// 9 Date
	private static final int COLUMN_DATE = 8;
	// 10 Assigned_by
	private static final int COLUMN_ASSIGNED_BY = 9;
	// 11 Annotation Extension
	private static final int COLUMN_EXTENSION = 10;
	// 12 Annotation Properties
	private static final int COLUMN_PROPERTIES = 11;

	private static final int columnCount = 12;

	// regexp used in the extraction of GO evidence codes from annotation properties
	private final static Pattern goEvidencePropertyPattern = Pattern.compile("go_evidence=([A-Z]{2,3})");
	private final static Matcher goEvidencePropertyMatcher = goEvidencePropertyPattern.matcher("");

	/**
	 * Gene Products cache retrieval
	 */
	CacheRetrievalImpl<GeneProduct> gpCacheRetrieval;

	/**
	 * Gene product retrieval
	 */
	GeneProductRetrieval geneProductRetrieval;

	/**
	 * Miscellaneous retrieval
	 */
	MiscellaneousRetrieval miscellaneousRetrieval;

	/**
	 * Miscellaneous cache retrieval
	 */
	CacheRetrievalImpl<Miscellaneous> miscellaneousCacheRetrieval;

	/**
	 * The complete set of gene ontology terms
	 */
	Map<String, GenericTerm> goTerms;

	/**
	 * The complete set of eco terms
	 */
	Map<String, GenericTerm> ecoTerms;

	/**
	 * The complete set of taxonomies
	 */
	Map<Integer, Miscellaneous> taxonomies;

	/**
	 * Chunk size of gene products to read
	 */
	int chunkSize = 0;

	/**
	 * Indicates if it's the first row of the chunk to index in order to read the next chunk of gene products
	 */
	boolean firstRowOfChunk;

	public GPAssociationFile(NamedFile f, Map<String, GenericTerm> goTerms, Map<String, GenericTerm> ecoTerms, Map<Integer, Miscellaneous> taxonomies, int chunkSize) throws Exception {
		super(f, columnCount, "gpa-version", "1.1");

		this.goTerms = goTerms;
		this.ecoTerms = ecoTerms;
		this.taxonomies = taxonomies;
		this.chunkSize = chunkSize;

		//Go Terms cache retrieval
		ApplicationContext appContext = new ClassPathXmlApplicationContext("common-beans.xml","query-beans.xml");
		gpCacheRetrieval = (CacheRetrievalImpl<GeneProduct>) appContext.getBean("gpCacheRetrieval");
		geneProductRetrieval = (GeneProductRetrieval) appContext.getBean("geneProductRetrieval");
		miscellaneousRetrieval = (MiscellaneousRetrieval) appContext.getBean("miscellaneousRetrieval");
		miscellaneousCacheRetrieval = (CacheRetrievalImpl<Miscellaneous>) appContext.getBean("miscellaneousCacheRetrieval");
		firstRowOfChunk = true;
	}

	/**
	 * Builds an annotation from the read row
	 */
	public GOAnnotation calculateRow(String[] columns) throws Exception {
		//Populate data from file
		GpaFile gpaFile = new GpaFile(columns);

		// Get gene products chunk from Solr
		getGeneProductsChunk(gpaFile.dbObjectID);

		// extract the GO evidence code from the annotation properties
		goEvidencePropertyMatcher.reset(gpaFile.properties);
		String goEvidence = goEvidencePropertyMatcher.matches() ? goEvidencePropertyMatcher.group(1) : "";

		// Parse annotation extensions column
		List<String> extensionsList = new ArrayList<>();
		if (!"".equals(gpaFile.extension)) {
			Collections.addAll(extensionsList, gpaFile.extension.split("\\|"));
		}

		//Get associated term to get its name
		GOTerm goTerm = (GOTerm)goTerms.get(gpaFile.goID);

		// Create annotation
		GOAnnotation annotation = new GOAnnotation(gpaFile.db, gpaFile.dbObjectID, gpaFile.qualifier, gpaFile.goID, goTerm.getName(), goTerm.getOntologyText(), gpaFile.ecoID, goEvidence,
				gpaFile.reference, gpaFile.with, gpaFile.fullWith, gpaFile.interactingTaxID, gpaFile.date, gpaFile.assignedBy, extensionsList, gpaFile.extension, gpaFile.properties);

		// cross-references to gene product identifiers in other databases
		List<String> proteinIDs = gpCacheRetrieval.retrieveEntry(annotation.getDbObjectID(),GeneProduct.class).getXRefsAsString();
		proteinIDs.add(gpaFile.dbObjectID);
		annotation.setXrefs(proteinIDs);

		// Get gene product and taxonomy information
		calculateGeneProductTaxonomy(annotation);

		// GO Ancestors
		annotation.setAncestorsI(goTerm.getAncestorIDs(EnumSet.of(RelationType.ISA, RelationType.IDENTITY)));
		annotation.setAncestorsIPO(goTerm.getAncestorIDs(EnumSet.of(RelationType.ISA, RelationType.IDENTITY, RelationType.PARTOF, RelationType.OCCURSIN)));
		annotation.setAncestorsIPOR(goTerm.getAncestorIDs(EnumSet.of(RelationType.ISA, RelationType.IDENTITY, RelationType.PARTOF, RelationType.OCCURSIN, RelationType.REGULATES, RelationType.POSITIVEREGULATES, RelationType.NEGATIVEREGULATES)));

		// ECO Ancestors
		ECOTerm ecoTerm = (ECOTerm)ecoTerms.get(gpaFile.ecoID);
		annotation.setEcoAncestorsI(ecoTerm.getAncestorIDs(EnumSet.of(RelationType.ISA, RelationType.IDENTITY)));

		return annotation;
	}

	/**
	 * Given an annotation, calculates the taxonomy closure and target set of its gene product
	 * @param annotation Annotation
	 * @throws NotFoundException
	 * @throws SolrServerException
	 */
	private void calculateGeneProductTaxonomy(GOAnnotation annotation) throws NotFoundException, SolrServerException {
		GeneProduct geneProduct = gpCacheRetrieval.retrieveEntry(annotation.getDbObjectID(), GeneProduct.class);

		// Set GP information
		annotation.setDbObjectName(geneProduct.getDbObjectName());
		annotation.setDbObjectSymbol(geneProduct.getDbObjectSymbol());
		annotation.setDbObjectType(geneProduct.getDbObjectType());
		annotation.setDbObjectSynonyms(geneProduct.getDbObjectSynonyms());
		annotation.setTargetSets(geneProduct.getTargetSets());
		annotation.setTaxonomyId(geneProduct.getTaxonId());

		// Set sequence length
		Miscellaneous miscellaneous = miscellaneousCacheRetrieval.getCacheBuilder().cachedValue(annotation.getDbObjectID());
		if (miscellaneous != null) {
			annotation.setSequenceLength(miscellaneous.getSequence().length());
		}

		//Set taxonomy name and closure
		final Miscellaneous misc = taxonomies.get(geneProduct.getTaxonId());
		if (misc != null) {
			annotation.setTaxonomyName(misc.getTaxonomyName());
			annotation.setTaxonomyClosure(misc.getTaxonomyClosure());
		}
	}

	/**
	 * Get a chunk of gene products
	 * @param dbObjectID Gene product id
	 * @throws SolrServerException
	 */
	private void getGeneProductsChunk(String dbObjectID) throws SolrServerException {

		// If it's the first row of the annotations chunk to index, then get the next chunk of gene products of this gene product and put them into the cache
		if (firstRowOfChunk) {
			// Clean gene products cache
			gpCacheRetrieval.getCacheBuilder().clearCache();

			firstRowOfChunk = false;
			List<GeneProduct> geneProducts = geneProductRetrieval.findByQuery(
							GeneProductField.DOCTYPE.getValue()
							+ ":("
							+ SolrGeneProductDocumentType.GENEPRODUCT
									.getValue() + " OR " + SolrGeneProductDocumentType.XREF.getValue() +") AND "
							+ GeneProductField.DBOBJECTID.getValue() + ":["
							+ dbObjectID + " TO *]", chunkSize);
			// Add them to the cache
			for (GeneProduct geneProduct : geneProducts) {
				gpCacheRetrieval.getCacheBuilder().addEntry(geneProduct.getDbObjectId(), geneProduct);
			}

			if(geneProducts.size() > 0){

				// Get Properties
				List<GeneProduct> properties = geneProductRetrieval.findByQuery(
						GeneProductField.DOCTYPE.getValue()
						+ ":"
						+ SolrGeneProductDocumentType.PROPERTY
								.getValue() + " AND "
						+ GeneProductField.DBOBJECTID.getValue() + ":["
						+ dbObjectID + " TO " + geneProducts.get(geneProducts.size()-1).getDbObjectId() + "]", -1);
				// Set properties
				for(GeneProduct property : properties){
					gpCacheRetrieval.getCacheBuilder().cachedValue(property.getDbObjectId()).setGeneProductProperties(property.getGeneProductProperties());
				}

				// Get sequences
				List<Miscellaneous> miscellaneousList = miscellaneousRetrieval.findByQuery(
						MiscellaneousField.TYPE.getValue() + ":"
								+ SolrMiscellaneousDocumentType.SEQUENCE.getValue()
								+ " AND " + MiscellaneousField.DBOBJECTID.getValue() + ":["
								+ dbObjectID + " TO " + geneProducts.get(geneProducts.size()-1).getDbObjectId() + "]", -1);
				// Clear previous cached values
				miscellaneousCacheRetrieval.getCacheBuilder().clearCache();
				// Add new values to cache
				for(Miscellaneous miscellaneous : miscellaneousList){
					miscellaneousCacheRetrieval.getCacheBuilder().addEntry(miscellaneous.getDbObjectID(), miscellaneous);
				}
			}
		}
	}

	public void setFirstRowOfChunk(boolean firstRowOfChunk) {
		this.firstRowOfChunk = firstRowOfChunk;
	}

	@Override
	public boolean index(IIndexer indexer, String[] columns) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	private class GpaFile{

		String db;
		String dbObjectID;
		String goID;
		String ecoID;
		String assignedBy;
		String reference;
		List<String> with;
		String fullWith;
		String qualifier;
		String interactingTaxID;
		String date;
		String extension;
		String properties;


		GpaFile(String[] columns){
			this.db = columns[COLUMN_DB];
			this.dbObjectID = columns[COLUMN_DB_OBJECT_ID];
			this.goID = columns[COLUMN_GO_ID];
			this.ecoID = columns[COLUMN_EVIDENCE];
			this.assignedBy = columns[COLUMN_ASSIGNED_BY];
			this.reference = columns[COLUMN_REFERENCE];
			this.with = parsePipeSeparatedValues(columns[COLUMN_WITH]);
			this.fullWith = columns[COLUMN_WITH];
			this.qualifier = columns[COLUMN_QUALIFIER];
			this.interactingTaxID = columns[COLUMN_INTERACTING_TAXID];
			this.date = columns[COLUMN_DATE];
			this.extension = columns[COLUMN_EXTENSION];
			this.properties = columns[COLUMN_PROPERTIES];
		}

		/**
		 * Returns values as a list
		 * @param values Values as string
		 * @return Values as a list
		 */
		private List<String> parsePipeSeparatedValues(String values) {
			if (values != null && values.trim().length() > 1) {
				return Arrays.asList(values.split("\\|"));
			}
			return new ArrayList<>();
		}
	}
}
