package uk.ac.ebi.quickgo.common.converter;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.joining;
import static uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf.newFlatFieldLeaf;

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
    public static final String VALUE_SEPARATOR = "|";
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFieldBuilder.class);

    private static final String LEVEL_SEPARATOR_START = "[";
    private static final String LEVEL_SEPARATOR_START_REGEX = "\\" + LEVEL_SEPARATOR_START;
    private static final String LEVEL_SEPARATOR_END = "]";
    private static final String LEVEL_SEPARATOR_END_REGEX = "\\" + LEVEL_SEPARATOR_END;
    private static final String VALUE_SEPARATOR_REGEX = "\\|";

    /**
     * A regular expression used to breakdown the string fed to {@link FlatFieldBuilder#parse(String)}, that splits the
     * string on:
     * <ul>
     *     <li>{@link FlatFieldBuilder#LEVEL_SEPARATOR_START}</li>
     *     <li>{@link FlatFieldBuilder#LEVEL_SEPARATOR_END}</li>
     *     <li>{@link FlatFieldBuilder#VALUE_SEPARATOR}</li>
     * </ul>
     */
    private static final String STRING_BREAKDOWN_REGEX =
            "(?<=" + LEVEL_SEPARATOR_START_REGEX + ")|" +
                    "(?=" + LEVEL_SEPARATOR_END_REGEX + ")|" +
                    VALUE_SEPARATOR_REGEX;
    private List<FlatField> fields;

    private FlatFieldBuilder() {
        fields = new ArrayList<>();
    }

    public static FlatFieldBuilder newFlatField() {
        return new FlatFieldBuilder();
    }

    public static FlatFieldBuilder parse(String str) {
        List<String> values = Arrays.asList(str.split(STRING_BREAKDOWN_REGEX));

        FlatFieldBuilder builder;

        if (!values.isEmpty() && values.get(0).equals(LEVEL_SEPARATOR_START)) {
            builder = parse(values.subList(1, values.size()).iterator(), newFlatField());
        } else {
            builder = parse(values.iterator(), newFlatField());
        }

        return builder;
    }

    private static FlatFieldBuilder parse(Iterator<String> valuesIt, FlatFieldBuilder builder) {
        if (!valuesIt.hasNext()) {
            return builder;
        } else {
            String value = valuesIt.next();

            if (value.startsWith(LEVEL_SEPARATOR_START)) {
                builder.addField(parse(valuesIt, newFlatField()));
            } else if (value.endsWith(LEVEL_SEPARATOR_END)) {
                //ignore the end character, no processing needed
                return builder;
            } else {
                builder.addField(newFlatFieldLeaf(value));
            }

            return parse(valuesIt, builder);
        }
    }

    public FlatFieldBuilder addField(FlatField field) {
        fields.add(field);
        return this;
    }

    @Override
    public List<FlatField> getFields() {
        return fields;
    }

    @Override
    public String buildString() {
        return fields.stream()
                .map(FlatField::buildString)
                .collect(joining(VALUE_SEPARATOR, "[", "]"));
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
