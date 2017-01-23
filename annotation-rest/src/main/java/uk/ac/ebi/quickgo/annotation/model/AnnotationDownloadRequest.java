package uk.ac.ebi.quickgo.annotation.model;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * // todo: put this back into annotation request -- there's little point to this class currently.
 *
 * A small specialisation of the {@link AnnotationRequest} data structure, which captures client request parameters.
 *
 * Created 23/01/17
 * @author Edd
 */
public class AnnotationDownloadRequest extends AnnotationRequest {
    private static final int MIN_DOWNLOAD_NUMBER = 1;
    private static final int MAX_DOWNLOAD_NUMBER = 50000;
    private static final int DEFAULT_DOWNLOAD_LIMIT = 10000;

    @ApiModelProperty(
            value = "The number of annotations to download. Note, the page size parameter [limit] will be ignored " +
                    "when downloading results.",
            allowableValues = "range[" + MIN_DOWNLOAD_NUMBER + "," + MAX_DOWNLOAD_NUMBER + "]")
    private int downloadLimit = DEFAULT_DOWNLOAD_LIMIT;

    @Min(value = MIN_DOWNLOAD_NUMBER, message = "Number of entries to download cannot be less than {value} " +
            "but found: ${validatedValue}")
    @Max(value = MAX_DOWNLOAD_NUMBER, message = "Number of entries to download cannot be more than {value} " +
            "but found: ${validatedValue}")
    public int getDownloadLimit() {
        return downloadLimit;
    }

    public void setDownloadLimit(int downloadLimit) {
        this.downloadLimit = downloadLimit;
    }
}
