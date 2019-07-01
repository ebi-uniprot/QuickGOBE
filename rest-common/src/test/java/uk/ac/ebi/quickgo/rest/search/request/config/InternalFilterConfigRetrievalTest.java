package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.common.SearchableField;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

/**
 * Tests the behaviour of the {@link InternalFilterConfigRetrieval} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalFilterConfigRetrievalTest {
    private static final String SEARCHABLE_FIELD_NAME = "field";
    private static final FilterConfig FIELD_EXECUTION_CONFIG =
            createExecutionConfig(SEARCHABLE_FIELD_NAME, ExecutionType.SIMPLE);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private InternalFilterConfigRetrieval config;

    @Mock
    private SearchableField searchableField;

    @Before
    public void setUp() throws Exception {
        when(searchableField.searchableFields()).thenReturn(Stream.of(SEARCHABLE_FIELD_NAME));
        config = new InternalFilterConfigRetrieval(searchableField);
    }

    @Test
    public void nullSearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("SearchableField instance cannot be null.");

        config = new InternalFilterConfigRetrieval(null);
    }

    @Test
    public void nullSearchableFieldNameThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Signature cannot be null or empty");

        config.getBySignature(null);
    }

    @Test
    public void emptySignatureThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Signature cannot be null or empty");

        config.getBySignature(asSet());
    }

    @Test
    public void nonSearchableFieldNameReturnsEmptyOptional() throws Exception {
        String nonSearchableField = "nonField";

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(asSet(nonSearchableField));

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void searchableFieldNameReturnsPopulatedOptional() throws Exception {
        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(asSet(SEARCHABLE_FIELD_NAME));

        assertThat(fieldConfigOpt, is(Optional.of(FIELD_EXECUTION_CONFIG)));
    }
}
