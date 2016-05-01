package uk.ac.ebi.quickgo.annotation.service.search;

import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.rest.search.SearchQueryRequestBuilder;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;

import java.util.function.Consumer;

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


    public static class Builder implements SearchQueryRequestBuilder, Consumer<AnnotationFilter.PrototypeFilter> {
        private final Iterable<String> returnedFields;
        private AnnotationFilter annotationFilter;
        private QueryRequest.Builder builder;

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
            builder = new QueryRequest.Builder(QuickGOQuery.createEmptyQuery());
            builder.setPageParameters(Integer.valueOf(annotationFilter.getPage()), Integer.valueOf(annotationFilter
                    .getLimit()));

            //Call back to filters accumulator, consume the PrototypeFilters found there and turn in to QuickGOQueries
            annotationFilter.requestConsumptionOfPrototypeFilters(this);

            returnedFields
                    .forEach(builder::addProjectedField);

            return builder.build();
        }


        /**
         * Add a new QuickGOQuery to the builder based on consuming the PrototypeFilter passed to this method.
         * @param builder
         * @param pr
         */
        private void addFilterToBuilder(QueryRequest.Builder builder, AnnotationFilter.PrototypeFilter pr) {
            if(pr.getArgs()==null){
                return;
            }
            QuickGOQuery quickGOQuery = pr.getArgs()
                    .parallelStream()
                    .reduce(null, (q, arg) -> QuickGOQuery.createQuery( pr.getSolrName(), arg), (q1,q2) -> q1.or(q2)) ;

            if(quickGOQuery!=null) {
                builder.addQueryFilter(quickGOQuery);
            }
        }

        /**
         * Consume the prototype filters passed to this method.
         * @param prototypeFilter
         */
        @Override public void accept(AnnotationFilter.PrototypeFilter prototypeFilter) {
            addFilterToBuilder(this.builder, prototypeFilter);
        }

        /**
         * Not Required
         * @param after
         * @return
         */
        @Override public Consumer<AnnotationFilter.PrototypeFilter> andThen(
                Consumer<? super AnnotationFilter.PrototypeFilter> after) {
            return null;
        }
    }
}
