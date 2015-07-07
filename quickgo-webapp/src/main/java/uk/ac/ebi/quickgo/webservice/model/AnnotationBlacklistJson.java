package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.ontology.go.AnnotationBlacklistEntry;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 02/03/2015
 * Time: 13:01
 * Created with IntelliJ IDEA.
 */
public class AnnotationBlacklistJson {
	private List<AnnotationBlacklistEntry> IEAReview;
	private List<AnnotationBlacklistEntry> blackListNotQualified;
	private List<AnnotationBlacklistEntry> blackListUniProtCaution;

	public void setIEAReview(List<AnnotationBlacklistEntry> IEAReview) {
		this.IEAReview = IEAReview;
	}

	public List<AnnotationBlacklistEntry> getIEAReview() {
		return IEAReview;
	}

	public void setBlackListNotQualified(List<AnnotationBlacklistEntry> blackListNotQualified) {
		this.blackListNotQualified = blackListNotQualified;
	}

	public List<AnnotationBlacklistEntry> getBlackListNotQualified() {
		return blackListNotQualified;
	}

	public void setBlackListUniProtCaution(List<AnnotationBlacklistEntry> blackListUniProtCaution) {
		this.blackListUniProtCaution = blackListUniProtCaution;
	}

	public List<AnnotationBlacklistEntry> getBlackListUniProtCaution() {
		return blackListUniProtCaution;
	}
}
