package uk.ac.ebi.quickgo.controller;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.security.auth.callback.Callback;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.go.AnnotationBlacklist.BlacklistEntryMinimal;
import uk.ac.ebi.quickgo.ontology.go.AnnotationExtensionRelations;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.render.Format;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.service.geneproduct.GeneProductService;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.geneproduct.enums.GeneProductField;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationBlackListContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.TaxonConstraintsContent;
import uk.ac.ebi.quickgo.web.util.ChartService;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationColumn;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationWSUtil;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.annotation.SlimmingUtil;
import uk.ac.ebi.quickgo.web.util.query.mapping.FilterRequestToSolr;
import uk.ac.ebi.quickgo.web.util.query.QueryProcessor;
import uk.ac.ebi.quickgo.web.util.query.QueryTo;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import uk.ac.ebi.quickgo.web.util.url.URLsResolver;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;
import uk.ac.ebi.quickgo.webservice.model.SearchFullResultsJson;

/**
 * REST services controller
 * @author cbonill
 *
 */

@Controller
@RequestMapping("/ws")
public class WebServiceController {

	@Autowired
	TermService termService;

	@Autowired
	GeneProductService geneProductService;

	@Autowired
	MiscellaneousUtil miscellaneousUtil;

	@Autowired
	MiscellaneousService miscellaneousService;

	@Autowired
	ChartService chartService;

	@Autowired
	QueryProcessor queryProcessor;

	@Autowired
	AnnotationWSUtil annotationWSUtil;

	@Autowired
	AnnotationBlackListContent annotationBlackListContent;

	@Autowired
	AnnotationService annotationService;

	@Autowired
	SlimmingUtil slimmingUtil;

	@Autowired
	URLsResolver urLsResolver;

	@Autowired
	FileService fileService;

	private static final int LIMIT = 1000;

	/**
	 * Lookup web service
	 *
	 * @param format              Response format
	 * @param id                  Id to lookup
	 * @param scope               Scope of the searched term
	 * @param httpServletResponse
	 * @throws IOException
	 */
	@RequestMapping("/lookup")
	public void lookup(
			@RequestParam(value = "format", required = false, defaultValue = "json") String format,
			@RequestParam(value = "id", required = true, defaultValue = "") String id,
			@RequestParam(value = "scope", required = false, defaultValue = "go") String scope,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

		String callback = httpServletRequest.getParameter("callback");// Protein2GO and other internal tools make requests using this parameter
		id = id.toUpperCase();
		Scope enumScope = Scope.valueOf(scope.trim().toUpperCase());
		Format enumFormat = Format.valueOf(format.trim().toUpperCase());

		switch (enumScope) {
			case COMPLEX:
				id = "\"" + id + "\""; //Put id between " " to escape '-' character
			case PROTEIN:
				lookupProtein(id, enumScope, enumFormat, httpServletResponse, callback);
				break;
			case ECO:
			case GO:
				lookupTerm(id, enumScope, enumFormat, httpServletResponse, callback);
				break;
			default:
				break;
		}
	}

