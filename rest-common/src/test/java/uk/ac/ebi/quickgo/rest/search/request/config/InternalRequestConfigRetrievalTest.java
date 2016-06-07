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
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType;

/**
 * Tests the behaviour of the {@link InternalRequestConfigRetrieval} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalRequestConfigRetrievalTest {
    private static final String SEARCHABLE_FIELD_NAME = "field";
    private static final RequestConfig FIELD_EXECUTION_CONFIG =
            createExecutionConfig(SEARCHABLE_FIELD_NAME, ExecutionType.SIMPLE);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private InternalRequestConfigRetrieval config;

    @Mock
    private SearchableDocumentFields searchableDocumentFields;

    @Before
    public void setUp() throws Exception {
        when(searchableDocumentFields.isDocumentSearchable(SEARCHABLE_FIELD_NAME)).thenReturn(true);

        when(searchableDocumentFields.searchableDocumentFields()).thenReturn(Stream.of(SEARCHABLE_FIELD_NAME));
        config = new InternalRequestConfigRetrieval(searchableDocumentFields);
    }

    @Test
    public void nullSearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("SearchableDocumentFields instance cannot be null.");

        config = new InternalRequestConfigRetrieval(null);
    }

    @Test
    public void nullSearchableFieldNameThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getSignature(null);
    }

    @Test
    public void emptySearchableFieldNameThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getSignature("");
    }

    @Test
    public void nonSearchableFieldNameReturnsEmptyOptional() throws Exception {
        String nonSearchableField = "nonField";

        Optional<RequestConfig> fieldConfigOpt = config.getSignature(nonSearchableField);

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void searchableFieldNameReturnsPopulatedOptional() throws Exception {
        Optional<RequestConfig> fieldConfigOpt = config.getSignature(SEARCHABLE_FIELD_NAME);

        assertThat(fieldConfigOpt, is(Optional.of(FIELD_EXECUTION_CONFIG)));
    }
}
