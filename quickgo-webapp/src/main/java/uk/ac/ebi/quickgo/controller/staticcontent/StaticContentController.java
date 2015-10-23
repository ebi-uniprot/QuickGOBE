package uk.ac.ebi.quickgo.controller.staticcontent;

import uk.ac.ebi.quickgo.ontology.generic.AuditRecord;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationBlackListContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.AnnotationPostProcessingContent;
import uk.ac.ebi.quickgo.web.staticcontent.annotation.TaxonConstraintsContent;
import uk.ac.ebi.quickgo.web.util.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class StaticContentController {

    @Autowired
    AnnotationService annotationService;

    @Autowired
    TermService goTermService;

    @Autowired
    AnnotationBlackListContent annotationBlackListContent;

    @Autowired
    AnnotationPostProcessingContent annotationPostProcessingContent;

    static List<Count> assignedByCount = new ArrayList<>();

    @RequestMapping(value = {"/dataset"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getAnnotationUpdates(Model model) {

        // Get AssignedBy counts
        if (assignedByCount.isEmpty()) {
            assignedByCount =
                    annotationService.getFacetFields("*:*", null, AnnotationField.ASSIGNEDBY.getValue(), 1000);
        }

        model.addAttribute("assignedByCount", assignedByCount);

        return new ModelAndView(View.STATICPAGES_PATH + "/" + View.DATASET);
    }

    @RequestMapping(value = {"/dataset/annotationBlacklist"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getAnnotationBlacklist(Model model) {
        model.addAttribute("ieaReview", annotationBlackListContent.getIEAReview());
        model.addAttribute("notQualified", annotationBlackListContent.getBlackListNotQualified());
        model.addAttribute("uniprotCaution", annotationBlackListContent.getBlackListUniProtCaution());

        return new ModelAndView(View.STATICPAGES_PATH + "/" + View.ANNOTATIONBLACKLIST);
    }

    @RequestMapping(value = {"/dataset/annotationPostProcessing"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getAnnotationPostProcessing(Model model) {
        model.addAttribute("postProcessingRules", annotationPostProcessingContent.getPostProcessingRules());

        return new ModelAndView(View.STATICPAGES_PATH + "/" + View.ANNOTATIONPOSTPROCESSING);
    }

    @RequestMapping(value = {"/webservices"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getWebServicesPage(Model model) {

        return new ModelAndView(View.STATICPAGES_PATH + "/" + View.WEBSERVICES);
    }

    @RequestMapping(value = {"/dataset/goTermHistory"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getGoTermsHistory(Model model,
            @RequestParam(value = "from", defaultValue = "2013-01-01", required = false) String from,
            @RequestParam(value = "to", defaultValue = "NOW", required = false) String to,
            @RequestParam(value = "limit", defaultValue = "500", required = false) String limit) {

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

        for (GOTerm term : historyChanges) {
            allChanges.addAll(term.history.auditRecords);
            termsRecords.addAll(term.history.getHistoryTerms());
            definitionsRecords.addAll(term.history.getHistoryDefinitions());
            relationsRecords.addAll(term.history.getHistoryRelations());
            xrefRecords.addAll(term.history.getHistoryXRefs());
        }

        model.addAttribute("from", from);
        model.addAttribute("allChanges", allChanges);
        model.addAttribute("termsRecords", termsRecords);
        model.addAttribute("definitionsRecords", definitionsRecords);
        model.addAttribute("relationsRecords", relationsRecords);
        model.addAttribute("xrefRecords", xrefRecords);

        return new ModelAndView(View.STATICPAGES_PATH + "/" + View.GOTERMHISTORY);
    }

    @RequestMapping(value = {"/dataset/taxonConstraints"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView getTaxonConstraints(Model model) {
        model.addAttribute("taxonConstraints", TaxonConstraintsContent.getTaxonConstraints());

        return new ModelAndView(View.STATICPAGES_PATH + "/" + View.TAXON_CONSTRAINTS);
    }
}