	/**
	 * Search web service
	 *
	 * @param format              Response format
	 * @param query               Text to search
	 * @param scope               Type of data searching for
	 * @param limit               Max number of results of each type of data to return
	 * @param httpServletResponse Servlet response
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@RequestMapping("/search")
	public void search(
			@RequestParam(value = "format", required = false, defaultValue = "json") String format,
			@RequestParam(value = "query", required = true, defaultValue = "apoptotic") String query,
			@RequestParam(value = "scope", required = false, defaultValue = "go") String scope,
			@RequestParam(value = "limit", required = false, defaultValue = "10") String limit,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, SolrServerException {

		String callback = httpServletRequest.getParameter("callback");// Protein2GO and other internal tools make requests using this parameter
		List<Scope> scopes = new ArrayList<>();
		if (scope.contains(",")) {
			String[] scopesString = scope.split(",");
			for (String scopeString : scopesString) {
				Scope enumScope = Scope.valueOf(scopeString.trim().toUpperCase());
				scopes.add(enumScope);
			}
		} else {// Just 1
			scopes.add(Scope.valueOf(scope.trim().toUpperCase()));
		}

		Format enumFormat = Format.valueOf(format.trim().toUpperCase());
		int limitValue = Integer.valueOf(limit);

		Map<String, List<Map<String, Object>>> serialised = new HashMap<>();

		for (Scope enumScope : scopes) {
			switch (enumScope) {
				case COMPLEX:
				case PROTEIN:
					Map<String, List<Map<String, Object>>> proteins = new HashMap<>();
					List<Map<String, Object>> proteinsSerialised = searchProtein(query, enumScope, limitValue, enumFormat, httpServletResponse);
					proteins.put(enumScope.value, proteinsSerialised);
					serialised.putAll(proteins);
					break;
				case ECO:
					Map<String, List<Map<String, Object>>> ecoTerms = new HashMap<>();
					List<Map<String, Object>> ecoTermsSerialised = searchTerm(query, TermField.ID.getValue() + ":" + "ECO*", limitValue, enumScope, enumFormat, httpServletResponse, callback);
					ecoTerms.put("eco", ecoTermsSerialised);
					serialised.putAll(ecoTerms);
					break;
				case GO:
					Map<String, List<Map<String, Object>>> goTerms = new HashMap<>();
					List<Map<String, Object>> goTermsSerialised = searchTerm(query, TermField.ID.getValue() + ":" + "GO*", limitValue, enumScope, enumFormat, httpServletResponse, callback);
					goTerms.put("go", goTermsSerialised);
					serialised.putAll(goTerms);
					break;
				default:
					break;
			}
		}
		Gson gson = new Gson();
		Type listOfserialisedElementsType = new TypeToken<Map<String, List<Map<String, Object>>>>() {
		}.getType();
		String json = gson.toJson(serialised, listOfserialisedElementsType);

		httpServletResponse.setContentType("application/json");
		httpServletResponse.getWriter().write(generateContentResponse(callback, json));
	}


	@RequestMapping(value = "/searchfull", method = { RequestMethod.POST, RequestMethod.GET })
	public void searchfull(
//			@RequestParam(value = "query", defaultValue="", required=false) String query,
			@RequestParam(value = "text", defaultValue="", required=false) String text,
			@RequestParam(value = "isProtein", defaultValue="false", required=false) String isProtein,
			@RequestParam(value = "viewBy", defaultValue="", required=false) String viewBy,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "rows", defaultValue = "25") int rows,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException,
			SolrServerException {

		String query = "\"text\":"+ "\"" + text + "\"";
		System.out.println("Search Full from WebService Controller " + "query:" + query + "<<isProtein:" + isProtein + "<<viewBy " + viewBy);


		List<Object> searchResults = new ArrayList<>();

		SearchFullResultsJson resultsJson = new SearchFullResultsJson();

		if(!query.isEmpty()){
			AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
			queryProcessor.processQuery(query, new AnnotationParameters(), appliedFilterSet, false);

			if(isProtein.equals("true")){
//				session.setAttribute("searchedText", appliedFilterSet.getParameters().get(AnnotationField.DBOBJECTID.getValue()));
//				session.setAttribute("appliedFilters", appliedFilterSet);
//				return new ModelAndView("redirect:" + View.ANNOTATIONS_PATH);

			} else {

				// Text
//				Properties data = gson.fromJson(query, Properties.class);
//				String text = data.getProperty("text");

				// Remove leading and trailing white spaces from query
//				text = StringUtils.trimLeadingWhitespace(text);
//				text = StringUtils.trimTrailingWhitespace(text);

//				String previousQuery = (String)session.getAttribute("searchedText");
//				if(text.equals(previousQuery)){// Query hasn't changed. No need to recalculate all the counts and search results
//					sameQuery = true;
//				}

//				session.setAttribute("searchedText", text);

				long gpNumberResults = 0, goTotalResults = 0,
						bpGoNumberResults = 0, mfGoNumberResults = 0,
						ccGoNumberResults = 0,ecoTotalResults = 0,
						expEcoTotalResults = 0,automaticEcoTotalResults = 0,
						evidenceEcoResults = 0;

				String expEcofilterQuery = "", automaticEcofilterQuery = "";
				String ecoFilterQuery = TermField.ID.getValue() + ":" + ECOTerm.ECO.toString() + "*" + " AND " +
						TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();

//				if(!sameQuery){

				// Calculate counts

					// Calculate number of results of each type

					// #Gene Products
					gpNumberResults = geneProductService.getTotalNumberHighlightResults("*" + text  + "*", null);
					resultsJson.setGpNumberResults(gpNumberResults);

					// #GO terms
					goTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
					resultsJson.setGoNumberResults(goTotalResults);

					String textToSearch = "*" + text + "*";

					// #BP GO terms
					bpGoNumberResults = calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.P.text, "bpGoNumberResults");
					resultsJson.setBiologicalProcessNumberResults(bpGoNumberResults);

					// #MF GO terms
					mfGoNumberResults = calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.F.text, "mfGoNumberResults");
					resultsJson.setMolecularFunctionNumberResults(mfGoNumberResults);

					// #CC GO terms
					ccGoNumberResults = calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.C.text, "ccGoNumberResults");
					resultsJson.setCellularComponentNumberResults(ccGoNumberResults);

					// #ECO terms
					ecoTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", ecoFilterQuery + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
					resultsJson.setEcoTermsNumberResults(ecoTotalResults);

					// Retrieve values
					List<GenericTerm> ecoTerms = highlightedTerms("*" + text  + "*", ecoFilterQuery, 1, 60000);

					// Remove null elements
					List<GenericTerm> noNullTerms = new ArrayList<>();
					for(GenericTerm genericTerm : ecoTerms){
						if(genericTerm != null && genericTerm.getId() != null){
							noNullTerms.add(genericTerm);
						}
					}

					ecoTerms = noNullTerms;

					// #Experimental ECO terms
					String expEcoValues = StringUtils.arrayToDelimitedString(getAllManualECOCodes(ecoTerms).toArray(), " OR ");

				if(!expEcoValues.isEmpty()){
						expEcofilterQuery = TermField.ID.getValue() + ":(" + expEcoValues.replaceAll(":","*") + ")" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();;
						//session.setAttribute("expEcofilterQuery", expEcofilterQuery);
						expEcoTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", expEcofilterQuery);
					}
					resultsJson.setManualEcoTotalResults(expEcoTotalResults);

					// #Automatic ECO terms
					String automaticEcoValues = StringUtils.arrayToDelimitedString(getAllAutomaticECOCodes(ecoTerms).toArray(), " OR ");
					if(!automaticEcoValues.isEmpty()){
						automaticEcofilterQuery =  TermField.ID.getValue() + ":(" + automaticEcoValues.replaceAll(":","*") + ")" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();;
						//session.setAttribute("automaticEcofilterQuery", automaticEcofilterQuery);
						automaticEcoTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", automaticEcofilterQuery);

					}
					resultsJson.setAutomaticEcoTotalResults( automaticEcoTotalResults);

					// #Evidence ECO Terms
					evidenceEcoResults = ecoTotalResults - (expEcoTotalResults + automaticEcoTotalResults);
					resultsJson.setEvidenceEcoTotalResults(evidenceEcoResults);
//				} else {
				// Same query. Get counts from session
//					gpNumberResults = (long)session.getAttribute("gpNumberResults");
//					goTotalResults = (long)session.getAttribute("goNumberResults");
//					bpGoNumberResults = (long)session.getAttribute("bpGoNumberResults");
//					mfGoNumberResults = (long)session.getAttribute("mfGoNumberResults");
//					ccGoNumberResults = (long)session.getAttribute("ccGoNumberResults");
//					ecoTotalResults = (long)session.getAttribute("ecoNumberResults");
//					expEcoTotalResults = (long)session.getAttribute("expEcoTotalResults");
//					automaticEcoTotalResults = (long)session.getAttribute("automaticEcoTotalResults");
//					evidenceEcoResults = (long)session.getAttribute("evidenceEcoTotalResults");
//				}

				// Set default view
				if (viewBy.isEmpty()) {
					viewBy = ViewBy.ENTITY.getValue();
					if (gpNumberResults > 0){
						viewBy = ViewBy.ENTITY.getValue();
					} else if (goTotalResults > 0) {
						viewBy = ViewBy.GOID.getValue();
					} else if (ecoTotalResults > 0) {
						viewBy = ViewBy.ECOID.getValue();
					}
				}

				long totalResults = 0;

				if (viewBy.equalsIgnoreCase(ViewBy.ENTITY.getValue())){
					 totalResults = gpNumberResults;
					List<GeneProduct> gpResults = geneProductService.highlight("*" + text  + "*", null, (page-1)*rows, rows);
					searchResults.addAll(gpResults);

				} else if (viewBy.equalsIgnoreCase(ViewBy.GOID.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.ECOID.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.BP.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.MF.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.CC.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.ECOAUTOMATIC.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.ECOMANUAL.getValue())
						|| viewBy.equalsIgnoreCase(ViewBy.EVIDENCEECO.getValue())) {

					String filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();
					totalResults = goTotalResults;
					if(viewBy.equalsIgnoreCase(ViewBy.BP.getValue())){
						filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
								+ TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() + " AND "
								+ SolrTerm.SolrTermDocumentType.ONTOLOGY
								.getValue() + ":"
								+ GOTerm.EGOAspect.P.text;
						totalResults = bpGoNumberResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.MF.getValue())){
						filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
								+ TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() + " AND "
								+ SolrTerm.SolrTermDocumentType.ONTOLOGY
								.getValue() + ":"
								+ GOTerm.EGOAspect.F.text;
						totalResults = mfGoNumberResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.CC.getValue())){
						filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*  AND "
								+ TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() + " AND "
								+ SolrTerm.SolrTermDocumentType.ONTOLOGY
								.getValue() + ":"
								+ GOTerm.EGOAspect.C.text;
						totalResults = ccGoNumberResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.ECOID.getValue())){
						filterQuery = ecoFilterQuery;
						totalResults = ecoTotalResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.ECOMANUAL.getValue())){
						//filterQuery = (String)session.getAttribute("expEcofilterQuery");
						totalResults = expEcoTotalResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.ECOAUTOMATIC.getValue())){
						//filterQuery = (String)session.getAttribute("automaticEcofilterQuery");
						totalResults = automaticEcoTotalResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.EVIDENCEECO.getValue())){
						String evidencefilterQuery = ecoFilterQuery;

//						if(session.getAttribute("expEcofilterQuery") != null && !((String)session.getAttribute("expEcofilterQuery")).isEmpty()){
//							evidencefilterQuery = evidencefilterQuery + " AND NOT " + (String)session.getAttribute("expEcofilterQuery");
//						}
//
//						if(session.getAttribute("automaticEcofilterQuery") != null && !((String)session.getAttribute("automaticEcofilterQuery")).isEmpty()){
//							evidencefilterQuery = evidencefilterQuery + " AND NOT " + (String)session.getAttribute("automaticEcofilterQuery");
//						}

						filterQuery = evidencefilterQuery;
						totalResults = evidenceEcoResults;
					}
					List<GenericTerm> termsResults = highlightedTerms("*" + text  + "*", filterQuery, page, rows);
					searchResults.addAll(termsResults);
				}



				// Set results in session
				resultsJson.setSearchResults(searchResults);

				// Set total number results
				resultsJson.setTotalNumberResults(totalResults);

				// Set current page
				resultsJson.setPage(page);

				// View by
				resultsJson.setViewBy(viewBy);

			}
		}

		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(	resultsJson);
			//sb.append(new Gson().toJson(annotationExtensionRelations.toGraph().toString()));

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			//logger.error(e.getMessage());
		}
	}






	/**
	 * Validate service
	 *
	 * @param format              Response format
	 * @param id                  Id to validate
	 * @param candidate           Candidates to validate with
	 * @param type                Type of validation
	 * @param action              Action
	 * @param httpServletResponse
	 * @throws Exception
	 */
	@RequestMapping("/validate")
	public void validate(
			@RequestParam(value = "format", required = false, defaultValue = "json") String format,
			@RequestParam(value = "id", required = true, defaultValue = "") String id,
			@RequestParam(value = "candidate", required = true, defaultValue = "") String candidate,
			@RequestParam(value = "type", required = false, defaultValue = "ann_ext") String type,
			@RequestParam(value = "action", required = false, defaultValue = "get_relations") String action,
			@RequestParam(value = "taxon_id", required = false, defaultValue = "9606") String taxonId,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		String callback = httpServletRequest.getParameter("callback");// Protein2GO and other internal tools make requests using this parameter
		ValidateType validateType = ValidateType.valueOf(type.trim().toUpperCase());
		ValidateAction validateAction = ValidateAction.valueOf(action.trim().toUpperCase());

		String responseFormat = "application/json";
		Format responseFormatEnum = Format.valueOf(format.trim().toUpperCase());
		switch (responseFormatEnum) {
			case XML:
				responseFormat = "text/xml";
				break;
		}
		httpServletResponse.setContentType(responseFormat);
		switch (validateType) {
			case ANN_EXT:
				AnnotationExtensionRelations annotationExtensionRelations = new AnnotationExtensionRelations(TermUtil.getGOOntology(), TermUtil.getSourceFiles().goSourceFiles);
				switch (validateAction) {
					case GET_RELATIONS:
						Map<String, Object> relations = annotationExtensionRelations.forDomain(id);
						httpServletResponse.getWriter().write(generateContentResponse(callback, new Gson().toJson(relations)));
						break;
					case VALIDATE_RELATION:
						Map<String, String> validation = new HashMap<>();
						validation.put("valid", "true");
						try {
							annotationExtensionRelations.validate(id, candidate);
						} catch (Exception e) {
							validation.clear();
							validation.put("valid", "false");
							validation.put("message", e.getMessage());
						}
						if (responseFormatEnum.name() == Format.XML.name()) {//XML response
							String xmlResponse = "<status ";
							if (validation.get("message") != null) {
								xmlResponse = xmlResponse + "message=\"" + validation.get("message") + "\" ";
							}
							xmlResponse = xmlResponse + "valid=\"" + validation.get("valid") + "\"/>";
							httpServletResponse.getWriter().write(xmlResponse);
						} else {//JSON
							httpServletResponse.getWriter().write(generateContentResponse(callback, new Gson().toJson(validation)));
						}
						break;
				}
				break;
			case TAXON:
				switch (validateAction) {
					case GET_BLACKLIST:
						List<BlacklistEntryMinimal> taxonBlackList = annotationBlackListContent.getTaxonBlackList(Integer.valueOf(taxonId));
						httpServletResponse.getWriter().write(generateContentResponse(callback, new Gson().toJson(taxonBlackList)));
						break;
					case GET_CONSTRAINTS:
						httpServletResponse.getWriter().write(generateContentResponse(callback, new Gson().toJson(TaxonConstraintsContent.getAllTaxonConstraintsSerialised())));
						break;
				}
				break;
		}
	}

