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
import static uk.ac.ebi.quickgo.rest.search.filter.FieldExecutionConfig.*;

/**
 * Test the behaviour of the {@link GlobalFilterExecutionConfig} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GlobalFilterExecutionConfigTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private GlobalFilterExecutionConfig config;

    @Mock
    private ExternalFilterExecutionConfig externalConfigMock;

    @Mock
    private InternalFilterExecutionConfig internalConfigMock;

    @Before
    public void setUp() throws Exception {
        config = new GlobalFilterExecutionConfig(internalConfigMock, externalConfigMock);
    }

    @Test
    public void nullInternalExecutionConfigThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("InternalExecutionConfiguration cannot be null.");

        config = new GlobalFilterExecutionConfig(null, externalConfigMock);
    }

    @Test
    public void nullExternalExecutionConfigThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ExternalExecutionConfiguration cannot be null.");

        config = new GlobalFilterExecutionConfig(internalConfigMock, null);
    }

    @Test
    public void nullSearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getConfig(null);
    }

    @Test
    public void emptySearchableFieldThrowsException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Field name cannot be null or empty");

        config.getConfig("");
    }

    @Test
    public void searchableFieldNameKnownToInternalConfigReturnsPopulatedOptional() throws Exception {
        String internalFieldName = "field";

        Optional<FieldExecutionConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(internalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getField(internalFieldName)).thenReturn(expectedFieldConfigOpt);
        when(externalConfigMock.getField(internalFieldName)).thenReturn(Optional.empty());
        when(internalConfigMock.getConfig(internalFieldName)).thenReturn(expectedFieldConfigOpt);

        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig(internalFieldName);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    public void searchableFieldNameKnownToExternalConfigReturnsPopulatedOptional() throws Exception {
        String externalFieldName = "field";

        when(internalConfigMock.getConfig(externalFieldName)).thenReturn(Optional.empty());

        Optional<FieldExecutionConfig> expectedFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(externalFieldName, ExecutionType.SIMPLE)
        );

        when(internalConfigMock.getField(externalFieldName)).thenReturn(Optional.empty());
        when(externalConfigMock.getField(externalFieldName)).thenReturn(expectedFieldConfigOpt);
        when(externalConfigMock.getConfig(externalFieldName)).thenReturn(expectedFieldConfigOpt);

        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig(externalFieldName);

        assertThat(fieldConfigOpt, is(expectedFieldConfigOpt));
    }

    @Test
    public void unknownSearchableFieldNameReturnsEmptyOptional() throws Exception {
        String unknownFieldName = "unknown";

        when(internalConfigMock.getConfig(unknownFieldName)).thenReturn(Optional.empty());
        when(externalConfigMock.getConfig(unknownFieldName)).thenReturn(Optional.empty());

        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig(unknownFieldName);

        assertThat(fieldConfigOpt, is(Optional.empty()));
    }

    @Test
    public void searchableFieldExistsInInternalAndExternalExecutionConfigSoInternalTakesPrecedence() throws Exception {
        String searchableField = "field";

        Optional<FieldExecutionConfig> internalFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(searchableField, ExecutionType.SIMPLE)
        );

        Optional<FieldExecutionConfig> externalFieldConfigOpt = Optional.of(
                FilterUtil.createExecutionConfig(searchableField, ExecutionType.JOIN)
        );


        when(internalConfigMock.getField(searchableField)).thenReturn(internalFieldConfigOpt);
        when(externalConfigMock.getField(searchableField)).thenReturn(externalFieldConfigOpt);

        Optional<FieldExecutionConfig> fieldConfigOpt = config.getField(searchableField);

        FieldExecutionConfig retrievedField = fieldConfigOpt.get();

        assertThat(retrievedField.getName(), is(searchableField));
        assertThat(retrievedField.getExecution(), is(ExecutionType.SIMPLE));
    }
}
