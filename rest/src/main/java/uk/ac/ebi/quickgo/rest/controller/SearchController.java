package uk.ac.ebi.quickgo.rest.controller;

import uk.ac.ebi.quickgo.service.OntologyService;
import uk.ac.ebi.quickgo.service.model.ontology.ECOTerm;
import uk.ac.ebi.quickgo.service.model.ontology.GOTerm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNull;

/**
 * Search controller responsible for providing consistent search
 * functionality over selected services.
 *
 * Created 13/01/16
 * @author Edd
 */
@RestController
@RequestMapping(value = "/QuickGO/internal/search")
public class SearchController {

    private final OntologyService<GOTerm> goOntologyService;
    private final OntologyService<ECOTerm> ecoOntologyService;

    @Autowired
    public SearchController(OntologyService<GOTerm> goOntologyService,
            OntologyService<ECOTerm> ecoOntologyService) {
        this.goOntologyService = requireNonNull(goOntologyService);
        this.ecoOntologyService = requireNonNull(ecoOntologyService);
    }

//    /**
//     * Method is invoked when a client wants to search for an ontology term via its identifier, or a generic query
//     * search
//     *
//     * @param query the query to search against
//     * @param limit the amount of queries to return
//     */
//    @RequestMapping(value = "/ontology", method = {RequestMethod.GET})
//    public ResponseEntity<QueryResult<GenericTerm>> ontologySearch(
//            @RequestParam(value = "query") String query,
//            @RequestParam(value = "limit", defaultValue = "25") int limit,
//            @RequestParam(value = "page", defaultValue = "1") int page,
//            @RequestParam(value = "filterQuery", required = false) List<String> filterQueries,
//            @RequestParam(value = "facet", required = false) List<String> facets) {
//
//        QueryRequest request = buildRequest(query, limit, page, filterQueries, facets, ontologyQueryConverter);
//        return search(request, termService);
//    }
//
//    private <T> ResponseEntity<QueryResult<T>> search(QueryRequest request, SearchService<T> searchService) {
//        ResponseEntity<QueryResult<T>> response;
//
//        if (request == null) {
//            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } else {
//
//            try {
//                QueryResult<T> queryResult = searchService.findByQuery(request);
//                response = new ResponseEntity<>(queryResult, HttpStatus.OK);
//            } catch (RetrievalException e) {
//                logger.error(createErrorMessage(request), e);
//                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        return response;
//    }
//
//    private boolean isValidQuery(String query) {
//        return query != null && query.trim().length() > 0;
//    }
//
//    private boolean isValidNumRows(int rows) {
//        return rows >= 0;
//    }
//
//    private boolean isValidPage(int page) {
//        return page >= 0;
//    }
//
//    private QueryRequest buildRequest(String query, int limit, int page, List<String> filterQueries,
//            List<String> facets, StringToGoQueryConverter converter) {
//
//        if (!isValidQuery(query) || !isValidNumRows(limit) || !isValidPage(page)) {
//            return null;
//        } else {
//            Builder builder = new Builder(converter.convert(query));
//
//            if (limit > 0 && page > 0) {
//                builder.setPageParameters(page, limit);
//            }
//
//            if (facets != null) {
//                facets.forEach(builder::addFacetField);
//            }
//
//            if (filterQueries != null) {
//                filterQueries.stream()
//                        .map(converter::convert)
//                        .forEach(builder::addQueryFilter);
//            }
//
//            return builder.build();
//        }
//    }
//
//    private String createErrorMessage(QueryRequest request) {
//        return "Unable to process search: [" + request + "]";
//    }
}
