package uk.ac.ebi.quickgo.controller.term;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.ebi.quickgo.controller.OntologyGraphController;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;
import uk.ac.ebi.quickgo.util.miscellaneous.MiscellaneousUtil;
import uk.ac.ebi.quickgo.util.term.TermUtil;
import uk.ac.ebi.quickgo.web.util.View;
import uk.ac.ebi.quickgo.web.util.term.XRefBean;
import uk.ac.ebi.quickgo.web.util.url.URLsResolverImpl;

/**
 * Term controller
 * @author cbonill
 *
 */

@Controller
public class TermController {

	@Autowired
	TermService goTermService;

	@Autowired
	TermUtil termUtil;	 
	
	@Autowired
	MiscellaneousUtil miscellaneousUtil;
	
	@Autowired
	URLsResolverImpl urLsResolver;
	
	@Autowired
	OntologyGraphController ontologyGraphController;
	
	@Autowired
	MiscellaneousService miscellaneousService;
	
	TreeSet<COOccurrenceStatsTerm> allCoOccurrenceStatsTerms = new TreeSet<>();
	
	TreeSet<COOccurrenceStatsTerm> nonIEACOOccurrenceStatistics = new TreeSet<>();

	private int NUM_VALUES = 100;
	
	@RequestMapping(value="term/{id}", method = RequestMethod.GET)
	public String termInformation(HttpSession session, HttpServletRequest request,
			@PathVariable(value="id") String id, Model model) throws UnsupportedEncodingException {
	
		
		GOTerm term = goTermService.retrieveTerm(id);		
		// Calculate ancestors graph
		ontologyGraphController.generateTermOntologyGraph(id, null, model);
		
		model.addAttribute("term", term);
		
		if(id.contains(GOTerm.GO)){			
		
			// Calculate extra information
			List<TermRelation> childTermsRelations = termUtil.calculateChildTerms(id);
			// Calculate replaces terms names
			termUtil.calculateReplacesTermsNames(term);
			// Calculate ancestors graph
			ontologyGraphController.generateTermOntologyGraph(id, null, model);
			// Calculate Xrefs URLs
			List<XRefBean> xRefBeans = urLsResolver.calculateXrefsUrls(term.getXrefs());
			// Calculate subsets counts
			List<Miscellaneous> subsetsCounts = miscellaneousUtil.getSubsetCount(term.getSubsetsNames());
			// Co-occurring stats
			allCoOccurrenceStatsTerms = (TreeSet)miscellaneousService.allCOOccurrenceStatistics(id.replaceAll("GO:", ""));
			nonIEACOOccurrenceStatistics = (TreeSet)miscellaneousService.nonIEACOOccurrenceStatistics(id.replaceAll("GO:", ""));
			
			model.addAttribute("termXrefs", xRefBeans);
			model.addAttribute("childTermsRelations", childTermsRelations);
			model.addAttribute("subsetsCounts", subsetsCounts);
			
			// All stats
			List<COOccurrenceStatsTerm> allStats = new ArrayList<>();
			allStats.addAll(allCoOccurrenceStatsTerms);
			allStats = getFirstOnes(allStats);
			processStats(allStats);
			model.addAttribute("allCoOccurrenceStatsTerms", allStats);
			
			
			// Non-IEA stats
			List<COOccurrenceStatsTerm> nonIEAStats = new ArrayList<>();
			nonIEAStats.addAll(nonIEACOOccurrenceStatistics);
			nonIEAStats = getFirstOnes(nonIEAStats);
			processStats(nonIEAStats);
			model.addAttribute("nonIEACOOccurrenceStatistics", nonIEAStats);		
		}
		
		// Is GO term attribute 
		model.addAttribute("isGO", id.contains(GOTerm.GO));
		
		return View.TERMS_PATH + "/" + View.TERM;
	}
	
	/**
	 * Return list of terms by ontology (Bot-friendly page) 
	 * @param ontology Ontology
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="terms/{ontology}", method = RequestMethod.GET)
	public String ontologyTerms(HttpSession session, HttpServletRequest request,
			@PathVariable(value="ontology") String ontology, Model model) throws UnsupportedEncodingException {
		
		EGOAspect aspect = EGOAspect.valueOf(ontology);
		Map<String,String> terms = uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTermsByOntology(aspect);
		model.addAttribute("ontology", aspect.description);
		model.addAttribute("terms", terms);
		return View.TERMS_PATH + "/" + View.ONTOLOGY_TERMS;
	}		
	
	
	/**
	 * Get first stats values (by default 100)
	 * @param stats Stats values
	 */
	public List<COOccurrenceStatsTerm> getFirstOnes(List<COOccurrenceStatsTerm> stats){
		if (stats != null && stats.size() > 0) {
			int end = calculateEnd(stats.size());
			stats = new ArrayList<>(stats.subList(0, end));
		}
		return stats;
	}
	
	/**
	 * Calculate number term to display for co-occurring term stats
	 * @param size Size of stats
	 * @return Num terms to display
	 */
	private int calculateEnd(int size) {
		int end = NUM_VALUES;
		if (size < NUM_VALUES) {
			end = size - 1;
		}
		return end;
	}
	
	/**
	 * Process stats values
	 * @param stats Stats to process
	 */
	private void processStats(List<COOccurrenceStatsTerm> stats){		
			for (COOccurrenceStatsTerm coOccurrenceStatsTerm : stats) {				
					String comparedId = "GO:" + coOccurrenceStatsTerm.getComparedTerm();
					coOccurrenceStatsTerm.setComparedTerm(comparedId);
					if (uk.ac.ebi.quickgo.web.util.term.TermUtil.getGOTerms().get(comparedId) != null) {
						coOccurrenceStatsTerm
								.setAspect(((GOTerm) uk.ac.ebi.quickgo.web.util.term.TermUtil
										.getGOTerms().get(comparedId)).getAspect().abbreviation);
						coOccurrenceStatsTerm
								.setName(uk.ac.ebi.quickgo.web.util.term.TermUtil
										.getGOTerms().get(comparedId).getName());
					}				
			}		
	}
}