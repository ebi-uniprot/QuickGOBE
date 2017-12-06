package uk.ac.ebi.quickgo.annotation.download;

import java.util.Arrays;
import java.util.List;

/**
 * Holds a list of fields names used for TSV. Specified as it's own class as it is used in more than one client.
 * @author Tony Wardell
 * Date: 28/06/2017
 * Time: 11:22
 * Created with IntelliJ IDEA.
 */
public class TSVDownload {

    public static final String GENE_PRODUCT_FIELD_NAME = "geneproductid";
    public static final String SYMBOL_FIELD_NAME = "symbol";
    public static final String QUALIFIER_FIELD_NAME = "qualifier";
    public static final String GO_TERM_FIELD_NAME = "goid";
    public static final String GO_NAME_FIELD_NAME = "goname";
    public static final String GO_ASPECT_FIELD_NAME = "goaspect";
    public static final String ECO_ID_FIELD_NAME = "evidencecode";
    public static final String GO_EVIDENCE_CODE_FIELD_NAME = "goevidence";
    public static final String REFERENCE_FIELD_NAME = "reference";
    public static final String WITH_FROM_FIELD_NAME = "withfrom";
    public static final String TAXON_ID_FIELD_NAME = "taxonid";
    public static final String ASSIGNED_BY_FIELD_NAME = "assignedby";
    public static final String ANNOTATION_EXTENSION_FIELD_NAME = "extensions";
    public static final String DATE_FIELD_NAME = "date";
    public static final String TAXON_NAME_FIELD_NAME = "taxonname";
    public static final String GENE_PRODUCT_NAME_FIELD_NAME = "name";
    public static final String GENE_PRODUCT_SYNONYMS_FIELD_NAME = "synonyms";
    public static final String GENE_PRODUCT_TYPE_FIELD_NAME = "type";

    private static final List<String> DEFAULT_FIELD_LIST = Arrays.asList(GENE_PRODUCT_FIELD_NAME, SYMBOL_FIELD_NAME,
                                                                      QUALIFIER_FIELD_NAME, GO_TERM_FIELD_NAME,
                                                                      GO_ASPECT_FIELD_NAME,
                                                                      ECO_ID_FIELD_NAME,
                                                                      GO_EVIDENCE_CODE_FIELD_NAME,
                                                                      REFERENCE_FIELD_NAME, WITH_FROM_FIELD_NAME,
                                                                      TAXON_ID_FIELD_NAME, ASSIGNED_BY_FIELD_NAME,
                                                                      ANNOTATION_EXTENSION_FIELD_NAME, DATE_FIELD_NAME);

    public static List<String> whichColumnsWillWeShow(List<String> selectedFields) {
        return selectedFields.isEmpty() ? DEFAULT_FIELD_LIST : selectedFields;
    }
}
