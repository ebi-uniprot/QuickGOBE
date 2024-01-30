package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.FieldType;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created 01/12/15
 * @author Edd
 */
class FieldConverterTest {
    private FakeFieldConverter converter;

    static class MockFieldType implements FieldType {
    }

    static class FakeFieldConverter implements FieldConverter<MockFieldType> {

        @Override public Optional<MockFieldType> apply(String s) {
            return null;
        }
    }

    @BeforeEach
    void setup() {
        this.converter = new FakeFieldConverter();
    }

    @Test
    void helperReturnsNullforEmptyString() {
        assertThat(converter.cleanFieldValue(""), is(nullValue()));
    }

    @Test
    void helperReturnsStringForNonEmptyString() {
        assertThat(converter.cleanFieldValue("hello"), is(equalTo("hello")));
    }

    @Test
    void helperReturnsNullForNullString() {
        assertThat(converter.cleanFieldValue(null), is(nullValue()));
    }

}