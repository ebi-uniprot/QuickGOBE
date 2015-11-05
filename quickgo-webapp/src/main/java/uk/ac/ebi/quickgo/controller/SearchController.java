package uk.ac.ebi.quickgo.controller;

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
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.query.QueryProcessor;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;
import uk.ac.ebi.quickgo.webservice.model.SearchResultType;
import uk.ac.ebi.quickgo.webservice.model.TypeAheadResult;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Search controller
 */
@RestController
@RequestMapping(value = {"/", "annotation", "/search"})
public class SearchController {
    private static final int HITS_TO_RETURN = 20;

    @Autowired
    private TermService termService;

    @Autowired
    private GeneProductService geneProductService;

    @Autowired
    private QueryProcessor queryProcessor;

    @Autowired
    private FileService fileService;

    private int selectedPage;
    private long totalResults;

    private List<Object> searchResults = new ArrayList<>();

    /**
     * This is the one used for the typeahead
     * @param query
     * @param httpServletResponse
     * @throws IOException
     * @throws SolrServerException
     */
    @RequestMapping(value = "searchTypeAhead", method = {RequestMethod.POST, RequestMethod.GET})
    public void searchTypeAhead(
            @RequestParam(value = "query") String query,
            HttpServletResponse httpServletResponse) throws IOException,
                                                            SolrServerException {

        List<TypeAheadResult> results;

        //TODO: move GO and ECO ide prefixes to somewhere more centralized
        if (query.toLowerCase().startsWith("go:") || query.toLowerCase().startsWith("eco:")) {
            List<GenericTerm> terms = termService.autosuggestOnlyGoTerms(query, null, HITS_TO_RETURN);
            results = convertTermsToTypeAhead(terms);
        } else {
            List<GenericTerm> terms = termService.autosuggest(query, null, HITS_TO_RETURN);
            results = convertTermsToTypeAhead(terms);
        }

        returnResultsJson(httpServletResponse, results);
    }

