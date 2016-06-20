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
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.JOIN;
import static uk.ac.ebi.quickgo.rest.search.request.config.RequestConfig.ExecutionType.SIMPLE;

/**
 * Tests the behaviour of the {@link ExternalFilterConfigRetrieval} class.
 */
public class ExternalFilterConfigRetrievalTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExternalFilterConfigRetrieval config;


    @Before
    public void setUp() throws Exception {
        config = new ExternalFilterConfigRetrieval();
    }

    @Test
    public void newExternalFilterConfigHasNoFields() {
        assertThat(config.getRequestConfigs(), hasSize(0));
    }

    @Test
    public void newExternalFilterConfigReturnsEmptyOptionalWhenCallingGetField() {
        Optional<RequestConfig> fieldConfigOpt = config.getBySignature(asSet("field"));

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsToNullReturnsAnEmptyListWhenCallingGetFields() {
        config.setRequestConfigs(null);

        assertThat(config.getRequestConfigs(), hasSize(0));
    }

    @Test
    public void settingFieldsToNullReturnsEmptyOptionalWhenCallingGetField(){
        config.setRequestConfigs(null);

        Optional<RequestConfig> fieldConfigOpt = config.getBySignature(asSet("field"));

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsWithOneFieldExecutionConfigReturnsAListWithThatField() {
        String name = "name";
        RequestConfig.ExecutionType type = SIMPLE;

        RequestConfig field = createExecutionConfig(name, type);

        config.setRequestConfigs(Collections.singletonList(field));

        assertThat(config.getRequestConfigs(), contains(field));
    }

    @Test
    public void gettingFieldWithRecognizedFieldNameReturnsAnPopulatedOptional() {
        String name = "aspect";
        RequestConfig.ExecutionType type = JOIN;

        RequestConfig field = createExecutionConfig(name, type);

        config.setRequestConfigs(Collections.singletonList(field));

        assertThat(config.getBySignature(asSet(name)), is(Optional.of(field)));
    }

    @Test
    public void gettingFieldWithUnrecognizedFieldNameReturnsAnEmptyOptional() {
        String name = "aspect";
        RequestConfig.ExecutionType type = JOIN;

        RequestConfig field = createExecutionConfig(name, type);

        config.setRequestConfigs(Collections.singletonList(field));

        assertThat(config.getBySignature(asSet("fake")), is(Optional.empty()));
    }

    @Test
    public void getsFirstOfTwoFieldsWithTheSameName() {
        String name = "field";
        RequestConfig.ExecutionType type1 = JOIN;
        RequestConfig.ExecutionType type2 = SIMPLE;

        RequestConfig field1 = createExecutionConfig(name, type1);
        RequestConfig field2 = createExecutionConfig(name, type2);

        config.setRequestConfigs(Arrays.asList(field1, field2));

        Optional<RequestConfig> retrievedFieldOpt = config.getBySignature(asSet(name));

        RequestConfig retrievedField = retrievedFieldOpt.get();

        assertThat(retrievedField.getSignature(), is(asSet(name)));
        assertThat(retrievedField.getExecution(), is(JOIN));
    }
}