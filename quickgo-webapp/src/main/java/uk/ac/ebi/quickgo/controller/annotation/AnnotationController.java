package uk.ac.ebi.quickgo.controller.annotation;

import uk.ac.ebi.quickgo.bean.annotation.AnnotationBean;
import uk.ac.ebi.quickgo.bean.statistics.StatisticsBean;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.go.GOEvidence2ECOMap;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.statistic.StatisticService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.web.util.TopTaxonomy;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationColumn;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.annotation.SlimmingUtil;
import uk.ac.ebi.quickgo.web.util.query.QueryProcessor;
import uk.ac.ebi.quickgo.web.util.stats.StatisticsCalculation;
import uk.ac.ebi.quickgo.web.util.stats.StatsDownload;
import uk.ac.ebi.quickgo.web.util.url.URLsResolver;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Annotation controller
 *
 * @author cbonill
 *
 */

@RestController
public class AnnotationController {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationController.class);
    // Number top organisms to show
    private final static int NUMBER_TOP_ORGANISMS = 17;
    // Most used taxonomies for filtering
    private static List<TopTaxonomy> mostCommonTaxonomies = new ArrayList<TopTaxonomy>(NUMBER_TOP_ORGANISMS);
    // Taxonomies including GOC model organisms
    private static List<String> taxonomiesIds = Arrays.asList("9606", "10090",
            "10116", "3702", "559292", "284812", "83333", "6239", "7955",
            "44689", "7227", "9031", "9913");
    // All the annotation columns that can be displayed
    private static AnnotationColumn[] allColumns = AnnotationColumn.values();
    // All evidence types
    private static Map<String, String> evidenceTypes = new HashMap<>();
    // Databases owner of any annotations
    private static TreeMap<String, String> assignedByDBs = new TreeMap<>();
    // Databases appear in with column
    private static TreeMap<String, String> withDBs = new TreeMap<>();
    // Subsets counts
    private static List<Miscellaneous> subsetsCounts = new ArrayList<>();
    // Excel format for stats
    private static final String contentTypeXLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private int selectedPage;
    private int selectedRows;
    private long totalNumberAnnotations;

    AnnotationParameters annotationParameters;

    // To keep statistics values
    private StatisticsBean statisticsBean = new StatisticsBean();

    @Autowired
    AnnotationService annotationService;

    @Autowired
    MiscellaneousUtil miscellaneousUtil;

    @Autowired
    MiscellaneousService miscellaneousService;

    @Autowired
    TermService termService;

    @Autowired
    URLsResolver urLsResolver;

    @Autowired
    QueryProcessor queryProcessor;

    @Autowired
    SlimmingUtil slimmingUtil;

    @Autowired
    StatisticService statisticService;

    StatisticsCalculation statisticsCalculation;

    String currentQuery;

    @RequestMapping(value = "annotation", method = {RequestMethod.POST, RequestMethod.GET})
    public String annotationList(
            HttpSession session,
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "rows", defaultValue = "25") int rows,
            @RequestParam(value = "cols", defaultValue = "") String cols,
            @RequestParam(value = "removeFilter", defaultValue = "") String removeFilter,
            @RequestParam(value = "removeAllFilters", defaultValue = "") String removeAllFilters,
            @RequestParam(value = "advancedFilter", defaultValue = "false") String advancedFilter,
            Model model) throws UnsupportedEncodingException {

        this.selectedPage = page;
        this.selectedRows = rows;

        // Get current applied filters from session
        AppliedFilterSet appliedFilterSet = (AppliedFilterSet) session.getAttribute("appliedFilters");
        if (appliedFilterSet == null) {
            appliedFilterSet = new AppliedFilterSet();
        }

        initializeStructures(session);

        processFilters(removeFilter, removeAllFilters, appliedFilterSet);

        // Calculate Annotations Parameters from Query parameter
        annotationParameters = new AnnotationParameters();
        // Process query
        queryProcessor.processQuery(query, annotationParameters, appliedFilterSet, Boolean.valueOf(advancedFilter));

        annotationParameters.setParameters(new HashMap<String, List<String>>(appliedFilterSet.getParameters()));

        // Create query from filter values
        String solrQuery = annotationParameters.toSolrQuery();

        currentQuery = solrQuery;

        // Calculate total number annotations
        this.totalNumberAnnotations = annotationService.getTotalNumberAnnotations(solrQuery);
        // Retrieve annotations
        List<GOAnnotation> annotations = annotationService.retrieveAnnotations(solrQuery, (page - 1) * rows, rows);
        // Create annotation wrappers
        List<AnnotationBean> annotationBeans = new ArrayList<>();
        for (GOAnnotation annotation : annotations) {
            List<String> slimValue = appliedFilterSet.getParameters().get("slim");
            List<String> filterGOIds = appliedFilterSet.getParameters().get(AnnotationField.ANCESTORSIPO.getValue());
            AnnotationBean annotationBean =
                    slimmingUtil.calculateOriginalAndSlimmingTerm(annotation, filterGOIds, slimValue);
            urLsResolver.setURLs(annotationBean);
            annotationBeans.add(annotationBean);
        }

        // Set list of annotations to display
        session.setAttribute("annotationsList", annotationBeans);
        // Set visible columns
        AnnotationColumn[] sortedVisibleAnnotationHeaders =
                (AnnotationColumn[]) session.getAttribute("visibleAnnotationsColumns");
        if (sortedVisibleAnnotationHeaders == null || !cols.isEmpty()) {
            sortedVisibleAnnotationHeaders =
                    AnnotationColumn.getAnnotationHeaders(URLDecoder.decode(cols, "UTF-8").split(","));
            // Set visible columns in session
            session.setAttribute("visibleAnnotationsColumns", sortedVisibleAnnotationHeaders);
        }
        // All columns
        AnnotationColumn[] allAnnotationsColumns = (AnnotationColumn[]) session.getAttribute("allAnnotationsColumns");
        if (allAnnotationsColumns == null) {
            session.setAttribute("allAnnotationsColumns", allColumns);
        }
        // Set annotations columns ordered
        session.setAttribute("annotationsColumns", AnnotationColumn.sort(sortedVisibleAnnotationHeaders));
        // Set current page
        model.addAttribute("currentPage", this.selectedPage);
        // Set total number of annotations
        model.addAttribute("totalNumberAnnotations", this.totalNumberAnnotations);
        // Set applied filters in session
        session.setAttribute("appliedFilters", appliedFilterSet);

        return View.ANNOTATIONS_PATH + "/" + View.ANNOTATIONS_LIST;
    }

    /**
     * Initialize some useful structures
     * @param session
     */
    private void initializeStructures(HttpSession session) {
        if (session.getAttribute("mostCommonTaxonomies") == null) {
            if (mostCommonTaxonomies.isEmpty()) {
                calculateMostCommmonTaxonomies();
                // Set top taxonomies for quick filtering
            }
            session.setAttribute("mostCommonTaxonomies", mostCommonTaxonomies);
        }

        if (session.getAttribute("evidenceTypes") == null) {
            if (evidenceTypes.isEmpty()) {
                Map<String, String> evidences = miscellaneousUtil.getEvidenceTypes();
                // Get corresponding ECO term
                for (String goEvidence : evidences.keySet()) {
                    String ecoTerm = GOEvidence2ECOMap.find(goEvidence);
                    evidenceTypes.put(ecoTerm, ecoTerm + " (" + goEvidence + ")\t" + evidences.get(goEvidence));
                }
            }
            // Set evidence types in session
            session.setAttribute("evidenceTypes", evidenceTypes);
        }

        if (session.getAttribute("assignedByDBs") == null) {
            if (assignedByDBs.isEmpty()) {
                populateByFacetField(assignedByDBs, AnnotationField.ASSIGNEDBY.getValue());
            }
            // Set DBs owner of annotations in session
            session.setAttribute("assignedByDBs", assignedByDBs);
        }

        if (session.getAttribute("withDBs") == null) {
            if (withDBs.isEmpty()) {
                withDBs.putAll(getWithDBs());
            }
            // Set DBs xrefs of annotations in session
            session.setAttribute("withDBs", withDBs);
        }
        // Calculate subsets counts
        if (session.getAttribute("allSubsetsCounts") == null) {
            if (subsetsCounts.isEmpty()) {
                subsetsCounts = miscellaneousUtil.getSubsetCount(null);
            }
            // Set subset count
            session.setAttribute("allSubsetsCounts", subsetsCounts);
        }
    }

    /**
     * Populate with facet fields
     * @param treeMap Structure to populate
     * @param field Facet field
     */
    private void populateByFacetField(TreeMap<String, String> treeMap, String field) {
        List<Count> counts = annotationService.getFacetFields("*:*", null, field, 1000);
        for (Count count : counts) {
            String dbAbbreviation = count.getName();
            String dbDescription =
                    miscellaneousUtil.getDBInformation(ClientUtils.escapeQueryChars(dbAbbreviation)).getXrefDatabase();
            treeMap.put(dbAbbreviation, dbDescription);
        }
    }

    /**
     * Get Dbs that appear in at least 1 annotation
     * @return With Dbs
     */
    private TreeMap<String, String> getWithDBs() {
        TreeMap<String, String> withs = new TreeMap<>();
        List<Miscellaneous> withDBs = miscellaneousService.getWithDBs();
        for (Miscellaneous withDB : withDBs) {
            List<Count> annotations = annotationService.getFacetFields(
                    AnnotationField.WITH.getValue() + ":"
                            + withDB.getXrefAbbreviation() + "*", null,
                    AnnotationField.WITH.getValue(), 1);
            if (annotations != null && !annotations.isEmpty()) {
                withs.put(withDB.getXrefAbbreviation() + "*",
                        withDB.getXrefDatabase());
            }
        }
        return withs;
    }

    /**
     * Calculate taxonomies names from a list of taxonomies ids
     */
    private void calculateMostCommmonTaxonomies() {
        Map<String, String> idName = miscellaneousUtil.getTaxonomiesNames(taxonomiesIds);
        for (String taxId : taxonomiesIds) {
            TopTaxonomy topTaxonomy = new TopTaxonomy();
            topTaxonomy.setId(Long.valueOf(taxId));
            topTaxonomy.setName(idName.get(String.valueOf(topTaxonomy.getId())));
            mostCommonTaxonomies.add(topTaxonomy);
        }
    }

    private void processFilters(String removeFilter, String removeAllFilters, AppliedFilterSet appliedFilterSet) {
        // Reset applied filters and top taxonomies
        if (removeAllFilters.equals("true")) {
            appliedFilterSet.setParameters(new HashMap<String, List<String>>());
        }

        // Remove filter (if any)
        if (!removeFilter.equals("")) {
            String[] idValue = null;
            if (removeFilter.startsWith(AnnotationField.QUALIFIER.getValue())) {
                idValue = removeFilter.split("-");
            } else {
                idValue = removeFilter.split("-", 2);
            }
            String key = idValue[0];
            String value = idValue[1];
            // Check "NOT" value  special case for qualifier
            if (key.equals(AnnotationField.QUALIFIER.getValue()) && value.contains("NOT")) {
                value = value.replaceAll("NOT", "\"NOT\"");
            }
            List<String> currentFilterValues = appliedFilterSet.getParameters().get(key);
            if (currentFilterValues != null && currentFilterValues.size() > 0) {
                List<String> currentValues = new ArrayList<>(currentFilterValues);
                currentValues.remove(value);// Remove filter
                appliedFilterSet.setValues(key, currentValues);
            }
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleIOException(Exception ex) {
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return "error";
    }

    public int getSelectedPage() {
        return selectedPage;
    }

    public int getSelectedRows() {
        return selectedRows;
    }

    public long getTotalNumberAnnotations() {
        return totalNumberAnnotations;
    }

    /**
     * To process annotations stats request
     * @return Annotations stats
     */
    //	@RequestMapping(value="annotation/stats", method = {RequestMethod.POST,RequestMethod.GET})
    //	public String calculateStatistics(Model model) {
    //		// Calculate stats
    //		if(statisticsCalculation != null && !statisticsCalculation.getQuery().equals(currentQuery)){
    //			statisticsCalculation.interrupt();
    //			createStatsThread(currentQuery);
    //		} else if(statisticsCalculation == null){
    //			createStatsThread(currentQuery);
    //		}
    //
    //		while (statisticsCalculation.isAlive()){
    //		}
    //		model.addAttribute("statsBean", this.statisticsBean);
    //
    //		// Set total number annotations
    //		model.addAttribute("totalNumberAnnotations", totalNumberAnnotations);
    //		long totalNumberProteins = annotationService.getTotalNumberProteins(this.currentQuery);
    //		// Set total number proteins
    //		model.addAttribute("totalNumberProteins", totalNumberProteins);
    //
    //		return View.ANNOTATIONS_PATH + "/" + View.ANNOTATIONS_LIST;
    //
    //	}

    /**
     * Download annotation statistics in excel format
     */
    @RequestMapping(value = "annotation/downloadStats", method = {RequestMethod.POST, RequestMethod.GET})
    public void downloadStatistics(
            @RequestParam(value = "categories", required = false) String categories,
            @RequestParam(value = "statsBy", required = false) String statsBy,
            Model model, HttpServletResponse httpServletResponse) {

        try {
            StatsDownload statsDownload = new StatsDownload();
            boolean byAnnotation = false, byProtein = false;
            switch (statsBy) {
                case "annotation":
                    byAnnotation = true;
                    break;
                case "protein":
                    byProtein = true;
                    break;
                case "both":
                    byProtein = true;
                    byAnnotation = true;
                    break;
            }

            // Calculate total number annotations
            long totalNumberProteins = annotationService.getTotalNumberProteins(this.currentQuery);

            ByteArrayOutputStream excelStatisticsOutputStream = statsDownload
                    .generateFile(this.statisticsBean, Arrays.asList(categories.split(",")),
                            this.totalNumberAnnotations, totalNumberProteins, byAnnotation, byProtein);

            String fileName = "annotation_statistics.xlsx";
            httpServletResponse.setContentType(contentTypeXLSX);
            httpServletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);

            OutputStream outputStream = httpServletResponse.getOutputStream();

            outputStream.write(excelStatisticsOutputStream.toByteArray());

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    /**
     * Create threads to calculate stats
     * @param solrQuery
     */
    //	private void createStatsThread(String solrQuery){
    //		statisticsBean = new StatisticsBean();
    //		statisticsCalculation = new StatisticsCalculation(statisticsBean, solrQuery);
    //		statisticsCalculation.setStatisticService(statisticService);
    //		statisticsCalculation.start();
    //	}
}
