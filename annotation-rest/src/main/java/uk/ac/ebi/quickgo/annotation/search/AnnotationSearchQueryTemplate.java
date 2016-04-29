package uk.ac.ebi.quickgo.annotation.search;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.rest.search.SearchQueryRequestBuilder;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Records common configuration details required to create a {@link QueryRequest} instance,
 * and provides a {@link SearchQueryRequestBuilder} instance that can be used to build
 * specialised forms of the query.
 * <p>
 * The purpose of this class is to encapsulate and simplify the creation of search queries.
 * <p>
 * More specifically, this class wraps {@link uk.ac.ebi.quickgo.rest.search.query.QueryRequest.Builder}
 * and performs boiler-plate configuration tasks which all callers
 * of a search require. These tasks include, setting up field projections,
 * highlighting fields, validating facets and filters, etc.
 *
 * Based on {@link uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate}
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 10:05
 * Created with IntelliJ IDEA.
 */
public class AnnotationSearchQueryTemplate {
    private final Iterable<String> returnedFields;


    public AnnotationSearchQueryTemplate(
            Iterable<String> returnedFields) {
        this.returnedFields = returnedFields;

    }

    public Builder newBuilder() {
        return new Builder(returnedFields);
    }


    public static class Builder implements SearchQueryRequestBuilder {
        private final Iterable<String> returnedFields;
        private AnnotationFilter annotationFilter;

        private Builder(Iterable<String> returnedFields) {
            this.returnedFields = returnedFields;
        }


        /**
         *
         * @param annotationFilter
         * @return
         */
        public AnnotationSearchQueryTemplate.Builder addAnnotationFilter(AnnotationFilter annotationFilter) {
            this.annotationFilter = annotationFilter;
            return this;
        }

        /**
         * Create QueryRequest which is an aggregation of the complete query to sent to Solr
         * Will not use query, only filterQueries,
         * @return
         */
        @Override public QueryRequest build() {
            QueryRequest.Builder builder = new QueryRequest.Builder(QuickGOQuery.createEmptyQuery());
            builder.setPageParameters(Integer.valueOf(annotationFilter.getPage()), Integer.valueOf(annotationFilter
                    .getLimit()));

            //Add all filters to builder here.. todo others to follow
            addFilterToBuilder(builder, annotationFilter.getAssignedby(),  AnnotationFields.ASSIGNED_BY);
            addFilterToBuilder(builder, annotationFilter.getQualifier(),  AnnotationFields.QUALIFIER);

            returnedFields
                    .forEach(builder::addProjectedField);

            return builder.build();
        }

        private void addFilterToBuilder(QueryRequest.Builder builder, List<String> filterArgs, String solrField) {
            if(filterArgs==null){
                return;
            }
            QuickGOQuery assignedByQuery = filterArgs
                .parallelStream()
                .reduce(null, (q, arg) -> QuickGOQuery.createQuery( solrField, arg), (q1,q2) -> q1.or(q2)) ;

            if(assignedByQuery!=null) {
                builder.addQueryFilter(assignedByQuery);
            }
        }
    }
}
