package uk.ac.ebi.quickgo.webservice.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import uk.ac.ebi.quickgo.graphics.GraphImage;
import uk.ac.ebi.quickgo.graphics.TermNode;

import java.util.Collection;

/**
 * @Author Tony Wardell
 * Date: 09/02/2015
 * Time: 11:44
 * Created with IntelliJ IDEA.
 */
public class OntologyGraphJson {
	private String termTermGraphTitle;
	@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@ida")
	private Collection<TermNode> termTermsNodes;
	@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@idb")
	private Collection<GraphImage.KeyNode> termLegendNodes;
	private String termGraphImageSrc;
	private int termGraphImageWidth;
	private int termGraphImageHeight;

	public void setTermTermGraphTitle(String termTermGraphTitle) {
		this.termTermGraphTitle = termTermGraphTitle;
	}

	public String getTermTermGraphTitle() {
		return termTermGraphTitle;
	}

	public void setTermTermsNodes(Collection<TermNode> termTermsNodes) {
		this.termTermsNodes = termTermsNodes;
	}

	public Collection<TermNode> getTermTermsNodes() {
		return termTermsNodes;
	}

	public void setTermLegendNodes(Collection<GraphImage.KeyNode> termLegendNodes) {
		this.termLegendNodes = termLegendNodes;
	}

	public Collection<GraphImage.KeyNode> getTermLegendNodes() {
		return termLegendNodes;
	}

	public void setTermGraphImageSrc(String termGraphImageSrc) {
		this.termGraphImageSrc = termGraphImageSrc;
	}

	public String getTermGraphImageSrc() {
		return termGraphImageSrc;
	}

	public void setTermGraphImageWidth(int termGraphImageWidth) {
		this.termGraphImageWidth = termGraphImageWidth;
	}

	public int getTermGraphImageWidth() {
		return termGraphImageWidth;
	}

	public void setTermGraphImageHeight(int termGraphImageHeight) {
		this.termGraphImageHeight = termGraphImageHeight;
	}

	public int getTermGraphImageHeight() {
		return termGraphImageHeight;
	}
}
