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
import static org.hamcrest.Matchers.is;
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
    public void setUp() throws Exception {
        config = new GlobalFilterConfigRetrieval(internalConfigMock, externalConfigMock);
    }

    @Test
    public void nullInternalExecutionConfigThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("InternalExecutionConfiguration cannot be null.");

        config = new GlobalFilterConfigRetrieval(null, externalConfigMock);
    }

    @Test
    public void nullExternalExecutionConfigThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ExternalExecutionConfiguration cannot be null.");

        config = new GlobalFilterConfigRetrieval(internalConfigMock, null);
    }

    @Test
    public void nullSearchableFieldThrowsException() throws Exception {
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
    public void searchableFieldNameKnownToInternalConfigReturnsPopulatedOptional() throws Exception {
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
    public void searchableFieldNameKnownToExternalConfigReturnsPopulatedOptional() throws Exception {
        String externalFieldName = "field";
        Set<String> externalFieldSet = asSet(externalFieldName);

        when(internalConfigMock.getBySignature(externalFieldSet)).thenReturn(Optional.empty());

        Optional<FilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(externalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getBySignature(externalFieldSet)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(externalFieldSet)).thenReturn(expectedFieldConfigOpt);

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(externalFieldSet);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    public void unknownSearchableFieldNameReturnsEmptyOptional() throws Exception {
        String unknownFieldName = "unknown";
        Set<String> unknownFieldSet = asSet(unknownFieldName);

        when(internalConfigMock.getBySignature(unknownFieldSet)).thenReturn(Optional.empty());
        when(externalConfigMock.getBySignature(unknownFieldSet)).thenReturn(Optional.empty());

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(unknownFieldSet);

        assertThat(fieldConfigOpt, is(Optional.empty()));
    }

    @Test
    public void searchableFieldExistsInInternalAndExternalExecutionConfigSoInternalTakesPrecedence() throws Exception {
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
}