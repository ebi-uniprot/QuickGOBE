package uk.ac.ebi.quickgo.index.annotation;

import java.util.regex.Pattern;

import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.KEY_EQUALS_VALUE_FORMAT;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.PIPE_SEPARATED_CSVs_FORMAT;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.WORD_LBRACE_WORD_RBRACE_FORMAT;

/**
 * This class defines common constants used during annotation indexing, when parsing the input files.
 *
 * Created 29/04/16
 * @author Edd
 */
public class AnnotationParsingHelper {

    // data file values
    static final String GO_EVIDENCE = "go_evidence";
    static final String TAXON_ID = "taxon_id";
    static final String DB_OBJECT_SUBSET = "db_subset";
    static final String DB_OBJECT_SYMBOL = "db_object_symbol";
    static final String DB_OBJECT_TYPE = "db_object_type";

    // string regex representations
    private static final String DB_COLON_REF_FORMAT = "[A-Za-z0-9_\\.-]+(:[A-Za-z0-9_\\.-]+){1,}";
    private static final String QUALIFIERS_FORMAT =
            "^(NOT\\|)?(involved_in|enables|part_of|contributes_to|colocalizes_with)$";
    private static final String RAW_TAXON_FORMAT = "([0-9]+)";
    private static final String INTERACTING_TAXON_FORMAT = "taxon:" + RAW_TAXON_FORMAT;

    static final Pattern PROPS_TAXON_REGEX = Pattern.compile(TAXON_ID + "=");
    static final Pattern PROPS_DB_OBJECT_TYPE_REGEX = Pattern.compile(DB_OBJECT_TYPE + "=");
    static final Pattern PROPS_GO_EVIDENCE_REGEX = Pattern.compile(GO_EVIDENCE + "=");

    // regex patterns
    static final Pattern WITH_REGEX = Pattern.compile(String.format(
            "(" + PIPE_SEPARATED_CSVs_FORMAT + ")|(With:Not_Supplied)",
            DB_COLON_REF_FORMAT, DB_COLON_REF_FORMAT, DB_COLON_REF_FORMAT, DB_COLON_REF_FORMAT));
    static final Pattern QUALIFIER_REGEX = Pattern.compile(QUALIFIERS_FORMAT);
    static final Pattern ANNOTATION_EXTENSION_REGEX = Pattern.compile(String.format(
            PIPE_SEPARATED_CSVs_FORMAT,
            WORD_LBRACE_WORD_RBRACE_FORMAT, WORD_LBRACE_WORD_RBRACE_FORMAT,
            WORD_LBRACE_WORD_RBRACE_FORMAT, WORD_LBRACE_WORD_RBRACE_FORMAT));
    static final Pattern ANNOTATION_PROPERTIES_REGEX = Pattern.compile(String.format(
            PIPE_SEPARATED_CSVs_FORMAT,
            KEY_EQUALS_VALUE_FORMAT, KEY_EQUALS_VALUE_FORMAT, KEY_EQUALS_VALUE_FORMAT, KEY_EQUALS_VALUE_FORMAT));
    static final Pattern INTERACTING_TAXON_REGEX = Pattern.compile(INTERACTING_TAXON_FORMAT);
    static final Pattern RAW_TAXON_REGEX = Pattern.compile(RAW_TAXON_FORMAT);
}
