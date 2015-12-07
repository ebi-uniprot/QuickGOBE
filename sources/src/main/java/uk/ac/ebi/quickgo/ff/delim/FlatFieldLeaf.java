package uk.ac.ebi.quickgo.ff.delim;

import java.util.List;

/**
 * Created 26/11/15
 * @author Edd
 */
public class FlatFieldLeaf extends FlatField {
    private String value;

    private FlatFieldLeaf(String value) {
        this.value = value;
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
        return null;
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
