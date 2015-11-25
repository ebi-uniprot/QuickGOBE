package uk.ac.ebi.quickgo.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created 25/11/15
 * @author Edd
 */
public class FlatFieldBuilder {
    public static final String SEPARATOR = "|||";
    public static final String SEPARATOR_REGEX = "\\|\\|\\|";

    private List<String> fields = new ArrayList<>();

    private FlatFieldBuilder() {

    }

    public FlatFieldBuilder addField(String value) {
        fields.add(value);
        return this;
    }

    public FlatFieldBuilder addField(int index, String value) {
        fields.add(index, value);
        return this;
    }

    public String buildString() {
        StringJoiner sj = new StringJoiner(SEPARATOR);
        fields.stream().forEach(sj::add);
        return sj.toString();
    }

    public List<String> getFields() {
        return fields;
    }

    public static FlatFieldBuilder newFlatField(String flatStr) {
        String[] parts = flatStr.split(SEPARATOR_REGEX);
        FlatFieldBuilder builder = new FlatFieldBuilder();
        Arrays.asList(parts).stream().forEach(builder::addField);
        return builder;
    }

    public static FlatFieldBuilder newFlatField() {
        return new FlatFieldBuilder();
    }
}
