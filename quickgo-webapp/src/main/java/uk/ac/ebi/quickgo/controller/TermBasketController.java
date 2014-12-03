package uk.ac.ebi.quickgo.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.web.util.View;

/**
 * Controller responsible for the term basket functionality
 * @author cbonill
 *
 */
@Controller
@Scope("session")
public class TermBasketController implements Serializable{
	
	private static final long serialVersionUID = 6486307441871971922L;

	@Autowired
	TermService termService;
	
	// All terms ids and names
	private static Map<String, Map<String, String>> allTermNames = new HashMap<>();
		
	// Basket terms ids and names
	private Map<String, String> basketTerms = new HashMap<>();
	
	
	/**
	 * Add term/s to the term basket (root and annotation context)
	 * @param id Term id
	 * @param httpRequest Http request
	 * @param session Http session
	 */
	@RequestMapping(value = { "/","annotation" }, method = {RequestMethod.POST, RequestMethod.GET }, params = { "addTerm", "page" })
	public String addTerm(
			@RequestParam(value = "addTerm", defaultValue = "", required=true) String addTermId,			
			HttpServletRequest httpRequest, HttpSession session, Model model) {
		
		// Get all term names 
		if (allTermNames.isEmpty()) {
			allTermNames = termService.retrieveNames();
		}
		
		// Term is not empty
		if (addTermId != null && !addTermId.isEmpty()) {			
			if(addTermId.contains(",")){// List of values
				String[] terms = addTermId.split(",");
				for(String term : terms){
					if(!term.isEmpty() && term.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}")){
						basketTerms.put(term, (String)allTermNames.get(term).values().toArray()[0]);
					}
				}
			} else if (addTermId.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}")){//1 value
				basketTerms.put(addTermId, (String)allTermNames.get(addTermId).values().toArray()[0]);
			}			
			session.setAttribute("basketTerms", basketTerms);			
		}
		return redirect(httpRequest, null, model);
	}	
	
	/**
	 * Add term/s to the term basket (term context)
	 * @param id Term id
	 * @param httpRequest Http request
	 * @param session Http session
	 */
	@RequestMapping(value = {"term/{id}"}, method = {RequestMethod.POST, RequestMethod.GET }, params = { "addTerm", "page" })
	public String addTerm(
			@RequestParam(value = "addTerm", defaultValue = "", required=true) String addTermId,
			@PathVariable(value="id") String id,
			HttpServletRequest httpRequest, HttpSession session, Model model) {
		
		addTerm(addTermId, httpRequest, session, model);
		return redirect(httpRequest, id, model);
	}

	/**
	 * Remove a term for the term basket (root and annotation context)
	 * @param removeTerm Term to remove
	 * @param session Http session
	 */
	@RequestMapping(value = { "/","annotation"}, method = {RequestMethod.POST, RequestMethod.GET }, params = { "removeTerm",
			"page" })
	public String removeTerm(
			@RequestParam(value = "removeTerm", defaultValue = "", required=true) String removeTerm, HttpServletRequest httpRequest,
			HttpSession session, Model model) {
		if(removeTerm != null && !removeTerm.isEmpty()){
			if(removeTerm.equalsIgnoreCase("all")){
				basketTerms.clear();
			} else if (removeTerm.matches(AnnotationParameters.GO_ID_REG_EXP + "\\d{7}")){//1 value
				basketTerms.remove(removeTerm);
			}
			session.setAttribute("basketTerms", basketTerms);
		}
		return redirect(httpRequest, null, model);
	}
	
	/**
	 * Remove a term for the term basket (term context)
	 * @param removeTerm Term to remove
	 * @param session Http session
	 */
	@RequestMapping(value = { "term/{id}" }, method = {RequestMethod.POST, RequestMethod.GET }, params = { "removeTerm",
			"page" })
	public String removeTerm(
			@RequestParam(value = "removeTerm", defaultValue = "", required=true) String removeTerm, HttpServletRequest httpRequest,
			@PathVariable(value="id") String id,
			HttpSession session, Model model) {
		removeTerm(removeTerm, httpRequest, session, model);
		return redirect(httpRequest, id, model);
	}
	
	/**
	 * Redirect to term or annotation page depending on request
	 * @param httpRequest
	 * @return Destination page
	 */
	private String redirect(HttpServletRequest httpRequest,String id, Model model) {
		if (id != null) {
			model.addAttribute("term", termService.retrieveTerm(id));
		}
		if (httpRequest.getServletPath().contains("/" + View.TERMS_PATH)) {
			return View.TERMS_PATH + "/" + View.TERM;
		} else if(httpRequest.getServletPath().contains("/" + View.ANNOTATIONS_PATH)){
			return View.ANNOTATIONS_PATH + "/" + View.ANNOTATIONS_LIST;
		} else {
			return View.INDEX;
		}
	}
}
