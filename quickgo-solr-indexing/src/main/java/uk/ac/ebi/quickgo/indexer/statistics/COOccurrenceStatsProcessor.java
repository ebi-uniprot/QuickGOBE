package uk.ac.ebi.quickgo.indexer.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousValue;
import uk.ac.ebi.quickgo.solr.query.service.annotation.AnnotationRetrieval;
import uk.ac.ebi.quickgo.statistic.COOccurrenceStatsTerm;

import com.google.common.collect.Sets;

/**
 * Thread responsible for calculating the co-occurrence stats of a given term
 * @author cbonill
 *
 */
public class COOccurrenceStatsProcessor extends Thread {

	// Log
	private static final Logger logger = Logger.getLogger(COOccurrenceStatsProcessor.class);
	
	// Annotation retrieval
	AnnotationRetrieval annotationRetrieval;
	
	// Term id to process
	String ontologyTermID;
	
	// Stats terms
	TreeSet<COOccurrenceStatsTerm> statsTerms = new TreeSet<COOccurrenceStatsTerm>();
	
	// Contains number of annotated proteins for each GO Term
	Map<String,Float> termsNumberProteins = new HashMap<String, Float>(); 

	// Indicates if we are taking into account electronic annotations or not
	boolean nonIEA;
	
	// Total number of annotated proteins
	float totalNumberProteins = 0;
	
	/**
	 * List of proteins annotated to each term taking into account the entire set of annotations
	 */
	Map<String, Set<String>> allProteinsByTerm = new HashMap<String, Set<String>>();
	
	/**
	 * List of proteins annotated to each term taking into account non electronic annotations
	 */
	Map<String, Set<String>> nonIEAProteinsByTerm = new HashMap<String, Set<String>>();
	
	// Non electronic annotations query (if necessary)
	String nonIEAquery = "";
	
	// Type of the statistics to calculate ("all" or "nonIEA")
	String type = MiscellaneousValue.ALL.getValue();
	
	// Size of proteins sublist query
	private final int QUERY_SIZE = 500;
	
