package uk.ac.ebi.quickgo.client.model.presets;

import java.util.function.Function;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static uk.ac.ebi.quickgo.client.model.presets.PresetItem.Property.DESCRIPTION;

/**
 * Created 04/10/16
 * @author Edd
 */
public class PresetItemTest {
    private static final String VALID_VALUE = "value value";
    private static final String EMPTY_VALUE = "";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PresetItem.Builder validPresetBuilder;

    @Before
    public void setUp() {
        validPresetBuilder = PresetItem.createWithName(VALID_VALUE);
    }

    // name
    @Test
    public void canCreateWithValidName() {
        correctlyBuildsValidPreset(
                PresetItem::createWithName,
                VALID_VALUE,
                extractProperty(PresetItem.Property.NAME));
    }

    @Test
    public void nullNameCausesException() {
        exceptionIsThrownFor(PresetItem::createWithName, null, IllegalArgumentException.class);
    }

    @Test
    public void emptyNameCausesException() {
        exceptionIsThrownFor(PresetItem::createWithName, EMPTY_VALUE, IllegalArgumentException.class);
    }

    // relevancy
    @Test
    public void canCreateWithValidRelevancy() {
        correctlyBuildsValidPreset(
                validPresetBuilder::withRelevancy,
                1,
                PresetItem::getRelevancy);
    }

    @Test
    public void cannotCreateWithNullRelevancy() {
        exceptionIsThrownFor(validPresetBuilder::withRelevancy, null, IllegalArgumentException.class);
    }

    @Test
    public void cannotCreateWithNegativeRelevancy() {
        exceptionIsThrownFor(validPresetBuilder::withRelevancy, -1, IllegalArgumentException.class);
    }

    // description
    @Test
    public void canCreateWithValidDescription() {
        correctlyBuildsValidPreset(
                addProperty(DESCRIPTION),
                VALID_VALUE,
                extractProperty(DESCRIPTION));
    }

    @Test
    public void cannotCreateWithNullDescription() {
        exceptionIsThrownFor(addProperty(DESCRIPTION), null, IllegalArgumentException.class);
    }

    @Test
    public void cannotCreateWithEmptyDescription() {
        exceptionIsThrownFor(addProperty(DESCRIPTION), EMPTY_VALUE, IllegalArgumentException.class);
    }

    // id
    @Test
    public void canCreateWithValidId() {
        correctlyBuildsValidPreset(
                addProperty(PresetItem.Property.ID),
                VALID_VALUE,
                extractProperty(PresetItem.Property.ID));
    }

    @Test
    public void cannotCreateWithNullId() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.ID), null, IllegalArgumentException.class);
    }

    @Test
    public void cannotCreateWithEmptyId() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.ID), EMPTY_VALUE, IllegalArgumentException.class);
    }

    // url
    @Test
    public void canCreateWithValidUrl() {
        correctlyBuildsValidPreset(
                addProperty(PresetItem.Property.URL),
                VALID_VALUE,
                extractProperty(PresetItem.Property.URL));
    }

    @Test
    public void cannotCreateWithNullUrl() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.URL), null, IllegalArgumentException.class);
    }

    @Test
    public void cannotCreateWithEmptyUrl() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.URL), EMPTY_VALUE, IllegalArgumentException.class);
    }

    // associations
    @Test
    public void canCreateWithValidAssociations() {
        correctlyBuildsValidPreset(
                validPresetBuilder::withAssociations,
                asList(PresetItem.createWithName("a").build(), PresetItem.createWithName("b").build()),
                PresetItem::getAssociations);
    }

    @Test
    public void cannotCreateWithNullAssociations() {
        exceptionIsThrownFor(validPresetBuilder::withAssociations, null, IllegalArgumentException.class);
    }

    private Function<PresetItem, String> extractProperty(PresetItem.Property property) {
        return p -> p.getProperties().get(property.getKey());
    }

    private Function<String, PresetItem.Builder> addProperty(PresetItem.Property property) {
        return value -> validPresetBuilder.withProperty(property.getKey(), value);
    }

    private <T, E extends Exception> void exceptionIsThrownFor(
            Function<T, PresetItem.Builder> builderFunction,
            T value,
            Class<E> exception) {
        thrown.expect(exception);
        builderFunction.apply(value);
    }

    private <T> PresetItem correctlyBuildsValidPreset(
            Function<T, PresetItem.Builder> builderFunction,
            T value,
            Function<PresetItem, T> getterCheck) {
        PresetItem.Builder builder = builderFunction.apply(value);
        PresetItem presetItem = builder.build();
        assertThat(presetItem, is(not(nullValue())));
        assertThat(getterCheck.apply(presetItem), is(value));
        return presetItem;
    }
}