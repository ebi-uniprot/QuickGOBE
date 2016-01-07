package uk.ac.ebi.quickgo.service.converter.ontology.field;

import uk.ac.ebi.quickgo.service.model.FieldConverter;
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
    private FieldConverterImpl converter;

    static class MockFieldType implements FieldType {
    }

    static class FieldConverterImpl implements FieldConverter<MockFieldType> {

        @Override public Optional<MockFieldType> apply(String s) {
            return null;
        }
    }

    @Before
    public void setup() {
        this.converter = new FieldConverterImpl();
    }

    @Test
    public void helperReturnsNullforEmptyString() {
        assertThat(converter.nullOrString(""), is(nullValue()));
    }

    @Test
    public void helperReturnsStringForNonEmptyString() {
        assertThat(converter.nullOrString("hello"), is(equalTo("hello")));
    }

    @Test
    public void helperReturnsNullForNullString() {
        assertThat(converter.nullOrString(null), is(nullValue()));
    }

}