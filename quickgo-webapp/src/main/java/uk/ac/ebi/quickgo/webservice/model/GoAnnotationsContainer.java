package uk.ac.ebi.quickgo.webservice.model;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 04/03/2015
 * Time: 10:33
 * Created with IntelliJ IDEA.
 */
public class GoAnnotationsContainer {
	private long numberAnnotations;
	private List<GoAnnotationJson> annotationsList;

	public void setNumberAnnotations(long numberAnnotations) {
		this.numberAnnotations = numberAnnotations;
	}

	public long getNumberAnnotations() {
		return numberAnnotations;
	}

	public void setAnnotationsList(List<GoAnnotationJson> annotationsList) {
		this.annotationsList = annotationsList;
	}

	public List<GoAnnotationJson> getAnnotationsList() {
		return annotationsList;
	}
}
