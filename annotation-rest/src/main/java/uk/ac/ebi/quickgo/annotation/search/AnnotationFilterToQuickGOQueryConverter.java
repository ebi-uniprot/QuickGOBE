package uk.ac.ebi.quickgo.annotation.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.rest.search.QuickGOConverter;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 10:39
 * Created with IntelliJ IDEA.
 */
public class AnnotationFilterToQuickGOQueryConverter implements QuickGOConverter<AnnotationFilter>  {

    public QuickGOQuery convert(AnnotationFilter queryFilter) {
        Preconditions.checkArgument(queryFilter != null, "Filter cannot be null");

        return convertToQuery(queryFilter);
    }

    private QuickGOQuery convertToQuery(AnnotationFilter annotationFilter) {
//        QuickGOQuery quickGoQuery;
//
//        int fieldSeparatorPos = query.indexOf(FIELD_SEPARATOR);
//
//        String field;
//        String value;
//
//        if (fieldSeparatorPos != SEPARATOR_PRESENT
//                && searchableField.isSearchable(field = query.substring(0, fieldSeparatorPos))) {
//            value = query.substring(fieldSeparatorPos + 1, query.length());
//
//            quickGoQuery = QuickGOQuery.createQuery(field, value);
//        } else {
//            value = query;
//
//            if(defaultSearchField != null) {
//                quickGoQuery = QuickGOQuery.createQuery(defaultSearchField, value);
//            } else {
//                quickGoQuery = QuickGOQuery.createQuery(value);
//            }
//        }


//        //Iterate through each field in the filter
//        List<QuickGOQuery> gpQueryList = new ArrayList<>();
//        annotationFilter.getGp()
//                .stream()
//                .map(e -> QuickGOQuery.createQuery(AnnotationFields.GENE_PRODUCT_ID, e))
//                .collect(toList());
//
//
//
//        QuickGOQuery gpQuery = gpQueryList.stream().map(q -> q.or(q))

        return null;
    }
}
