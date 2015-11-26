package uk.ac.ebi.quickgo.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static uk.ac.ebi.quickgo.document.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 26/11/15
 * @author Edd
 */
public class FlatFieldBuilder extends FlatField {
    // add more separators to allow handling deeper nesting
    private static final String[] SEPARATORS = new String[]{
            "|||",
            "%%%",
            "^^^"

    };
    // regexes for elements of SEPARATORS
    private static final String[] SEPARATOR_REGEXES = new String[]{
            "\\|\\|\\|",
            "%%%",
            "\\^\\^\\^"
    };

    private List<FlatField> fields;

    private FlatFieldBuilder() {
        fields = new ArrayList<>();
    }

    public static FlatFieldBuilder newFlatField() {
        return new FlatFieldBuilder();
    }

    public static FlatFieldBuilder parseFlatFieldTree(String flatStr) {
        return parseFlatFieldTree(flatStr, 0);
    }

    private static FlatFieldBuilder parseFlatFieldTree(String flatStr, int level) {
        String[] parts = flatStr.split(SEPARATOR_REGEXES[level]);
        FlatFieldBuilder flatFieldTree = new FlatFieldBuilder();
        Arrays.asList(parts).stream().forEach(f -> {
            if (level+1<SEPARATORS.length && f.contains(SEPARATORS[level + 1])) {
                flatFieldTree.addField(parseFlatFieldTree(f, level + 1));
            } else {
                flatFieldTree.addField(newFlatFieldLeaf(f));
            }
        });
        return flatFieldTree;
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
        fields.stream().forEach( f->
                {
                    sj.add(f.buildString(level + 1));}
        );
        return sj.toString();
    }
}
