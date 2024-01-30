package uk.ac.ebi.quickgo.rest.search.request.config;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.search.request.FilterUtil;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

/**
 * Test the behaviour of the {@link GlobalFilterConfigRetrieval} class.
 */
@ExtendWith(MockitoExtension.class)
class GlobalFilterConfigRetrievalTest {

    private GlobalFilterConfigRetrieval config;

    @Mock
    private ExternalFilterConfigRetrieval externalConfigMock;

    @Mock
    private InternalFilterConfigRetrieval internalConfigMock;

    @BeforeEach
    void setUp() {
        config = new GlobalFilterConfigRetrieval(internalConfigMock, externalConfigMock);
    }

    @Test
    void nullInternalExecutionConfigThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config = new GlobalFilterConfigRetrieval(null, externalConfigMock));
        assertTrue(exception.getMessage().contains("InternalExecutionConfiguration cannot be null."));
    }

    @Test
    void nullExternalExecutionConfigThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config = new GlobalFilterConfigRetrieval(internalConfigMock, null));
        assertTrue(exception.getMessage().contains("ExternalExecutionConfiguration cannot be null."));
    }

    @Test
    void nullSearchableFieldThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config.getBySignature(null));
        assertTrue(exception.getMessage().contains("Signature cannot be null or empty"));
    }

    @Test
    void emptySignatureThrowsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> config.getBySignature(asSet()));
        assertTrue(exception.getMessage().contains("Signature cannot be null or empty"));
    }

    @Test
    void searchableFieldNameKnownToInternalConfigReturnsPopulatedOptional() {
        String internalFieldName = "field";
        Set<String> internalFieldSet = asSet(internalFieldName);

        Optional<FilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(internalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getBySignature(internalFieldSet)).thenReturn(expectedFieldConfigOpt);
        when(externalConfigMock.getBySignature(internalFieldSet)).thenReturn(Optional.empty());

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(internalFieldSet);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    void searchableFieldNameKnownToExternalConfigReturnsPopulatedOptional() {
        String externalFieldName = "field";
        Set<String> externalFieldSet = asSet(externalFieldName);

        Optional<FilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(externalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getBySignature(externalFieldSet)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(externalFieldSet)).thenReturn(expectedFieldConfigOpt);

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(externalFieldSet);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    void unknownSearchableFieldNameReturnsEmptyOptional() {
        String unknownFieldName = "unknown";
        Set<String> unknownFieldSet = asSet(unknownFieldName);

        when(internalConfigMock.getBySignature(unknownFieldSet)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(unknownFieldSet)).thenReturn(Optional.empty());

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(unknownFieldSet);

        assertThat(fieldConfigOpt, is(Optional.empty()));
    }

    @Test
    void searchableFieldExistsInInternalAndExternalExecutionConfigSoInternalTakesPrecedence() {
        String searchableField = "field";
        Set<String> searchableFieldSet = asSet(searchableField);

        Optional<FilterConfig> internalFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(searchableField, ExecutionType.SIMPLE)
        );

        Optional<FilterConfig> externalFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(searchableField, ExecutionType.JOIN)
        );

        when(internalConfigMock.getBySignature(searchableFieldSet)).thenReturn(internalFieldConfigOpt);
        when(externalConfigMock.getBySignature(searchableFieldSet)).thenReturn(externalFieldConfigOpt);

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(searchableFieldSet);

        FilterConfig retrievedField = fieldConfigOpt.get();

        assertThat(retrievedField.getSignature(), is(asSet(searchableField)));
        assertThat(retrievedField.getExecution(), is(ExecutionType.SIMPLE));
    }

    @Test
    void checkConfigCacheIsPopulatedWhenSignatureIsFetched() {
        String externalFieldName = "field";
        Set<String> signature = asSet(externalFieldName);

        Optional<FilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(externalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getBySignature(signature)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(signature)).thenReturn(expectedFieldConfigOpt);

        assertThat(config.configCache.entrySet(), is(empty()));

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(signature);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
        assertThat(config.configCache, hasEntry(signature, fieldConfigOpt));
    }

    @Test
    void checkConfigCacheIsUsedWhenFetchingSameSignatureMultipleTimes() {
        String externalFieldName = "field";
        Set<String> signature = asSet(externalFieldName);

        Optional<FilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(externalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getBySignature(signature)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(signature)).thenReturn(expectedFieldConfigOpt);

        Optional<FilterConfig> configOptOnFirstCall = config.getBySignature(signature);
        assertThat(configOptOnFirstCall, is(expectedFieldConfigOpt));

        verify(externalConfigMock, times(1)).getBySignature(signature);

        Optional<FilterConfig> configOptOnSecondCall = config.getBySignature(signature);
        assertThat(configOptOnSecondCall, is(expectedFieldConfigOpt));

        verify(externalConfigMock, times(1)).getBySignature(signature);

    }
}