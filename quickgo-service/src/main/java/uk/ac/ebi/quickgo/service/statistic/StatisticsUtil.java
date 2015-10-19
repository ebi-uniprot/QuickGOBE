package uk.ac.ebi.quickgo.service.statistic;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.miscellaneous.MiscellaneousService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;

/**
 * Useful class for the calculation of statistics
 * @author cbonill
 *
 */
@Service("statisticsUtil")
public class StatisticsUtil {


	@Autowired
	TermService goTermService;

	@Autowired
	MiscellaneousService miscellaneousService;

	// Ontology term names
	private Map<String, Map<String, String>> ontologyTermsNames = new HashMap<String, Map<String,String>>();

	// Taxonomies names
	private Map<String, Map<String, String>> taxonomiesNames = new HashMap<String, Map<String,String>>();


	/**
	 * Given a code returns the corresponding name
	 * @param code Term/Taxonomy code
	 * @param field Go id or taxon id fields
	 * @return Name
	 */
	public String getName(String code, String field){
		String name = "";
		// For Ontology terms and Taxonomies is needed to get the names
		if (AnnotationField.GOID.getValue().equals(field)) {
			if(!code.contains("GO")){
				code = "GO:" + code;
			}
			if (ontologyTermsNames.get(code) != null) {
				name = ontologyTermsNames.get(code).get(TermField.NAME.getValue());
			}
		} else if (AnnotationField.TAXONOMYID.getValue().equals(field)) {
			if (taxonomiesNames.get(code) != null) {
				name = taxonomiesNames.get(code).get(MiscellaneousField.TAXONOMY_NAME.getValue());
			}
		} else if(code.startsWith(ECOTerm.ECO)){
			name = ontologyTermsNames.get(code).get(TermField.NAME.getValue());
		}
		return name;
	}

	/**
	 * Load names in case of ontologies and taxonomies
	 * @param field Ontology or Taxonomy
	 */
	public void loadNames(String field) {
		// Ontology term names
		if (AnnotationField.GOID.getValue().equals(field)) {
			if (ontologyTermsNames.isEmpty()) {
				ontologyTermsNames = goTermService.retrieveNames();
			}
		}
		// Taxonomies names
		if (AnnotationField.TAXONOMYID.getValue().equals(field)) {
			if (taxonomiesNames.isEmpty()) {
				taxonomiesNames = miscellaneousService.retrieveTaxonomiesNames();
			}
		}
	}

}
