package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.ontology.go.PostProcessingRule;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 03/03/2015
 * Time: 10:30
 * Created with IntelliJ IDEA.
 */
public class AnnotationPostProcessingJson {
	private List<PostProcessingRule> content;

	public void setContent(List<PostProcessingRule> content) {
		this.content = content;
	}

	public List<PostProcessingRule> getContent() {
		return content;
	}
}
