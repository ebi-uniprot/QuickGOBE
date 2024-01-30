package uk.ac.ebi.quickgo.rest.search.results;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the behaviour of the {@link uk.ac.ebi.quickgo.rest.search.results.FieldFacet} implementation.
 */
class FieldFacetTest {
    @Test
    void nullFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FieldFacet(null));
    }

    @Test
    void emptyFieldThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FieldFacet(""));
    }

    @Test
    void retrievesFacetField() {
        String field = "field1";

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);

        assertThat(fieldFacet.getField(), is(equalTo(field)));
    }

    @Test
    void returnsEmptyMapWhenNothingAddedToCategories() {
        String field = "field1";

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);

        assertThat(fieldFacet.getCategories().isEmpty(), is(true));
    }

    @Test
    void addCategoryAndCountToFacet() {
        String field = "field1";
        String categoryName = "cat1";
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

		Category category = new Category(categoryName, count);

        assertThat(fieldFacet.getCategories(), contains(category));
    }

    @Test
    void deletingElementInCategoriesThrowsException() {
        String field = "field1";
        String categoryName = "cat1";
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
          fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

        Category category = new Category(categoryName, count);
        assertThrows(UnsupportedOperationException.class, () -> fieldFacet.getCategories().remove(category));
    }

    @Test
    void addNullCategoryToFacetThrowsException() {
        String field = "field1";
        String categoryName = null;
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
          fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        assertThrows(IllegalArgumentException.class, () -> {
            fieldFacet.addCategory(categoryName, count);

            Category category = new Category(categoryName, count);

            assertThat(fieldFacet.getCategories(), contains(category));
        });
    }

    @Test
    void addEmptyCategoryToFacetThrowsException() {
        String field = "field1";
        String categoryName = "";
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
          fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        assertThrows(IllegalArgumentException.class, () -> {
            fieldFacet.addCategory(categoryName, count);

            Category category = new Category(categoryName, count);

            assertThat(fieldFacet.getCategories(), contains(category));
        });
    }

    @Test
    void addNegativeValueToFacetThrowsException() {
        String field = "field1";
        String categoryName = "cat1";
        long count = -1;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet fieldFacet = new FieldFacet(field);
        assertThrows(IllegalArgumentException.class, () -> {
            fieldFacet.addCategory(categoryName, count);

            Category category = new Category(categoryName, count);

            assertThat(fieldFacet.getCategories(), contains(category));
        });
    }
}
