package uk.ac.ebi.quickgo.common.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
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
public class FlatFieldBuilder implements FlatField {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFieldBuilder.class);

    static final String LEVEL_SEPARATOR_START = "{--";
    private static final String LEVEL_SEPARATOR_START_REGEX = "\\" + LEVEL_SEPARATOR_START;
    static final String LEVEL_SEPARATOR_END = "--}";
    private static final String LEVEL_SEPARATOR_END_REGEX = "\\" + LEVEL_SEPARATOR_END;
    static final String VALUE_SEPARATOR = ";;;";
    private static final String VALUE_SEPARATOR_REGEX = VALUE_SEPARATOR;
    private static final String TAB = "\t";

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

    private static final Pattern STRING_BREAKDOWN_PATTERN = Pattern.compile(STRING_BREAKDOWN_REGEX);

    private List<FlatField> fields;

    private FlatFieldBuilder() {
        fields = new ArrayList<>();
    }

    public static FlatFieldBuilder newFlatField() {
        return new FlatFieldBuilder();
    }

    /**
     * Converts a string representation of a flat field into a {@link FlatField}} instance.
     *
     * @param flatFieldText text representing a flat field
     * @return an
     */
    public static FlatField parse(String flatFieldText) {
        List<String> values = Arrays.asList(STRING_BREAKDOWN_PATTERN.split(flatFieldText));
        LOGGER.debug("flatFieldText: [{}], has been broken down into {}", flatFieldText, values);
        FlatFieldBuilder builder;

        if (!values.isEmpty() && values.get(0).equals(LEVEL_SEPARATOR_START)) {
            builder = parse(values.subList(1, values.size()).iterator(), newFlatField(), 0);
        } else {
            builder = parse(values.iterator(), newFlatField(), 0);
        }

        return builder;
    }

    /**
     * Recursively constructs a {@link FlatField} by consuming the elements within the {@code valuesIt}.
     *
     * @param valuesIt the text representation that is to be converted into a {@link FlatField}
     * @param builder the builder that will create the {@link FlatField}
     * @param level indicates teh level the builder is currently at (used for debugging purposes)
     * @return the {@link FlatField} that represents the conversion of the {@code valuesIt}
     */
    private static FlatFieldBuilder parse(Iterator<String> valuesIt, FlatFieldBuilder builder, int level) {
        if (!valuesIt.hasNext()) {
            return builder;
        } else {
            String value = valuesIt.next();

            if (value.startsWith(LEVEL_SEPARATOR_START)) {
                LOGGER.debug("{}{}", printTab(level), LEVEL_SEPARATOR_START);
                builder.addField(parse(valuesIt, newFlatField(), level + 1));
            } else if (value.endsWith(LEVEL_SEPARATOR_END)) {
                LOGGER.debug("{}{}", printTab(--level), LEVEL_SEPARATOR_END);
                /*
                 * End level character means that no more parsing is required at this level. Processing should
                 * continue one level up
                 */
                return builder;
            } else {
                LOGGER.debug("{}{}", printTab(level), value);
                builder.addField(newFlatFieldLeaf(value.trim()));
            }

            return parse(valuesIt, builder, level);
        }
    }

    private static String printTab(int level) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < level; i++) {
            builder.append(TAB);
        }

        return builder.toString();
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
                .collect(joining(VALUE_SEPARATOR, LEVEL_SEPARATOR_START, LEVEL_SEPARATOR_END));
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