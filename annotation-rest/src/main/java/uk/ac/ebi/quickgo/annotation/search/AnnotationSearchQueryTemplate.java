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

    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int DEFAULT_PAGE_NUMBER = 1;
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
        private int page = DEFAULT_PAGE_NUMBER;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private List<String> filterQueries;

        private Builder(Iterable<String> returnedFields) {
            this.returnedFields = returnedFields;
            this.filterQueries = new ArrayList<>();
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
         * Specify the number of results to be returned per page, i.e., page size.
         *
         * @param pageSize the page size.
         * @return this {@link AnnotationSearchQueryTemplate.Builder} instance
         */
        public AnnotationSearchQueryTemplate.Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        /**
         * Specify which page of results to return.
         *
         * @param page the page of results to return.
         * @return this {@link AnnotationSearchQueryTemplate.Builder} instance
         */
        public AnnotationSearchQueryTemplate.Builder setPage(int page) {
            this.page = page;
            return this;
        }

        @Override public QueryRequest build() {
//            checkFacets(facets);
//            checkFilters(filterQueries);

            //Will not use query, only filterQueries
            //but set a select all query so it doesn't blow up
            QueryRequest.Builder builder = new QueryRequest.Builder(QuickGOQuery.createEmptyQuery());


            //This can be used as a default
//            QueryRequest.Builder builder = new QueryRequest.Builder(QuickGOQuery.createQuery(AnnotationFields
//                    .ID,"*"));


            builder.setPageParameters(page, pageSize);


//            //Gene Product ID
//            if(annotationFilter.getGp()!=null && !annotationFilter.getGp().isEmpty()){
//                QuickGOQuery gpQuery = null;
//                for( String gp:annotationFilter.getGp()){
//
//                    if(gpQuery==null){
//                        gpQuery = QuickGOQuery.createQuery( AnnotationFields.GENE_PRODUCT_ID,gp);
//                    }else{
//                        gpQuery = gpQuery.or(QuickGOQuery.createQuery( AnnotationFields.GENE_PRODUCT_ID,gp));
//                    }
//                }
//                builder.addQueryFilter(gpQuery);
//            }
//
            //Assigned By
            if(annotationFilter.getAssignedby()!=null && !annotationFilter.getAssignedby().isEmpty()){
                QuickGOQuery gpQuery = null;
                for( String assignedBy:annotationFilter.getAssignedby()){

                    if(gpQuery==null){
                        gpQuery = QuickGOQuery.createQuery( AnnotationFields.ASSIGNED_BY,assignedBy);
                    }else{
                        gpQuery = gpQuery.or(QuickGOQuery.createQuery( AnnotationFields.ASSIGNED_BY,assignedBy));
                    }
                }
                builder.addQueryFilter(gpQuery);
            }

            returnedFields
                    .forEach(builder::addProjectedField);

            return builder.build();
        }
    }

}
