package uk.ac.ebi.quickgo.annotation.download.header;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static java.util.Arrays.stream;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GAF_SUB_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.GPAD_SUB_TYPE;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.TSV_SUB_TYPE;

/**
 * Produce a header for TSV downloaded files. Only the column names are required.
 *
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 10:09
 * Created with IntelliJ IDEA.
 */
@Component
public class TsvHeaderCreator implements HeaderCreator{
    static final String TSV_COL_HEADINGS_INCLUDING_SLIM = "GENE PRODUCT\tSYMBOL\tQUALIFIER\tGO TERM\tGO TERM " +
            "NAME\tSLIMMED FROM\tEVIDENCE\tREFERENCE\tWITH/FROM\tTAXON\tASSIGNED BY\tANNOTATION EXTENSION" +
            "\tDATE\tTAXON NAME";
    static final String TSV_COL_HEADINGS_EXCLUDING_SLIM = "GENE PRODUCT\tSYMBOL\tQUALIFIER\tGO TERM\tGO TERM " +
            "NAME\tEVIDENCE\tREFERENCE\tWITH/FROM\tTAXON\tASSIGNED BY\tANNOTATION EXTENSION\tDATE\tTAXON NAME";

    /**
     * Write the contents of the header to the ResponseBodyEmitter instance.
     * @param emitter streams the header content to the client
     * @param content holds values used to control or populate the header output.;
     */

    @Override public void write(ResponseBodyEmitter emitter, HeaderContent content) {
        Preconditions.checkArgument(Objects.nonNull(emitter), "The GTypeHeaderCreator emitter must not be null");
        Preconditions.checkArgument(Objects.nonNull(content), "The GTypeHeaderCreator content instance must not be " +
                "null");
        try {
            emitter.send(colHeadings(content) + "\n", MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send TSV download header", e);
        }
    }

    private String colHeadings(HeaderContent headerContent) {
       return headerContent.isSlimmed()? TSV_COL_HEADINGS_INCLUDING_SLIM : TSV_COL_HEADINGS_EXCLUDING_SLIM;
    }
}