	/**
	 * Statistics service
	 *
	 * @param format              Response format
	 * @param id                  GO Term id to calculate the statistics for
	 * @param threshold           Minimum threshold
	 * @param limit               Max number of statistics to retrieve
	 * @param httpServletResponse
	 * @throws IOException
	 */
	@RequestMapping("/statistics")
	public void statistics(
			@RequestParam(value = "format", required = false, defaultValue = "json") String format,
			@RequestParam(value = "id", required = true, defaultValue = "") String id,
			@RequestParam(value = "threshold", required = true, defaultValue = "5") String threshold,
			@RequestParam(value = "limit", required = false, defaultValue = "10") String limit,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

		String callback = httpServletRequest.getParameter("callback");// Protein2GO and other internal tools make requests using this parameter
		Set<COOccurrenceStatsTerm> coOccurrenceStatsTerms = miscellaneousService.nonIEACOOccurrenceStatistics(id.replaceAll("GO:", ""));
		List<Map<String, String>> termsStats = new ArrayList<>();
		for (COOccurrenceStatsTerm coOccurrenceStatsTerm : coOccurrenceStatsTerms) {
			if (coOccurrenceStatsTerm.getProbabilitySimilarityRatio() >= Float.valueOf(threshold)) {
				Map<String, String> termStat = new HashMap<>();
				termStat.put("id", "GO:" + coOccurrenceStatsTerm.getComparedTerm());
				termStat.put("name", TermUtil.getGOTerms().get("GO:" + coOccurrenceStatsTerm.getComparedTerm()).getName());
				termStat.put("aspect", ((GOTerm) TermUtil.getGOTerms().get("GO:" + coOccurrenceStatsTerm.getComparedTerm())).getAspectDescription());
				termStat.put("s", String.valueOf(coOccurrenceStatsTerm.getProbabilitySimilarityRatio()));
				termsStats.add(termStat);
			}
		}
		if (termsStats.size() > Integer.valueOf(limit)) {
			termsStats = termsStats.subList(0, Integer.valueOf(limit));
		}
		Map<String, List<Map<String, String>>> results = new HashMap<>();
		results.put("co_occurring_terms", termsStats);
		httpServletResponse.setContentType("application/json");
		httpServletResponse.getWriter().write(generateContentResponse(callback, new Gson().toJson(results)));
	}

