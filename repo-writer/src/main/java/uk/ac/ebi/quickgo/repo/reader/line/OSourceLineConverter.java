package uk.ac.ebi.quickgo.repo.reader.line;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.ff.delim.FlatField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.SEPARATOR_REGEXES;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.ff.delim.FlatFieldBuilder.newFlatFieldFromDepth;

/**
 * Converts an ontology record/line from a delimited file to an {@link OntologyDocument} instance.
 *
 * Performs a simple mapping of fields of a delimited line, to values of a Solr document.
 *
 * Created 03/12/15
 * @author Edd
 */
public class OSourceLineConverter implements Function<String, OntologyDocument> {

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

    private static List<String> level1FieldAsStrList(String field) {
        return field.equals("")? null : Arrays.asList(field.split(SEPARATOR_REGEXES[1]));
    }

    @Override public OntologyDocument apply(String s) {
        List<FlatField> flatFields = newFlatField().parseToDepth(s, 0).getFields();

        if (flatFields.size() == 20) {
            OntologyDocument doc = new OntologyDocument();
            doc.id = flatFields.get(ID_INDEX).buildString();
            doc.ontologyType = doc.id.substring(0, doc.id.indexOf(":"));
            doc.name = flatFields.get(NAME_INDEX).buildString();
            doc.isObsolete = Boolean.valueOf(flatFields.get(IS_OBSOLETE_INDEX).buildString());
            doc.definition = flatFields.get(DEFINITION_INDEX).buildString();
            doc.comment = flatFields.get(COMMENT_INDEX).buildString();
            doc.secondaryIds = level1FieldAsStrList(flatFields.get(SECONDARIES_INDEX).buildString());
            doc.usage = flatFields.get(USAGE_INDEX).buildString();
            doc.synonyms = level1FieldAsStrList(flatFields.get(SYNONYMS_INDEX).buildString());
            doc.synonymNames = doc.synonyms == null? null : doc.synonyms.stream()
                    .map(synField ->
                            newFlatFieldFromDepth(2).parse(synField).getFields().get(0).buildString())
                    .collect(Collectors.toList());
            doc.subsets = level1FieldAsStrList(flatFields.get(SUBSETS_INDEX).buildString());
            doc.replacedBy = flatFields.get(REPLACED_BY_INDEX).buildString();
            doc.considers = level1FieldAsStrList(flatFields.get(CONSIDERS_INDEX).buildString());
            doc.children = level1FieldAsStrList(flatFields.get(CHILDREN_INDEX).buildString());
            doc.ancestors = level1FieldAsStrList(flatFields.get(ANCESTORS_INDEX).buildString());
            doc.aspect = level1FieldAsStrList(flatFields.get(ASPECT_INDEX).buildString());
            doc.history = level1FieldAsStrList(flatFields.get(HISTORY_INDEX).buildString());
            doc.xrefs = level1FieldAsStrList(flatFields.get(XREFS_INDEX).buildString());
            doc.taxonConstraints = level1FieldAsStrList(flatFields.get(TAXON_CONSTRAINTS_INDEX).buildString());
            doc.blacklist = level1FieldAsStrList(flatFields.get(BLACKLIST_INDEX).buildString());
            doc.annotationGuidelines = level1FieldAsStrList(flatFields.get(ANNOTATION_GUIDELINES_INDEX).buildString());
            doc.xRelations = level1FieldAsStrList(flatFields.get(XRELATIONS_INDEX).buildString());

            return doc;
        } else {
            return null;
        }
    }

}
