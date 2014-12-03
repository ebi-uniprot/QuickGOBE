package uk.ac.ebi.quickgo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.quickgo.web.util.SlimmingUtil;
import uk.ac.ebi.quickgo.web.util.View;

/**
 * To generate ontology graphs 
 * @author cbonill
 *
 */

@Controller
@Scope("session")
public class SlimmingGraphController {
	
	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "inactiveSlimmingGraphTermId" })
	public String inactivateSlimmingTerm(
			@RequestParam(value = "inactiveSlimmingGraphTermId", defaultValue = "", required = false) String inactiveSlimmingGraphTermId,
			HttpServletRequest httpRequest, HttpSession session, Model model) {
			
		Map<String, String> activeSlimmingGraphTerms = getTermsFromSession(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, session);
		
		activeSlimmingGraphTerms.remove(inactiveSlimmingGraphTermId);				
		session.setAttribute(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, activeSlimmingGraphTerms);
				
		return View.INDEX;
	}
	
	
	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "activeSlimmingGraphTermId" })
	public String activateSlimmingTerm(
			@RequestParam(value = "activeSlimmingGraphTermId", defaultValue = "", required = false) String activeSlimmingGraphTermId,
			HttpServletRequest httpRequest, HttpSession session, Model model) {
		
		Map<String, String> slimmingTerms = getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);
		Map<String, String> activeSlimmingGraphTerms = getTermsFromSession(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, session);
		
		activeSlimmingGraphTerms.put(activeSlimmingGraphTermId, slimmingTerms.get(activeSlimmingGraphTermId));		
		session.setAttribute(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, activeSlimmingGraphTerms);
				
		return View.INDEX;
	}
	
	/**
	 * Get terms attribute from session
	 * @param attribute Attibute to get from session
	 * @param session
	 * @return
	 */
	private Map<String, String> getTermsFromSession(String attribute, HttpSession session) {
		Map<String, String> terms = (HashMap<String, String>) session.getAttribute(attribute);
		if (terms == null) {
			return new HashMap<String, String>();
		}
		return terms;
	}
}