	/**
	 * Generate the ancestors chart for a list of ginved ids
	 *
	 * @param ids Ids to display on the chart
	 */
	@RequestMapping("/chart")
	public void statistics(
			@RequestParam(value = "ids", required = true, defaultValue = "") String ids,
			@RequestParam(value = "scope", required = false, defaultValue = "") String scope,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

		chartService.createChart(ids, scope);

		ImageWriter iw = ImageIO.getImageWritersByFormatName("png").next();
		ImageOutputStream ios = new MemoryCacheImageOutputStream(httpServletResponse.getOutputStream());
		iw.setOutput(ios);
		iw.write(chartService.getRenderableImage());
	}

	/**
	 * Annotation web service
	 */
	@RequestMapping("/annotation")
	public void annotation(@RequestParam(value = "format", required = false, defaultValue = "gpad") String format,
						   @RequestParam(value = "limit", required = false, defaultValue = "1000") String limit,
						   @RequestParam(value = "gz", required = false, defaultValue = "") String gz,
						   @RequestParam(value = "go_id", required = false, defaultValue = "") String goid,
						   @RequestParam(value = "aspect", required = false, defaultValue = "") String aspect,
						   @RequestParam(value = "enable_slim", required = false, defaultValue = "false") String enableSlim,
						   @RequestParam(value = "go_relations", required = false, defaultValue = "") String goRelations,
						   @RequestParam(value = "evidence", required = false, defaultValue = "") String evidence,
						   @RequestParam(value = "source", required = false, defaultValue = "") String source,
						   @RequestParam(value = "reference", required = false, defaultValue = "") String ref,
						   @RequestParam(value = "with", required = false, defaultValue = "") String with,
						   @RequestParam(value = "taxonomy_id", required = false, defaultValue = "") String tax,
						   @RequestParam(value = "protein", required = false, defaultValue = "") String protein,
						   @RequestParam(value = "qualifier", required = false, defaultValue = "") String qualifier,
						   @RequestParam(value = "database", required = false, defaultValue = "") String db,
						   @RequestParam(value = "columns", required = false, defaultValue = "") String cols,
						   HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {

		String query = "";
		boolean gzip = false;

		if (!format.trim().isEmpty()) {
			query = query + "\"format\"" + ":\"" + format + "\",\"";
		}
		if (!limit.trim().isEmpty()) {
			query = query + "\"limit\"" + ":\"" + limit + "\",\"";
		}
		if (httpServletRequest.getParameter("gz") != null) {
			gzip = true;
		}
		if (!goid.trim().isEmpty()) {
			if (!enableSlim.trim().isEmpty() && Boolean.valueOf(enableSlim) == true) {
				if (!goRelations.trim().isEmpty()) {
					switch (goRelations) {
						case "=":
							query = query + "\"" + "\"goid\"" + "\"" + ":\"" + goid + "\",\"";
						case "I":
							query = query + "\"" + AnnotationField.ANCESTORSI.getValue() + "\"" + ":\"" + goid + "\",\"";
							break;
						case "?":
						case "POI":
						case "IPO":
							query = query + "\"" + AnnotationField.ANCESTORSIPO.getValue() + "\"" + ":\"" + goid + "\",\"";
							break;
						case "RPOI":
						case "IRPO":
						case "POIR":
						case "PORI":
						case "IPOR":
							query = query + "\"" + AnnotationField.ANCESTORSIPOR.getValue() + "\"" + ":\"" + goid + "\",\"";
							break;
					}
				}
			} else {
				query = query + "\"" + AnnotationField.ANCESTORSIPO.getValue() + "\"" + ":\"" + goid + "\",\"";
			}

		}
		if (!aspect.trim().isEmpty()) {
			query = query + "\"aspect\"" + ":\"" + aspect + "\",\"";
		}
		if (!evidence.trim().isEmpty()) {
			query = query + "\"evidence\"" + ":\"" + evidence + "\",\"";
		}
		if (!source.trim().isEmpty()) {
			query = query + "\"source\"" + ":\"" + source + "\",\"";
		}
		if (!ref.trim().isEmpty()) {
			query = query + "\"ref\"" + ":\"" + ref + "\",\"";
		}
		if (!with.trim().isEmpty()) {
			query = query + "\"with\"" + ":\"" + with + "\",\"";
		}
		if (!tax.trim().isEmpty()) {
			query = query + "\"tax\"" + ":\"" + tax + "\",\"";
		}
		if (!protein.trim().isEmpty()) {
			query = query + "\"protein\"" + ":\"" + protein + "\",\"";
		}
		if (!qualifier.trim().isEmpty()) {
			query = query + "\"qualifier\"" + ":\"" + qualifier + "\",\"";
		}
		if (!db.trim().isEmpty()) {
			query = query + "\"db\"" + ":\"" + db + "\",\"";
		}

		AnnotationParameters annotationParameters = new AnnotationParameters();

		/**
		 * new
		 */

		String solrQuery = annotationParameters.toSolrQuery();

		// Get columns to display
		AnnotationColumn[] columns = {AnnotationColumn.DATABASE, AnnotationColumn.PROTEIN, AnnotationColumn.GOID};
		if (format.equals(FileService.FILE_FORMAT.TSV.getValue()) && (cols != null && !cols.trim().isEmpty())) {
			columns = annotationWSUtil.mapColumns(cols);
		}

		// Download file
		annotationWSUtil.downloadAnnotations(format, gzip, solrQuery, columns, Integer.valueOf(limit), httpServletResponse);
	}


	/**
	 * USE THIS ONE
	 * @param httpServletResponse
	 * @param query
	 * @param limit
	 * @param page
	 * @param rows
	 * @param cols
	 * @param advancedFilter
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="/annotationfiltered", method = {RequestMethod.GET})
	public void annotationListWithFilter(
			HttpServletResponse httpServletResponse,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "limit", required = false, defaultValue = "1000") String limit,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "rows", defaultValue = "25") int rows,
			@RequestParam(value = "cols", defaultValue = "") String cols,
			@RequestParam(value = "advancedFilter", defaultValue = "false") String advancedFilter,
			@RequestParam(value = "slim", required = false, defaultValue = "false") String slim)
			throws UnsupportedEncodingException {

		System.out.println("The query passed to the Webservice controller is " + query);
		System.out.println("The value of slim passed to the Webservice controller is " + slim);
		//query = "\"goID\":\"GO:0033014\",\"ancestorsIPO\":\"ancestorsIPO\",";
		//System.out.println("The query hardcoded is " + query);
		AnnotationParameters annotationParameters = createAnnotationParameters(query, advancedFilter);
		String solrQuery = annotationParameters.toSolrQuery();
		System.out.println("Solr query is :" + solrQuery);

		//Do slimming if required
		ITermContainer slimTermSet = null;
		Boolean slimmingRequired = Boolean.parseBoolean(slim);
		if(slimmingRequired) {
			slimTermSet = createSlimTermSet(annotationParameters);
		}


		//todo should we be using the annotation service?
		AnnotationColumn[] columns = new AnnotationColumn[0];	//ignored
		annotationWSUtil.downloadAnnotationsInternal(solrQuery, columns, Integer.valueOf(limit),
				(page - 1) * rows, rows, httpServletResponse, slimmingRequired, slimTermSet);
	}


	static class FormData{
		public String myString;
	}

	/**
	 */
	@RequestMapping(value="/annotationPostNewNames", method = {RequestMethod.POST})
	@ResponseBody public void  postQueryRequest(@RequestBody FormData formData){


//			HttpServletRequest request,
//			HttpServletResponse httpServletResponse,
//			@RequestParam(value = "filterRequest", required = false) Map filterRequest,
//			@RequestParam(value = "myObject", required = false) Object myObject,
//			@RequestParam(value = "myString", required = false) String myString,
//			//@RequestParam(value = "myjson", required = false) Object myjson,
//			@RequestParam(value = "myList", required = false) List myList)
//			throws UnsupportedEncodingException {

//		System.out.println("Request::" +  request);
//		System.out.println("filterRequest::" +  filterRequest);
//		System.out.println("myObject::" + myObject);
//		System.out.println("myList::" + myList);
//		System.out.println("myString::" + myString);

		System.out.println(formData.myString);

//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			System.out.println("write myjson as json string" + mapper.writeValueAsString(myjson));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		try {
//			System.out.println("write myString as json string" + mapper.writeValueAsString(myString));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}

		//FilterParametersToSolr filterParametersToSr = new FilterParametersToSolr();ol
		//filterParametersToSolr.queryToAnnotationParameters(filterRequest, false);

	}

//	static class FilterData{
//		public int rows;
//		public int page;
//		public boolean isSlim;
//		public List<Filter> list;
//
//		@Override
//		public String toString() {
//			return "FilterData{" +
//					"rows=" + rows +
//					", page=" + page +
//					", isSlim=" + isSlim +
//					", filterList=" + list +
//					'}';
//		}
//	}
//
//	static class Filter {
//		public String type;
//		public String value;
//
//		@Override
//		public String toString() {
//			return "Filter{" +
//					"type='" + type + '\'' +
//					", value='" + value + '\'' +
//					'}';
//		}
//	}


	@RequestMapping(value="/annotationPostNewNamesNotSpring", method = {RequestMethod.POST})
	public void postQueryRequestNotSpring(HttpServletRequest request,
										  HttpServletResponse response) {

		FilterRequestJson filterRequest = null;

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { /*report an error*/ }

		try {
			ObjectMapper om = new ObjectMapper();
			String result = jb.toString();
			System.out.println("raw string is " + result);
			 filterRequest = om.readValue(result, FilterRequestJson.class);
			System.out.println("Not Spring " + filterRequest);


			FilterRequestToSolr filterRequestToSolr = new FilterRequestToSolr();
			//AnnotationParameters annotationParameters = filterRequestToSolr.queryToAnnotationParameters(filterRequest);

			AnnotationParameters annotationParameters = null;
			String solrQuery = filterRequestToSolr.toSolrQuery(filterRequest);
			System.out.println("Solr query is :" + solrQuery);

			//Do slimming if required
			ITermContainer slimTermSet = null;
			Boolean slimmingRequired = filterRequest.isSlim();


			if(slimmingRequired) {
				slimTermSet = createSlimTermSet(annotationParameters);
			}


			//todo should we be using the annotation service?
			AnnotationColumn[] columns = new AnnotationColumn[0];	//ignored
			annotationWSUtil.downloadAnnotationsInternal(solrQuery, columns, Integer.valueOf(LIMIT),
					(filterRequest.getPage() - 1) * filterRequest.getRows(), filterRequest.getRows(), response,
					slimmingRequired, slimTermSet);

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


		/**
	 * USE THIS ONE
	 * @param httpServletResponse
	 * @param query
	 * @param limit
	 * @param advancedFilter
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="/downloadfiltered", method = {RequestMethod.GET})
	public void downloadListWithFilter(
			HttpServletResponse httpServletResponse,
			@RequestParam(value = "format", required = false, defaultValue = "json") String format,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "limit", required = false, defaultValue = "1000") String limit,
			@RequestParam(value = "advancedFilter", defaultValue = "false") String advancedFilter,
			@RequestParam(value = "slim", required = false, defaultValue = "false") String slim)
			throws UnsupportedEncodingException {

		System.out.println("The query passed to the Webservice controller is " + query);
		System.out.println("The value of slim passed to the Webservice controller is " + slim);
		//query = "\"goID\":\"GO:0033014\",\"ancestorsIPO\":\"ancestorsIPO\",";
		//System.out.println("The query hardcoded is " + query);
		AnnotationParameters annotationParameters = createAnnotationParameters(query, advancedFilter);
		String solrQuery = annotationParameters.toSolrQuery();
		System.out.println("Solr query is :" + solrQuery);

		//Do slimming if required
		ITermContainer slimTermSet = null;
		Boolean slimmingRequired = Boolean.parseBoolean(slim);
		if(slimmingRequired) {
			slimTermSet = createSlimTermSet(annotationParameters);
		}


		//todo should we be using the annotation service?
		annotationWSUtil.downloadAnnotationsFile(solrQuery, Integer.valueOf(limit),
				 httpServletResponse, slimmingRequired, slimTermSet, format);
	}




	@RequestMapping(value = "/term/{id}", method = RequestMethod.GET)
	public void termInformation(@PathVariable(value = "id") String id,
								HttpServletResponse httpServletResponse,
								HttpServletRequest httpServletRequest)
			throws UnsupportedEncodingException {


		annotationWSUtil.downloadTerm(id, httpServletResponse);

	}


	@RequestMapping(value = "/ontologyGraph/{id}", method = RequestMethod.GET)
	public void ontologyGraph(@PathVariable(value = "id") String id,
							  HttpServletResponse httpServletResponse,
							  HttpServletRequest httpServletRequest)
			throws UnsupportedEncodingException {

		annotationWSUtil.downloadOntologyGraph(id, httpServletResponse);
	}


	@RequestMapping(value = "/predefinedslims", method = RequestMethod.GET)
	public void index(HttpServletResponse httpServletResponse) {

		annotationWSUtil.downloadPredefinedSlims(httpServletResponse);

	}


	@RequestMapping(value = {"/predefinedSetTerms/{id}"}, method = {RequestMethod.GET})
	public void addPredefinedSetTerms(@PathVariable(value = "id") String id,
			HttpServletResponse httpServletResponse) {

		annotationWSUtil.downloadPredefinedSetTerms(httpServletResponse, id);

	}

	@RequestMapping(value={"/evidencetypes"}, method = {RequestMethod.GET })
	public void getEvidenceTypes(HttpServletResponse httpServletResponse){

		annotationWSUtil.downloadEvidenceTypes(httpServletResponse);
	}


	@RequestMapping(value={"/withdbs"}, method = {RequestMethod.GET })
	public void getWithDBs(HttpServletResponse httpServletResponse){

		annotationWSUtil.downloadWithDBs(httpServletResponse);
	}


	@RequestMapping(value={"/assigneddbs"}, method = {RequestMethod.GET })
	public void getAssignedDBs(HttpServletResponse httpServletResponse){

		annotationWSUtil.downloadAssignedDBs(httpServletResponse);
	}

	@RequestMapping(value={"/dataset"}, method = {RequestMethod.GET })
	public void getAnnotationUpdates(HttpServletResponse httpServletResponse){

		annotationWSUtil.downloadAnnotationUpdates(httpServletResponse);
	}


	@RequestMapping(value = { "/dataset/goTermHistory"}, method = {RequestMethod.GET })
	public void getGoTermsHistory(HttpServletResponse httpServletResponse,
										  @RequestParam(value = "from", defaultValue = "2013-01-01", required=false) String from,
										  @RequestParam(value = "to", defaultValue = "NOW", required=false) String to,
										  @RequestParam(value = "limit", defaultValue = "500", required=false) String limit){

		annotationWSUtil.downloadGoTermHistory(httpServletResponse, from, to, limit);

	}


	@RequestMapping(value = { "/dataset/taxonConstraints"}, method = { RequestMethod.GET })
	public void getTaxonConstraints(HttpServletResponse httpServletResponse){

		annotationWSUtil.downloadTaxonConstraints(httpServletResponse);

	}


	@RequestMapping(value = { "/dataset/annotationBlacklist"}, method = { RequestMethod.GET })
	public void getAnnotationBlacklist(HttpServletResponse httpServletResponse){

		annotationWSUtil.downloadAnnotationBlacklist(httpServletResponse);
	}


	@RequestMapping(value = { "/dataset/annotationPostProcessing"},  method = { RequestMethod.GET })
	public void getAnnotationPostProcessing(HttpServletResponse httpServletResponse){
		annotationWSUtil.getAnnotationPostProcessing(httpServletResponse);
	}


	@RequestMapping("/chartfull")
	public void generateChart(HttpServletResponse httpServletResponse,
			@RequestParam(value = "ids", required = true, defaultValue = "") String ids,
			@RequestParam(value = "scope", required = false, defaultValue = "") String scope){

		annotationWSUtil.downloadChartFullModel(httpServletResponse, ids, scope);
	}


	@RequestMapping(value = { "/graphmulti"}, method = { RequestMethod.GET })
	public void generateAnnotationOntologyGraph(
			HttpServletResponse httpServletResponse,
			@RequestParam(value = "ids", required = true) String termsIds,
			@RequestParam(value = "relations",  required = false, defaultValue="ISA") String relations,
			@RequestParam(value = "type", required = false, defaultValue = "allBasketTerms") String requestType,
			HttpServletRequest request){

		annotationWSUtil.downloadAnnotationOntologyGraph(httpServletResponse, termsIds, relations, requestType);

	}


	/**
	 * Return list of terms by ontology (Bot-friendly page)
	 * @param ontology Ontology
	 *
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="terms/{ontology}", method = RequestMethod.GET)
	public void ontologyTerms(HttpServletResponse httpServletResponse,
								@PathVariable(value="ontology") String ontology) throws UnsupportedEncodingException {

		annotationWSUtil.downloadOntologyList(httpServletResponse, ontology);

	}



	/**
	 * Return stats information
	 * @return
	 */
	@RequestMapping(value="/stats", method = {RequestMethod.POST,RequestMethod.GET})
	public void calculateStatistics(
			HttpServletResponse httpServletResponse,
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "advancedFilter",  required = false, defaultValue = "false") String advancedFilter)
			throws UnsupportedEncodingException {

		AnnotationParameters annotationParameters = createAnnotationParameters(query, advancedFilter);
		String solrQuery = annotationParameters.toSolrQuery();
		annotationWSUtil.downloadStatistics(httpServletResponse, query, advancedFilter, solrQuery);
	}



//	@RequestMapping(value = { "/slim" }, method = {RequestMethod.POST, RequestMethod.GET }, params = { "q", "slim", "proteinIds", "proteinSets" })
//	public void slim(@RequestParam(value = "termList", required = false) String termList,
//					 @@RequestParam(value = "proteinIds", defaultValue = "", required = false) String proteinIds,
//							 @RequestParam(value = "proteinSets", defaultValue = "", required = false) String proteinSets,
//							 HttpServletResponse httpServletResponse) {
//
//		int rows,pages;
//		annotationWSUtil.downloadSlim(httpServletResponse, termList, proteinIds, proteinSets, rows, pages);
//	}


	@RequestMapping("/annotationExtensionRelations")
	public void showAnnotationExtensionRelationsGraph(
			HttpServletResponse httpServletResponse ) throws Exception {
		AnnotationExtensionRelations annotationExtensionRelations = new AnnotationExtensionRelations(TermUtil.getGOOntology(), TermUtil.getSourceFiles().goSourceFiles);
		//model.addAttribute("data", new Gson().toJson(annotationExtensionRelations.toGraph()));
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFile(annotationExtensionRelations.toGraph());
			//sb.append(new Gson().toJson(annotationExtensionRelations.toGraph().toString()));

			writeOutJsonResponse(httpServletResponse, sb);
		} catch (IOException e) {
			e.printStackTrace();
			//logger.error(e.getMessage());
		}
	}


	//----------------------- End of public interface   ------------------------------------ //

	private void writeOutJsonResponse(HttpServletResponse httpServletResponse, StringBuffer sb) throws IOException {
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		IOUtils.copy(in, httpServletResponse.getOutputStream());

		// Set response header and content
		httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + "json");
		httpServletResponse.setContentLength(sb.length());
		httpServletResponse.flushBuffer();
	}

