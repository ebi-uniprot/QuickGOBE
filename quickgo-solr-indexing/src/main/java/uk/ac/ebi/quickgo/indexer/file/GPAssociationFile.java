/**
 *
 */
package uk.ac.ebi.quickgo.indexer.file;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.cache.query.service.CacheRetrievalImpl;
import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.indexer.IIndexer;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.exception.NotFoundException;
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
	private static final Logger logger = Logger.getLogger(GPAssociationFile.class);

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
	Map<String, GenericTerm> ontologyTerms;

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

	public GPAssociationFile(NamedFile f, Map<String, GenericTerm> terms, Map<String, GenericTerm> ecoTerms, Map<Integer, Miscellaneous> taxonomies, int chunkSize) throws Exception {
		super(f, columnCount, "gpa-version", "1.1");

		this.ontologyTerms = terms;
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
	public Annotation calculateRow(String[] columns) throws Exception {

		//Populate data from file
		GpaFile gpaFile = new GpaFile(columns);

		// Get gene products chunk from Solr
		getGeneProductsChunk(gpaFile.dbObjectID);

		// Read properties and populate go Evidence
		String goEvidence = "";
		if (!"".equals(gpaFile.properties)) {
			for (String property : gpaFile.properties.split("\\|")) {
				String[] pv = property.split("=", 2);
				switch (pv[0]) {
					case "go_evidence":
						goEvidence = pv[1];
						break;
				}
			}
		}

		// Parse annotation extensions column
		List<String> extensionsList = new ArrayList<>();
		if (!"".equals(gpaFile.extensions)) {
			Collections.addAll(extensionsList, gpaFile.extensions.split("\\|"));
		}

		//Get associated term to get its name
		GOTerm term = (GOTerm) ontologyTerms.get(gpaFile.goID);

		if(term == null){//TODO Temp solution because annotation files are inconsistent with terms ones
			term = new GOTerm();
			term.setName("Undefined");
		}

		// Create annotation
		Annotation annotation = new Annotation(goEvidence, gpaFile.db, gpaFile.dbObjectID, gpaFile.goID, gpaFile.ecoID,
				term.getName(), gpaFile.assignedBy, gpaFile.reference, gpaFile.with, gpaFile.qualifier,
				gpaFile.interactingTaxID, gpaFile.date, extensionsList, gpaFile.properties);

		//GP2Protein identifiers
		List<String> proteinIDs = gpCacheRetrieval.retrieveEntry(annotation.getDbObjectID(),GeneProduct.class).getXRefsAsString();
		proteinIDs.add(gpaFile.dbObjectID);
		annotation.setGp2protein(proteinIDs);

		// GO Aspect
		GOTerm goTerm = (GOTerm)this.ontologyTerms.get(gpaFile.goID);
		if (goTerm != null) {
			annotation.setGoAspect(goTerm.getOntologyText());
		} else {
			logger.warn("No ontology with id : " + gpaFile.goID);
		}

		// Sub set
		annotation.setSubset(term.getSubsetsNames());

		// Get gene product and taxonomy information
		calculateGeneProductTaxonomy(annotation);

		// Ancestors
		calculateAncestors(gpaFile.goID,annotation);

		// ECO Ancestors
		calculateECOAncestors(annotation.getEcoID(), annotation);

		return annotation;
	}

	/**
	 * Given an annotation, calculates the taxonomy closure and target set of its gene product
	 * @param annotation Annotation
	 * @throws NotFoundException
	 * @throws SolrServerException
	 */
	private void calculateGeneProductTaxonomy(Annotation annotation) throws NotFoundException, SolrServerException {
		GeneProduct geneProduct = gpCacheRetrieval.retrieveEntry(annotation.getDbObjectID(), GeneProduct.class);

		// Set GP information
		annotation.setDbObjectName(geneProduct.getDbObjectName());
		annotation.setDbObjectSymbol(geneProduct.getDbObjectSymbol());
		annotation.setDbObjectType(geneProduct.getDbObjectType());
		annotation.setDbObjectSynonyms(geneProduct.getDbObjectSynonyms());
		annotation.setTargetSet(geneProduct.getTargetSet());
		annotation.setTaxonomyId(geneProduct.getTaxonId());

		// Set sequence length
		Miscellaneous miscellaneous = miscellaneousCacheRetrieval.getCacheBuilder().cachedValue(annotation.getDbObjectID());
		if (miscellaneous != null) {
			annotation.setSequenceLength(miscellaneous.getSequence().length());
		}

		//Set taxonomy name
		final GenericTerm genericTerm = ontologyTerms.get(geneProduct.getTaxonId());
		if (genericTerm != null) {
			annotation.setTaxonomyName(genericTerm.getName());
		}


		//Set taxonomy closure
		final Miscellaneous misc = taxonomies.get(geneProduct.getTaxonId());
		if (misc != null) {
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

	/**
	 * Calculate ancestors
	 * @param goID Gene Ontology id
	 * @param annotation Annotation to index
	 */
	private void calculateAncestors(String goID, Annotation annotation){
		GOTerm term = (GOTerm) ontologyTerms.get(goID);

		if (term == null) {
			logger.warn("No ontology with id : " + goID);
		} else {

			// Ancestors I
			List<String> annotationAncestorsI = getTermAncestry(term, EnumSet.of(RelationType.ISA, RelationType.IDENTITY));
			annotation.setAncestorsI(annotationAncestorsI);

			// Ancestors IPO
			List<String> annotationAncestorsIPO = getTermAncestry(term, EnumSet.of(
					RelationType.ISA, RelationType.IDENTITY,
					RelationType.PARTOF, RelationType.OCCURSIN));
			annotation.setAncestorsIPO(annotationAncestorsIPO);

			// Ancestors IPOR
			List<String> annotationAncestorsIPOR = getTermAncestry(term, EnumSet.of(
					RelationType.ISA, RelationType.IDENTITY,
					RelationType.PARTOF, RelationType.OCCURSIN,
					RelationType.REGULATES, RelationType.POSITIVEREGULATES,
					RelationType.NEGATIVEREGULATES));
			annotation.setAncestorsIPOR(annotationAncestorsIPOR);
		}
	}

	/**
	 * Calcualte ECO ancestors for ISA relation
	 * @param ecoID ECO id
	 * @param annotation Annotation
	 */
	private void calculateECOAncestors(String ecoID, Annotation annotation) {
		GenericTerm ecoTerm = this.ecoTerms.get(ecoID);
		if (ecoTerm == null) {
			logger.warn("No ECO term with id : " + ecoID);
		} else {
			// Ancestors I
			List<String> ecoAnotationAncestorsI = getTermAncestry(ecoTerm, EnumSet.of(RelationType.ISA,RelationType.IDENTITY));
			annotation.setEcoAncestorsI(ecoAnotationAncestorsI);
		}
	}

	/**
	 * Returns the ancestry term depending on the specified relation types
	 * @param term Term
	 * @param relationTypes Relations
	 * @return List with ancestors ids
	 */
	private List<String> getTermAncestry(GenericTerm term, EnumSet<RelationType> relationTypes) {
		List<GenericTerm> ancestors = term.getAncestry(relationTypes);
		List<String> ancestorsString = new ArrayList<>();
		for (GenericTerm genericTerm : ancestors) {
			ancestorsString.add(genericTerm.getId());
		}
		return ancestorsString;
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
		String qualifier;
		String interactingTaxID;
		String date;
		String extensions;
		String properties;


		GpaFile(String[] columns){
			this.db = columns[COLUMN_DB];
			this.dbObjectID = columns[COLUMN_DB_OBJECT_ID];
			this.goID = columns[COLUMN_GO_ID];
			this.ecoID = columns[COLUMN_EVIDENCE];
			this.assignedBy = columns[COLUMN_ASSIGNED_BY];
			this.reference = columns[COLUMN_REFERENCE];
			this.with = parsePipeSeparatedValues(columns[COLUMN_WITH]);
			this.qualifier = columns[COLUMN_QUALIFIER];
			this.interactingTaxID = columns[COLUMN_INTERACTING_TAXID];
			this.date = columns[COLUMN_DATE];
			this.extensions = columns[COLUMN_EXTENSION];
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
