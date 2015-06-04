package uk.ac.ebi.quickgo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.service.geneproduct.GeneProductService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.query.QueryProcessor;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

import com.google.gson.Gson;

/**
 * Search controller
 */
@Controller
@RequestMapping(value = { "/", "annotation", "/search" })
public class SearchController {

	@Autowired
	TermService termService;

	@Autowired
	GeneProductService geneProductService;

	@Autowired
	QueryProcessor queryProcessor;

	private int selectedPage;

	private long totalResults;

	private List<Object> searchResults = new ArrayList<>();
	Gson gson = new Gson();

	private final int NUMBER_SHOWN_HITS = 10;

	@RequestMapping(value = "autoSuggestByName", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> findByName(
			@RequestParam(value = "q") String query,
			HttpServletResponse httpServletResponse) throws IOException,
			SolrServerException {

		List<Object> results = new ArrayList<>();

		List<GeneProduct> geneproducts = new ArrayList<>();
		List<GenericTerm> terms = termService.autosuggest(query,null, 30);
		if (!query.contains(":")) {
			geneproducts = geneProductService.autosuggest(query, null, 30);
			addTerms(query, results, terms);
			addGeneProducts(query, results, geneproducts);
		} else {// GO or ECO id
			addTermsIds(query, results, terms);
		}

		// Sort results (shortest first)
		Collections.sort(results, new HistsComparator());

		if (results.size() > NUMBER_SHOWN_HITS) {
			results = results.subList(0, NUMBER_SHOWN_HITS);// first 10 results
		} else {
			results.subList(0, results.size());
		}
		// JSON representation
		String json = new Gson().toJson(results);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity<String>(json, responseHeaders, HttpStatus.CREATED);
	}


	@RequestMapping(value = "search", method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView search(
			@RequestParam(value = "query", defaultValue="", required=false) String query,
			@RequestParam(value = "isProtein", defaultValue="false", required=false) String isProtein,
			@RequestParam(value = "viewBy", defaultValue="", required=false) String viewBy,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "rows", defaultValue = "25") int rows,
			HttpSession session, HttpServletResponse httpServletResponse,Model model) throws IOException,
			SolrServerException {

		System.out.println("Search run " + "query:" + query + "<<isProtein:" + isProtein + "<<viewBy " + viewBy);
		boolean sameQuery = false;

		this.selectedPage = page;
		searchResults = new ArrayList<>();

		if(!query.isEmpty()){
			AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
			queryProcessor.processQuery(query, new AnnotationParameters(), appliedFilterSet, false);
			if(isProtein.equals("true")){
				session.setAttribute("searchedText", appliedFilterSet.getParameters().get(AnnotationField.DBOBJECTID.getValue()));
				session.setAttribute("appliedFilters", appliedFilterSet);
				return new ModelAndView("redirect:" + View.ANNOTATIONS_PATH);
			} else {// Text
				Properties data = gson.fromJson(query, Properties.class);
				String text = data.getProperty("text");
				// Remove leading and trailing white spaces from query
				text = StringUtils.trimLeadingWhitespace(text);
				text = StringUtils.trimTrailingWhitespace(text);

				String previousQuery = (String)session.getAttribute("searchedText");
				if(text.equals(previousQuery)){// Query hasn't changed. No need to recalculate all the counts and search results
					sameQuery = true;
				}

				session.setAttribute("searchedText", text);

				long gpNumberResults = 0, goTotalResults = 0,
						bpGoNumberResults = 0, mfGoNumberResults = 0,
						ccGoNumberResults = 0,ecoTotalResults = 0,
						expEcoTotalResults = 0,automaticEcoTotalResults = 0,
						evidenceEcoResults = 0;

				String expEcofilterQuery = "", automaticEcofilterQuery = "";
				String ecoFilterQuery = TermField.ID.getValue() + ":" + ECOTerm.ECO.toString() + "*" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();

				if(!sameQuery){// Recalculate counts

					// Calculate number of results of each type

					// #Gene Products
					gpNumberResults = geneProductService.getTotalNumberHighlightResults("*" + text  + "*", null);
					session.setAttribute("gpNumberResults", gpNumberResults);

					// #GO terms
					goTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
					session.setAttribute("goNumberResults", goTotalResults);

					String textToSearch = "*" + text + "*";
					// #BP GO terms
					bpGoNumberResults = calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.P.text, "bpGoNumberResults", session);

					// #MF GO terms
					mfGoNumberResults = calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.F.text, "mfGoNumberResults", session);

					// #CC GO terms
					ccGoNumberResults = calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.C.text, "ccGoNumberResults", session);

					// #ECO terms
					ecoTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", ecoFilterQuery + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
					session.setAttribute("ecoNumberResults", ecoTotalResults);
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
						session.setAttribute("expEcofilterQuery", expEcofilterQuery);
						expEcoTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", expEcofilterQuery);
					}
					session.setAttribute("expEcoTotalResults", expEcoTotalResults);

					// #Automatic ECO terms
					String automaticEcoValues = StringUtils.arrayToDelimitedString(getAllAutomaticECOCodes(ecoTerms).toArray(), " OR ");
					if(!automaticEcoValues.isEmpty()){
						automaticEcofilterQuery =  TermField.ID.getValue() + ":(" + automaticEcoValues.replaceAll(":","*") + ")" + " AND " + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();;
						session.setAttribute("automaticEcofilterQuery", automaticEcofilterQuery);
						automaticEcoTotalResults = termService.getTotalNumberHighlightResults("*" + text  + "*", automaticEcofilterQuery);

					}
					session.setAttribute("automaticEcoTotalResults", automaticEcoTotalResults);

					// #Evidence ECO Terms
					evidenceEcoResults = ecoTotalResults - (expEcoTotalResults + automaticEcoTotalResults);
					session.setAttribute("evidenceEcoTotalResults", evidenceEcoResults);
				} else {// Same query. Get counts from session
					gpNumberResults = (long)session.getAttribute("gpNumberResults");
					goTotalResults = (long)session.getAttribute("goNumberResults");
					bpGoNumberResults = (long)session.getAttribute("bpGoNumberResults");
					mfGoNumberResults = (long)session.getAttribute("mfGoNumberResults");
					ccGoNumberResults = (long)session.getAttribute("ccGoNumberResults");
					ecoTotalResults = (long)session.getAttribute("ecoNumberResults");
					expEcoTotalResults = (long)session.getAttribute("expEcoTotalResults");
					automaticEcoTotalResults = (long)session.getAttribute("automaticEcoTotalResults");
					evidenceEcoResults = (long)session.getAttribute("evidenceEcoTotalResults");
				}

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
						filterQuery = (String)session.getAttribute("expEcofilterQuery");
						totalResults = expEcoTotalResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.ECOAUTOMATIC.getValue())){
						filterQuery = (String)session.getAttribute("automaticEcofilterQuery");
						totalResults = automaticEcoTotalResults;
					}
					else if(viewBy.equalsIgnoreCase(ViewBy.EVIDENCEECO.getValue())){
						String evidencefilterQuery = ecoFilterQuery;

						if(session.getAttribute("expEcofilterQuery") != null && !((String)session.getAttribute("expEcofilterQuery")).isEmpty()){
							evidencefilterQuery = evidencefilterQuery + " AND NOT " + (String)session.getAttribute("expEcofilterQuery");
						}

						if(session.getAttribute("automaticEcofilterQuery") != null && !((String)session.getAttribute("automaticEcofilterQuery")).isEmpty()){
							evidencefilterQuery = evidencefilterQuery + " AND NOT " + (String)session.getAttribute("automaticEcofilterQuery");
						}

						filterQuery = evidencefilterQuery;
						totalResults = evidenceEcoResults;
					}
					List<GenericTerm> termsResults = highlightedTerms("*" + text  + "*", filterQuery, page, rows);
					searchResults.addAll(termsResults);
				}

				// Set results in session
				model.addAttribute("searchResults", searchResults);

				// Set total number results
				model.addAttribute("totalNumberResults", totalResults);

				// Set current page
				model.addAttribute("searchCurrentPage", this.selectedPage);

				// View by
				model.addAttribute("viewBy", viewBy);

				return new ModelAndView(View.SEARCH);
			}
		}
		return new ModelAndView("");
	}

	private List<GenericTerm> highlightedTerms(String text, String filterQuery, int page, int rows){
		return termService.highlight("*" + text  + "*", filterQuery, (page-1)*rows, rows);
	}


	private long calculateAspectsTotalResults(String query, String aspect, String attibute, HttpSession session){
		long numberResults = termService.getTotalNumberHighlightResults(query,
				TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
						+ TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() + " AND "
						+ SolrTerm.SolrTermDocumentType.ONTOLOGY.getValue()
						+ ":" + aspect);
		session.setAttribute(attibute, numberResults);
		return numberResults;
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
	 * Check term/synonym contains query text before adding to results
	 * @param query Auto complete query
	 * @param results Results to be shown
	 * @param terms Terms results
	 */
	public void addTerms(String query, List<Object> results,
			List<GenericTerm> terms) {
		for (GenericTerm genericTerm : terms) {
			if ((genericTerm.getName() != null && genericTerm.getName()
					.toLowerCase().trim().matches(".*" + query.toLowerCase().replaceAll("\\s+",".*") + ".*"))
					|| (!genericTerm.getSynonyms().isEmpty() && genericTerm
							.getSynonyms().get(0).getName().toLowerCase()
							.trim().matches(".*" + query.toLowerCase().replaceAll("\\s+",".*") + ".*"))) {
				results.add(genericTerm);
			}
		}
	}

	/**
	 * Check gene product contains query text before adding to results
	 * @param query Auto complete query
	 * @param results Results to be shown
	 * @param terms Terms results
	 */
	public void addGeneProducts(String query, List<Object> results,
			List<GeneProduct> geneProducts) {
		for (GeneProduct geneProduct : geneProducts) {
			if (geneProduct.getDbObjectName().toLowerCase().trim()
					.matches(".*" + query.toLowerCase().replaceAll("\\s+",".*") + ".*")) {
				results.add(geneProduct);
			}
		}
	}

	/**
	 * Add terms when specific ids are searched
	 * @param query Query to search for
	 * @param results Results to display
	 * @param terms Candidate terms to be displayed
	 */
	private void addTermsIds(String query, List<Object> results, List<GenericTerm> terms) {
		for (GenericTerm term : terms) {
			if (term.getId() != null && (term.getId().toLowerCase().trim().matches(".*" + query.toLowerCase().replaceAll("\\s+",".*") + ".*") ||
					(term.getId() + term.getName()).toLowerCase().trim().matches(".*" + query.toLowerCase().replaceAll("\\s+",".*") + ".*"))) {//Don't want to add synonyms to results when we are looking for specific ECO/GO ids
				results.add(term);
			}
		}
	}

	/**
	 * Shortest hits are displayed first
	 * @author cbonill
	 *
	 */
	private class HistsComparator implements Comparator<Object>{

		@Override
		public int compare(Object o1, Object o2) {
			String text1 = getText(o1);
			String text2 = getText(o2);
			if (text1.length() < text2.length()) {
				return -1;
			} else if (text1.length() > text2.length()) {
				return 1;
			}
			return 0;
		}

		private String getText(Object o) {
			if (o instanceof GeneProduct) {
				return ((GeneProduct) o).getDbObjectName();
			} else if (o instanceof GenericTerm) {
				if (((GenericTerm) o).getName() != null){
					return ((GenericTerm) o).getName();
				} else {// Synonym
					if(!(((GenericTerm) o).getSynonyms()).isEmpty()){
						((GenericTerm) o).setName(((GenericTerm) o).getSynonyms().get(0).getName());// Set synonym name as term result name
						return ((GenericTerm) o).getSynonyms().get(0).getName();
					}
				}
			}
			return "";
		}

	}
}