	private List<Map<String, Object>> searchTerm(String query, String filterQuery, int limit, Scope enumScope, Format enumFormat, HttpServletResponse httpServletResponse, String callback) throws IOException, SolrServerException {

		if ((query.length() == 10 && query //Check if query is GO or ECO term. In that case, retrieve term
				.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}"))
				|| (query.length() == 11 && query
						.matches(AnnotationParameters.ECO_ID_REG_EXP + "\\d{7}"))) {

			GenericTerm genericTerm = TermUtil.getTerm(query);
			return Arrays.asList(genericTerm.serialise());

		} else {

			List<GenericTerm> genericTerms = termService.autosuggest(query, filterQuery, limit);
			List<Map<String, Object>> termsSerialised = new ArrayList<>();
			for(GenericTerm genericTerm : genericTerms){
				GenericTerm term = TermUtil.getTerm(genericTerm.getId());//to get ancestors and other info
				termsSerialised.add(term.serialise());
			}

			return termsSerialised;
		}
	}

	private List<Map<String, Object>> searchProtein(String query, Scope enumScope, int limit, Format enumFormat, HttpServletResponse httpServletResponse) throws IOException {
		List<GeneProduct> geneProducts = geneProductService.autosuggest(query, GeneProductField.DBOBJECTTYPE.getValue() + ":" + enumScope.value, limit);
		List<Map<String, Object>> gpSerialised = new ArrayList<>();
		Map<String, String> taxName = new HashMap<>();
		for(GeneProduct geneProduct : geneProducts){
			if(taxName.get(String.valueOf(geneProduct.getTaxonId())) == null){
				taxName = miscellaneousUtil.getTaxonomiesNames(Arrays.asList(String.valueOf(geneProduct.getTaxonId())));
			}
			geneProduct.setTaxonName(taxName.get(String.valueOf(geneProduct.getTaxonId())));
			gpSerialised.add(geneProduct.serialise());
		}

		return gpSerialised;
	}

