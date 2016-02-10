package uk.ac.ebi.quickgo.rest.search.results;

import uk.ac.ebi.quickgo.rest.search.results.Category;
import uk.ac.ebi.quickgo.rest.search.results.FieldFacet;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests the behaviour of the {@link uk.ac.ebi.quickgo.rest.search.results.FieldFacet} implementation.
 */
public class FieldFacetTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullFieldThrowsException() throws Exception {
        new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFieldThrowsException() throws Exception {
        new uk.ac.ebi.quickgo.rest.search.results.FieldFacet("");
    }

    @Test
    public void retrievesFacetField() throws Exception {
        String field = "field1";

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);

        assertThat(fieldFacet.getField(), is(equalTo(field)));
    }

    @Test
    public void returnsEmptyMapWhenNothingAddedToCategories() throws Exception {
        String field = "field1";

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);

        assertThat(fieldFacet.getCategories().isEmpty(), is(true));
    }

    @Test
    public void addCategoryAndCountToFacet() throws Exception {
        String field = "field1";
        String categoryName = "cat1";
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

		Category category = new Category(categoryName, count);

        assertThat(fieldFacet.getCategories(), contains(category));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void deletingElementInCategoriesThrowsException() throws Exception {
        String field = "field1";
        String categoryName = "cat1";
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

		Category category = new Category(categoryName, count);

        fieldFacet.getCategories().remove(category);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullCategoryToFacetThrowsException() throws Exception {
        String field = "field1";
        String categoryName = null;
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

		Category category = new Category(categoryName, count);

        assertThat(fieldFacet.getCategories(), contains(category));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmptyCategoryToFacetThrowsException() throws Exception {
        String field = "field1";
        String categoryName = "";
        long count = 3;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet
                fieldFacet = new uk.ac.ebi.quickgo.rest.search.results.FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

		Category category = new Category(categoryName, count);

		assertThat(fieldFacet.getCategories(), contains(category));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNegativeValueToFacetThrowsException() throws Exception {
        String field = "field1";
        String categoryName = "cat1";
        long count = -1;

        uk.ac.ebi.quickgo.rest.search.results.FieldFacet fieldFacet = new FieldFacet(field);
        fieldFacet.addCategory(categoryName, count);

		Category category = new Category(categoryName, count);

		assertThat(fieldFacet.getCategories(), contains(category));
    }
}
