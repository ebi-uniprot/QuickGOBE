package uk.ac.ebi.quickgo.rest.search.filter;

import uk.ac.ebi.quickgo.common.SearchableDocumentFields;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.filter.FieldExecutionConfig.*;
import static uk.ac.ebi.quickgo.rest.search.filter.FilterUtil.createExecutionConfig;

/**
 * Tests the behaviour of the {@link InternalFilterExecutionConfig} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalFilterExecutionConfigTest {
    private static final String SEARCHABLE_FIELD_NAME = "field";
    private static final FieldExecutionConfig FIELD_EXECUTION_CONFIG =
            createExecutionConfig(SEARCHABLE_FIELD_NAME, ExecutionType.SIMPLE);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private InternalFilterExecutionConfig config;

    @Mock
    private SearchableDocumentFields searchableDocumentFields;

    @Before
    public void setUp() throws Exception {
        when(searchableDocumentFields.isDocumentSearchable(SEARCHABLE_FIELD_NAME)).thenReturn(true);

        when(searchableDocumentFields.searchableDocumentFields()).thenReturn(Stream.of(SEARCHABLE_FIELD_NAME));
        config = new InternalFilterExecutionConfig(searchableDocumentFields);
    }

    @Test
    public void nullSearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("SearchableDocumentFields instance cannot be null.");

        config = new InternalFilterExecutionConfig(null);
    }

    @Test
    public void nullSearchableFieldNameThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getConfig(null);
    }

    @Test
    public void emptySearchableFieldNameThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getConfig("");
    }

    @Test
    public void nonSearchableFieldNameReturnsEmptyOptional() throws Exception {
        String nonSearchableField = "nonField";

        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig(nonSearchableField);

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void searchableFieldNameReturnsPopulatedOptional() throws Exception {
        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig(SEARCHABLE_FIELD_NAME);

        assertThat(fieldConfigOpt, is(Optional.of(FIELD_EXECUTION_CONFIG)));
    }
}
