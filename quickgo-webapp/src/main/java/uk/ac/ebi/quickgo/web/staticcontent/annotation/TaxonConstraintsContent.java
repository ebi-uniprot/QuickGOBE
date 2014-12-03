package uk.ac.ebi.quickgo.web.staticcontent.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.web.util.term.TermUtil;

/**
 * Retrieve all the taxon constraints
 * @author cbonill
 *
 */
public class TaxonConstraintsContent {

	private static List<TaxonConstraint> taxonConstraints = new ArrayList<>();
	
	private static List<Map<String, Object>> serialisedTaxonConstraints = new ArrayList<>();
	
	private static List<TaxonConstraint> processedTaxons = new ArrayList<>();
	
	public static List<Map<String, Object>> getAllTaxonConstraintsSerialised() {
		
		if (serialisedTaxonConstraints.isEmpty()) {
			Map<String, GenericTerm> goTerms = TermUtil.getGOTerms();
			for (String goId : TermUtil.getGOTerms().keySet()) {
				GOTerm term = (GOTerm) goTerms.get(goId);
				for (TaxonConstraint constraint : term.getTaxonConstraints()) {
					if (!processedTaxons.contains(constraint)) {
						processedTaxons.add(constraint);
						taxonConstraints.add(constraint);
						serialisedTaxonConstraints.add(constraint.serialise());
					}
				}
			}
		}
		return serialisedTaxonConstraints;
	}

	public static List<TaxonConstraint> getTaxonConstraints() {
		if(taxonConstraints.isEmpty()){
			getAllTaxonConstraintsSerialised();
			Collections.sort(taxonConstraints);
		}
		return taxonConstraints;
	}	
}