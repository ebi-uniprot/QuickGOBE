package uk.ac.ebi.quickgo.web.util.annotation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.FacetField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.generic.AuditRecord;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;


import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.util.term.TermUtil;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationBlackListContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationPostProcessingContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.TaxonConstraintsContent;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.webservice.model.AnnotationBlacklistJson;
import uk.ac.ebi.quickgo.webservice.model.AnnotationPostProcessingJson;
import uk.ac.ebi.quickgo.webservice.model.GoTermHistoryJson;
import uk.ac.ebi.quickgo.webservice.model.TermJson;

/**
 * Annotation WS util methods
 * @author cbonill
 *
 */
@Service
public class AnnotationWSUtilImpl implements AnnotationWSUtil{


	@Autowired
	AnnotationService annotationService;

	@Autowired
	TermService goTermService;

	@Autowired
	TermUtil termUtil;

	@Autowired
	FileService fileService;

	@Autowired
	MiscellaneousService miscellaneousService;

	@Autowired
	MiscellaneousUtil miscellaneousUtil;

	@Autowired
	AnnotationBlackListContent annotationBlackListContent;

	@Autowired
	AnnotationPostProcessingContent annotationPostProcessingContent;

	// All go terms
	Map<String, GenericTerm> terms = uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTerms(); //todo this should be properly cached.

	private static final Logger logger = Logger.getLogger(AnnotationWSUtilImpl.class);

	/**
	 * Map GAnnotation columns to the *new* QuickGO ones
	 * @param cols Columns to map
	 * @return COlumns mapped
	 */
	public AnnotationColumn[] mapColumns(String cols) {
		List<AnnotationColumn> enumColumnsList = new ArrayList<>();
		String[] columns = cols.split(",");
		for(String col : columns){
			AnnotationColumn enumColumn = mapColumn(col);
			if (enumColumn != null){
				enumColumnsList.add(mapColumn(col));
			}
		}
		AnnotationColumn[] enumColumns = new AnnotationColumn[enumColumnsList.size()];
		return enumColumnsList.toArray(enumColumns);
	}

	/**
	 * Map a specific QuickGO *old* column to one of the *new* ones
	 * @param col
	 * @return
	 */
	private AnnotationColumn mapColumn(String col) {
		switch(col){
		case "protein_db":
		case "proteinDB":
			return AnnotationColumn.DATABASE;
		case "protein_id":
		case "proteinID":
			return AnnotationColumn.PROTEIN;
		case "protein_symbol":
		case "proteinSymbol":
			return AnnotationColumn.SYMBOL;
		case "qualifier":
			return AnnotationColumn.QUALIFIER;
		case "go_id":
		case "goID":
			return AnnotationColumn.GOID;
		case "go_name":
		case "goName":
			return AnnotationColumn.TERMNAME;
		case "aspect":
			return AnnotationColumn.ASPECT;
		case "evidence":
			return AnnotationColumn.EVIDENCE;
		case "ref":
			return AnnotationColumn.REFERENCE;
		case "with":
			return AnnotationColumn.WITH;
		case "protein_taxonomy":
		case "proteinTaxon":
			return AnnotationColumn.TAXON;
		case "date":
			return AnnotationColumn.DATE;
		case "from":
			return AnnotationColumn.ASSIGNEDBY;
		case "splice":
			return AnnotationColumn.EXTENSION;
		case "protein_name":
		case "proteinName":
			return AnnotationColumn.NAME;
		case "protein_synonym":
		case "proteinSynonym":
			return AnnotationColumn.SYNONYM;
		case "protein_type":
		case "proteinType":
			return AnnotationColumn.TYPE;
		case "protein_taxonomy_name":
		case "proteinTaxonName":
			return AnnotationColumn.TAXONNAME;
		case "original_term_id":
		case "originalTermID":
			return AnnotationColumn.ORIGINALTERMID;
		case "original_go_name":
		case "originalGOName":
			return AnnotationColumn.ORIGINALTERMNAME;
		}
		return null;
	}

