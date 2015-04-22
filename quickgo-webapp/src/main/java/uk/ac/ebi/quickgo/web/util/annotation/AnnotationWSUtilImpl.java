package uk.ac.ebi.quickgo.web.util.annotation;

import java.awt.image.RenderedImage;
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
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.bean.annotation.AnnotationBean;
import uk.ac.ebi.quickgo.bean.statistics.StatisticsBean;
import uk.ac.ebi.quickgo.graphics.*;
import uk.ac.ebi.quickgo.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.go.GOEvidence2ECOMap;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;


import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.statistic.StatisticService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.util.term.TermUtil;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationBlackListContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationPostProcessingContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.TaxonConstraintsContent;
import uk.ac.ebi.quickgo.web.util.ChartService;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.web.util.WebUtils;
import uk.ac.ebi.quickgo.web.util.query.QueryProcessor;
import uk.ac.ebi.quickgo.web.util.stats.StatisticsCalculation;
import uk.ac.ebi.quickgo.web.util.url.URLsResolver;
import uk.ac.ebi.quickgo.webservice.model.*;

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

	@Autowired
	ChartService chartService;

	@Autowired
	StatisticService statisticService;

	@Autowired
	QueryProcessor queryProcessor;

	@Autowired
	SlimmingUtil slimmingUtil;

	@Autowired
	URLsResolver urLsResolver;

	// All go terms
	Map<String, GenericTerm> terms = uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTerms(); //todo this should be properly cached.

	private static final Logger logger = Logger.getLogger(AnnotationWSUtilImpl.class);

	//Static members acting as a cache...
	private static List<Miscellaneous> subsetsCounts;
	private static List<FacetField.Count> assignedByCount;
	private static List<TaxonConstraint> taxonConstraints;
	private static AnnotationBlacklistJson annotationBlacklistJson;
	private static AnnotationPostProcessingJson annotationPostProcessingJson;
	private static List<EvidenceTypeJson> evidenceTypesJson = new ArrayList<>();
	private static List<DBJson> withDBs = new ArrayList<>();
	private static List<DBJson> assignedByDBs = new ArrayList<>();


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
											int start, int rows, HttpServletResponse httpServletResponse, boolean isSlim){

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
					sb = fileService.generateJsonFileWithPageAndRow(query, totalAnnotations, start, rows, isSlim);
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


	/**
	 * // Calculate subsets counts for slimming
	 * @param httpServletResponse
	 */
	@Override
	public void downloadPredefinedSlims(HttpServletResponse httpServletResponse){

		if(subsetsCounts==null) {
			subsetsCounts = miscellaneousUtil.getSubsetCount(null);
		}

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

		if( assignedByCount==null) {
			assignedByCount = annotationService.getFacetFields("*:*", null, AnnotationField.ASSIGNEDBY.getValue(), 1000);
		}
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(assignedByCount);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	//Todo - cache  the history look up?
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
		if(taxonConstraints==null) {
			taxonConstraints = TaxonConstraintsContent.getTaxonConstraints();
		}
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

		if(annotationBlacklistJson==null) {
			 annotationBlacklistJson = new AnnotationBlacklistJson();
			annotationBlacklistJson.setIEAReview(annotationBlackListContent.getIEAReview());
			annotationBlacklistJson.setBlackListNotQualified(annotationBlackListContent.getBlackListNotQualified());
			annotationBlacklistJson.setBlackListUniProtCaution(annotationBlackListContent.getBlackListUniProtCaution());
		}
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

		if(annotationPostProcessingJson==null) {
			annotationPostProcessingJson = new AnnotationPostProcessingJson();
			annotationPostProcessingJson.setContent(annotationPostProcessingContent.getPostProcessingRules());
		}
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(annotationPostProcessingJson);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadEvidenceTypes(HttpServletResponse httpServletResponse) {

		if(evidenceTypesJson.isEmpty()) {

			Map<String, String> evidences = miscellaneousUtil.getEvidenceTypes();

			// Get corresponding ECO term
			for (String goEvidence : evidences.keySet()) {
				String ecoTerm = GOEvidence2ECOMap.find(goEvidence);
				EvidenceTypeJson evidenceTypeJson = new EvidenceTypeJson();
				evidenceTypeJson.setKey(ecoTerm);
				evidenceTypeJson.setValue(ecoTerm + " (" + goEvidence + ")\t" + evidences.get(goEvidence));
				evidenceTypesJson.add(evidenceTypeJson);
			}
		}


		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(evidenceTypesJson);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadWithDBs(HttpServletResponse httpServletResponse) {

		if(withDBs.isEmpty()) {
			for (Miscellaneous withDB : miscellaneousService.getWithDBs()) {

				List<FacetField.Count> annotations = annotationService.getFacetFields(
						AnnotationField.WITH.getValue() + ":" + withDB.getXrefAbbreviation() + "*",
						null,
						AnnotationField.WITH.getValue(), 1);

				if (annotations != null && !annotations.isEmpty()) {

					withDBs.add(new DBJson(withDB.getXrefAbbreviation() + "*",
							withDB.getXrefDatabase()));
				}
			}
		}

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(withDBs);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}


	@Override
	public void downloadAssignedDBs(HttpServletResponse httpServletResponse) {

		if (assignedByDBs.isEmpty()) {
			populateByFacetField(assignedByDBs, AnnotationField.ASSIGNEDBY.getValue());
		}

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(assignedByDBs);
			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadChartFullModel(HttpServletResponse httpServletResponse, String ids, String scope) {

		chartService.createChart(ids,scope);

		String src = ImageArchive.store(chartService.getGraphImage());

		ChartJson chartJson = new ChartJson();
		Collection<TermNode> ontTerms = chartService.getGraphImage().getOntologyTerms();
		for (Iterator<TermNode> iterator = ontTerms.iterator();iterator.hasNext();) {
			TermNode next = iterator.next();
			chartJson.addLayoutNode(chartJson.new LayoutNode(next.getId(),next.left(), next.right(), next.top(), next.bottom()));
		}
		chartJson.setLegendNodes(chartService.getGraphImage().legend);
		chartJson.setGraphImageSrc(src);
		chartJson.setGraphImageWidth(chartService.getRenderableImage().getWidth());
		chartJson.setGraphImageHeight(chartService.getRenderableImage().getHeight());
		chartJson.setTermsToDisplay(chartService.getTermsToDisplay());



		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(chartJson);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadAnnotationOntologyGraph(HttpServletResponse httpServletResponse, String termsIds, String relations, String requestType) {
		// Basket terms
		//Map<String, String> basketTerms = new HashMap<String, String>();
		String[] basketTerms;

		List<String> termsIdsList = new ArrayList<>();
//		if(requestType.equals("allBasketTerms")){
//			//Nothing to do - we already have our list with commas delimiting
//
//		}
//		else if (requestType.equals("slimming")){
//			Map<String, String> activeSlimmingGraphTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, session);
//			termsIdsList = new ArrayList<>(activeSlimmingGraphTerms.keySet());
//			if(termsIdsList.isEmpty()){// No term selected
//				model.addAttribute("graphImageSrc", null);
//				return redirect(request);
//			}
//			termsIds = StringUtils.arrayToDelimitedString(termsIdsList.toArray(), ",");
//		} else {


		termsIdsList = Arrays.asList(termsIds.split(","));

		// Get corresponding ontology
		GenericOntology genericOntology = uk.ac.ebi.quickgo.web.util.term.TermUtil.getOntology(termsIdsList.get(0));

		// Create graph image
		GraphImage graphImage = createRenderableImage(genericOntology, termsIds);

		ChartJson chartJson = new ChartJson();

		if(termsIdsList.size() == 1){//Just one term, set id as the graph title
			String id = termsIdsList.get(0);
			String name = genericOntology.getTerm(id).getName();
			String title = id;
			if (id.contains(GOTerm.GO)) {// Add name for GO terms
				title = title + " " + name;
			}
			chartJson.setTermGraphTitle(title);
		}

		//model.addAttribute("termsNodes", graphImage.getOntologyTerms());
		Collection<TermNode> ontTerms =  graphImage.getOntologyTerms();
		for (Iterator<TermNode> iterator = ontTerms.iterator();iterator.hasNext();) {
			TermNode next = iterator.next();
			chartJson.addLayoutNode(chartJson.new LayoutNode(next.getId(),next.left(), next.right(), next.top(), next.bottom()));
		}


		chartJson.setLegendNodes(graphImage.legend);
		chartJson.setGraphImageSrc(ImageArchive.store(graphImage));

		RenderedImage renderableImage = graphImage.render();
		chartJson.setGraphImageWidth(renderableImage.getWidth());
		chartJson.setGraphImageHeight(renderableImage.getHeight());

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(chartJson);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}


	/**
	 *  @param query
	 * @param advancedFilter
	 * @param solrQuery
	 */
	@Override
	public void downloadStatistics(HttpServletResponse httpServletResponse, String query, String advancedFilter, String solrQuery) {

		// Calculate stats
		StatisticsBean 		statisticsBean = new StatisticsBean();
		StatisticsCalculation statisticsCalculation = new StatisticsCalculation(statisticsBean, solrQuery);
		statisticsCalculation.setStatisticService(statisticService);
		statisticsCalculation.start();

//		if(statisticsCalculation != null && !statisticsCalculation.getQuery().equals(currentQuery)){
//			statisticsCalculation.interrupt();
//			createStatsThread(currentQuery);
//		} else if(statisticsCalculation == null){
//			createStatsThread(currentQuery);
//		}

		try {
			statisticsCalculation.join();
		} catch (InterruptedException e) {
			System.out.println("Statistics calculation interrupted");
			throw new RuntimeException(e);
		}

		StatisticsJson statisticsJson = new StatisticsJson();
		statisticsJson.setStatsBean(statisticsBean);

		//todo get this from other methods
		// Calculate total number annotations
		long totalNumberAnnotations = annotationService.getTotalNumberAnnotations(solrQuery);
		statisticsJson.setTotalNumberAnnotations(totalNumberAnnotations);

		long totalNumberProteins = annotationService.getTotalNumberProteins(solrQuery);
		statisticsJson.setTotalNumberProteins(totalNumberProteins);


		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(statisticsJson);

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	@Override
	public void downloadOntologyList(HttpServletResponse httpServletResponse, String ontology) {
		GOTerm.EGOAspect aspect = GOTerm.EGOAspect.valueOf(ontology);
		Map<String,String> terms = uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTermsByOntology(aspect);
		OntologyListJson ontologyListJson = new OntologyListJson();
		ontologyListJson.setOntology(aspect.description);
		ontologyListJson.setTerms(terms);
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(ontologyListJson);

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

	private void populateByFacetField(List<DBJson> dbJsons, String field){
		List<FacetField.Count> counts = annotationService.getFacetFields("*:*", null, field, 1000);
		for (FacetField.Count count : counts) {
			String dbAbbreviation = count.getName();
			String dbDescription = miscellaneousUtil.getDBInformation(ClientUtils.escapeQueryChars(dbAbbreviation)).getXrefDatabase();
			DBJson dbJson = new DBJson(dbAbbreviation, dbDescription);
			dbJsons.add(dbJson);
		}
	}

	/**
	 * Generates ancestors graph for a list of terms
	 *
	 * @param genericOntology
	 * @param termsIds List of terms to calculate the graph for
	 * @return Graph image
	 */
	public GraphImage createRenderableImage(GenericOntology genericOntology, String termsIds){
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
	 * Given a list of terms ids, return associated names
	 * @param ids Terms ids
	 * @return Terms names
	 */
	private Map<String, String> calculateNames(List<String> ids){
		Map<String, String> idName = new HashMap<String, String>();
		for(String id : ids){
			if(id.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}")){// Valid GO id
				idName.put(id, terms.get(id).getName());
			}
		}
		return idName;
	}

}
