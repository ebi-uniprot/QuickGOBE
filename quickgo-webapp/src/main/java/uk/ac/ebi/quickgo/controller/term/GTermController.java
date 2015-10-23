package uk.ac.ebi.quickgo.controller.term;

import uk.ac.ebi.quickgo.controller.WebServiceController;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.render.Format;
import uk.ac.ebi.quickgo.util.term.TermUtil;
import uk.ac.ebi.quickgo.web.util.View;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to keep the functionality of the old version QuickGO GTerm web service 
 * @author cbonill
 *
 */
@RestController
public class GTermController {

    @Autowired
    WebServiceController webServiceController;

    @Autowired
    TermUtil termUtil;

    /**
     * Old QuickGO lookup term web service
     * @param format Response format
     * @param id Id to lookup
     * @param httpServletResponse Servlet response
     * @throws IOException
     */
    @RequestMapping("/GTerm")
    public String lookup(@RequestParam(value = "format", required = false, defaultValue = "json") String format,
            @RequestParam(value = "id", required = true, defaultValue = "") String id,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model)
            throws IOException {

        if (format.equalsIgnoreCase(Format.MINI.name())) {
            GenericTerm genericTerm = uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTerms().get(id);
            // Calculate extra information
            List<TermRelation> childTermsRelations = termUtil.calculateChildTerms(id);
            model.addAttribute("term", genericTerm);
            model.addAttribute("childTermsRelations", childTermsRelations);
            return View.TERMS_PATH + "/" + View.TERM_WS;
        } else {
            webServiceController.lookup(format, id, "go", httpServletRequest, httpServletResponse);
            return null;
        }
    }
}
