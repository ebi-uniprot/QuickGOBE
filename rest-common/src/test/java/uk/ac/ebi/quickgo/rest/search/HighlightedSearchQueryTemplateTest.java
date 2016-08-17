package uk.ac.ebi.quickgo.rest.search;

import uk.ac.ebi.quickgo.rest.search.query.FieldHighlight;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Tests the behaviour of the {@link HighlightedSearchQueryTemplate} class.
 */
public class HighlightedSearchQueryTemplateTest {
    private static final String START_DELIMETER = "<";
    private static final String END_DELIMETER = ">";
    private static final Set<String> EMPTY_FIELD_SET = Collections.emptySet();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private HighlightedSearchQueryTemplate searchTemplate;

    @Before
    public void setUp() throws Exception {
        searchTemplate = new HighlightedSearchQueryTemplate(START_DELIMETER, END_DELIMETER);
    }

    @Test
    public void nullStartDelimiterThrowsExceptionInTemplateConstruction() throws Exception {
        assertExceptionInTemplateConstruction(null, END_DELIMETER, EMPTY_FIELD_SET,
                "Highlighting start delimiter cannot be null or empty");
    }

    @Test
    public void emptyStartDelimiterThrowsExceptionInTemplateConstruction() throws Exception {
        assertExceptionInTemplateConstruction("", END_DELIMETER, EMPTY_FIELD_SET,
                "Highlighting start delimiter cannot be null or empty");
    }

    @Test
    public void nullEndDelimiterThrowsExceptionInTemplateConstruction() throws Exception {
        assertExceptionInTemplateConstruction(START_DELIMETER, null, EMPTY_FIELD_SET,
                "Highlighting end delimiter cannot be null or empty");
    }

    @Test
    public void emptyEndDelimiterThrowsExceptionInTemplateConstruction() throws Exception {
        assertExceptionInTemplateConstruction(START_DELIMETER, "", EMPTY_FIELD_SET,
                "Highlighting end delimiter cannot be null or empty");
    }

    @Test
    public void nullFieldSetThrowsExceptionInTemplateConstruction() throws Exception {
        assertExceptionInTemplateConstruction(START_DELIMETER, END_DELIMETER, null,
                "Highlighting field set cannot be null");
    }

    @Test
    public void newBuilderInheritsDefinitionsFromFakeBuilder() throws Exception {
        QuickGOQuery query = QuickGOQuery.createQuery("field", "value");

        HighlightedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder(query));

        QueryRequest request = builder.build();

        assertThat(request.getQuery(), is(query));
    }

    @Test
    public void builderInheritsStartAndEndHighlightDelimitersFromSearchQueryTemplate() throws Exception {
        HighlightedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());

        QueryRequest request = builder.build();

        assertThat(request.getHighlightStartDelim(), is(START_DELIMETER));
        assertThat(request.getHighlightEndDelim(), is(END_DELIMETER));
    }

    @Test
    public void builderCreatedFromSearchTemplateWithEmptyHighlightedFieldsCreatesQueryRequestWithNoHighlightedFields()
            throws Exception {
        searchTemplate = new HighlightedSearchQueryTemplate(START_DELIMETER, END_DELIMETER);
        HighlightedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());

        QueryRequest request = builder.build();

        assertThat(request.getHighlightedFields(), hasSize(0));
    }

    @Test
    public void
    builderCreatedFromSearchTemplateWithPopulatedHighlightedFieldsCreatesQueryRequestWithHighlightedFields()
            throws Exception {
        String field1 = "field1";
        String field2 = "field2";

        Set<String> fields = new HashSet<>();
        fields.add(field1);
        fields.add(field2);

        searchTemplate = new HighlightedSearchQueryTemplate(START_DELIMETER, END_DELIMETER, fields);
        HighlightedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());

        QueryRequest request = builder.build();

        FieldHighlight actualField1Highlight = new FieldHighlight(field1);
        FieldHighlight actualField2Highlight = new FieldHighlight(field2);

        assertThat(request.getHighlightedFields(), containsInAnyOrder(actualField1Highlight, actualField2Highlight));
    }

    @Test
    public void builderOverwritesInheritedSearchFieldsWithUserSuppliedOnes()
            throws Exception {
        String inheritedField = "inheritedField";
        String field1 = "field1";
        String field2 = "field2";

        Set<String> fields = new HashSet<>();
        fields.add(field1);
        fields.add(field2);

        searchTemplate = new HighlightedSearchQueryTemplate(START_DELIMETER, END_DELIMETER,
                Collections.singleton(inheritedField));

        HighlightedSearchQueryTemplate.Builder builder = searchTemplate.newBuilder(new FakeCompositeBuilder());
        builder.setFields(fields);

        QueryRequest request = builder.build();

        FieldHighlight inheritedHighlight = new FieldHighlight(inheritedField);
        FieldHighlight actualField1Highlight = new FieldHighlight(field1);
        FieldHighlight actualField2Highlight = new FieldHighlight(field2);

        assertThat(request.getHighlightedFields(), containsInAnyOrder(actualField1Highlight, actualField2Highlight));
        assertThat(request.getHighlightedFields(), not(hasItem(inheritedHighlight)));
    }

    private void assertExceptionInTemplateConstruction(String startDelimiter, String endDelimiter, Set<String>
            fields, String errorMsg) {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(errorMsg);

        new HighlightedSearchQueryTemplate(startDelimiter, endDelimiter, fields);
    }
}