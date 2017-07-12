package uk.ac.ebi.quickgo.annotation.download.header;

import uk.ac.ebi.quickgo.annotation.download.TSVDownload;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static uk.ac.ebi.quickgo.annotation.download.TSVDownload.*;

/**
 * Produce a header for TSV downloaded files. Only the (selected) column names are required. Or all columns if none
 * are selected.
 *
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 10:09
 * Created with IntelliJ IDEA.
 */
public class TsvHeaderCreator implements HeaderCreator {

    static final String GENE_PRODUCT_ID = "GENE PRODUCT ID";
    static final String SYMBOL = "SYMBOL";
    static final String QUALIFIER = "QUALIFIER";
    static final String GO_TERM = "GO TERM";
    static final String GO_NAME = "GO NAME";
    static final String SLIMMED_FROM = "SLIMMED FROM";
    static final String ECO_ID = "ECO_ID";
    static final String GO_EVIDENCE_CODE = "GO EVIDENCE CODE";
    static final String REFERENCE = "REFERENCE";
    static final String WITH_FROM = "WITH/FROM";
    static final String TAXON_ID = "TAXON ID";
    static final String ASSIGNED_BY = "ASSIGNED BY";
    static final String ANNOTATION_EXTENSION = "ANNOTATION EXTENSION";
    static final String DATE = "DATE";
    static final String TAXON_NAME = "TAXON NAME";
    static final String GENE_PRODUCT_NAME = "GENE_PRODUCT_NAME";
    static final String GENE_PRODUCT_SYNONYMS = "GENE_PRODUCT_SYNONYMS";
    static final String GENE_PRODUCT_TYPE = "GENE_PRODUCT_TYPE";

    private static final Logger LOGGER = LoggerFactory.getLogger(TsvHeaderCreator.class);
    private static final String OUTPUT_DELIMITER = "\t";
    private static final Map<String, BiConsumer<HeaderContent, StringJoiner>> selected2Content;

    static {
        selected2Content = new HashMap<>();
        initialiseContentMappings();
    }

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
            emitter.send(output(content) + "\n", MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            String errorMessage = "Failed to send TSV download header";
            HeaderCreationException headerCreationException = new HeaderCreationException(errorMessage, e);
            LOGGER.error(errorMessage, headerCreationException);
            throw headerCreationException;
        }
    }

    private static void initialiseContentMappings() {
        selected2Content.put(GENE_PRODUCT_ID_FIELD_NAME, (hc, j) -> j.add(GENE_PRODUCT_ID));
        selected2Content.put(SYMBOL_FIELD_NAME, (hc, j) -> j.add(SYMBOL));
        selected2Content.put(QUALIFIER_FIELD_NAME, (hc, j) -> j.add(QUALIFIER));
        selected2Content.put(GO_TERM_FIELD_NAME, (hc, j) -> {
            j.add(GO_TERM);
            if (hc.isSlimmed()) {
                j.add(SLIMMED_FROM);
            }
        });
        selected2Content.put(GO_NAME_FIELD_NAME, (hc, j) -> j.add(GO_NAME));
        selected2Content.put(ECO_ID_FIELD_NAME, (hc, j) -> j.add(ECO_ID));
        selected2Content.put(GO_EVIDENCE_CODE_FIELD_NAME, (hc, j) -> j.add(GO_EVIDENCE_CODE));
        selected2Content.put(REFERENCE_FIELD_NAME, (hc, j) -> j.add(REFERENCE));
        selected2Content.put(WITH_FROM_FIELD_NAME, (hc, j) -> j.add(WITH_FROM));
        selected2Content.put(TAXON_ID_FIELD_NAME, (hc, j) -> j.add(TAXON_ID));
        selected2Content.put(ASSIGNED_BY_FIELD_NAME, (hc, j) -> j.add(ASSIGNED_BY));
        selected2Content.put(ANNOTATION_EXTENSION_FIELD_NAME, (hc, j) -> j.add(ANNOTATION_EXTENSION));
        selected2Content.put(DATE_FIELD_NAME, (hc, j) -> j.add(DATE));
        selected2Content.put(TAXON_NAME_FIELD_NAME, (hc, j) -> j.add(TAXON_NAME));
        selected2Content.put(GENE_PRODUCT_NAME_FIELD_NAME, (hc, j) -> j.add(GENE_PRODUCT_NAME));
        selected2Content.put(GENE_PRODUCT_SYNONYMS_FIELD_NAME, (hc, j) -> j.add(GENE_PRODUCT_SYNONYMS));
        selected2Content.put(GENE_PRODUCT_TYPE_FIELD_NAME, (hc, j) -> j.add(GENE_PRODUCT_TYPE));
    }

    private String output(HeaderContent headerContent) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        List<String> selectedFields = TSVDownload.whichColumnsWillWeShow(headerContent.getSelectedFields());
        LOGGER.debug("Requested which fields will we show from " + headerContent.getSelectedFields() + " and will show "
                + selectedFields);
        for (String selectedField : selectedFields) {
            selected2Content.get(selectedField).accept(headerContent, tsvJoiner);
        }
        return tsvJoiner.toString();
    }

}
