package uk.ac.ebi.quickgo.client.model.presets;

import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.client.model.presets.PresetItem.Property.DESCRIPTION;

/**
 * Created 04/10/16
 * @author Edd
 */
class PresetItemTest {
    private static final String VALID_VALUE = "value value";
    private static final String EMPTY_VALUE = "";

    private PresetItem.Builder validPresetBuilder;

    @BeforeEach
    void setUp() {
        validPresetBuilder = PresetItem.createWithName(VALID_VALUE);
    }

    // name
    @Test
    void canCreateWithValidName() {
        correctlyBuildsValidPreset(
                PresetItem::createWithName,
                VALID_VALUE,
                extractProperty(PresetItem.Property.NAME));
    }

    @Test
    void nullNameCausesException() {
        exceptionIsThrownFor(PresetItem::createWithName, null, IllegalArgumentException.class);
    }

    @Test
    void emptyNameCausesException() {
        exceptionIsThrownFor(PresetItem::createWithName, EMPTY_VALUE, IllegalArgumentException.class);
    }

    // relevancy
    @Test
    void canCreateWithValidRelevancy() {
        correctlyBuildsValidPreset(
                validPresetBuilder::withRelevancy,
                1,
                PresetItem::getRelevancy);
    }

    @Test
    void cannotCreateWithNullRelevancy() {
        exceptionIsThrownFor(validPresetBuilder::withRelevancy, null, IllegalArgumentException.class);
    }

    @Test
    void cannotCreateWithNegativeRelevancy() {
        exceptionIsThrownFor(validPresetBuilder::withRelevancy, -1, IllegalArgumentException.class);
    }

    // description
    @Test
    void canCreateWithValidDescription() {
        correctlyBuildsValidPreset(
                addProperty(DESCRIPTION),
                VALID_VALUE,
                extractProperty(DESCRIPTION));
    }

    @Test
    void cannotCreateWithNullDescription() {
        exceptionIsThrownFor(addProperty(DESCRIPTION), null, IllegalArgumentException.class);
    }

    @Test
    void cannotCreateWithEmptyDescription() {
        exceptionIsThrownFor(addProperty(DESCRIPTION), EMPTY_VALUE, IllegalArgumentException.class);
    }

    // id
    @Test
    void canCreateWithValidId() {
        correctlyBuildsValidPreset(
                addProperty(PresetItem.Property.ID),
                VALID_VALUE,
                extractProperty(PresetItem.Property.ID));
    }

    @Test
    void cannotCreateWithNullId() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.ID), null, IllegalArgumentException.class);
    }

    @Test
    void cannotCreateWithEmptyId() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.ID), EMPTY_VALUE, IllegalArgumentException.class);
    }

    // url
    @Test
    void canCreateWithValidUrl() {
        correctlyBuildsValidPreset(
                addProperty(PresetItem.Property.URL),
                VALID_VALUE,
                extractProperty(PresetItem.Property.URL));
    }

    @Test
    void cannotCreateWithNullUrl() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.URL), null, IllegalArgumentException.class);
    }

    @Test
    void cannotCreateWithEmptyUrl() {
        exceptionIsThrownFor(addProperty(PresetItem.Property.URL), EMPTY_VALUE, IllegalArgumentException.class);
    }

    // associations
    @Test
    void canCreateWithValidAssociations() {
        correctlyBuildsValidPreset(
                validPresetBuilder::withAssociations,
                asList(PresetItem.createWithName("a").build(), PresetItem.createWithName("b").build()),
                PresetItem::getAssociations);
    }

    @Test
    void cannotCreateWithNullAssociations() {
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
        assertThrows(exception, () -> builderFunction.apply(value));
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