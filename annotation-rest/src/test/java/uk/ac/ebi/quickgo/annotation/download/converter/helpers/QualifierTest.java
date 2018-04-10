package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Does the Qualifier class do everything expected of it?
 *
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 10:47
 * Created with IntelliJ IDEA.
 */
public class QualifierTest {

    @Test
    public void enables() {
        assertThat(Qualifier.gafQualifierAsString("enables"), is(""));
    }

    @Test
    public void notPart_of() {
        assertThat(Qualifier.gafQualifierAsString("not|part_of"), is("NOT"));
    }

    @Test
    public void contributesTo() {
        assertThat(Qualifier.gafQualifierAsString("contributes_to"), is("contributes_to"));
    }

    @Test
    public void notContributesTo() {
        assertThat(Qualifier.gafQualifierAsString("not|contributes_to"), is("NOT|contributes_to"));
    }

    @Test
    public void colocalizesWith() {
        assertThat(Qualifier.gafQualifierAsString("colocalizes_with"), is("colocalizes_with"));
    }

    @Test
    public void notcolocalizesWith() {
        assertThat(Qualifier.gafQualifierAsString("not|colocalizes_with"), is("NOT|colocalizes_with"));
    }

    @Test
    public void isNull() {
        assertThat(Qualifier.gafQualifierAsString(null), is(""));
    }

    @Test
    public void isEmpty() {
        assertThat(Qualifier.gafQualifierAsString(""), is(""));
    }

}