	private void lookupProtein(String id, Scope enumScope, Format enumFormat, HttpServletResponse httpServletResponse, String callback) throws IOException{
		List<GeneProduct> geneProducts = geneProductService
				.findByQuery(GeneProductField.DBOBJECTID.getValue() + ":"
						+ id + " AND "
						+ GeneProductField.DBOBJECTTYPE.getValue() + ":"
						+ enumScope.value);
		GeneProduct geneProduct = new GeneProduct();
		if (geneProducts != null && !geneProducts.isEmpty()) {
			geneProduct = geneProducts.get(0);
		}
		Map<String, String> taxName = miscellaneousUtil.getTaxonomiesNames(Arrays.asList(String.valueOf(geneProduct.getTaxonId())));
		geneProduct.setTaxonName(taxName.get(String.valueOf(geneProduct.getTaxonId())));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		switch (enumFormat) {
			case XML:
				httpServletResponse.setContentType("text/xml");
				break;
			case JSON:
				httpServletResponse.setContentType("application/json");
				break;
			default:
				httpServletResponse.setContentType("application/json");
				break;
		}

		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		geneProductService.convertToStream(geneProduct, enumFormat, byteArrayOutputStream);
		httpServletResponse.getWriter().write(generateContentResponse(callback, new String(byteArrayOutputStream.toByteArray())));
	}

