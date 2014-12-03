package uk.ac.ebi.quickgo.ontology.go;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnnotationBlacklist {
	HashMap<String, List<AnnotationBlacklistEntry>> subsets = new HashMap<>();

	public void add(String category, String proteinAc, String taxonId, String goId, String reason, String methodId) {
		List<AnnotationBlacklistEntry> subset = subsets.get(category);
		if (subset == null) {
			subset = new ArrayList<>();
			subsets.put(category, subset);
		}
		
		subset.add(new AnnotationBlacklistEntry(proteinAc, taxonId, goId, reason, methodId));
	}

	public List<AnnotationBlacklistEntry> blacklistIEAReview() {
		return subsets.get("IEA Review");
	}

	public List<AnnotationBlacklistEntry> blacklistUniProtCaution() {
		return subsets.get("UniProt COMMENT_CAUTION");
	}

	public List<AnnotationBlacklistEntry> blacklistNotQualified() {
		return subsets.get("NOT-qualified manual");
	}

	public static class BlacklistEntryMinimal {
		public String protein_accession;
		public String go_id;

		public BlacklistEntryMinimal(String proteinAc, String goId) {
			this.protein_accession = proteinAc;
			this.go_id = goId;
		}
	}

	public List<BlacklistEntryMinimal> forTaxon(int taxonId) {
		List<BlacklistEntryMinimal> ab = new ArrayList<>();

		for (String category : subsets.keySet()) {
			for (AnnotationBlacklistEntry abe : subsets.get(category)) {
				if (abe.taxonId == taxonId) {
					ab.add(new BlacklistEntryMinimal(abe.proteinAc, abe.goId));
				}
			}
		}

		return ab;
	}

	public HashMap<String, List<AnnotationBlacklistEntry>> getSubsets() {
		return subsets;
	}

	public void setSubsets(HashMap<String, List<AnnotationBlacklistEntry>> subsets) {
		this.subsets = subsets;
	}
	
}
