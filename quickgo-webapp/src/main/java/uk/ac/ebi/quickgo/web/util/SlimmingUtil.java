package uk.ac.ebi.quickgo.web.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Useful class for slimming
 * @author cbonill
 */
public class SlimmingUtil {

	// Own terms added 
	public static  final String OWN_TERMS_ADDED = "slimmingOwnTerms";
	
	// Terms added from the term basket
	public static  final String BASKET_TERMS_ADDED = "slimmingBasketTerms";	
	
	// Terms added from predefined sets
	public static  final String SETS_TERMS_ADDED = "setsSlimmingTerms";
			
	// Unselected terms
	public static  final String INACTIVE_SLIMMING_TERMS_ATTRIBUTE = "inactiveSlimmingTerms";
		
	//Molecular Function terms 
	public static  final String MF_SLIMMING_TERMS_ATTRIBUTE = "mf_slimmingTerms";
		
	//Cellular Component terms
	public static  final String CC_SLIMMING_TERMS_ATTRIBUTE = "cc_slimmingTerms";
		
	//Biological Process terms
	public static  final String BP_SLIMMING_TERMS_ATTRIBUTE = "bp_slimmingTerms";
	
	// Final selected terms
	public static final String SLIMMING_TERMS_ATTRIBUTE = "slimmingTerms";

	// Unselected terms to visualise
	public static final String ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE = "activeSlimmingGraphTerms";
	
	/**
	 * Remove all sliming attributes
	 */
	public static void removeAllSlimAttributes(HttpSession session){
		session.setAttribute(OWN_TERMS_ADDED, null);
		session.setAttribute(BASKET_TERMS_ADDED, null);
		session.setAttribute(SETS_TERMS_ADDED, null);
		
		session.setAttribute(MF_SLIMMING_TERMS_ATTRIBUTE, null);
		session.setAttribute(CC_SLIMMING_TERMS_ATTRIBUTE, null);
		session.setAttribute(BP_SLIMMING_TERMS_ATTRIBUTE, null);
		
		session.setAttribute(SlimmingUtil.SLIMMING_TERMS_ATTRIBUTE, null);
		session.setAttribute(INACTIVE_SLIMMING_TERMS_ATTRIBUTE, null);
				
		session.setAttribute(SlimmingUtil.ACTIVE_SLIMMING_GRAPH_TERMS_ATTRIBUTE, null);
		
		session.setAttribute("selectedSet", "");
	}
	
	/**
	 * Get terms attribute from session
	 * @param attribute Attibute to get from session
	 * @param session
	 * @return
	 */
	public static Map<String, String> getTermsFromSession(String attribute, HttpSession session) {
		Map<String, String> terms = (HashMap<String, String>) session.getAttribute(attribute);
		if (terms == null) {
			return new HashMap<String, String>();
		}
		return terms;
	}
}
