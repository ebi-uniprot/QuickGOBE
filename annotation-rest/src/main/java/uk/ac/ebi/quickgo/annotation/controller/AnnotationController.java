package uk.ac.ebi.quickgo.annotation.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.service.AnnotationService;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.SearchDispatcher;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Objects;

/**
 * Provides RESTful endpoints for retrieving Annotations
 *
 * @author Tony Wardell
 *         Date: 21/04/2016
 *         Time: 11:26
 *         Created with IntelliJ IDEA.
 */
@RestController
@RequestMapping(value = "/QuickGO/services/annotation")
public class AnnotationController {
	private static final int MAX_PAGE_RESULTS = 100;
	private static final String DEFAULT_ENTRIES_PER_PAGE = "25";
	private static final String DEFAULT_PAGE_NUMBER = "1";
	private final AnnotationService annotationService;
	private final ControllerValidationHelperImpl controllerValidationHelper;

	@Autowired
	public AnnotationController(AnnotationService annoService) {
		Objects.requireNonNull(annoService, "The AnnotationService instance passed to the constructor of " +
				"AnnotationController should not be null.");
		this.annotationService = annoService;
		this.controllerValidationHelper = new ControllerValidationHelperImpl(MAX_PAGE_RESULTS);
	}

	/**
	 * Search for an Annotations based on their attributes
	 *
	 * @param query the query to search against
	 * @param limit the amount of queries to return
	 * @return a {@link QueryResult} instance containing the results of the search
	 */
	@RequestMapping(value = "/*", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<Annotation>> annotationSearch(
			@RequestParam(value = "assignedBy") String assignedBy,
			@RequestParam(value = "limit", defaultValue = DEFAULT_ENTRIES_PER_PAGE) int limit,
			@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page) {

		controllerValidationHelper.validateRequestedResults(limit);

		QueryRequest request = buildRequest(
				query,
				limit,
				page,
				ontologyQueryConverter);

		return SearchDispatcher.search(request, ontologySearchService);
	}
}
