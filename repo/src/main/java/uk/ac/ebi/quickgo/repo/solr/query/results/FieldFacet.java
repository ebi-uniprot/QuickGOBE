package uk.ac.ebi.quickgo.repo.solr.query.results;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains the facet response of a particular field within the context of the {@link QueryResult}.
 *
 * A field facet subdivides the result set into categories, where each category contains the number of result rows
 * that match the facet category.
 *
 * A FieldFacet will always be associated to a {@link Facet}
 */
public class FieldFacet {
    private final String field;
    private final List<Category> categories;

    public FieldFacet(String field) {
        Preconditions.checkArgument(field != null && field.length() > 0, "Facet field can not be null");

        this.field = field;
        categories = new ArrayList<>();
    }

    /**
     * The field the facet is based upon
     *
     * @return the facet field
     */
    public String getField() {
        return field;
    }

    /**
     * The breakdown of the field into its distinct values and the number of results per value
     *
     * @return a Map
     */
    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void addCategory(String categoryName, long count) {
        Preconditions.checkArgument(categoryName != null && categoryName.length() > 0,
                "Can not add null or empty category to facet");
        Preconditions.checkArgument(count >= 0, "Attempted to add negative count for category: " + categoryName);
		Category category = new Category(categoryName, count);
        categories.add(category);
    }
}