	private void lookupTerm(String id, Scope enumScope, Format enumFormat, HttpServletResponse httpServletResponse, String callback) throws IOException{
		GenericTerm genericTerm = TermUtil.getTerm(id);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");

		switch (enumFormat) {
			case OBOXML:
			case XML:
				httpServletResponse.setContentType("text/xml");
				termService.convertToStream(genericTerm, enumFormat, byteArrayOutputStream);
				httpServletResponse.getWriter().write(generateContentResponse(callback, new String(byteArrayOutputStream.toByteArray())));
				break;
			case JSON:
				httpServletResponse.setContentType("application/json");
				termService.convertToStream(genericTerm, enumFormat, byteArrayOutputStream);
				httpServletResponse.getWriter().write(generateContentResponse(callback, new String(byteArrayOutputStream.toByteArray())));
				break;
			case OBO:
				String obo = termService.convertToOBO(genericTerm);
				httpServletResponse.getWriter().write(generateContentResponse(callback, obo));
				break;
			default:
				httpServletResponse.setContentType("application/json");
				termService.convertToStream(genericTerm, enumFormat, byteArrayOutputStream);
				httpServletResponse.getWriter().write(generateContentResponse(callback, new String(byteArrayOutputStream.toByteArray())));
				break;
		}
	}

	/**
	 * Data scope
	 * @author cbonill
	 *
	 */
	enum Scope{
		GO("go"),
		ECO("eco"),
		PROTEIN("protein"),
		COMPLEX("complex"),
		ALL("all");

		String value;

		private Scope(String value) {
			this.value = value;
		}
	}

	/**
	 * Validate web service types
	 * @author cbonill
	 *
	 */
	enum ValidateType{
		ANN_EXT("ann_ext"),
		TAXON("taxon");

		String value;

		private ValidateType(String value) {
			this.value = value;
		}
	}

	/**
	 * Validate web service actions
	 * @author cbonill
	 *
	 */
	enum ValidateAction{
		VALIDATE_RELATION("validate_relation"),
		GET_RELATIONS("get_relations"),
		GET_BLACKLIST("get_blacklist"),
		GET_CONSTRAINTS("get_constraints");

		String value;

		private ValidateAction(String value) {
			this.value = value;
		}
	}

	/**
	 * To handle WS exceptions
	 * @param e Exception
	 * @return Error message in JSON format
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleException(Exception e) {
	    Map<String, String> errors = new HashMap<String, String>();
	    errors.put("error_message", "The request cannot be fulfilled due to bad syntax");
		return new Gson().toJson(errors);
	}

	/**
	 * Generate content response
	 * @param callback {@link Callback} parameter
	 * @param content Response content
	 * @return Content to write
	 */
	private String generateContentResponse(String callback, String content) {
		if (callback != null && callback.trim().length() > 0) {
			return callback + "(" + content + ")";
		}
		return content;
	}

