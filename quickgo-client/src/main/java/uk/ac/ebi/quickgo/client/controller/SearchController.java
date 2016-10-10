package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.ontology.OntologyRequest;
import uk.ac.ebi.quickgo.client.model.ontology.OntologyTerm;
import uk.ac.ebi.quickgo.client.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ParameterBindingException;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.SearchableField;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

/**
 * Search controller responsible for providing consistent search functionality over selected services.
 *
 * Created 13/01/16
 * @author Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/internal/search")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchService<OntologyTerm> ontologySearchService;
    private final DefaultSearchQueryTemplate requestTemplate;
    private final FilterConverterFactory converterFactory;

    @Autowired
    public SearchController(
            SearchService<OntologyTerm> ontologySearchService,
            SearchableField ontologySearchableField,
            SearchServiceConfig.OntologyCompositeRetrievalConfig ontologyRetrievalConfig,
            FilterConverterFactory converterFactory) {

        Preconditions.checkArgument(ontologySearchService != null, "Ontology search service cannot be null");
        Preconditions.checkArgument(ontologySearchableField != null, "Ontology searchable field cannot be null");
        Preconditions.checkArgument(ontologyRetrievalConfig != null, "Ontology retrieval configuration cannot be null");
        Preconditions.checkArgument(converterFactory != null, "Ontology converter factory cannot be null");

        this.ontologySearchService = ontologySearchService;
        this.converterFactory = converterFactory;

        this.requestTemplate = new DefaultSearchQueryTemplate(
                ontologySearchableField,
                ontologyRetrievalConfig.getSearchReturnedFields(),
                ontologyRetrievalConfig.repo2DomainFieldMap().keySet(),
                ontologyRetrievalConfig.getHighlightStartDelim(),
                ontologyRetrievalConfig.getHighlightEndDelim());
    }

    /**
     * Perform a custom client search
     *
     * @param request an object that wraps all possible configurations for this endpoint
     * @return the search results
     */
    @ApiOperation(value="Searches a user defined query, e.g., query=apopto",
            notes = "Response fields include: id and name (and aspect for GO terms)")
    @RequestMapping(value = "/ontology", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<OntologyTerm>> ontologySearch(@Valid @ModelAttribute OntologyRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }

        DefaultSearchQueryTemplate.Builder requestBuilder = requestTemplate.newBuilder()
                .setQuery(request.createQuery())
                .addFacets(request.getFacet() == null ? null : Arrays.asList(request.getFacet()))
                .addFilters(request.createFilterRequests().stream()
                        .map(converterFactory::convert)
                        .filter(Objects::nonNull)
                        .map(ConvertedFilter::getConvertedValue)
                        .collect(Collectors.toList()))
                .useHighlighting(request.isHighlighting())
                .setPage(request.getPage())
                .setPageSize(request.getLimit());

        return search(requestBuilder.build(), ontologySearchService);
    }
}