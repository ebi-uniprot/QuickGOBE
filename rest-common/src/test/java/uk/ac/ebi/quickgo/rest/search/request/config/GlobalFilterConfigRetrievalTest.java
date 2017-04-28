package uk.ac.ebi.quickgo.rest.search.request.config;

import uk.ac.ebi.quickgo.rest.search.request.FilterUtil;

import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType;

/**
 * Test the behaviour of the {@link GlobalFilterConfigRetrieval} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GlobalFilterConfigRetrievalTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private GlobalFilterConfigRetrieval config;

    @Mock
    private ExternalFilterConfigRetrieval externalConfigMock;

    @Mock
    private InternalFilterConfigRetrieval internalConfigMock;

    @Before
    public void setUp() {
        config = new GlobalFilterConfigRetrieval(internalConfigMock, externalConfigMock);
    }

    @Test
    public void nullInternalExecutionConfigThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("InternalExecutionConfiguration cannot be null.");

        config = new GlobalFilterConfigRetrieval(null, externalConfigMock);
    }

    @Test
    public void nullExternalExecutionConfigThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ExternalExecutionConfiguration cannot be null.");

        config = new GlobalFilterConfigRetrieval(internalConfigMock, null);
    }

    @Test
    public void nullSearchableFieldThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Signature cannot be null or empty");

        config.getBySignature(null);
    }

    @Test
    public void emptySignatureThrowsException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Signature cannot be null or empty");

        config.getBySignature(asSet());
    }

    @Test
    public void searchableFieldNameKnownToInternalConfigReturnsPopulatedOptional() {
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
    public void searchableFieldNameKnownToExternalConfigReturnsPopulatedOptional() {
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
    public void unknownSearchableFieldNameReturnsEmptyOptional() {
        String unknownFieldName = "unknown";
        Set<String> unknownFieldSet = asSet(unknownFieldName);

        when(internalConfigMock.getBySignature(unknownFieldSet)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(unknownFieldSet)).thenReturn(Optional.empty());

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(unknownFieldSet);

        assertThat(fieldConfigOpt, is(Optional.empty()));
    }

    @Test
    public void searchableFieldExistsInInternalAndExternalExecutionConfigSoInternalTakesPrecedence() {
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
    public void checkConfigCacheIsPopulatedWhenSignatureIsFetched() {
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
    public void checkConfigCacheIsUsedWhenFetchingSameSignatureMultipleTimes() {
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