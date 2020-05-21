package uk.ac.ebi.quickgo.ontology.model;

import uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Min;

/**
 * The client can provide parameters to change the rendering of a terms chart. This class binds the parameters.
 * @author Tony Wardell
 * Date: 12/02/2018
 * Time: 13:54
 * Created with IntelliJ IDEA.
 */
public class GraphRequest {

    @ApiModelProperty(value = "Comma-separated term IDs")
    private String ids;
    @ApiModelProperty(value = "Whether or not to encode the image as base64")
    private boolean base64 = false;
    @ApiModelProperty(value = "Whether or not to show the key for the ancestor graph")
    private boolean showKey = GraphPresentation.defaultShowKey;
    @ApiModelProperty(value = "Whether or not to show the GO IDs for the ancestor graph")
    private boolean showIds = GraphPresentation.defaultShowTermIds;
    @ApiModelProperty(value = "Term box width in pixels")
    private Integer termBoxWidth = GraphPresentation.defaultWidth;
    @ApiModelProperty(value = "Term box height in pixels")
    private Integer termBoxHeight = GraphPresentation.defaultHeight;
    @ApiModelProperty(value = "Whether or not to show the slim set a term appears in")
    private boolean showSlimColours = GraphPresentation.defaultShowSlimColours;
    @ApiModelProperty(value = "Whether or not to show the children of terms")
    private boolean showChildren = GraphPresentation.defaultShowChildren;
    @ApiModelProperty(value = "Text font size in pixels")
    private Integer fontSize = GraphPresentation.defaultFontSize;

    public GraphRequest() {}

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public boolean isBase64() {
        return base64;
    }

    public void setBase64(boolean val) {
        this.base64 = val;
    }

    public boolean isShowKey() {
        return showKey;
    }

    public void setShowKey(boolean val) {
        this.showKey = val;
    }

    public boolean isShowIds() {
        return showIds;
    }

    public void setShowIds(boolean val) {
        this.showIds = val;
    }

    @Min(value = 1)
    public int getTermBoxWidth() {
        return termBoxWidth;
    }

    public void setTermBoxWidth(int val) {
        this.termBoxWidth = val;
    }

    @Min(value = 1)
    public int getTermBoxHeight() {
        return termBoxHeight;
    }

    public void setTermBoxHeight(int val) {
        this.termBoxHeight = val;
    }

    public boolean isShowSlimColours() {
        return showSlimColours;
    }

    public void setShowSlimColours(boolean val) {
        this.showSlimColours = val;
    }

    public boolean isShowChildren() {
        return showChildren;
    }

    public void setShowChildren(boolean val) {
        this.showChildren = val;
    }

    @Min(value = 1)
    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }
}
