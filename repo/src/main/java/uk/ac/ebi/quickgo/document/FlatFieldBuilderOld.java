package uk.ac.ebi.quickgo.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created 25/11/15
 * @author Edd
 */
public class FlatFieldBuilderOld {
    public static final String SEPARATOR = "|||";
    public static final String SEPARATOR_REGEX = "\\|\\|\\|";

    private List<String> fields = new ArrayList<>();

    private FlatFieldBuilderOld() {

    }

    public FlatFieldBuilderOld addField(String value) {
        fields.add(value);
        return this;
    }

    public FlatFieldBuilderOld addField(int index, String value) {
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

    public static FlatFieldBuilderOld newFlatField(String flatStr) {
        String[] parts = flatStr.split(SEPARATOR_REGEX);
        FlatFieldBuilderOld builder = new FlatFieldBuilderOld();
        Arrays.asList(parts).stream().forEach(builder::addField);
        return builder;
    }

    public static FlatFieldBuilderOld newFlatField() {
        return new FlatFieldBuilderOld();
    }
}
