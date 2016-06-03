package uk.ac.ebi.quickgo.rest.search.filter;

import java.util.Optional;
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
import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType;

/**
 * Test the behaviour of the {@link GlobalRequestFilterConfigRetrieval} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GlobalRequestFilterConfigRetrievalTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private GlobalRequestFilterConfigRetrieval config;

    @Mock
    private ExternalRequestFilterConfigRetrieval externalConfigMock;

    @Mock
    private InternalRequestFilterConfigRetrieval internalConfigMock;

    @Before
    public void setUp() throws Exception {
        config = new GlobalRequestFilterConfigRetrieval(internalConfigMock, externalConfigMock);
    }

    @Test
    public void nullInternalExecutionConfigThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("InternalExecutionConfiguration cannot be null.");

        config = new GlobalRequestFilterConfigRetrieval(null, externalConfigMock);
    }

    @Test
    public void nullExternalExecutionConfigThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ExternalExecutionConfiguration cannot be null.");

        config = new GlobalRequestFilterConfigRetrieval(internalConfigMock, null);
    }

    @Test
    public void nullSearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getSignature(null);
    }

    @Test
    public void emptySearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getSignature("");
    }

    @Test
    public void searchableFieldNameKnownToInternalConfigReturnsPopulatedOptional() throws Exception {
        String internalFieldName = "field";

        Optional<RequestFilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(internalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getSignature(internalFieldName)).thenReturn(expectedFieldConfigOpt);
        when(externalConfigMock.getSignature(internalFieldName)).thenReturn(Optional.empty());

        Optional<RequestFilterConfig> fieldConfigOpt = config.getSignature(internalFieldName);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    public void searchableFieldNameKnownToExternalConfigReturnsPopulatedOptional() throws Exception {
        String externalFieldName = "field";

        when(internalConfigMock.getSignature(externalFieldName)).thenReturn(Optional.empty());

        Optional<RequestFilterConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(externalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getSignature(externalFieldName)).thenReturn(Optional.empty());
        when(externalConfigMock.getSignature(externalFieldName)).thenReturn(expectedFieldConfigOpt);

        Optional<RequestFilterConfig> fieldConfigOpt = config.getSignature(externalFieldName);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    public void unknownSearchableFieldNameReturnsEmptyOptional() throws Exception {
        String unknownFieldName = "unknown";

        when(internalConfigMock.getSignature(unknownFieldName)).thenReturn(Optional.empty());
        when(externalConfigMock.getSignature(unknownFieldName)).thenReturn(Optional.empty());

        Optional<RequestFilterConfig> fieldConfigOpt = config.getSignature(unknownFieldName);

        assertThat(fieldConfigOpt, is(Optional.empty()));
    }

    @Test
    public void searchableFieldExistsInInternalAndExternalExecutionConfigSoInternalTakesPrecedence() throws Exception {
        String searchableField = "field";

        Optional<RequestFilterConfig> internalFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(searchableField, ExecutionType.SIMPLE)
        );

        Optional<RequestFilterConfig> externalFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(searchableField, ExecutionType.JOIN)
        );


        when(internalConfigMock.getSignature(searchableField)).thenReturn(internalFieldConfigOpt);
        when(externalConfigMock.getSignature(searchableField)).thenReturn(externalFieldConfigOpt);

        Optional<RequestFilterConfig> fieldConfigOpt = config.getSignature(searchableField);

        RequestFilterConfig retrievedField = fieldConfigOpt.get();

        assertThat(retrievedField.getSignature(), is(searchableField));
        assertThat(retrievedField.getExecution(), is(ExecutionType.SIMPLE));
    }
}