package uk.ac.ebi.quickgo.rest.controller.request;

import uk.ac.ebi.quickgo.common.FacetableField;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static uk.ac.ebi.quickgo.rest.controller.request.AllowableFacets.DEFAULT_ERROR_MESSAGE;

/**
 * Implementation of the {@link AllowableFacets} annotation interface.
 * <p> This class checks if the array of facets are all valid.
 *
 * @author Ricardo Antunes
 */
public class AllowableFacetsImpl implements ConstraintValidator<AllowableFacets, String[]> {
    private final FacetableField facetableField;

    public AllowableFacetsImpl(FacetableField facetableField) {
        Preconditions.checkArgument(facetableField != null, "Facetable fields cannot be null");

        this.facetableField = facetableField;
    }

    @Override public void initialize(AllowableFacets constraintAnnotation) {}

    @Override public boolean isValid(String[] facetValues, ConstraintValidatorContext context) {
        List<String> invalidFacets = null;

        if (facetValues != null) {
            invalidFacets = new ArrayList<>();

            for (String facet : facetValues) {
                if (!facetableField.isFacetable(facet)) {
                    invalidFacets.add(facet);
                }
            }

            if (!invalidFacets.isEmpty() &&
                    context.getDefaultConstraintMessageTemplate().equals(DEFAULT_ERROR_MESSAGE)) {
                context.disableDefaultConstraintViolation();

                context.buildConstraintViolationWithTemplate(createErrorMessage(invalidFacets))
                        .addConstraintViolation();
            }
        }

        return invalidFacets == null || invalidFacets.isEmpty();
    }

    private String createErrorMessage(Collection<String> invalidFacets) {
        String invalidItemsText = invalidFacets.stream().collect(Collectors.joining(", "));

        return DEFAULT_ERROR_MESSAGE + ":" + invalidItemsText;
    }
}
