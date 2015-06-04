package uk.ac.ebi.quickgo.webservice.model;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 04/06/2015
 * Time: 14:05
 * Created with IntelliJ IDEA.
 */
public class SearchFullResultsJson {
	private List<Object> searchResults;
	private long totalNumberResults;
	private int page;
	private String viewBy;
	private long biologicalProcessNumberResults;
	private long molecularFunctionNumberResults;
	private long cellularComponentNumberResults;
	private long ecoTermsNumberResults;
	private long expEcoTotalResults;
	private long automaticEcoTotalResults;
	private long evidenceEcoTotalResults;

	public void setSearchResults(List<Object> searchResults) {
		this.searchResults = searchResults;
	}

	public List<Object> getSearchResults() {
		return searchResults;
	}

	public void setTotalNumberResults(long totalNumberResults) {
		this.totalNumberResults = totalNumberResults;
	}

	public long getTotalNumberResults() {
		return totalNumberResults;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}

	public String getViewBy() {
		return viewBy;
	}

	public void setBiologicalProcessNumberResults(long biologicalProcessNumberResults) {
		this.biologicalProcessNumberResults = biologicalProcessNumberResults;
	}

	public long getBiologicalProcessNumberResults() {
		return biologicalProcessNumberResults;
	}

	public void setMolecularFunctionNumberResults(long molecularFunctionNumberResults) {
		this.molecularFunctionNumberResults = molecularFunctionNumberResults;
	}

	public long getMolecularFunctionNumberResults() {
		return molecularFunctionNumberResults;
	}

	public void setCellularComponentNumberResults(long cellularComponentNumberResults) {
		this.cellularComponentNumberResults = cellularComponentNumberResults;
	}

	public long getCellularComponentNumberResults() {
		return cellularComponentNumberResults;
	}

	public void setEcoTermsNumberResults(long ecoTermsNumberResults) {
		this.ecoTermsNumberResults = ecoTermsNumberResults;
	}

	public void setManualEcoTotalResults(long expEcoTotalResults) {
		this.expEcoTotalResults = expEcoTotalResults;
	}

	public long getExpEcoTotalResults() {
		return expEcoTotalResults;
	}

	public void setAutomaticEcoTotalResults(long automaticEcoTotalResults) {
		this.automaticEcoTotalResults = automaticEcoTotalResults;
	}

	public long getAutomaticEcoTotalResults() {
		return automaticEcoTotalResults;
	}

	public void setEvidenceEcoTotalResults(long evidenceEcoTotalResults) {
		this.evidenceEcoTotalResults = evidenceEcoTotalResults;
	}

	public long getEvidenceEcoTotalResults() {
		return evidenceEcoTotalResults;
	}
}
