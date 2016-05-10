package uk.ac.ebi.quickgo.annotation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.FilterOnlySearchQueryTemplate;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

/**
 * Provides RESTful endpoints for retrieving Gene Ontology (GO) Annotations to gene products.
 *
 * Gene Ontology: the framework for the model of biology. The GO defines concepts/classes used
 * to describe gene function, and relationships between these concepts.
 *
 * GO annotations: the model of biology. Annotations are statements describing the functions of specific genes,
 * using concepts in the Gene Ontology. The simplest and most common annotation links one gene to one function,
 * e.g. FZD4 + Wnt signaling pathway. Each statement is based on a specified piece of evidence
 *
 * Sets of annotations can be tailored for each user by powerful filtering capabilities
 * Annotations will be downloadable in a variety of formats.
 *
 * taxon=1234,343434
 *
 * gp=A0A000,A0A001
 * gpSet=BHF-UCL,Exosome
 * gpType=protein,rna,complex
 *
 * goTerm=GO:0016021,GO:0016022
 * goTermSet=goslim_chembl, goSlimGeneric .. and others.
 *
 * ..the following are only applicable if goTerm ids or sets have been selected
 * goTermUse=ancestor or goTermUse=slim  or goTermUse=exact
 *
 * goTermRelationship=I or goTermRelationship=IPO or goTermRelationship=IPOR
 *
 * aspect=F,P,C
 *
 * evidence=ECO:0000352,ECO0000269
 *
 * goEvidence=IEA etc
 *
 * ..the following is only applicable if any evidence code has been selected
 * evidenceRelationship=ancestor or evidenceRelationship=exact
 *
 * qualifier=enables,not_enables
 *
 * reference=DOI,GO_REF
 *
 * with=AGI_LocusCode,CGD
 *
 * assignedby=ASPGD,Agbase
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

	Logger logger = LoggerFactory.getLogger(AnnotationController.class);
	//todo @Autowired
	private final ControllerValidationHelper validationHelper = new ControllerValidationHelperImpl(MAX_PAGE_RESULTS);

	private final SearchService<Annotation> annotationSearchService;
	private final FilterOnlySearchQueryTemplate requestTemplate;

	@Autowired
	public AnnotationController(SearchService<Annotation> annotationSearchService,
			SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig) {
		checkNotNull(annotationSearchService, "The SearchService<Annotation> instance passed to the constructor of " +
				"AnnotationController should not be null.");
		checkNotNull(annotationRetrievalConfig, "The SearchServiceConfig.AnnotationCompositeRetrievalConfig" +
				" instance passed to the constructor of AnnotationController should not be null.");
		this.annotationSearchService = annotationSearchService;
		this.requestTemplate = new FilterOnlySearchQueryTemplate(annotationRetrievalConfig.getSearchReturnedFields());
	}

	/**
	 * Search for an Annotations based on their attributes
	 * @return a {@link QueryResult} instance containing the results of the search
	 */
	@RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<Annotation>> annotationLookup(AnnotationFilter filter, BindingResult bindingResult) {

		checkState(!bindingResult.hasErrors(), "The binding of the request parameters to " +
				"AnnotationFilter %s has errors, see binding result %s", filter, bindingResult);

		filter.stream().forEach(pf -> pf.validate(bindingResult));
		validationHelper.validateRequestedResults(filter.getLimit());
		FilterOnlySearchQueryTemplate.Builder requestBuilder = requestTemplate.newBuilder()
				.setFilterProvider(filter);
		return search(requestBuilder.build(), annotationSearchService);

	}
}
