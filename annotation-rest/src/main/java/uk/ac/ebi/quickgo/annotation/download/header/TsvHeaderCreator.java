package uk.ac.ebi.quickgo.annotation.download.header;

import com.google.common.base.Preconditions;
import java.io.IOException;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static uk.ac.ebi.quickgo.annotation.download.converter.AnnotationToTSV.*;

/**
 * Produce a header for TSV downloaded files. Only the (selected) column names are required. Or all columns if none
 * are selected.
 *
 * @author Tony Wardell
 * Date: 25/01/2017
 * Time: 10:09
 * Created with IntelliJ IDEA.
 */
public class TsvHeaderCreator implements HeaderCreator{

    private static final String OUTPUT_DELIMITER = "\t";
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
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        //todo missing the following fields that come from other services yet to be plugged in
        //todo name, synonym, type from the gene product service
        List<String> selectedFields = headerContent.selectedFields();
        if (selectedFields.isEmpty() || selectedFields.contains(GENE_PRODUCT_ID_FIELD_NAME)) {
            tsvJoiner.add(GENE_PRODUCT_ID);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(SYMBOL_FIELD_NAME)) {
            tsvJoiner.add(SYMBOL);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(QUALIFIER_FIELD_NAME)) {
            tsvJoiner.add(QUALIFIER);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GO_TERM_FIELD_NAME)) {
            tsvJoiner.add(GO_TERM);
        }
        if (headerContent.isSlimmed()) {
            tsvJoiner.add(SLIMMED_FROM);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GO_NAME_FIELD_NAME)) {
            tsvJoiner.add(GO_NAME);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(ECO_ID_FIELD_NAME)) {
            tsvJoiner.add(ECO_ID);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GO_EVIDENCE_CODE_FIELD_NAME)) {
            tsvJoiner.add(GO_EVIDENCE_CODE);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(REFERENCE_FIELD_NAME)) {
            tsvJoiner.add(REFERENCE);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(WITH_FROM_FIELD_NAME)) {
            tsvJoiner.add(WITH_FROM);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(TAXON_ID_FIELD_NAME)) {
            tsvJoiner.add(TAXON_ID);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(ASSIGNED_BY_FIELD_NAME)) {
            tsvJoiner.add(ASSIGNED_BY);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(ANNOTATION_EXTENSION_FIELD_NAME)) {
            tsvJoiner.add(ANNOTATION_EXTENSION);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(DATE_FIELD_NAME)) {
            tsvJoiner.add(DATE);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(TAXON_NAME_FIELD_NAME)) {
            tsvJoiner.add(TAXON_NAME);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GENE_PRODUCT_NAME_FIELD_NAME)) {
            tsvJoiner.add(GENE_PRODUCT_NAME);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GENE_PRODUCT_SYNONYMS_FIELD_NAME)) {
            tsvJoiner.add(GENE_PRODUCT_SYNONYMS);
        }
        if (selectedFields.isEmpty() || selectedFields.contains(GENE_PRODUCT_TYPE_FIELD_NAME)) {
            tsvJoiner.add(GENE_PRODUCT_TYPE);
        }
        return tsvJoiner.toString();
    }
}
