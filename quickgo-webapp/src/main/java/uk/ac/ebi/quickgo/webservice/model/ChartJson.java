package uk.ac.ebi.quickgo.webservice.model;

import uk.ac.ebi.quickgo.graphics.GraphImage;
import uk.ac.ebi.quickgo.graphics.TermNode;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 01/04/2015
 * Time: 16:17
 * Created with IntelliJ IDEA.
 */
public class ChartJson {

	//private Collection<TermNode> termNodes;
	private List<LayoutNode> layoutNodeList;
	private Collection<GraphImage.KeyNode> legendNodes;
	private String graphImageSrc;
	private int graphImageWidth;
	private int graphImageHeight;
	private String termsToDisplay;

	public ChartJson() {
		this.layoutNodeList = new ArrayList<>();
	}
	public void addLayoutNode(LayoutNode layoutNode) {
		this.layoutNodeList.add(layoutNode);
	}

	public List<LayoutNode> getLayoutNodes() {
		return layoutNodeList;
	}

	public void setLegendNodes(Collection<GraphImage.KeyNode> legendNodes) {
		this.legendNodes = legendNodes;
	}

	public Collection<GraphImage.KeyNode> getLegendNodes() {
		return legendNodes;
	}

	public void setGraphImageSrc(String graphImageSrc) {
		this.graphImageSrc = graphImageSrc;
	}

	public String getGraphImageSrc() {
		return graphImageSrc;
	}

	public void setGraphImageWidth(int graphImageWidth) {
		this.graphImageWidth = graphImageWidth;
	}

	public int getGraphImageWidth() {
		return graphImageWidth;
	}

	public void setGraphImageHeight(int graphImageHeight) {
		this.graphImageHeight = graphImageHeight;
	}

	public int getGraphImageHeight() {
		return graphImageHeight;
	}

	public void setTermsToDisplay(String termsToDisplay) {
		this.termsToDisplay = termsToDisplay;
	}

	public String getTermsToDisplay() {
		return termsToDisplay;
	}


	 public class LayoutNode{
		 private final String id;
		 int left, right, top, bottom;

		public LayoutNode(String id, int left, int right, int top, int bottom) {
			this.id=id;
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		}

		 public String getId() {
			 return id;
		 }

		 public int getLeft() {
			 return left;
		 }

		 public int getRight() {
			 return right;
		 }

		 public int getTop() {
			 return top;
		 }

		 public int getBottom() {
			 return bottom;
		 }
	 }
}