	/**
	 * Generate a file with the annotations results
	 * @param format File format
	 * @param gzip
	 * @param query Query to run
	 * @param columns Columns to display
	 */
	public void downloadAnnotations(String format, boolean gzip, String query, AnnotationColumn[] columns, int limit,
									HttpServletResponse httpServletResponse){

		// Get total number annotations
		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);

		// Check file format
		StringBuffer sb = null;
		try {
			switch (FileService.FILE_FORMAT.valueOf(format.toUpperCase())) {
			case TSV:
				sb = fileService.generateTSVfile(FileService.FILE_FORMAT.TSV, "", query, totalAnnotations, columns, limit);
				break;
			case FASTA:
				sb = fileService.generateFastafile(query, totalAnnotations, limit);
				break;
			case GAF:
				sb = fileService.generateGAFFile(query, totalAnnotations, limit);
				break;
			case GPAD:
				sb = fileService.generateGPADFile(query, totalAnnotations, limit);
				break;
			case PROTEINLIST:
				sb = fileService.generateProteinListFile(query, totalAnnotations, limit);
				break;
			case GENE2GO:
				sb = fileService.generateGene2GoFile(query, totalAnnotations, limit);
				break;
			case JSON:
				sb = fileService.generateJsonFile(query, totalAnnotations, limit );
				break;
			}

			String extension = format;
			if (gzip) {
				extension = extension + ".gz";
				httpServletResponse.setContentType("application/x-gzip");
				httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + extension);
				OutputStream os = new GZIPOutputStream(httpServletResponse.getOutputStream());

				Writer wr = new OutputStreamWriter(os, "ASCII");
				wr.write(sb.toString());
				wr.close();
			} else {
				InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
				IOUtils.copy(in, httpServletResponse.getOutputStream());
				// Set response header and content
				httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + extension);
				httpServletResponse.setContentLength(sb.length());
				httpServletResponse.flushBuffer();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	/**
	 * Generate a file with the annotations results
	 * @param format File format
	 * @param query Query to run
	 * @param columns Columns to display
	 */
	public void downloadAnnotationsInternal(String format, String query, AnnotationColumn[] columns, int limit,
									int start, int rows, HttpServletResponse httpServletResponse){

		// Get total number annotations
		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);

		// Check file format
		StringBuffer sb = null;
		try {
			switch (FileService.FILE_FORMAT.valueOf(format.toUpperCase())) {
				case TSV:
					sb = fileService.generateTSVfile(FileService.FILE_FORMAT.TSV, "", query, totalAnnotations, columns, limit);
					break;
				case FASTA:
					sb = fileService.generateFastafile(query, totalAnnotations, limit);
					break;
				case GAF:
					sb = fileService.generateGAFFile(query, totalAnnotations, limit);
					break;
				case GPAD:
					sb = fileService.generateGPADFile(query, totalAnnotations, limit);
					break;
				case PROTEINLIST:
					sb = fileService.generateProteinListFile(query, totalAnnotations, limit);
					break;
				case GENE2GO:
					sb = fileService.generateGene2GoFile(query, totalAnnotations, limit);
					break;
				case JSON:
					sb = fileService.generateJsonFileWithPageAndRow(query, totalAnnotations, start, rows);
					break;
			}

			String extension = format;

			InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			IOUtils.copy(in, httpServletResponse.getOutputStream());
			// Set response header and content
			httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + extension);
			httpServletResponse.setContentLength(sb.length());
			httpServletResponse.flushBuffer();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}


	/**
	 * Generate a file with the annotations results
//	 * @param query Query to run
	 */
