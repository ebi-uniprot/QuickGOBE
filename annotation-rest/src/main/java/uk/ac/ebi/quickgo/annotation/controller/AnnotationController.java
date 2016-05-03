package uk.ac.ebi.quickgo.annotation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationFilter;
import uk.ac.ebi.quickgo.annotation.service.search.AnnotationSearchQueryTemplate;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Objects;

import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

/**
 * Provides RESTful endpoints for retrieving Annotations
 *
 * taxon=1234,343434
 *
 * gp=A0A000,A0A001
 * gpSet=BHF-UCL,Exosome
 * gpType=protein,rna,complexe
 *
 * goTerm=GO:0016021,GO:0016022
 * goTermSet=goslim_chembl,goSlimGeneric
 *
 * ..the following are only applicable if goTerm ids or sets have been selected
 * goTermUse=ancestor or goTermUse=slim  or goTermUse=exact
 * goTermRelationship=I or goTermRelationship=IPO or goTermRelationship=IPOR
 *
 * aspect=F,P,C
 *
 * evidence=ECO:0000352,ECO0000269            ?allow go evidence codes??
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

	private final SearchService<Annotation> annotationSearchService;
	private final AnnotationSearchQueryTemplate requestTemplate;

	@Autowired
	public AnnotationController(SearchService<Annotation> annotationSearchService,
			SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig ) {
		Objects.requireNonNull(annotationSearchService, "The SearchService<Annotation> instance passed to the constructor of " +
				"AnnotationController should not be null.");
		this.annotationSearchService = annotationSearchService;
		this.requestTemplate = new AnnotationSearchQueryTemplate(annotationRetrievalConfig.getSearchReturnedFields());
	}

	/**
	 * Search for an Annotations based on their attributes
	 * @return a {@link QueryResult} instance containing the results of the search
	 */
	@RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<QueryResult<Annotation>> annotationLookup(AnnotationFilter filter, BindingResult bindingResult) {

		System.out.println(bindingResult.toString());

		filter.validation();

		AnnotationSearchQueryTemplate.Builder requestBuilder = requestTemplate.newBuilder()
				.addAnnotationFilter(filter);
		return search(requestBuilder.build(), annotationSearchService);

	}
}
