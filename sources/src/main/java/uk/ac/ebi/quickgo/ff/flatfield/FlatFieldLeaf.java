package uk.ac.ebi.quickgo.ff.flatfield;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * A flat field representing a {@link String} value. These are stored
 * within a (possibly nested) list of {@link FlatField}s inside the
 * {@FlatFieldBuilder} class.
 *
 * Created 26/11/15
 * @author Edd
 */
public class FlatFieldLeaf extends FlatField {
    private static final String PRINTED_NULL_STRING = "";
    private String value;
    private final static List<FlatField> EMPTY_LIST = Collections.unmodifiableList(Collections.emptyList());

    private FlatFieldLeaf(String value) {
        if (nonNull(value)) {
            this.value = value;
        } else {
            this.value = PRINTED_NULL_STRING;
        }
    }

    public static FlatFieldLeaf newFlatFieldLeaf(String value) {
        return new FlatFieldLeaf(value);
    }

    public static FlatFieldLeaf newFlatFieldLeaf() {
        return new FlatFieldLeaf("");
    }

    @Override protected String buildStringFromDepth(int level) {
        return buildString();
    }

    @Override public List<FlatField> getFields() {
        return EMPTY_LIST;
    }

    @Override public String buildString() {
        return this.value;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FlatFieldLeaf that = (FlatFieldLeaf) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);
    }

    @Override public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override public String toString() {
        return "FlatFieldLeaf{" +
                "value='" + value + '\'' +
                '}';
    }
}