//	public void downloadAnnotationsTotalInternal( String query,HttpServletResponse httpServletResponse){
//
//		// Get total number annotations
//		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);
//
//		// Check file format
//		StringBuffer sb = null;
//		try {
//			sb = fileService.generateJsonFileWithTotalAnnotations(totalAnnotations);
//
//			writeOutJsonResponse(httpServletResponse, sb);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error(e.getMessage());
//		}
//	}

	@Override
	public void downloadTerm(String termId, HttpServletResponse httpServletResponse){
		StringBuffer sb = null;
		try {
			GOTerm term = goTermService.retrieveTerm(termId);

			// Calculate extra information
			List<TermRelation> childTermsRelations = termUtil.calculateChildTerms(termId);

			//co-occurring
			TreeSet<COOccurrenceStatsTerm> allCoOccurrenceStatsTerms =
					(TreeSet)miscellaneousService.allCOOccurrenceStatistics(termId.replaceAll("GO:", ""));
			TreeSet<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics =
					(TreeSet)miscellaneousService.nonIEACOOccurrenceStatistics(termId.replaceAll("GO:", ""));

			// All stats
			List<COOccurrenceStatsTerm> allStats = new ArrayList<>();
			allStats.addAll(allCoOccurrenceStatsTerms);
			allStats = getFirstOnes(allStats);
			processStats(allStats);

			// Non-IEA stats
			List<COOccurrenceStatsTerm> nonIEAStats = new ArrayList<>();
			nonIEAStats.addAll(nonIEACOOccurrenceStatistics);
			nonIEAStats = getFirstOnes(nonIEAStats);
			processStats(nonIEAStats);


			sb = fileService.generateJsonFileForTerm(term, childTermsRelations, allStats, nonIEAStats);

			writeOutJsonResponse(httpServletResponse, sb);

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}


	public void downloadOntologyGraph(String termId, HttpServletResponse httpServletResponse){

		StringBuffer sb = null;
		try {

			sb = fileService.generateJsonFileForOntologyTerm(termId);

			writeOutJsonResponse(httpServletResponse, sb);

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}


	/**
	 * Get first stats values (by default 100)
	 * @param stats Stats values
	 */
	public List<COOccurrenceStatsTerm> getFirstOnes(List<COOccurrenceStatsTerm> stats){
		if (stats != null && stats.size() > 0) {
			int end = calculateEnd(stats.size());
			stats = new ArrayList<>(stats.subList(0, end));
		}
		return stats;
	}

	/**
	 * Calculate number term to display for co-occurring term stats
	 * @param size Size of stats
	 * @return Num terms to display
	 */
	private int calculateEnd(int size) {
		int NUM_VALUES = 100;
		int end = NUM_VALUES;
		if (size < NUM_VALUES) {
			end = size - 1;
		}
		return end;
	}

	/**
	 * Process stats values
	 * @param stats Stats to process
	 */
	private void processStats(List<COOccurrenceStatsTerm> stats){
		for (COOccurrenceStatsTerm coOccurrenceStatsTerm : stats) {
			String comparedId = "GO:" + coOccurrenceStatsTerm.getComparedTerm();
			coOccurrenceStatsTerm.setComparedTerm(comparedId);
			if (uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTerms().get(comparedId) != null) {
				coOccurrenceStatsTerm
						.setAspect(((GOTerm) uk.ac.ebi.quickgo.web.util.term.TermUtil
								.getGOTerms().get(comparedId)).getAspect().abbreviation);
				coOccurrenceStatsTerm
						.setName(uk.ac.ebi.quickgo.web.util.term.TermUtil
								.getGOTerms().get(comparedId).getName());
			}
		}
	}




	@Override
	public void downloadPredefinedSlims(HttpServletResponse httpServletResponse){

		// Calculate subsets counts for slimming
		List<Miscellaneous> subsetsCounts = new ArrayList<>();
		subsetsCounts = miscellaneousUtil.getSubsetCount(null);

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFileForPredefinedSlims(subsetsCounts);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadPredefinedSetTerms(HttpServletResponse httpServletResponse, String setName) {

		if (setName == null || setName.isEmpty()) {
			return;
		}

		List<TermJson> setTerms = new ArrayList<>();

		for (GenericTerm goTerm : terms.values()) {
			if (goTerm.getSubsetsNames().contains(setName)) {
				TermJson termJson = new TermJson();
				termJson.setTermId(goTerm.getId());
				termJson.setName(goTerm.getName());
				termJson.setAspectDescription(((GOTerm)goTerm).getAspectDescription());
				setTerms.add(termJson);
			}
		}

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFileForPredefinedSetTerms(setTerms);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}



	@Override
	public void downloadAnnotationUpdates(HttpServletResponse httpServletResponse) {

		List<FacetField.Count> assignedByCount = annotationService.getFacetFields("*:*", null,
				AnnotationField.ASSIGNEDBY.getValue(), 1000);

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(assignedByCount);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadGoTermHistory(HttpServletResponse httpServletResponse, String from, String to, String limit) {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = null, toDate = null;
		try {
			fromDate = formatter.parse(from);
			if (to.contains("NOW")) {
				toDate = new Date();
			} else {
				formatter.parse(to);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		List<GOTerm> historyChanges = goTermService.retrieveByHistoryDate(fromDate, toDate, Integer.valueOf(limit));

		List<AuditRecord> allChanges = new ArrayList<>();
		List<AuditRecord> termsRecords = new ArrayList<>();
		List<AuditRecord> definitionsRecords = new ArrayList<>();
		List<AuditRecord> relationsRecords = new ArrayList<>();
		List<AuditRecord> xrefRecords = new ArrayList<>();



		for(GOTerm term : historyChanges){
			allChanges.addAll(term.history.auditRecords);
			termsRecords.addAll(term.history.getHistoryTerms());
			definitionsRecords.addAll(term.history.getHistoryDefinitions());
			relationsRecords.addAll(term.history.getHistoryRelations());
			xrefRecords.addAll(term.history.getHistoryXRefs());
		}

		GoTermHistoryJson goTermHistoryJson = new GoTermHistoryJson();
		goTermHistoryJson.setFrom(from);
		goTermHistoryJson.setAllChanges(allChanges);
		goTermHistoryJson.setTermsRecords(termsRecords);
		goTermHistoryJson.setDefinitionRecords(definitionsRecords);
		goTermHistoryJson.setRelationsRecords(relationsRecords);
		goTermHistoryJson.setXrefRecords(xrefRecords);


		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(goTermHistoryJson);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}


	@Override
	public void downloadTaxonConstraints(HttpServletResponse httpServletResponse) {
		List<TaxonConstraint> taxonConstraints = TaxonConstraintsContent.getTaxonConstraints();
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(taxonConstraints);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadAnnotationBlacklist(HttpServletResponse httpServletResponse){

		AnnotationBlacklistJson annotationBlacklistJson = new AnnotationBlacklistJson();
		annotationBlacklistJson.setIEAReview(annotationBlackListContent.getIEAReview());
		annotationBlacklistJson.setBlackListNotQualified(annotationBlackListContent.getBlackListNotQualified());
		annotationBlacklistJson.setBlackListUniProtCaution(annotationBlackListContent.getBlackListUniProtCaution());
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(annotationBlacklistJson);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void getAnnotationPostProcessing(HttpServletResponse httpServletResponse) {
		AnnotationPostProcessingJson annotationPostProcessingJson = new AnnotationPostProcessingJson();
		annotationPostProcessingJson.setContent(annotationPostProcessingContent.getPostProcessingRules());
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(annotationPostProcessingJson);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void writeOutJsonResponse(HttpServletResponse httpServletResponse, StringBuffer sb) throws IOException {
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		IOUtils.copy(in, httpServletResponse.getOutputStream());

		// Set response header and content
		httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + "json");
		httpServletResponse.setContentLength(sb.length());
		httpServletResponse.flushBuffer();
	}



}
