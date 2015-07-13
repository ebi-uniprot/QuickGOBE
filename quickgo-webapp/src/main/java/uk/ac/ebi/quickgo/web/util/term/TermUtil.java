package uk.ac.ebi.quickgo.web.util.term;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.quickgo.data.SourceFiles;
import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.eco.EvidenceCodeOntology;
import uk.ac.ebi.quickgo.ontology.generic.GenericOntology;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm.EGOAspect;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;

/**
 * Contains useful methods for ontology terms
 * @author cbonill
 *
 */
public class TermUtil {
	
	// Log
	private static final Logger logger = LoggerFactory.getLogger(TermUtil.class);
	
	static SourceFiles sourceFiles = null;
	
	static {
		if(sourceFiles == null){
			Properties prop = new Properties();
			String profile = "ves-hx-cd";
			if (System.getProperty("spring.profiles.active") != null) {
				profile = System.getProperty("spring.profiles.active");
			}
			InputStream in = TermUtil.class.getResourceAsStream("/quickgo-" + profile + ".properties");
			try {
				prop.load(in);		
				in.close();
			} catch (IOException e) {		
				e.printStackTrace();
			}
			
			String sourceFilesPath = prop.getProperty("sourcefiles.path");
			sourceFiles = new SourceFiles(new File(sourceFilesPath));
		}
	}	
	
	// cache of the GO data
	static GeneOntology ontology = new GeneOntology();
	
	static EvidenceCodeOntology evidenceCodeOntology = new EvidenceCodeOntology();	
	
	static Map<String, String> processTerms = new HashMap<String, String>();
	
	static Map<String, String> functionTerms = new HashMap<String, String>();
	
	static Map<String, String> componentTerms = new HashMap<String, String>();
	
	public static Map<String, GenericTerm> getGOTerms() {
		if(ontology.terms.isEmpty()){			
			try {
				ontology.load(sourceFiles.goSourceFiles);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return ontology.terms;		
	}
	
	public static GeneOntology getGOOntology() {
		if(ontology.terms.isEmpty()){			
			try {
				ontology.load(sourceFiles.goSourceFiles);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		// Calculate terms of each type
		if(componentTerms.isEmpty()){
			for (GenericTerm term : ontology.getTerms()) {
				if (((GOTerm) term).getOntologyText().equalsIgnoreCase(EGOAspect.C.text)) {
					componentTerms.put(term.getId(), term.getName());
				} else if (((GOTerm) term).getOntologyText().equalsIgnoreCase(EGOAspect.F.text)) {
					functionTerms.put(term.getId(), term.getName());
				} else if (((GOTerm) term).getOntologyText().equalsIgnoreCase(EGOAspect.P.text)) {
					processTerms.put(term.getId(), term.getName());
				}
			}
		}
		return ontology;
	}
	
	public static Map<String, GenericTerm> getECOTerms() {
		if(evidenceCodeOntology.terms.isEmpty()){
			try {
				evidenceCodeOntology.load(sourceFiles.ecoSourceFiles, "root");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return evidenceCodeOntology.terms;
	}
	
	/**
	 * Return ECO terms contain any of the given names
	 * @param names Names to look up
	 * @return ECO ids with specified names
	 */
	public static List<String> getECOTermsByName(List<String> names) {
		List<String> ecoIds = new ArrayList<>();
		Map<String, GenericTerm> ecoterms = getECOTerms();	
		for(String name : names){
			for(GenericTerm ecoTerm : ecoterms.values()){
				if(ecoTerm.getName().toLowerCase().contains(name.toLowerCase())){
					if(!ecoIds.contains(ecoTerm.id)){
						ecoIds.add(ecoTerm.id);
					}
				}
			}
		}		
		return ecoIds;
	}
	
	public static EvidenceCodeOntology getECOOntology() {
		if(evidenceCodeOntology.terms.isEmpty()){
			try {
				evidenceCodeOntology.load(sourceFiles.ecoSourceFiles, "root");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return evidenceCodeOntology;
	}
	
	/**
	 * Return corresponding ontology depending on the term we are looking for
	 * @param termID term ID
	 * @return Generic ontology that contains the term
	 */
	public static GenericOntology getOntology(String termID) {
		if (termID.contains(GOTerm.GO)) {
			return getGOOntology();
		} else if (termID.contains(ECOTerm.ECO)) {
			return getECOOntology();
		}
		return null;
	}

	public static GenericTerm getTerm(String id){
		GenericTerm genericTerm = getOntology(id).getTerm(id);
		if(genericTerm.isECOTerm()){
			ECOTerm ecoTerm = new ECOTerm();
			ecoTerm.setId(id.toUpperCase());
			ecoTerm.setName(genericTerm.getName());
			ecoTerm.setObsolete(genericTerm.isObsolete());
			ecoTerm.setAncestors(genericTerm.getAncestors());
			genericTerm = ecoTerm;
		}
		return genericTerm;
	}
	
	public static SourceFiles getSourceFiles() {
		return sourceFiles;
	}

	public static void setSourceFiles(SourceFiles sourceFiles) {
		TermUtil.sourceFiles = sourceFiles;
	}	
	
	public static Map<String,String> getGOTermsByOntology(EGOAspect ontology){
		getGOOntology();
		switch(ontology){
			case C:
				return componentTerms;
			case F:
				return functionTerms;
			case P:
				return processTerms;
			default:
					return componentTerms;
		}
	}
}
