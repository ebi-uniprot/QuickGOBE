package uk.ac.ebi.quickgo.rest.search.filter;

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
import static uk.ac.ebi.quickgo.rest.search.filter.FieldExecutionConfig.ExecutionType.*;
import static uk.ac.ebi.quickgo.rest.search.filter.FilterUtil.createExecutionConfig;

/**
 * Tests the behaviour of the {@link ExternalFilterExecutionConfig} class.
 */
public class ExternalFilterExecutionConfigTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExternalFilterExecutionConfig config;


    @Before
    public void setUp() throws Exception {
        config = new ExternalFilterExecutionConfig();
    }

    @Test
    public void newExternalFilterConfigHasNoFields() {
        assertThat(config.getFields(), hasSize(0));
    }

    @Test
    public void newExternalFilterConfigReturnsEmptyOptionalWhenCallingGetField() {
        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig("field");

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsToNullReturnsAnEmptyListWhenCallingGetFields() {
        config.setFields(null);

        assertThat(config.getFields(), hasSize(0));
    }

    @Test
    public void settingFieldsToNullReturnsEmptyOptionalWhenCallingGetField(){
        config.setFields(null);

        Optional<FieldExecutionConfig> fieldConfigOpt = config.getConfig("field");

        assertThat(fieldConfigOpt.isPresent(), is(false));
    }

    @Test
    public void settingFieldsWithOneFieldExecutionConfigReturnsAListWithThatField() {
        String name = "name";
        FieldExecutionConfig.ExecutionType type = SIMPLE;

        FieldExecutionConfig field = createExecutionConfig(name, type);

        config.setFields(Collections.singletonList(field));

        assertThat(config.getFields(), contains(field));
    }

    @Test
    public void gettingFieldWithRecognizedFieldNameReturnsAnPopulatedOptional() {
        String name = "aspect";
        FieldExecutionConfig.ExecutionType type = JOIN;

        FieldExecutionConfig field = createExecutionConfig(name, type);

        config.setFields(Collections.singletonList(field));

        assertThat(config.getConfig(name), is(Optional.of(field)));
    }

    @Test
    public void gettingFieldWithUnrecognizedFieldNameReturnsAnEmptyOptional() {
        String name = "aspect";
        FieldExecutionConfig.ExecutionType type = JOIN;

        FieldExecutionConfig field = createExecutionConfig(name, type);

        config.setFields(Collections.singletonList(field));

        assertThat(config.getConfig("fake"), is(Optional.empty()));
    }
}