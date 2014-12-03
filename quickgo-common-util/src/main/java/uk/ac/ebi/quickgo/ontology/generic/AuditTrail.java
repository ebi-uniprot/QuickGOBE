package uk.ac.ebi.quickgo.ontology.generic;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AuditTrail {
	public ArrayList<AuditRecord> auditRecords = new ArrayList<AuditRecord>();

	public void add(AuditRecord ar) {
		auditRecords.add(ar);
	}

	public int count() {
		return auditRecords.size();
	}

	protected List<AuditRecord> getFilteredHistory(EnumSet<AuditRecord.AuditCategory> categories) {
		List<AuditRecord> filteredHistory = new ArrayList<AuditRecord>();

		for (AuditRecord ar : auditRecords) {
			if (ar.isA(categories)) {
				filteredHistory.add(ar);
			}
		}
		return filteredHistory;
	}

	public List<AuditRecord> history(AuditRecord.AuditCategory category) {
		return getFilteredHistory(EnumSet.of(category));
	}

	public List<AuditRecord> history(EnumSet<AuditRecord.AuditCategory> categories) {
		return getFilteredHistory(categories);
	}

}
