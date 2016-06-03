package uk.ac.ebi.quickgo.rest.search.filter;

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
import static uk.ac.ebi.quickgo.rest.search.filter.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType.JOIN;
import static uk.ac.ebi.quickgo.rest.search.filter.RequestFilterConfig.ExecutionType.SIMPLE;

/**
 * Tests the behaviour of the {@link ExternalRequestFilterConfigRetrieval} class.
 */
public class ExternalRequestFilterConfigRetrievalTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExternalRequestFilterConfigRetrieval config;


    @Before
    public void setUp() throws Exception {
        config = new ExternalRequestFilterConfigRetrieval();
    }

    @Test
    public void newExternalFilterConfigHasNoFields() {
        assertThat(config.getFilterConfigs(), hasSize(0));
    }

    @Test
    public void newExternalFilterConfigReturnsEmptyOptionalWhenCallingGetField() {
        Optional<RequestFilterConfig> fieldConfigOpt = config.getSignature("field");

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

        Optional<RequestFilterConfig> fieldConfigOpt = config.getSignature("field");

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsWithOneFieldExecutionConfigReturnsAListWithThatField() {
        String name = "name";
        RequestFilterConfig.ExecutionType type = SIMPLE;

        RequestFilterConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getFilterConfigs(), contains(field));
    }

    @Test
    public void gettingFieldWithRecognizedFieldNameReturnsAnPopulatedOptional() {
        String name = "aspect";
        RequestFilterConfig.ExecutionType type = JOIN;

        RequestFilterConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getSignature(name), is(Optional.of(field)));
    }

    @Test
    public void gettingFieldWithUnrecognizedFieldNameReturnsAnEmptyOptional() {
        String name = "aspect";
        RequestFilterConfig.ExecutionType type = JOIN;

        RequestFilterConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getSignature("fake"), is(Optional.empty()));
    }

    @Test
    public void getsFirstOfTwoFieldsWithTheSameName() {
        String name = "field";
        RequestFilterConfig.ExecutionType type1 = JOIN;
        RequestFilterConfig.ExecutionType type2 = SIMPLE;

        RequestFilterConfig field1 = createExecutionConfig(name, type1);
        RequestFilterConfig field2 = createExecutionConfig(name, type2);

        config.setFilterConfigs(Arrays.asList(field1, field2));

        Optional<RequestFilterConfig> retrievedFieldOpt = config.getSignature(name);

        RequestFilterConfig retrievedField = retrievedFieldOpt.get();

        assertThat(retrievedField.getSignature(), is(name));
        assertThat(retrievedField.getExecution(), is(JOIN));
    }
}