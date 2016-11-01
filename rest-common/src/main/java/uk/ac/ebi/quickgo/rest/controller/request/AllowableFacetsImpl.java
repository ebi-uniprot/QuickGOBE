package uk.ac.ebi.quickgo.rest.controller.request;

import uk.ac.ebi.quickgo.common.SearchableField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import static uk.ac.ebi.quickgo.rest.controller.request.AllowableFacets.DEFAULT_ERROR_MESSAGE;

/**
 * Implementation of the {@link AllowableFacets} annotation interface.
 * <p> This class checks if the array of facets are all valid.
 *
 * @author Ricardo Antunes
 */
public class AllowableFacetsImpl implements ConstraintValidator<AllowableFacets, String[]> {
    private SearchableField searchableField;

    @Autowired
    public AllowableFacetsImpl(SearchableField searchableField){
        this.searchableField = searchableField;
    }

    @Override public void initialize(AllowableFacets constraintAnnotation) {}

    @Override public boolean isValid(String[] facetValues, ConstraintValidatorContext context) {
        List<String> invalidItems = null;

        if (facetValues != null) {
            invalidItems = new ArrayList<>();

            for (String facet : facetValues) {
                if (!searchableField.isSearchable(facet)) {
                    invalidItems.add(facet);
                }
            }

            if (!invalidItems.isEmpty() &&
                    context.getDefaultConstraintMessageTemplate().equals(DEFAULT_ERROR_MESSAGE)) {
                context.disableDefaultConstraintViolation();

                String invalidItemsText = invalidItems.stream().collect(Collectors.joining(", "));

                context.buildConstraintViolationWithTemplate(
                        String.format(DEFAULT_ERROR_MESSAGE, invalidItemsText)).addConstraintViolation();
            }
        }

        return invalidItems == null || invalidItems.isEmpty();
    }
}
