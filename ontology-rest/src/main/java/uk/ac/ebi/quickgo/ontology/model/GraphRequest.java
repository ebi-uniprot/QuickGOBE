package uk.ac.ebi.quickgo.ontology.model;

import io.swagger.annotations.ApiModelProperty;
import java.util.Optional;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

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
    private Optional<Boolean> base64 = Optional.empty();
    @ApiModelProperty(value = "Whether or not to show the key for the ancestor graph")
    private Optional<Boolean> showKey = Optional.empty();
    @ApiModelProperty(value = "Whether or not to show the GO IDs for the ancestor graph")
    private Optional<Boolean> showIds = Optional.empty();
    @ApiModelProperty(value = "Term box width in pixels")
    private Optional<Integer> termBoxWidth = Optional.empty();
    @ApiModelProperty(value = "Term box height in pixels")
    private Optional<Integer> termBoxHeight = Optional.empty();
    @ApiModelProperty(value = "Whether or not to show the slim set a term appears in")
    private Optional<Boolean> showSlimColours = Optional.empty();
    @ApiModelProperty(value = "Whether or not to show the children of terms")
    private Optional<Boolean> showChildren = Optional.empty();

    public GraphRequest() {}

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Pattern(regexp = "^true|false$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid base64: ${validatedValue}")
    public Optional<Boolean> isBase64() {
        return base64;
    }

    public void setBase64(boolean val) {
        this.base64 = Optional.of(val);
    }

    @Pattern(regexp = "^true|false$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid showKey: ${validatedValue}")
    public Optional<Boolean> showKey() {
        return showKey;
    }

    public void setShowKey(boolean val) {
        this.showKey = Optional.of(val);
    }

    @Pattern(regexp = "^true|false$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid showIds: ${validatedValue}")
    public Optional<Boolean> showIds() {
        return showIds;
    }

    public void setShowIds(boolean val) {
        this.showIds = Optional.of(val);
    }

    @Min(value = 1)
    public Optional<Integer> getTermBoxWidth() {
        return termBoxWidth;
    }

    public void setTermBoxWidth(int val) {
        this.termBoxWidth = Optional.of(val);
    }

    @Min(value = 1)
    public Optional<Integer> getTermBoxHeight() {
        return termBoxHeight;
    }

    public void setTermBoxHeight(int val) {
        this.termBoxHeight = Optional.of(val);
    }

    @Pattern(regexp = "^true|false$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid showSlimColours: ${validatedValue}")
    public Optional<Boolean> showSlimColours() {
        return showSlimColours;
    }

    public void setShowSlimColours(boolean val) {
        this.showSlimColours = Optional.of(val);
    }

    @Pattern(regexp = "^true|false$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid showChildren: ${validatedValue}")
    public Optional<Boolean> showChildren() {
        return this.showChildren;
    }

    public void setShowChildren(boolean val) {
        this.showChildren = Optional.of(val);
    }

}
