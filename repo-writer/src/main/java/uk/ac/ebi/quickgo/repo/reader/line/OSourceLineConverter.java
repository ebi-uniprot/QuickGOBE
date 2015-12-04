package uk.ac.ebi.quickgo.repo.reader.line;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts an ontology record/line from a delimited file to an {@link OntologyDocument} instance.
 *
 * Performs a simple mapping of fields of a delimited line, to values of a Solr document.
 *
 * Created 03/12/15
 * @author Edd
 */
public class OSourceLineConverter implements Function<String, OntologyDocument> {

    private static final String SEPARATOR0 = "\t";
    private static final String SEPARATOR1 = "^^^";
    private static final String SEPARATOR1_REGEX = "\\^\\^\\^";
    private static final String SEPARATOR2 = "|||";

    private final static int ID_INDEX = 0;
    private final static int NAME_INDEX = 1;
    private final static int IS_OBSOLETE_INDEX = 2;
    private final static int DEFINITION_INDEX = 3;
    private final static int COMMENT_INDEX = 4;
    private final static int SECONDARIES_INDEX = 5;
    private final static int USAGE_INDEX = 6;
    private final static int SYNONYMS_INDEX = 7;
    private final static int SUBSETS_INDEX = 8;
    private final static int REPLACED_BY_INDEX = 9;
    private final static int CONSIDERS_INDEX = 10;
    private final static int CHILDREN_INDEX = 11;
    private final static int ANCESTORS_INDEX = 12;
    private final static int ASPECT_INDEX = 13;
    private final static int HISTORY_INDEX = 14;
    private final static int XREFS_INDEX = 15;
    private final static int TAXON_CONSTRAINTS_INDEX = 16;
    private final static int BLACKLIST_INDEX = 17;
    private final static int ANNOTATION_GUIDELINES_INDEX = 18;
    private final static int XRELATIONS_INDEX = 19;

    private static List<String> fieldAsStrList(String field) {
        return field.equals("")? null : Arrays.asList(field.split(SEPARATOR1_REGEX));
    }

    @Override public OntologyDocument apply(String s) {
        String[] fields = s.split(SEPARATOR0);

        if (fields.length == 20) {
            OntologyDocument doc = new OntologyDocument();
            doc.id = fields[ID_INDEX];
            doc.ontologyType = doc.id.substring(0, doc.id.indexOf(":"));
            doc.name = fields[NAME_INDEX];
            doc.isObsolete = Boolean.valueOf(fields[IS_OBSOLETE_INDEX]);
            doc.definition = fields[DEFINITION_INDEX];
            doc.comment = fields[COMMENT_INDEX];
            doc.secondaryIds = fieldAsStrList(fields[SECONDARIES_INDEX]);
            doc.usage = fields[USAGE_INDEX];
            doc.synonyms = fieldAsStrList(fields[SYNONYMS_INDEX]);
            doc.synonymNames = doc.synonyms == null? null : doc.synonyms.stream()
                    .map(synField ->
                            synField.substring(0, synField.indexOf(SEPARATOR2)))
                    .collect(Collectors.toList());
            doc.subsets = fieldAsStrList(fields[SUBSETS_INDEX]);
            doc.replacedBy = fields[REPLACED_BY_INDEX];
            doc.considers = fieldAsStrList(fields[CONSIDERS_INDEX]);
            doc.children = fieldAsStrList(fields[CHILDREN_INDEX]);
            doc.ancestors = fieldAsStrList(fields[ANCESTORS_INDEX]);
            doc.aspect = fieldAsStrList(fields[ASPECT_INDEX]);
            doc.history = fieldAsStrList(fields[HISTORY_INDEX]);
            doc.xrefs = fieldAsStrList(fields[XREFS_INDEX]);
            doc.taxonConstraints = fieldAsStrList(fields[TAXON_CONSTRAINTS_INDEX]);
            doc.blacklist = fieldAsStrList(fields[BLACKLIST_INDEX]);
            doc.annotationGuidelines = fieldAsStrList(fields[ANNOTATION_GUIDELINES_INDEX]);
            doc.xRelations = fieldAsStrList(fields[XRELATIONS_INDEX]);

            return doc;
        } else {
            return null;
        }
    }

}
