package uk.ac.ebi.quickgo.rest.search.request.config;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.common.SearchableField;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

/**
 * Tests the behaviour of the {@link InternalFilterConfigRetrieval} class.
 */
@ExtendWith(MockitoExtension.class)
class InternalFilterConfigRetrievalTest {
    private static final String SEARCHABLE_FIELD_NAME = "field";
    private static final FilterConfig FIELD_EXECUTION_CONFIG =
            createExecutionConfig(SEARCHABLE_FIELD_NAME, ExecutionType.SIMPLE);

    private InternalFilterConfigRetrieval config;

    @Mock
    private SearchableField searchableField;

    @BeforeEach
    void setUp() {
        when(searchableField.searchableFields()).thenReturn(Stream.of(SEARCHABLE_FIELD_NAME));
        config = new InternalFilterConfigRetrieval(searchableField);
    }

    @Test
    void nullSearchableFieldThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config = new InternalFilterConfigRetrieval(null));
        assertTrue(exception.getMessage().contains("SearchableField instance cannot be null."));
    }

    @Test
    void nullSearchableFieldNameThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config.getBySignature(null));
        assertTrue(exception.getMessage().contains("Signature cannot be null or empty"));
    }

    @Test
    void emptySignatureThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config.getBySignature(asSet()));
        assertTrue(exception.getMessage().contains("Signature cannot be null or empty"));
    }

    @Test
    void nonSearchableFieldNameReturnsEmptyOptional() {
        String nonSearchableField = "nonField";

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(asSet(nonSearchableField));

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    void searchableFieldNameReturnsPopulatedOptional() {
        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(asSet(SEARCHABLE_FIELD_NAME));

        assertThat(fieldConfigOpt, is(Optional.of(FIELD_EXECUTION_CONFIG)));
    }
}