	public void run() {
		try {			
			if (nonIEA) {// If non electronic annotations, add the restriction
				nonIEAquery = " AND NOT " + AnnotationField.GOEVIDENCE.getValue() + ":" + "IEA";
				type = MiscellaneousValue.NON_IEA.getValue();
			}
	
			// Get proteins annotated with the specified GO term
			List<String> proteins = getGOIDAnnotatedProteins();
			Collections.sort(proteins);

			// To store stats for each term
			TreeMap<String, COOccurrenceStatsTerm> termsCount = new TreeMap<>();
			
			int start = 0;
			int end = QUERY_SIZE;
			float selected = 0;
			// Split the proteins in chunks of 700 proteins (Solr by default doesn't allow "OR" queries bigger than 1024 values)
			TreeSet<String> goTermIDS = new TreeSet<String>();
			while (start < proteins.size()) {
				if (end > proteins.size()) {
					end = proteins.size();
				}
				// Create sub list of proteins to query
				List<String> proteinsSubList = proteins.subList(start, end);
				//Escape proteins identifiers
				List<String> escapedProteins = new ArrayList<>();
				for(String protein : proteinsSubList){
					escapedProteins.add(ClientUtils.escapeQueryChars(protein));
				}
				String proteinValues = "(" + StringUtils.arrayToDelimitedString(escapedProteins.toArray(), " OR ") + ")";

				// Get GO IDS annotated with the same proteins as the go term we are processing
				goTermIDS.addAll(getTermsAnnotatedSameProteins(proteinValues));					
				
				start = end;
				end = end + QUERY_SIZE;			
			}
	
			// Calculate stats values for each GO Term
			Set<String> proteinSet = new HashSet<>(proteins);
			goTermIDS.remove("go");// Remove "go" term
			for (String goTerm : goTermIDS) {
				float compared = calculateComparedValue(goTerm, termsCount);
				float together = getTogether(goTerm, proteinSet);
				if (goTerm.equals(ontologyTermID)) {					
					selected = together;
				}
				// Build the stats object
				COOccurrenceStatsTerm coOccurrenceStatsTerm = buildCoOccurrenceStatsTerm(goTerm, termsCount, together, compared);
				termsCount.put(goTerm, coOccurrenceStatsTerm);
			}
			
			// Set "selected" value and sort the set
			List<COOccurrenceStatsTerm> coOccurrenceStatsTermsList = new ArrayList<>(termsCount.values());
			for (COOccurrenceStatsTerm coOccurrenceStatsTerm : coOccurrenceStatsTermsList) {
				coOccurrenceStatsTerm.setSelected(selected);				
			}
			statsTerms = new TreeSet<COOccurrenceStatsTerm>(coOccurrenceStatsTermsList);
			statsTerms.descendingSet();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Calculate "compared" value
	 * @param goTerm GO Term to 
	 * @param termsCount
	 * @return Count of proteins where compared term is annotated
	 * @throws SolrServerException
	 */
	private float calculateComparedValue(String goTerm, TreeMap<String, COOccurrenceStatsTerm> termsCount) throws SolrServerException {
		float compared = 0;
		if (termsCount.get(goTerm) == null) {// GO term not processed yet
			if (termsNumberProteins.get(goTerm) == null) {// Total number proteins not calculated yet
				compared = getCompared(goTerm);
				termsNumberProteins.put(goTerm, compared);
			} else {
				compared = termsNumberProteins.get(goTerm);
			}
		}
		return compared;
	}

	/**
	 * Calculate GO IDS annotated with the same proteins as the go term we are processing
	 * @param proteinValues Processed go term proteins
	 * @return GO IDS annotated with the same proteins as the processed go term
	 * @throws SolrServerException
	 */
	private List<String> getTermsAnnotatedSameProteins(String proteinValues) {
		List<String> goTermIDS = new ArrayList<>();		
		List<Count> goTerms = new ArrayList<>();
		try {
			goTerms.addAll(annotationRetrieval.getFacetFields(AnnotationField.DBOBJECTID.getValue() + ":" + proteinValues + nonIEAquery, null, AnnotationField.GOID.getValue(), Integer.MAX_VALUE));
		} catch (SolrServerException e) {
			System.out.println("query to run: " + AnnotationField.DBOBJECTID.getValue() + ":" + proteinValues + nonIEAquery);
			e.printStackTrace();
		}

		// Get GO Term names
		for (Count count : goTerms) {
			goTermIDS.add(count.getName());
		}
		return goTermIDS;
	}

	/**
	 * Calculate count of proteins where both selected and compared terms are annotated
	 * @param goTerm Compared term
	 * @param proteins Selected term proteins
	 * @return Count of proteins where both selected and compared terms are annotated
	 * @throws SolrServerException
	 */
	private float getTogether(String goTerm, Set<String> proteins) throws SolrServerException{
		if (goTerm.equals(ontologyTermID)) {
			return proteins.size();
		}
		Set<String> comparedTermproteins = new HashSet<String>();
		Map<String, Set<String>> proteinsByTerm = allProteinsByTerm;
		
		if (nonIEA) {
			proteinsByTerm = nonIEAProteinsByTerm;
		}		
		if (proteinsByTerm.get(goTerm) != null) {// Previously calculated
			comparedTermproteins = proteinsByTerm.get(goTerm);
		} else {			
				List<Count> counts = annotationRetrieval.getFacetFields(
						AnnotationField.GOID.getValue() + ":"
								+ ClientUtils.escapeQueryChars(goTerm)
								+ nonIEAquery, null,
						AnnotationField.DBOBJECTID.getValue(), Integer.MAX_VALUE);
				for (Count count : counts) {
					comparedTermproteins.add(count.getName());
				}				
			// Put into corresponding set
			if (nonIEA) {
				nonIEAProteinsByTerm.put(goTerm, comparedTermproteins);
			} else {
				allProteinsByTerm.put(goTerm, comparedTermproteins);
			}
		}
		// Calculate proteins in common
		if(proteins.size() > comparedTermproteins.size()){// The intersection method is faster when the smaller set is the first		
			return Sets.intersection(comparedTermproteins, proteins).size();			
		} else {
			return Sets.intersection(comparedTermproteins, proteins).size();
		}		
	}
	
	/**
	 * Calculate count of proteins where compared term is annotated
	 * @param goTerm GO Term to query
	 * @return Count of proteins where compared term is annotated
	 * @throws SolrServerException
	 */
	private float getCompared(String goTerm) throws SolrServerException{
		return annotationRetrieval.getTotalNumberProteins(AnnotationField.GOID.getValue() + ":" + ClientUtils.escapeQueryChars(goTerm) + nonIEAquery);
	}
	
	/**
	 * Build a {@link COOccurrenceStatsTerm} 
	 * @param goTerm GO Term stats to build
	 * @param termsCount Map with the terms stats
	 * @param together "Together" stats value
	 * @param compared "Compared" stats value
	 * @return {@link COOccurrenceStatsTerm} representation
	 */
	private COOccurrenceStatsTerm buildCoOccurrenceStatsTerm(String goTerm, TreeMap<String, COOccurrenceStatsTerm> termsCount, float together, float compared) {
		
		COOccurrenceStatsTerm coOccurrenceStatsTerm = new COOccurrenceStatsTerm();		
		coOccurrenceStatsTerm.setTogether((int)together);
		coOccurrenceStatsTerm.setCompared((int)compared);
		coOccurrenceStatsTerm.setComparedTerm(goTerm);
		coOccurrenceStatsTerm.setAll(totalNumberProteins);
		coOccurrenceStatsTerm.setStatsType(type);
		coOccurrenceStatsTerm.setTerm(ontologyTermID);
		return coOccurrenceStatsTerm;
	}

	/**
	 * Calculate list of proteins annotated to a term
	 * @return List of proteins annotated to a term
	 * @throws SolrServerException 
	 */
	private List<String> getGOIDAnnotatedProteins() throws SolrServerException {
		List<Count> counts = new ArrayList<>();		
		counts = annotationRetrieval.getFacetFields(
				AnnotationField.GOID.getValue() + ":"
						+ ClientUtils.escapeQueryChars(ontologyTermID)
						+ nonIEAquery, null,
				AnnotationField.DBOBJECTID.getValue(), Integer.MAX_VALUE);		
		// Get protein accessions
		List<String> proteins = new ArrayList<>();
		for (Count count : counts) {
			proteins.add(count.getName());
		}
		return proteins;
	}

	public void setAnnotationRetrieval(AnnotationRetrieval annotationRetrieval) {
		this.annotationRetrieval = annotationRetrieval;
	}

	public String getOntologyTermID() {
		return ontologyTermID;
	}

	public void setOntologyTermID(String ontologyTermID) {
		this.ontologyTermID = ontologyTermID;
	}

	public TreeSet<COOccurrenceStatsTerm> getStatsTerms() {
		return statsTerms;
	}

	public AnnotationRetrieval getAnnotationRetrieval() {
		return annotationRetrieval;
	}

	public float getTotalNumberProteins() {
		return totalNumberProteins;
	}

	public void setTotalNumberProteins(float totalNumberProteins) {
		this.totalNumberProteins = totalNumberProteins;
	}

	public Map<String, Float> getTermsNumberProteins() {
		return termsNumberProteins;
	}

	public void setTermsNumberProteins(Map<String, Float> termsNumberProteins) {
		this.termsNumberProteins = termsNumberProteins;
	}

	public boolean isNonIEA() {
		return nonIEA;
	}

	public void setNonIEA(boolean nonIEA) {
		this.nonIEA = nonIEA;
	}

	public Map<String, Set<String>> getAllProteinsByTerm() {
		return allProteinsByTerm;
	}

	public void setAllProteinsByTerm(Map<String, Set<String>> allProteinsByTerm) {
		this.allProteinsByTerm = allProteinsByTerm;
	}

	public Map<String, Set<String>> getNonIEAProteinsByTerm() {
		return nonIEAProteinsByTerm;
	}

	public void setNonIEAProteinsByTerm(
			Map<String, Set<String>> nonIEAProteinsByTerm) {
		this.nonIEAProteinsByTerm = nonIEAProteinsByTerm;
	}
}