    private void returnResultsJson(HttpServletResponse httpServletResponse, List<TypeAheadResult> results) {
        StringBuffer sb;

        try {
            sb = fileService.generateJsonFile(results);
            writeOutJsonResponse(httpServletResponse, sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "search", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView search(
            @RequestParam(value = "query", defaultValue = "", required = false) String query,
            @RequestParam(value = "isProtein", defaultValue = "false", required = false) String isProtein,
            @RequestParam(value = "viewBy", defaultValue = "", required = false) String viewBy,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "rows", defaultValue = "25") int rows,
            HttpSession session, HttpServletResponse httpServletResponse, Model model) throws IOException,
                                                                                              SolrServerException {

        System.out.println("Search run " + "query:" + query + "<<isProtein:" + isProtein + "<<viewBy " + viewBy);
        boolean sameQuery = false;

        this.selectedPage = page;
        searchResults = new ArrayList<>();

        if (!query.isEmpty()) {
            AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
            queryProcessor.processQuery(query, new AnnotationParameters(), appliedFilterSet, false);
            if (isProtein.equals("true")) {
                session.setAttribute("searchedText",
                        appliedFilterSet.getParameters().get(AnnotationField.DBOBJECTID.getValue()));
                session.setAttribute("appliedFilters", appliedFilterSet);
                return new ModelAndView("redirect:" + View.ANNOTATIONS_PATH);
            } else {// Text
                Properties data = new Gson().fromJson(query, Properties.class);
                String text = data.getProperty("text");
                // Remove leading and trailing white spaces from query
                text = StringUtils.trimLeadingWhitespace(text);
                text = StringUtils.trimTrailingWhitespace(text);

                String previousQuery = (String) session.getAttribute("searchedText");
                if (text.equals(
                        previousQuery)) {// Query hasn't changed. No need to recalculate all the counts and search
                    // results
                    sameQuery = true;
                }

                session.setAttribute("searchedText", text);

                long gpNumberResults = 0, goTotalResults = 0,
                        bpGoNumberResults = 0, mfGoNumberResults = 0,
                        ccGoNumberResults = 0, ecoTotalResults = 0,
                        expEcoTotalResults = 0, automaticEcoTotalResults = 0,
                        evidenceEcoResults = 0;

                String expEcofilterQuery = "", automaticEcofilterQuery = "";
                String ecoFilterQuery = TermField.ID.getValue() + ":" + ECOTerm.ECO.toString() + "*" + " AND " +
                        TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();

                if (!sameQuery) {// Recalculate counts

                    // Calculate number of results of each type

                    // #Gene Products
                    gpNumberResults = geneProductService.getTotalNumberHighlightResults("*" + text + "*", null);
                    session.setAttribute("gpNumberResults", gpNumberResults);

                    // #GO terms
                    goTotalResults = termService.getTotalNumberHighlightResults("*" + text + "*",
                            TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*" + " AND " +
                                    TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
                    session.setAttribute("goNumberResults", goTotalResults);

                    String textToSearch = "*" + text + "*";
                    // #BP GO terms
                    bpGoNumberResults =
                            calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.P.text, "bpGoNumberResults",
                                    session);

                    // #MF GO terms
                    mfGoNumberResults =
                            calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.F.text, "mfGoNumberResults",
                                    session);

                    // #CC GO terms
                    ccGoNumberResults =
                            calculateAspectsTotalResults(textToSearch, GOTerm.EGOAspect.C.text, "ccGoNumberResults",
                                    session);

                    // #ECO terms
                    ecoTotalResults = termService.getTotalNumberHighlightResults("*" + text + "*",
                            ecoFilterQuery + " AND " + TermField.TYPE.getValue() + ":" +
                                    SolrTerm.SolrTermDocumentType.TERM.getValue());
                    session.setAttribute("ecoNumberResults", ecoTotalResults);
                    // Retrieve values
                    List<GenericTerm> ecoTerms = highlightedTerms("*" + text + "*", ecoFilterQuery, 1, 60000);
                    // Remove null elements
                    List<GenericTerm> noNullTerms = new ArrayList<>();
                    for (GenericTerm genericTerm : ecoTerms) {
                        if (genericTerm != null && genericTerm.getId() != null) {
                            noNullTerms.add(genericTerm);
                        }
                    }

                    ecoTerms = noNullTerms;

                    // #Experimental ECO terms
                    String expEcoValues =
                            StringUtils.arrayToDelimitedString(getAllManualECOCodes(ecoTerms).toArray(), " OR ");
                    if (!expEcoValues.isEmpty()) {
                        expEcofilterQuery =
                                TermField.ID.getValue() + ":(" + expEcoValues.replaceAll(":", "*") + ")" + " AND " +
                                        TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();
                        ;
                        session.setAttribute("expEcofilterQuery", expEcofilterQuery);
                        expEcoTotalResults =
                                termService.getTotalNumberHighlightResults("*" + text + "*", expEcofilterQuery);
                    }
                    session.setAttribute("expEcoTotalResults", expEcoTotalResults);

                    // #Automatic ECO terms
                    String automaticEcoValues =
                            StringUtils.arrayToDelimitedString(getAllAutomaticECOCodes(ecoTerms).toArray(), " OR ");
                    if (!automaticEcoValues.isEmpty()) {
                        automaticEcofilterQuery =
                                TermField.ID.getValue() + ":(" + automaticEcoValues.replaceAll(":", "*") + ")" +
                                        " AND " + TermField.TYPE.getValue() + ":" +
                                        SolrTerm.SolrTermDocumentType.TERM.getValue();
                        ;
                        session.setAttribute("automaticEcofilterQuery", automaticEcofilterQuery);
                        automaticEcoTotalResults =
                                termService.getTotalNumberHighlightResults("*" + text + "*", automaticEcofilterQuery);

                    }
                    session.setAttribute("automaticEcoTotalResults", automaticEcoTotalResults);

                    // #Evidence ECO Terms
                    evidenceEcoResults = ecoTotalResults - (expEcoTotalResults + automaticEcoTotalResults);
                    session.setAttribute("evidenceEcoTotalResults", evidenceEcoResults);
                } else {// Same query. Get counts from session
                    gpNumberResults = (long) session.getAttribute("gpNumberResults");
                    goTotalResults = (long) session.getAttribute("goNumberResults");
                    bpGoNumberResults = (long) session.getAttribute("bpGoNumberResults");
                    mfGoNumberResults = (long) session.getAttribute("mfGoNumberResults");
                    ccGoNumberResults = (long) session.getAttribute("ccGoNumberResults");
                    ecoTotalResults = (long) session.getAttribute("ecoNumberResults");
                    expEcoTotalResults = (long) session.getAttribute("expEcoTotalResults");
                    automaticEcoTotalResults = (long) session.getAttribute("automaticEcoTotalResults");
                    evidenceEcoResults = (long) session.getAttribute("evidenceEcoTotalResults");
                }

                // Set default view
                if (viewBy.isEmpty()) {
                    viewBy = ViewBy.ENTITY.getValue();
                    if (gpNumberResults > 0) {
                        viewBy = ViewBy.ENTITY.getValue();
                    } else if (goTotalResults > 0) {
                        viewBy = ViewBy.GOID.getValue();
                    } else if (ecoTotalResults > 0) {
                        viewBy = ViewBy.ECOID.getValue();
                    }
                }

                if (viewBy.equalsIgnoreCase(ViewBy.ENTITY.getValue())) {
                    totalResults = gpNumberResults;
                    List<GeneProduct> gpResults =
                            geneProductService.highlight("*" + text + "*", null, (page - 1) * rows, rows);
                    searchResults.addAll(gpResults);

                } else if (viewBy.equalsIgnoreCase(ViewBy.GOID.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.ECOID.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.BP.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.MF.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.CC.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.ECOAUTOMATIC.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.ECOMANUAL.getValue())
                        || viewBy.equalsIgnoreCase(ViewBy.EVIDENCEECO.getValue())) {

                    String filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*" + " AND " +
                            TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue();
                    totalResults = goTotalResults;
                    if (viewBy.equalsIgnoreCase(ViewBy.BP.getValue())) {
                        filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
                                + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() +
                                " AND "
                                + SolrTerm.SolrTermDocumentType.ONTOLOGY
                                .getValue() + ":"
                                + GOTerm.EGOAspect.P.text;
                        totalResults = bpGoNumberResults;
                    } else if (viewBy.equalsIgnoreCase(ViewBy.MF.getValue())) {
                        filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
                                + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() +
                                " AND "
                                + SolrTerm.SolrTermDocumentType.ONTOLOGY
                                .getValue() + ":"
                                + GOTerm.EGOAspect.F.text;
                        totalResults = mfGoNumberResults;
                    } else if (viewBy.equalsIgnoreCase(ViewBy.CC.getValue())) {
                        filterQuery = TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "*  AND "
                                + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() +
                                " AND "
                                + SolrTerm.SolrTermDocumentType.ONTOLOGY
                                .getValue() + ":"
                                + GOTerm.EGOAspect.C.text;
                        totalResults = ccGoNumberResults;
                    } else if (viewBy.equalsIgnoreCase(ViewBy.ECOID.getValue())) {
                        filterQuery = ecoFilterQuery;
                        totalResults = ecoTotalResults;
                    } else if (viewBy.equalsIgnoreCase(ViewBy.ECOMANUAL.getValue())) {
                        filterQuery = (String) session.getAttribute("expEcofilterQuery");
                        totalResults = expEcoTotalResults;
                    } else if (viewBy.equalsIgnoreCase(ViewBy.ECOAUTOMATIC.getValue())) {
                        filterQuery = (String) session.getAttribute("automaticEcofilterQuery");
                        totalResults = automaticEcoTotalResults;
                    } else if (viewBy.equalsIgnoreCase(ViewBy.EVIDENCEECO.getValue())) {
                        String evidencefilterQuery = ecoFilterQuery;

                        if (session.getAttribute("expEcofilterQuery") != null &&
                                !((String) session.getAttribute("expEcofilterQuery")).isEmpty()) {
                            evidencefilterQuery = evidencefilterQuery + " AND NOT " +
                                    (String) session.getAttribute("expEcofilterQuery");
                        }

                        if (session.getAttribute("automaticEcofilterQuery") != null &&
                                !((String) session.getAttribute("automaticEcofilterQuery")).isEmpty()) {
                            evidencefilterQuery = evidencefilterQuery + " AND NOT " +
                                    (String) session.getAttribute("automaticEcofilterQuery");
                        }

                        filterQuery = evidencefilterQuery;
                        totalResults = evidenceEcoResults;
                    }
                    List<GenericTerm> termsResults = highlightedTerms("*" + text + "*", filterQuery, page, rows);
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

    private List<GenericTerm> highlightedTerms(String text, String filterQuery, int page, int rows) {
        return termService.highlight("*" + text + "*", filterQuery, (page - 1) * rows, rows);
    }

    private long calculateAspectsTotalResults(String query, String aspect, String attibute, HttpSession session) {
        long numberResults = termService.getTotalNumberHighlightResults(query,
                TermField.ID.getValue() + ":" + GOTerm.GO.toString() + "* AND "
                        + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue() + " AND "
                        + SolrTerm.SolrTermDocumentType.ONTOLOGY.getValue()
                        + ":" + aspect);
        session.setAttribute(attibute, numberResults);
        return numberResults;
    }

    public enum ViewBy {
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

        ViewBy(String value) {
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
                    Collections.singletonList("ECO:0000352"))) {
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
                    Collections.singletonList("ECO:0000501"))) {
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
    private boolean emptyIntersection(List<GenericTerm> terms, List<String> ecoIds) {
        boolean empty = true;
        for (GenericTerm element : terms) {
            if (ecoIds.contains(element.getId())) {
                empty = false;
            }
        }
        return empty;
    }

    /**
     * Converts a list of {@link GenericTerm} to a list of {@link TypeAheadResult}.
     *
     * @param terms The terms to convert
     * @return a List of TypeAheadResult elements that result from the conversion
     */
    public List<TypeAheadResult> convertTermsToTypeAhead(List<GenericTerm> terms) {
        return  terms.stream()
                .map(t -> new TypeAheadResult(t.getId(), t.getName(), SearchResultType.TERM))
                .collect(Collectors.toList());
    }

/**
 * Shortest hits are displayed first
 * @author cbonill
 *
 */
//TODO: This needs to be refactored if it is to continued to be used
private class HitsComparator implements Comparator<Object> {

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
            if (((GenericTerm) o).getName() != null) {
                return ((GenericTerm) o).getName();
            } else {// Synonym
                if (!(((GenericTerm) o).getSynonyms()).isEmpty()) {
                    ((GenericTerm) o).setName(((GenericTerm) o).getSynonyms().get(0)
                            .getName());// Set synonym name as term result name
                    return ((GenericTerm) o).getSynonyms().get(0).getName();
                }
            }
        }
        return "";
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