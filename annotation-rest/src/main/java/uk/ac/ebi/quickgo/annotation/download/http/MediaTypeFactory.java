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
    public static final String TSV_SUB_TYPE = "tsv";
    public static final String GAF_SUB_TYPE = "gaf";
    public static final String GPAD_SUB_TYPE = "gpad";
    private static final String TEXT_TYPE = "text";
    public static final String TSV_MEDIA_TYPE_STRING = TEXT_TYPE + "/" + TSV_SUB_TYPE;
    public static final String GAF_MEDIA_TYPE_STRING = TEXT_TYPE + "/" + GAF_SUB_TYPE;
    public static final String GPAD_MEDIA_TYPE_STRING = TEXT_TYPE + "/" + GPAD_SUB_TYPE;
    private static final String APPLICATION_TYPE = "application";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final MediaType TSV_MEDIA_TYPE = new MediaType(TEXT_TYPE, TSV_SUB_TYPE, DEFAULT_CHARSET);
    public static final MediaType GAF_MEDIA_TYPE = new MediaType(TEXT_TYPE, GAF_SUB_TYPE, DEFAULT_CHARSET);
    public static final MediaType GPAD_MEDIA_TYPE = new MediaType(TEXT_TYPE, GPAD_SUB_TYPE, DEFAULT_CHARSET);
    private static final String EXCEL_SUB_TYPE = "vnd.ms-excel";
    public static final String EXCEL_MEDIA_TYPE_STRING = APPLICATION_TYPE + "/" + EXCEL_SUB_TYPE;
    public static final MediaType EXCEL_MEDIA_TYPE = new MediaType(APPLICATION_TYPE, EXCEL_SUB_TYPE);
    private static final String JSON_SUB_TYPE = "json";
    public static final String JSON_MEDIA_TYPE_STRING = APPLICATION_TYPE + "/" + JSON_SUB_TYPE;
    public static final MediaType JSON_MEDIA_TYPE = new MediaType(APPLICATION_TYPE, JSON_SUB_TYPE);
    private static final String EXCEL_FILE_TYPE = "xls";

    public static String fileExtension(MediaType mediaType) {
        if (EXCEL_MEDIA_TYPE.equals(mediaType)) {
            return EXCEL_FILE_TYPE;
        }
        return mediaType.getSubtype();
    }

}
