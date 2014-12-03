package uk.ac.ebi.quickgo.ontology.go;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.quickgo.render.JSONSerialise;

@SuppressWarnings("serial")
public class TaxonConstraintSet extends HashMap<String, TaxonConstraint> implements JSONSerialise {
	public Map<Integer, TaxonUnion> taxonUnions = new HashMap<>();

	public void addTaxonUnion(String id, String name, String taxa) {
		Integer i = Integer.parseInt(id);
		taxonUnions.put(i, new TaxonUnion(i, name, taxa));
	}

	public void addConstraint(String ruleId, String goId, String name, String relationship, String taxIdType, String taxId, String taxonName, String sources) {
		if ("NCBITaxon_Union".equals(taxIdType)) {
			TaxonUnion tu = taxonUnions.get(Integer.parseInt(taxId));
			if (tu != null) {
				taxId = tu.taxa;
			}
		}
		this.put(ruleId, new TaxonConstraint(ruleId, goId, name, relationship, taxIdType, taxId, taxonName, sources));
	}

	public Map<String, Object> serialise() {
		Map<String, Object> constraints = new HashMap<String, Object>();

		for (String id : this.keySet()) {
			constraints.put(id, this.get(id).serialise());
		}

		return constraints;
	}
}
