package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.ontology.generic.AuditRecord;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 27/02/2015
 * Time: 09:00
 * Created with IntelliJ IDEA.
 */
public class GoTermHistoryJson {
	private String from;
	private List<AuditRecord> allChanges;
	private List<AuditRecord> termsRecords;
	private List<AuditRecord> definitionRecords;
	private List<AuditRecord> relationsRecords;
	private List<AuditRecord> xrefRecords;

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setAllChanges(List<AuditRecord> allChanges) {
		this.allChanges = allChanges;
	}

	public List<AuditRecord> getAllChanges() {
		return allChanges;
	}

	public void setTermsRecords(List<AuditRecord> termsRecords) {
		this.termsRecords = termsRecords;
	}

	public List<AuditRecord> getTermsRecords() {
		return termsRecords;
	}

	public void setDefinitionRecords(List<AuditRecord> definitionRecords) {
		this.definitionRecords = definitionRecords;
	}

	public List<AuditRecord> getDefinitionRecords() {
		return definitionRecords;
	}

	public void setRelationsRecords(List<AuditRecord> relationsRecords) {
		this.relationsRecords = relationsRecords;
	}

	public List<AuditRecord> getRelationsRecords() {
		return relationsRecords;
	}

	public void setXrefRecords(List<AuditRecord> xrefRecords) {
		this.xrefRecords = xrefRecords;
	}

	public List<AuditRecord> getXrefRecords() {
		return xrefRecords;
	}
}
