package uk.ac.ebi.quickgo.web.util;

import java.awt.image.RenderedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.graphics.*;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.geneproduct.GeneProductService;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousServiceImpl;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationColumn;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;
import uk.ac.ebi.quickgo.web.util.url.AnnotationTotal;
import uk.ac.ebi.quickgo.webservice.model.GoAnnotationJson;
import uk.ac.ebi.quickgo.webservice.model.OntologyGraphJson;
import uk.ac.ebi.quickgo.webservice.model.TermJson;

/**
 * Class to deal with the generation of common files (gpad,gaff,fasta,...)
 * @author cbonill
 *
 */
@Service
public class FileService {

	@Autowired
	AnnotationService annotationService;

	@Autowired
	MiscellaneousServiceImpl miscellaneousService;

	@Autowired
	GeneProductService geneProductService;

	private final int NUM_ROWS = 5000;

	private static final Logger logger = Logger.getLogger(FileService.class);

	public enum FILE_FORMAT {
		TSV("tsv"),
		FASTA("fasta"),
		GAF("gaf"),
		GPAD("gpad"),
		PROTEINLIST("proteinlist"),
		GENE2GO("gene2go"),
		JSON("json");

		private String value;

		private FILE_FORMAT(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * To generate GPAD file
	 * @param query Query
	 * @param numberAnnotations Total number of annotations
	 * @return GPAD file
	 */
	public StringBuffer generateGPADFile(String query, long numberAnnotations, int numResults) {
		AnnotationColumn[] columns = { AnnotationColumn.DATABASE,
				AnnotationColumn.PROTEIN, AnnotationColumn.QUALIFIER,
				AnnotationColumn.GOID, AnnotationColumn.REFERENCE,
				AnnotationColumn.EVIDENCE, AnnotationColumn.WITH,
				AnnotationColumn.DATE, AnnotationColumn.ASSIGNEDBY };
		String header= getGPADHeader();
		return generateTSVfile(FILE_FORMAT.GPAD, header, query, numberAnnotations, columns, numResults);
	}

	/**
	 * Generate fasta files
	 * @param query Filter query
	 * @param numberAnnotations Total number of annotations to download
	 * @return String buffer with the annotations information
	 */
	public StringBuffer generateFastafile(String query, long numberAnnotations, int numResults) {
		StringBuffer writer = new StringBuffer();
		List<Count> counts = annotationService.getFacetFields(query, null, AnnotationField.DBOBJECTID.getValue(), numResults);
		for (Count count : counts){
			List<Miscellaneous> entry;
			try {
				entry = miscellaneousService
						.getMiscellaneousRetrieval()
						.findByQuery(
								MiscellaneousField.TYPE.getValue()
										+ ":"
										+ MiscellaneousField.SEQUENCE.getValue()
										+ "  AND "
										+ MiscellaneousField.DBOBJECTID.getValue()
										+ ":" + count.getName().toUpperCase(),
								1);
				if (entry != null && !entry.isEmpty()) {
					String sequence = entry.get(0).getSequence();
					writer.append(">" + count.getName().toUpperCase() + "\n");
					writer.append(sequence + "\n");
				}
			} catch (SolrServerException e) {
				logger.error(e.getMessage());
			}
		}
		return writer;
	}

	/**
	 * To generate GAF file
	 * @param query Query
	 * @param numberAnnotations Total number of annotations
	 * @return GAF file
	 */
	public StringBuffer generateGAFFile(String query, long numberAnnotations, int numResults) {
		AnnotationColumn[] columns = { AnnotationColumn.DATABASE,
				AnnotationColumn.PROTEIN, AnnotationColumn.SYMBOL,
				AnnotationColumn.QUALIFIER, AnnotationColumn.GOID,
				AnnotationColumn.REFERENCE, AnnotationColumn.EVIDENCE,
				AnnotationColumn.WITH, AnnotationColumn.ASPECT,
				AnnotationColumn.NAME, AnnotationColumn.SYNONYM,
				AnnotationColumn.TYPE, AnnotationColumn.TAXON,
				AnnotationColumn.DATE, AnnotationColumn.ASSIGNEDBY };
		String header= FileService.getGAFFHeader();
		return generateTSVfile(FileService.FILE_FORMAT.GAF, header, query, numberAnnotations, columns, numResults);
	}

	/**
	 * To generate Protein List file
	 * @param query Query
	 * @param numberAnnotations Total number of annotations
	 * @return Protein List file
	 */
	public StringBuffer generateProteinListFile(String query, long numberAnnotations, int numResults) {
		AnnotationColumn[] columns = { AnnotationColumn.DATABASE,
				AnnotationColumn.PROTEIN, AnnotationColumn.SYMBOL,
				AnnotationColumn.NAME, AnnotationColumn.SYNONYM,
				AnnotationColumn.TYPE, AnnotationColumn.TAXON};
		StringBuffer writer = new StringBuffer();
		writer.append(StringUtils.arrayToDelimitedString(columns, "\t"));
		writer.append("\n");
		List<Count> counts = annotationService.getFacetFields(query, null, AnnotationField.DBOBJECTID.getValue(), numResults);
		for (Count count : counts){
			GeneProduct entry;
			entry = geneProductService.findById(count.getName().toUpperCase());
			if (entry != null) {
				writer.append(entry.getDb() + "\t" + entry.getDbObjectId()
						+ "\t" + entry.getDbObjectSymbol() + "\t"
						+ entry.getDbObjectName() + "\t"
						+ StringUtils.arrayToDelimitedString(entry.getDbObjectSynonyms().toArray(), "|") + "\t"
						+ entry.getDbObjectType() + "\t" + entry.getTaxonId());
			}
		}
		return writer;
	}


	/**
	 * To generate gene2go file
	 * @param query Query
	 * @param numberAnnotations Total number of annotations
	 * @return gene2go file
	 */
	public StringBuffer generateGene2GoFile(String query, long numberAnnotations, int numResults) {
		AnnotationColumn[] columns = { AnnotationColumn.TAXON,
				AnnotationColumn.PROTEIN, AnnotationColumn.EVIDENCE,
				AnnotationColumn.QUALIFIER, AnnotationColumn.TERMNAME,
				AnnotationColumn.REFERENCE, AnnotationColumn.ASPECT};
		String header = StringUtils.arrayToDelimitedString(columns, "\t");
		return generateTSVfile(FileService.FILE_FORMAT.GAF, header, query, numberAnnotations, columns, numResults);
	}

	/**
	 * Generate TSV annotations file
	 * @param query Filter query
	 * @param numberAnnotations Total number of annotations to download
	 * @param numResults
	 * @return String buffer with the annotations information
	 */
	public StringBuffer generateTSVfile(FILE_FORMAT format, String header, String query, long numberAnnotations, AnnotationColumn[] columns, int numResults) {
		StringBuffer writer = new StringBuffer();
		int rows = NUM_ROWS;
		if (numResults > 0) {// Limit specified
			numberAnnotations = numResults;
			if(numResults < NUM_ROWS){
				rows = numResults;
			}
		}
		int start = 0;
		writer.append(header + "\n");
		while (start < numberAnnotations) {
			List<Annotation> annotations = annotationService.retrieveAnnotations(query, start, rows);
			for (Annotation annotation : annotations) {
				try {
					writer.append(AnnotationColumn.getAnnotationColumns(format,annotation,columns,"\t") + "\n");
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			start = start + rows;
		}
		return writer;
	}


	/**
	 * To generate GPAD file
	 * @param query Query
	 * @param numberAnnotations Total number of annotations
	 * @return GPAD file
	 */
	public StringBuffer generateJsonFile(String query, long numberAnnotations, int numResults) {
		AnnotationColumn[] columns = {
				AnnotationColumn.DATABASE, AnnotationColumn.PROTEIN, AnnotationColumn.SYMBOL, AnnotationColumn.QUALIFIER,
				AnnotationColumn.GOID, AnnotationColumn.REFERENCE, AnnotationColumn.EVIDENCE, AnnotationColumn.WITH,
				AnnotationColumn.DATE, AnnotationColumn.ASSIGNEDBY, AnnotationColumn.TERMNAME, AnnotationColumn.ASPECT,
				AnnotationColumn.TAXON};


		int rows = NUM_ROWS;
		if (numResults > 0) {// Limit specified
			numberAnnotations = numResults;
			if(numResults < NUM_ROWS){
				rows = numResults;
			}
		}
		int start = 0;
		boolean firstIteration=true;
		StringBuffer writer = new StringBuffer();
		writer.append("[");
		while (start < numberAnnotations) {
			List<Annotation> annotations = annotationService.retrieveAnnotations(query, start, rows);
			//annotationService.retrieveAnnotations(solrQuery, (page-1)*rows, rows);
			for (Annotation annotation : annotations) {

				if(!firstIteration){
					writer.append(",");
				}

				GoAnnotationJson targetClass = new GoAnnotationJson();


				try {
					writer.append(AnnotationColumn.getAnnotationColumnsForJson(annotation,columns, targetClass));
					firstIteration=false;
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			start = start + rows;
		}
		writer.append("]");
		return writer;
	}


	/**
	 * To generate GPAD file
	 * @param query Query
	 * @param numberAnnotations Total number of annotations
	 * @return GPAD file
	 */
	public StringBuffer generateJsonFileWithPageAndRow(String query, long numberAnnotations, int start, int rows) {
		AnnotationColumn[] columns = {
				AnnotationColumn.DATABASE, AnnotationColumn.PROTEIN, AnnotationColumn.SYMBOL, AnnotationColumn.QUALIFIER,
				AnnotationColumn.GOID, AnnotationColumn.REFERENCE, AnnotationColumn.EVIDENCE, AnnotationColumn.WITH,
				AnnotationColumn.DATE, AnnotationColumn.ASSIGNEDBY, AnnotationColumn.TERMNAME, AnnotationColumn.ASPECT,
				AnnotationColumn.TAXON};


		//		int rows = NUM_ROWS;
		//		if (numResults > 0) {// Limit specified
		//			numberAnnotations = numResults;
		//			if(numResults < NUM_ROWS){
		//				rows = numResults;
		//			}
		//		}
		//		int start = 0;

		boolean firstIteration=true;
		StringBuffer writer = new StringBuffer();
		writer.append("[");
//		while (start < numberAnnotations) {
			List<Annotation> annotations = annotationService.retrieveAnnotations(query, start, rows);
			//annotationService.retrieveAnnotations(solrQuery, (page-1)*rows, rows);
			for (Annotation annotation : annotations) {

				if(!firstIteration){
					writer.append(",");
				}

				GoAnnotationJson targetClass = new GoAnnotationJson();


				try {
					writer.append(AnnotationColumn.getAnnotationColumnsForJson(annotation,columns, targetClass));
					firstIteration=false;
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
//			start = start + rows;
//		}
		writer.append("]");
		return writer;
	}


	/**
	 * To generate json file with total number of annotations
	 * @param numberAnnotations Total number of annotations
	 * @return json file
	 */
	public StringBuffer generateJsonFileWithTotalAnnotations(long numberAnnotations) {

		StringBuffer writer = new StringBuffer();
//		writer.append("[");

			try {
				writer.append(AnnotationColumn.getAnnotationTotalInJson(numberAnnotations, new AnnotationTotal()));

			} catch (Exception e) {
				logger.error(e.getMessage());
			}


//		writer.append("]");
		return writer;
	}


	public StringBuffer generateJsonFileForTerm(GOTerm term, List<TermRelation> childTermsRelations,
												List<COOccurrenceStatsTerm> allStats,
												List<COOccurrenceStatsTerm> nonIEAStats) {
		StringBuffer buffer = new StringBuffer();
		try {
			TermJson termJson = new TermJson();
			buffer.append(AnnotationColumn.getTermInJson(term, childTermsRelations, allStats, nonIEAStats, termJson));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return buffer;
	}


	public StringBuffer generateJsonFileForPredefinedSlims(List<Miscellaneous> subsetsCounts) {
		StringBuffer buffer = new StringBuffer();
		try {
			TermJson termJson = new TermJson();
			buffer.append(AnnotationColumn.toJson(subsetsCounts));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return buffer;
	}


	public StringBuffer generateJsonFileForPredefinedSetTerms(List<TermJson> setTerms) {
		StringBuffer buffer = new StringBuffer();
		try {
			TermJson termJson = new TermJson();
			buffer.append(AnnotationColumn.toJson(setTerms));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return buffer;
	}




	public StringBuffer generateJsonFileForOntologyTerm(String termids) {
		StringBuffer buffer = new StringBuffer();
		final OntologyGraphJson ontologyGraphJson = generateTermOntologyGraph(termids, null);
		try {

			buffer.append(AnnotationColumn.getOntologyGraphInJson(ontologyGraphJson));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return buffer;
	}


	GenericOntology genericOntology;

	public OntologyGraphJson generateTermOntologyGraph(
			String termsIds,
			String relations
	) {

		List<String> termsIdsList = new ArrayList<>();
		termsIdsList = Arrays.asList(termsIds.split(","));

		// Get corresponding ontology
		genericOntology = TermUtil.getOntology(termsIdsList.get(0));

		// Create graph image
		GraphImage graphImage = createRenderableImage(termsIds);
		String src = ImageArchive.store(graphImage);
		RenderedImage renderableImage = graphImage.render();

		String id = termsIdsList.get(0);
		String name = genericOntology.getTerm(id).getName();
		String title = id;
		title = title + " " + name;

		OntologyGraphJson ontologyGraphJson = new OntologyGraphJson();
		ontologyGraphJson.setTermTermGraphTitle(title);
		//ontologyGraphJson.setTermTermsNodes(graphImage.getOntologyTerms());
		for(TermNode termNode:graphImage.getOntologyTerms()){
			termNode.getId();
			for(TermRelation termRelation : termNode.getTerm().getAncestors()){
				termRelation.toString();
			}
			for(TermRelation termRelation : termNode.getTerm().getChildren()){
				termRelation.toString();
			}
			for(TermRelation termRelation : termNode.getTerm().getParents()){
				termRelation.toString();
			}
			for(TermRelation termRelation : termNode.getTerm().getReplacements()){
				termRelation.toString();
			}
			for(TermRelation termRelation : termNode.getTerm().getReplaces()){
				termRelation.toString();
			}
			for(TermRelation termRelation : termNode.getTerm().otherParents()){
				termRelation.toString();
			}
		}
		ontologyGraphJson.setTermLegendNodes(graphImage.legend);
		ontologyGraphJson.setTermGraphImageSrc(src);
		ontologyGraphJson.setTermGraphImageWidth(renderableImage.getWidth());
		ontologyGraphJson.setTermGraphImageHeight(renderableImage.getHeight());

		return ontologyGraphJson;
	}

	/**
	 * Generates ancestors graph for a list of terms
	 * @param termsIds List of terms to calculate the graph for
	 * @return Graph image
	 */
	public GraphImage createRenderableImage(String termsIds){
		// Check if the selected terms exist
		List<GenericTerm> terms = new ArrayList<GenericTerm>();
		List<String> termsIdsList = Arrays.asList(termsIds.split(","));
		for(String id : termsIdsList){
			GenericTerm term = genericOntology.getTerm(id);
			if(term != null){
				terms.add(term);
			}
		}

		// Build GO term set
		GenericTermSet termSet = new GenericTermSet(genericOntology, "Term Set", 0);
		for(GenericTerm term : terms){
			termSet.addTerm(term);
		}

		// Create ontology graph
		OntologyGraph ontologyGraph = OntologyGraph.makeGraph(termSet, EnumSet.of(RelationType.USEDIN, RelationType.ISA, RelationType.PARTOF, RelationType.REGULATES, /*RelationType.HASPART,*/ RelationType.OCCURSIN), 0, 0, new GraphPresentation());
		return ontologyGraph.layout();
	}


	/**
	 * GAFF header
	 * @return GAFF header
	 */
	public static String getGAFFHeader(){
		return "!gaf-version: 2.0\n" +
				"!Project_name: UniProt GO Annotation (UniProt-GOA)\n" +
				"!URL: http://www.ebi.ac.uk/GOA\n" +
				"!Contact Email: goa@ebi.ac.uk\n" +
				"!Date downloaded from the QuickGO browser: " + getCurrentDate();
	}

	/**
	 * GPAD header
	 * @return GPAD header
	 */
	public static String getGPADHeader(){
		return "!gpa-version: 1.1\n" +
				"!Project_name: UniProt GO Annotation (UniProt-GOA)\n" +
				"!URL: http://www.ebi.ac.uk/GOA\n" +
				"!Contact Email: goa@ebi.ac.uk\n" +
				"!Date downloaded from the QuickGO browser: " + getCurrentDate();
	}

	/**
	 * Get current date
	 * @return Current date
	 */
	private static String getCurrentDate(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
