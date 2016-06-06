package uk.ac.ebi.quickgo.rest.search.request.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.JOIN;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.SIMPLE;

/**
 * Tests the behaviour of the {@link ExternalRequestConfigRetrieval} class.
 */
public class ExternalRequestConfigRetrievalTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExternalRequestConfigRetrieval config;


    @Before
    public void setUp() throws Exception {
        config = new ExternalRequestConfigRetrieval();
    }

    @Test
    public void newExternalFilterConfigHasNoFields() {
        assertThat(config.getFilterConfigs(), hasSize(0));
    }

    @Test
    public void newExternalFilterConfigReturnsEmptyOptionalWhenCallingGetField() {
        Optional<RequestConfig> fieldConfigOpt = config.getSignature("field");

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsToNullReturnsAnEmptyListWhenCallingGetFields() {
        config.setFilterConfigs(null);

        assertThat(config.getFilterConfigs(), hasSize(0));
    }

    @Test
    public void settingFieldsToNullReturnsEmptyOptionalWhenCallingGetField(){
        config.setFilterConfigs(null);

        Optional<RequestConfig> fieldConfigOpt = config.getSignature("field");

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsWithOneFieldExecutionConfigReturnsAListWithThatField() {
        String name = "name";
        RequestConfig.ExecutionType type = SIMPLE;

        RequestConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getFilterConfigs(), contains(field));
    }

    @Test
    public void gettingFieldWithRecognizedFieldNameReturnsAnPopulatedOptional() {
        String name = "aspect";
        RequestConfig.ExecutionType type = JOIN;

        RequestConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getSignature(name), is(Optional.of(field)));
    }

    @Test
    public void gettingFieldWithUnrecognizedFieldNameReturnsAnEmptyOptional() {
        String name = "aspect";
        RequestConfig.ExecutionType type = JOIN;

        RequestConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getSignature("fake"), is(Optional.empty()));
    }

    @Test
    public void getsFirstOfTwoFieldsWithTheSameName() {
        String name = "field";
        RequestConfig.ExecutionType type1 = JOIN;
        RequestConfig.ExecutionType type2 = SIMPLE;

        RequestConfig field1 = createExecutionConfig(name, type1);
        RequestConfig field2 = createExecutionConfig(name, type2);

        config.setFilterConfigs(Arrays.asList(field1, field2));

        Optional<RequestConfig> retrievedFieldOpt = config.getSignature(name);

        RequestConfig retrievedField = retrievedFieldOpt.get();

        assertThat(retrievedField.getSignature(), is(name));
        assertThat(retrievedField.getExecution(), is(JOIN));
    }
}