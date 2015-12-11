package uk.ac.ebi.quickgo.model.ontology.generic;

import java.util.EnumSet;
import java.util.List;


public class TermOntologyHistory extends AuditTrail {
	public List<AuditRecord> getHistoryAll() {
		return getFilteredHistory(EnumSet.allOf(AuditRecord.AuditCategory.class));
	}
	
	public List<AuditRecord> getHistoryRelations() {
		return getFilteredHistory(EnumSet.of(AuditRecord.AuditCategory.RELATION));
	}

	public List<AuditRecord> getHistoryTerms() {
		return getFilteredHistory(EnumSet.of(AuditRecord.AuditCategory.TERM));
	}

	public List<AuditRecord> getHistoryDefinitions() {
		return getFilteredHistory(EnumSet.of(AuditRecord.AuditCategory.DEFINITION, AuditRecord.AuditCategory.SYNONYM));
	}

	public List<AuditRecord> getHistoryXRefs() {
		return getFilteredHistory(EnumSet.of(AuditRecord.AuditCategory.XREF));
	}

	public List<AuditRecord> getHistoryObsoletions() {
		return getFilteredHistory(EnumSet.of(AuditRecord.AuditCategory.OBSOLETION));
	}

	public List<AuditRecord> getHistoryOther() {
		return getFilteredHistory(EnumSet.of(AuditRecord.AuditCategory.SECONDARY, AuditRecord.AuditCategory.SUBSET));
	}
}
