package uk.ac.ebi.quickgo.ff.delim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.delim.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Used to build a {@link String} representation of a field that has nested sub-fields.
 * For example, a name field could be made up of two sub-fields; a first and a second name.
 *
 * This class is used when constructing nested structures that need serialising as a {@link String}
 * so they can be stored in a Solr document. Methods are also provided to reconstruct
 * the original model from the serialised string.
 *
 * Created 26/11/15
 * @author Edd
 */
public class FlatFieldBuilder extends FlatField {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFieldBuilder.class);

    // add more separators to allow handling deeper nesting
    private static final String[] SEPARATORS = new String[]{
            "\t",
            "|||",
            "^^^",
            "%%%",
            ":::",
            "£££"
    };
    // regexes for elements of SEPARATORS
    private static final String[] SEPARATOR_REGEXES = new String[]{
            "\t",
            "\\|\\|\\|",
            "\\^\\^\\^",
            "%%%",
            ":::",
            "£££"
    };

    private List<FlatField> fields;
    private int depth;

    private FlatFieldBuilder(int depth) {
        fields = new ArrayList<>();
        this.depth = depth;
    }

    public static FlatFieldBuilder newFlatField() {
        return new FlatFieldBuilder(0);
    }

    public static FlatFieldBuilder newFlatFieldFromDepth(int depth) {
        return new FlatFieldBuilder(depth);
    }

    public FlatFieldBuilder parse(String str) {
        return parse(str, depth, SEPARATORS.length - 1);
    }

    public FlatFieldBuilder parseToDepth(String str, int max) {
        return parse(str, depth, max);
    }

    private FlatFieldBuilder parse(String flatStr, int startLevel, int maxLevel) {
        if (maxLevel >= SEPARATORS.length) {
            IllegalArgumentException exception = new IllegalArgumentException(
                    "FlatFieldBuilder maximum depth specified is greater than known number" +
                            " of delimiters (" + SEPARATORS.length + ")");
            LOGGER.error("Cannot create FlatFieldBuilder: ", exception);
            throw exception;
        }

        ArrayList<String> components = new ArrayList<>();
        if (flatStr.startsWith(SEPARATORS[startLevel])) {
            components.add("");
        }
        components.addAll(Arrays.asList(flatStr.split(SEPARATOR_REGEXES[startLevel])));
        if (flatStr.endsWith(SEPARATORS[startLevel])) {
            components.add("");
        }

        FlatFieldBuilder flatField = new FlatFieldBuilder(startLevel);

        components.stream().forEach(f -> {
            if (startLevel + 1 < SEPARATORS.length && startLevel < maxLevel && f.contains(SEPARATORS[startLevel + 1])) {
                flatField.addField(parse(f, startLevel + 1, SEPARATORS.length - 1));
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

    protected String buildStringFromLevel(int level) {
        StringJoiner sj = new StringJoiner(SEPARATORS[level]);
        fields.stream().forEach(f ->
                sj.add(f.buildStringFromLevel(level + 1))
        );
        return sj.toString();
    }

    public String buildString() {
        StringJoiner sj = new StringJoiner(SEPARATORS[depth]);
        fields.stream().forEach(f ->
                sj.add(f.buildStringFromLevel(depth + 1))
        );
        return sj.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FlatFieldBuilder that = (FlatFieldBuilder) o;

        return !(fields != null ? !fields.equals(that.fields) : that.fields != null);

    }

    @Override public int hashCode() {
        return fields != null ? fields.hashCode() : 0;
    }

    @Override public String toString() {
        return "FlatFieldBuilder{" +
                "fields=" + fields +
                '}';
    }
}
