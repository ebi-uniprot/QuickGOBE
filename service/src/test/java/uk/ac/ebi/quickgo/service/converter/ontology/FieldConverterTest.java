package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.service.converter.FieldConverter;
import uk.ac.ebi.quickgo.service.model.FieldType;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;


/**
 * Created 01/12/15
 * @author Edd
 */
public class FieldConverterTest {
    private FakeFieldConverter converter;

    static class MockFieldType implements FieldType {
    }

    static class FakeFieldConverter implements FieldConverter<MockFieldType> {

        @Override public Optional<MockFieldType> apply(String s) {
            return null;
        }
    }

    @Before
    public void setup() {
        this.converter = new FakeFieldConverter();
    }

    @Test
    public void helperReturnsNullforEmptyString() {
        assertThat(converter.cleanFieldValue(""), is(nullValue()));
    }

    @Test
    public void helperReturnsStringForNonEmptyString() {
        assertThat(converter.cleanFieldValue("hello"), is(equalTo("hello")));
    }

    @Test
    public void helperReturnsNullForNullString() {
        assertThat(converter.cleanFieldValue(null), is(nullValue()));
    }

}