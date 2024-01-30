package uk.ac.ebi.quickgo.rest.search.request.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.asSet;
import static uk.ac.ebi.quickgo.rest.search.request.FilterUtil.createExecutionConfig;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType.JOIN;
import static uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig.ExecutionType.SIMPLE;

/**
 * Tests the behaviour of the {@link ExternalFilterConfigRetrieval} class.
 */
class ExternalFilterConfigRetrievalTest {

    private ExternalFilterConfigRetrieval config;


    @BeforeEach
    void setUp()  {
        config = new ExternalFilterConfigRetrieval();
    }

    @Test
    void newExternalFilterConfigHasNoFields() {
        assertThat(config.getFilterConfigs(), hasSize(0));
    }

    @Test
    void newExternalFilterConfigReturnsEmptyOptionalWhenCallingGetField() {
        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(asSet("field"));

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    void settingFieldsToNullReturnsAnEmptyListWhenCallingGetFields() {
        config.setFilterConfigs(null);

        assertThat(config.getFilterConfigs(), hasSize(0));
    }

    @Test
    void settingFieldsToNullReturnsEmptyOptionalWhenCallingGetField(){
        config.setFilterConfigs(null);

        Optional<FilterConfig> fieldConfigOpt = config.getBySignature(asSet("field"));

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    void settingFieldsWithOneFieldExecutionConfigReturnsAListWithThatField() {
        String name = "name";
        FilterConfig.ExecutionType type = SIMPLE;

        FilterConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getFilterConfigs(), contains(field));
    }

    @Test
    void gettingFieldWithRecognizedFieldNameReturnsAnPopulatedOptional() {
        String name = "aspect";
        FilterConfig.ExecutionType type = JOIN;

        FilterConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getBySignature(asSet(name)), is(Optional.of(field)));
    }

    @Test
    void gettingFieldWithUnrecognizedFieldNameReturnsAnEmptyOptional() {
        String name = "aspect";
        FilterConfig.ExecutionType type = JOIN;

        FilterConfig field = createExecutionConfig(name, type);

        config.setFilterConfigs(Collections.singletonList(field));

        assertThat(config.getBySignature(asSet("fake")), is(Optional.empty()));
    }

    @Test
    void getsFirstOfTwoFieldsWithTheSameName() {
        String name = "field";
        FilterConfig.ExecutionType type1 = JOIN;
        FilterConfig.ExecutionType type2 = SIMPLE;

        FilterConfig field1 = createExecutionConfig(name, type1);
        FilterConfig field2 = createExecutionConfig(name, type2);

        config.setFilterConfigs(Arrays.asList(field1, field2));

        Optional<FilterConfig> retrievedFieldOpt = config.getBySignature(asSet(name));

        FilterConfig retrievedField = retrievedFieldOpt.get();

        assertThat(retrievedField.getSignature(), is(asSet(name)));
        assertThat(retrievedField.getExecution(), is(JOIN));
    }
}