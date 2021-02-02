package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Does the Qualifier class do everything expected of it?
 *
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 10:47
 * Created with IntelliJ IDEA.
 */
class QualifierTest {

    @Test
    void enables() {
        assertThat(Qualifier.gafQualifierAsString("enables"), is("enables"));
    }

    @Test
    void notPart_of() {
        assertThat(Qualifier.gafQualifierAsString("not|part_of"), is("NOT|part_of"));
    }

    @Test
    void notPart_ofUppercase() {
        assertThat(Qualifier.gafQualifierAsString("NOT|part_of"), is("NOT|part_of"));
    }

    @Test
    void contributesTo() {
        assertThat(Qualifier.gafQualifierAsString("contributes_to"), is("contributes_to"));
    }

    @Test
    void notContributesTo() {
        assertThat(Qualifier.gafQualifierAsString("not|contributes_to"), is("NOT|contributes_to"));
    }

    @Test
    void colocalizesWith() {
        assertThat(Qualifier.gafQualifierAsString("colocalizes_with"), is("colocalizes_with"));
    }

    @Test
    void notcolocalizesWith() {
        assertThat(Qualifier.gafQualifierAsString("not|colocalizes_with"), is("NOT|colocalizes_with"));
    }

    @Test
    void isNull() {
        assertThat(Qualifier.gafQualifierAsString(null), is(""));
    }

    @Test
    void isEmpty() {
        assertThat(Qualifier.gafQualifierAsString(""), is(""));
    }

    @Test
    void actsUpstreamOf() {
        assertThat(Qualifier.gafQualifierAsString("acts_upstream_of"), is("acts_upstream_of"));
    }

    @Test
    void actsUpstreamOfPositiveEffect() {
        assertThat(Qualifier.gafQualifierAsString("acts_upstream_of_positive_effect"), is("acts_upstream_of_positive_effect"));
    }

    @Test
    void actsUpstreamOfNegativeEffect() {
        assertThat(Qualifier.gafQualifierAsString("acts_upstream_of_negative_effect"), is("acts_upstream_of_negative_effect"));
    }

    @Test
    void actsUpstreamOfOrWithin() {
        assertThat(Qualifier.gafQualifierAsString("acts_upstream_of_or_within"), is("acts_upstream_of_or_within"));
    }

    @Test
    void actsUpstreamOfOrWithinPositiveEffect() {
        assertThat(Qualifier.gafQualifierAsString("acts_upstream_of_or_within_positive_effect"), is("acts_upstream_of_or_within_positive_effect"));
    }

    @Test
    void actsUpstreamOfOrWithinNegativeEffect() {
        assertThat(Qualifier.gafQualifierAsString("acts_upstream_of_or_within_negative_effect"), is("acts_upstream_of_or_within_negative_effect"));
    }

    @Test
    void is_active_in() {
        assertThat(Qualifier.gafQualifierAsString("is_active_in"), is("is_active_in"));
    }

    @Test
    void located_in() {
        assertThat(Qualifier.gafQualifierAsString("located_in"), is("located_in"));
    }

    @Test
    void notLocated_in() {
        assertThat(Qualifier.gafQualifierAsString("not|located_in"), is("NOT|located_in"));
    }
}
