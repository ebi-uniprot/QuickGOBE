package uk.ac.ebi.quickgo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.SlimmingUtil;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.WebUtils;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

/**
 * Controller for slimming process
 *
 * @author cbonill
 *
 */
@Controller
@Scope("session")
public class SlimmingController {

	// All go terms
	Map<String, GenericTerm> terms = TermUtil.getGOTerms();

	// Structure contains associated terms to each predefined set (to avoid recalculating it)
	public static Map<String, Map<String,String>> predefindSetTerms = new HashMap<String, Map<String,String>>();


	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "ownTerms" })
	public String addOwnTerms(
			@RequestParam(value = "ownTerms", defaultValue = "", required = false) String termsIds,
			HttpServletRequest httpRequest, HttpSession session, Model model) {
		Map<String, String> ownTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.OWN_TERMS_ADDED, session);
		Map<String, String> slimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);
		List<String> terms = WebUtils.parseAndFormatFilterValues(termsIds);
		Map<String, String> idName = calculateNames(terms);
		ownTerms.putAll(idName);
		session.setAttribute(SlimmingUtil.OWN_TERMS_ADDED, ownTerms);
		slimmingTerms.putAll(idName);
		session.setAttribute(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, slimmingTerms);

		setSlimmingTermsByAspect(session);
		return View.INDEX;

	}

	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "basketTerms" })
	public String addBasketTerms(
			@RequestParam(value = "basketTerms", defaultValue = "", required = false) String termsIds,
			HttpServletRequest httpRequest, HttpSession session, Model model) {
		Map<String, String>  basketTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.BASKET_TERMS_ADDED, session);
		Map<String, String> slimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);
		List<String> terms = WebUtils.parseAndFormatFilterValues(termsIds);
		Map<String, String> idName = calculateNames(terms);
		basketTerms.putAll(idName);
		session.setAttribute(SlimmingUtil.BASKET_TERMS_ADDED, basketTerms);
		slimmingTerms.putAll(idName);
		session.setAttribute(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, slimmingTerms);

		setSlimmingTermsByAspect(session);
		return View.INDEX;

	}

	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "set" })
	public String addPredefinedSetTerms(
			@RequestParam(value = "set", defaultValue = "", required = false) String setName,
			HttpServletRequest httpRequest, HttpSession session, Model model) {
		Map<String, String>  predefinedSetTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SETS_TERMS_ADDED, session);
		Map<String, String> slimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);

		Map<String, String> setTerms = new HashMap<>();
		if (setName != null && !setName.isEmpty()) {

			if (!predefindSetTerms.containsKey(setName)) {// Check if it was calculated before
				for (GenericTerm goTerm : terms.values()) {
					if (goTerm.getSubsetsNames().contains(setName)) {
						setTerms.put(goTerm.getId(), goTerm.getName());
					}
				}
				predefindSetTerms.put(setName, setTerms);
			} else {// Get cached values
				setTerms = predefindSetTerms.get(setName);
			}
			predefinedSetTerms.putAll(setTerms);
			session.setAttribute(SlimmingUtil.SETS_TERMS_ADDED, predefinedSetTerms);
			session.setAttribute("selectedSet", setName);
			slimmingTerms.putAll(setTerms);
			session.setAttribute(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, slimmingTerms);
		}

		setSlimmingTermsByAspect(session);
		return View.INDEX;
	}

	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "inactiveSlimmingTermId" })
	public String inactivateSlimmingTerm(
			@RequestParam(value = "inactiveSlimmingTermId", defaultValue = "", required = false) String inactiveSlimmingTermId,
			HttpServletRequest httpRequest, HttpSession session, Model model) {

		Map<String, String> slimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);

		Map<String, String> inactiveSlimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.INACTIVE_SLIMMING_TERMS_ATTRIBUTE, session);

		if (inactiveSlimmingTermId.equals(EGOAspect.F.abbreviation)) {
			inactiveSlimmingTerms.putAll(SlimmingUtil.getTermsFromSession(SlimmingUtil.MF_SLIMMING_TERMS_ATTRIBUTE, session));
		} else if (inactiveSlimmingTermId.equals(EGOAspect.P.abbreviation)) {
			inactiveSlimmingTerms.putAll(SlimmingUtil.getTermsFromSession(SlimmingUtil.BP_SLIMMING_TERMS_ATTRIBUTE, session));
		} else if (inactiveSlimmingTermId.equals(EGOAspect.C.abbreviation)) {
			inactiveSlimmingTerms.putAll(SlimmingUtil.getTermsFromSession(SlimmingUtil.CC_SLIMMING_TERMS_ATTRIBUTE, session));
		} else { // GO term
			inactiveSlimmingTerms.put(inactiveSlimmingTermId, slimmingTerms.get(inactiveSlimmingTermId));
		}
		session.setAttribute(SlimmingUtil.INACTIVE_SLIMMING_TERMS_ATTRIBUTE, inactiveSlimmingTerms);

		setSlimmingTermsByAspect(session);
		return View.INDEX;
	}


	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "activeSlimmingTermId" })
	public String activateSlimmingTerm(
			@RequestParam(value = "activeSlimmingTermId", defaultValue = "", required = false) String activeSlimmingTermId,
			HttpServletRequest httpRequest, HttpSession session, Model model) {

		Map<String, String> inactiveSlimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.INACTIVE_SLIMMING_TERMS_ATTRIBUTE, session);
		if (activeSlimmingTermId.equals(EGOAspect.F.abbreviation)) {
			inactiveSlimmingTerms.keySet().removeAll(SlimmingUtil.getTermsFromSession(SlimmingUtil.MF_SLIMMING_TERMS_ATTRIBUTE, session).keySet());
		} else if (activeSlimmingTermId.equals(EGOAspect.P.abbreviation)) {
			inactiveSlimmingTerms.keySet().removeAll(SlimmingUtil.getTermsFromSession(SlimmingUtil.BP_SLIMMING_TERMS_ATTRIBUTE, session).keySet());
		} else if (activeSlimmingTermId.equals(EGOAspect.C.abbreviation)) {
			inactiveSlimmingTerms.keySet().removeAll(SlimmingUtil.getTermsFromSession(SlimmingUtil.CC_SLIMMING_TERMS_ATTRIBUTE, session).keySet());
		} else { // GO term
			inactiveSlimmingTerms.remove(activeSlimmingTermId);
		}
		session.setAttribute(SlimmingUtil.INACTIVE_SLIMMING_TERMS_ATTRIBUTE, inactiveSlimmingTerms);

		setSlimmingTermsByAspect(session);
		return View.INDEX;
	}

	@RequestMapping(value = { "/", "annotation" }, method = {
			RequestMethod.POST, RequestMethod.GET }, params = { "removeAll" })
	public String removeAll(@RequestParam(value = "removeAll", defaultValue = "true", required = false) String removeAll,
			HttpServletRequest httpRequest, HttpSession session, Model model) {

		SlimmingUtil.removeAllSlimAttributes(session);

		return View.INDEX;
	}


	@RequestMapping(value = { "/", "annotation" }, method = {RequestMethod.POST, RequestMethod.GET }, params = { "slim", "proteinIds", "proteinSets" })
	public ModelAndView slim(@RequestParam(value = "slim", defaultValue = "true", required = false) String slim,
			@RequestParam(value = "proteinIds", defaultValue = "", required = false) String proteinIds,
			@RequestParam(value = "proteinSets", defaultValue = "", required = false) String proteinSets,
			HttpServletRequest httpRequest, HttpSession session, Model model) {

		// Go Ids
		Map<String, String> slimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);
		Map<String, String> inactiveSlimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.INACTIVE_SLIMMING_TERMS_ATTRIBUTE, session);
		slimmingTerms.keySet().removeAll(inactiveSlimmingTerms.keySet());//Active slimming terms
		AppliedFilterSet appliedFilterSet = new AppliedFilterSet();
		Map<String, List<String>> goSlimmingTerms = new HashMap<>();
		goSlimmingTerms.put(AnnotationField.ANCESTORSIPO.getValue(), new ArrayList<>(slimmingTerms.keySet()));
		appliedFilterSet.addParameters(goSlimmingTerms);
		// Proteins Ids
		if(!proteinIds.trim().isEmpty()){
			List<String> proteinsIds = WebUtils.parseAndFormatFilterValues(proteinIds);
			Map<String, List<String>> proteins = new HashMap<>();
			proteins.put(AnnotationField.DBOBJECTID.getValue(), proteinsIds);
			appliedFilterSet.addParameters(proteins);
		}
		// Proteins Sets
		if(!proteinSets.trim().isEmpty()){
			List<String> proteinsSets = WebUtils.parseAndFormatFilterValues(proteinSets);
			Map<String, List<String>> sets = new HashMap<>();
			sets.put(AnnotationField.TARGETSET.getValue(), proteinsSets);
			appliedFilterSet.addParameters(sets);
		}

		session.setAttribute("appliedFilters", appliedFilterSet);

        return new ModelAndView("redirect:" + View.ANNOTATIONS_PATH);
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

	/**
	 * Set slimming terms by aspect
	 * @param session
	 * @param model
	 */
	private void setSlimmingTermsByAspect(HttpSession session){
		Map<String, String>  slimmingTerms = SlimmingUtil.getTermsFromSession(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, session);
		Map<String, String>  mf_slimmingTerms = new HashMap<String, String>();
		Map<String, String>  bp_slimmingTerms = new HashMap<String, String>();
		Map<String, String>  cc_slimmingTerms = new HashMap<String, String>();

		for (String termId : slimmingTerms.keySet()) {
			if (((GOTerm) terms.get(termId)).getAspect() == EGOAspect.F) {
				mf_slimmingTerms.put(termId, slimmingTerms.get(termId));
			} else if (((GOTerm) terms.get(termId)).getAspect() == EGOAspect.C) {
				cc_slimmingTerms.put(termId, slimmingTerms.get(termId));
			} else if (((GOTerm) terms.get(termId)).getAspect() == EGOAspect.P) {
				bp_slimmingTerms.put(termId, slimmingTerms.get(termId));
			}
		}
		session.setAttribute(SlimmingUtil.MF_SLIMMING_TERMS_ATTRIBUTE, mf_slimmingTerms);
		session.setAttribute(SlimmingUtil.CC_SLIMMING_TERMS_ATTRIBUTE, cc_slimmingTerms);
		session.setAttribute(SlimmingUtil.BP_SLIMMING_TERMS_ATTRIBUTE, bp_slimmingTerms);
	}
}
