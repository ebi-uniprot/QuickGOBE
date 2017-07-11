package uk.ac.ebi.quickgo.annotation.download.http;

import java.nio.charset.Charset;
import org.springframework.http.MediaType;

/**
 * Hold the definitions of MediaTypes used in downloads, and their components.
 * @author Tony Wardell
 * Date: 27/04/2017
 * Time: 10:38
 * Created with IntelliJ IDEA.
 */
public class MediaTypeFactory {
    private static final String TYPE = "text";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final String TSV_SUB_TYPE = "tsv";
    public static final String GAF_SUB_TYPE = "gaf";
    public static final String GPAD_SUB_TYPE = "gpad";

    public static final String TSV_MEDIA_TYPE_STRING = TYPE + "/" + TSV_SUB_TYPE;
    public static final String GAF_MEDIA_TYPE_STRING = TYPE + "/" + GAF_SUB_TYPE;
    public static final String GPAD_MEDIA_TYPE_STRING = TYPE + "/" + GPAD_SUB_TYPE;

    public static final MediaType TSV_MEDIA_TYPE = new MediaType(TYPE, TSV_SUB_TYPE, DEFAULT_CHARSET);
    public static final MediaType GAF_MEDIA_TYPE = new MediaType(TYPE, GAF_SUB_TYPE, DEFAULT_CHARSET);
    public static final MediaType GPAD_MEDIA_TYPE = new MediaType(TYPE, GPAD_SUB_TYPE, DEFAULT_CHARSET);
}