	private void processFilters(String removeFilter, String removeAllFilters, AppliedFilterSet appliedFilterSet){

		// Reset applied filters and top taxonomies
		if(removeAllFilters.equals("true")){
			appliedFilterSet.setParameters(new HashMap<String,List<String>>());
		}

		// Remove filter (if any)
		if(!removeFilter.equals("")){
			String[] idValue = null;
			if (removeFilter.startsWith(AnnotationField.QUALIFIER.getValue())) {
				idValue = removeFilter.split("-");
			} else {
				idValue = removeFilter.split("-", 2);
			}
			String key = idValue[0];
			String value = idValue[1];
			// Check "NOT" value  special case for qualifier
			if(key.equals(AnnotationField.QUALIFIER.getValue()) && value.contains("NOT")){
				value = value.replaceAll("NOT","\"NOT\"");
			}

			List<String> currentFilterValues = appliedFilterSet.getParameters().get(key);

			if(currentFilterValues != null && currentFilterValues.size() > 0){
				List<String> currentValues = new ArrayList<>(currentFilterValues);
				currentValues.remove(value);// Remove filter
				appliedFilterSet.setValues(key, currentValues);
			}
		}
	}



	private AnnotationParameters createAnnotationParameters(@RequestParam(value = "q", required = false) String query,
								   @RequestParam(value = "advancedFilter", defaultValue = "false") String advancedFilter) {
		// Calculate Annotations Parameters from Query parameter
		QueryTo queryTo = new QueryTo();
		AnnotationParameters annotationParameters = queryTo.queryToAnnotationParameters(query, Boolean.valueOf(advancedFilter));
		System.out.println("Annotation Parameters: " + annotationParameters);

		// Calculate Applied Filter from Query parameter
		//AppliedFilterSet appliedFilterSet = queryTo.queryToAppliedFilterSet(query, Boolean.valueOf(advancedFilter));
		//System.out.println("Applied Filter Set: " + appliedFilterSet);

		// Create query from filter values
		//String solrQuery = annotationParameters.toSolrQuery();
		//System.out.println("Solr Query: " + solrQuery);
		//return solrQuery;
		return annotationParameters;
	}

	/**
	 * Populate the slimset with fully fledged go terms for each request slimming term
	 * @param annotationParameters
	 * @return
	 */
	private ITermContainer createSlimTermSet(AnnotationParameters annotationParameters) {


		List<String> slimsetGOTerms = annotationParameters.getParameters().get("ancestorsIPO");	//todo should change to requested

		//If not slim terms have been selected then return with a null
		if(slimsetGOTerms==null) {
			return null;
		}

		GeneOntology slimTerms = new GeneOntology();
		for (Iterator iterator = slimsetGOTerms.iterator();iterator.hasNext();) {
			String next =  (String)iterator.next();
			slimTerms.addTerm(TermUtil.getTerm(next));
		}

		return slimTerms;
	}

	private long calculateAspectsTotalResults(String query, String aspect, String attibute){
		long numberResults = termService.getTotalNumberHighlightResults(query,
				TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
						+ TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() + " AND "
						+ SolrTerm.SolrTermDocumentType.ONTOLOGY.getValue()
						+ ":" + aspect);
//		session.setAttribute(attibute, numberResults);
		return numberResults;
	}


	/**
	 * Return ECO terms used in automatic assertion
	 * @param results EXO terms to check
	 * @return ECO terms used in automatic assertion
	 */
	private List<String> getAllAutomaticECOCodes(List<GenericTerm> results) {
		List<String> allAutomaticECOCodes = new ArrayList<>();
		for (GenericTerm term : results) {
			GenericTerm ecoTerm = TermUtil.getECOTerms().get(term.getId());
			List<GenericTerm> ancestors = ecoTerm.getAncestry(EnumSet.of(
					RelationType.USEDIN, RelationType.ISA));
			List<GenericTerm> ancestorsNoRoot = new ArrayList<>();
			for (GenericTerm genericTerm : ancestors) {
				if (!genericTerm.getId().equals("ECO:0000000")) {
					ancestorsNoRoot.add(genericTerm);
				}
			}
			if (!emptyIntersection(ancestorsNoRoot,
					Arrays.asList("ECO:0000501"))) {
				if (!allAutomaticECOCodes.contains(ecoTerm.getId())) {
					allAutomaticECOCodes.add(ecoTerm.getId());
				}
			}
		}
		return allAutomaticECOCodes;
	}


	/**
	 * Check if lists intersection is empty
	 */
	private boolean emptyIntersection(List<GenericTerm> terms, List<String> ecoIds){
		boolean empty = true;
		for(GenericTerm element : terms){
			if(ecoIds.contains(element.getId())){
				empty = false;
			}
		}
		return empty;
	}
	/**
	 * Return ECO terms used in manual assertion
	 * @param results EXO terms to check
	 * @return ECO terms used in manual assertion
	 */
	private List<String> getAllManualECOCodes(List<GenericTerm> results) {
		List<String> allExperimentalECOCodes = new ArrayList<>();
		for (GenericTerm term : results) {
			GenericTerm ecoTerm = TermUtil.getECOTerms().get(term.getId());
			List<GenericTerm> ancestors = ecoTerm.getAncestry(EnumSet.of(
					RelationType.USEDIN, RelationType.ISA));
			List<GenericTerm> ancestorsNoRoot = new ArrayList<>();
			for (GenericTerm genericTerm : ancestors) {
				if (!genericTerm.getId().equals("ECO:0000000")) {
					ancestorsNoRoot.add(genericTerm);
				}
			}
			if (!emptyIntersection(ancestorsNoRoot,
					Arrays.asList("ECO:0000352"))) {
				if (!allExperimentalECOCodes.contains(ecoTerm.getId())) {
					allExperimentalECOCodes.add(ecoTerm.getId());
				}
			}
		}
		return allExperimentalECOCodes;
	}

	private List<GenericTerm> highlightedTerms(String text, String filterQuery, int page, int rows){
		return termService.highlight("*" + text  + "*", filterQuery, (page-1)*rows, rows);
	}

	public enum ViewBy{
		ENTITY("entity"),
		GOID("goID"),
		BP("bp"),
		MF("mf"),
		CC("cc"),
		ECOID("ecoID"),
		ECOMANUAL("ecoManual"),
		ECOAUTOMATIC("ecoAutomatic"),
		EVIDENCEECO("evidenceEco");

		String value;

		private ViewBy(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
