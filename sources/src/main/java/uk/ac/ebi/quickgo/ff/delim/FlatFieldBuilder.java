package uk.ac.ebi.quickgo.ff.delim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static uk.ac.ebi.quickgo.ff.delim.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Used to build a {@link String} representation of field that has nested sub-fields.
 * For example, a name field could be made up of two sub-fields first and second name.
 *
 * This class is used when constructing nested structures that need serialising as a {@link String}
 * so they can be stored in a Solr document. Methods are also provided to reconstruct
 * the original model from the serialised string.
 *
 * Created 26/11/15
 * @author Edd
 */
public class FlatFieldBuilder extends FlatField {
    // add more separators to allow handling deeper nesting
    private static final String[] SEPARATORS = new String[]{
            "\t",
            "|||",
            "^^^",
            "%%%",
            ":::"
    };
    // regexes for elements of SEPARATORS
    private static final String[] SEPARATOR_REGEXES = new String[]{
            "\t",
            "\\|\\|\\|",
            "\\^\\^\\^",
            "%%%",
            ":::"
    };

    private List<FlatField> fields;

    private FlatFieldBuilder() {
        fields = new ArrayList<>();
    }

    public static FlatFieldBuilder newFlatField() {
        return new FlatFieldBuilder();
    }

    public static FlatFieldBuilder parseFlatField(String flatStr) {
        return parseFlatField(flatStr, 0);
    }

    public static FlatFieldBuilder parseFlatField(String flatStr, int level) {
        ArrayList<String> components = new ArrayList<>();
        if (flatStr.startsWith(SEPARATORS[level])) {
            components.add("");
        }
        components.addAll(Arrays.asList(flatStr.split(SEPARATOR_REGEXES[level])));
        if (flatStr.endsWith(SEPARATORS[level])) {
            components.add("");
        }

        FlatFieldBuilder flatField = new FlatFieldBuilder();
        components.stream().forEach(f -> {
            if (level + 1 < SEPARATORS.length && f.contains(SEPARATORS[level + 1])) {
                flatField.addField(parseFlatField(f, level + 1));
            } else {
                flatField.addField(newFlatFieldLeaf(f));
            }
        });
        return flatField;
    }

    public FlatFieldBuilder addField(FlatField field) {
        fields.add(field);
        return this;
    }

    public List<FlatField> getFields() {
        return fields;
    }

    public String buildString() {
        return buildString(0);
    }

    public String buildString(int level) {
        StringJoiner sj = new StringJoiner(SEPARATORS[level]);
        fields.stream().forEach(f ->
                sj.add(f.buildString(level + 1))
        );
        return sj.toString();
    }
}
