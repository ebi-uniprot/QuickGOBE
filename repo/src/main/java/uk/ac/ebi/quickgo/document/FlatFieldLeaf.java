package uk.ac.ebi.quickgo.document;

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

    @Override protected String buildString(int level) {
        return buildString();
    }

    @Override public List<FlatField> getFields() {
        return null;
    }

    @Override public String buildString() {
        return this.value;
    }
}
