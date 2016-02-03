package uk.ac.ebi.quickgo.common.search.solr;

import uk.ac.ebi.quickgo.common.search.QueryResultConverter;
import uk.ac.ebi.quickgo.common.search.query.Page;
import uk.ac.ebi.quickgo.common.search.query.QueryRequest;
import uk.ac.ebi.quickgo.common.search.results.Facet;
import uk.ac.ebi.quickgo.common.search.results.FieldFacet;
import uk.ac.ebi.quickgo.common.search.results.PageInfo;
import uk.ac.ebi.quickgo.common.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 * Abstract class that deals with the conversion of the non type specific aspects of a {@link QueryResult}.
 *
 * @author Ricardo Antunes
 */
public abstract class AbstractSolrQueryResultConverter<T> implements QueryResultConverter<T, QueryResponse> {
    @Override public QueryResult<T> convert(QueryResponse toConvert, QueryRequest request) {
        Preconditions.checkArgument(toConvert != null, "Query response cannot be null");
        Preconditions.checkArgument(request != null, "Query request cannot be null");

        SolrDocumentList solrResults = toConvert.getResults();
        Page page = request.getPage();
        List<FacetField> facetFieldResults = toConvert.getFacetFields();

        long totalNumberOfResults = 0;

        List<T> results;

        if(solrResults != null) {
            totalNumberOfResults = solrResults.getNumFound();
            results = convertResults(solrResults);
        } else {
            results = Collections.emptyList();
        }

        PageInfo pageInfo = null;

        if (page != null) {
            pageInfo = convertPage(page, totalNumberOfResults);
        }

        Facet facet = null;

        if (facetFieldResults != null && !facetFieldResults.isEmpty()) {
            facet = convertFacet(facetFieldResults);
        }

        return new QueryResult<>(totalNumberOfResults, results, pageInfo, facet);
    }

    /**
     * Creates a {@link Facet} object containing all facet related information.
     *
     * @param fields Solr facet fields to convert
     * @return a domain facet object
     */
    private Facet convertFacet(List<FacetField> fields) {
        Facet facet = new Facet();

        fields.stream()
                .map(this::convertFacetField)
                .forEach(facet::addFacetField);

        return facet;
    }

    /**
     * Converts a Solr {@link FacetField} into a domain {@link FieldFacet} object
     *
     * @param solrField field containing facet information
     * @return a domain facet field
     */
    private FieldFacet convertFacetField(FacetField solrField) {
        String name = solrField.getName();

        final FieldFacet domainFieldFacet = new FieldFacet(name);

        solrField.getValues().stream()
                .forEach(count -> domainFieldFacet.addCategory(count.getName(), count.getCount()));

        return domainFieldFacet;
    }

    private PageInfo convertPage(Page page, long totalNumberOfResults) {
        int resultsPerPage = page.getPageSize();
        int totalPages = (int) Math.ceil((double) totalNumberOfResults / (double) resultsPerPage);

        int currentPage = (totalPages == 0 ? 0 : page.getPageNumber());

        return new PageInfo(totalPages, currentPage, resultsPerPage);
    }

    protected abstract List<T> convertResults(SolrDocumentList results);
}