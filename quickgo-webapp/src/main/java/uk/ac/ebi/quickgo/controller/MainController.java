package uk.ac.ebi.quickgo.controller;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.web.util.SlimmingUtil;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Index page controller
 *
 * @author cbonill
 *
 */

@RestController
@RequestMapping("/")
public class MainController {

    @Autowired
    MiscellaneousUtil miscellaneousUtil;

    // Subsets counts
    private static List<Miscellaneous> subsetsCounts = new ArrayList<>();

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session, HttpServletRequest httpServletRequest) {

        // Calculate subsets counts for slimming
        if (session.getAttribute("allSubsetsCounts") == null) {
            if (subsetsCounts.isEmpty()) {
                subsetsCounts = miscellaneousUtil.getSubsetCount(null);
            }
            // Set subset count
            session.setAttribute("allSubsetsCounts", subsetsCounts);
        }

        // Remove all previous applied filters
        session.removeAttribute("appliedFilters");

        // Remove searched text value
        session.removeAttribute("searchedText");

        // Remove all sliming attributes
        SlimmingUtil.removeAllSlimAttributes(session);

        // Load GO terms and calculate terms by ontology
        TermUtil.getGOOntology();

        return View.INDEX;
    }
}
