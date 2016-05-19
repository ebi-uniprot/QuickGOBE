package uk.ac.ebi.quickgo.annotation.controller;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.search.BasicSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;

import uk.ac.ebi.quickgo.rest.search.filter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.filter.RequestFilter;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.search;

/**
 * Provides RESTful endpoints for retrieving Gene Ontology (GO) Annotations to gene products.
 *
/ * Gene Ontology: the framework for the model of biology. The GO defines concepts/classes used
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
    private final ControllerValidationHelper validationHelper;

    private final SearchService<Annotation> annotationSearchService;

    private final BasicSearchQueryTemplate queryTemplate;

    @Autowired
    public AnnotationController(SearchService<Annotation> annotationSearchService,
            SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig,
            ControllerValidationHelper validationHelper,
            FilterConverterFactory filterConverterFactory) {
        checkNotNull(annotationSearchService, "The SearchService<Annotation> instance passed to the constructor of " +
                "AnnotationController should not be null.");
        checkNotNull(annotationRetrievalConfig, "The SearchServiceConfig.AnnotationCompositeRetrievalConfig" +
                " instance passed to the constructor of AnnotationController should not be null.");
        checkNotNull(filterConverterFactory, "The FilterConverterFactory cannot be null.");

        this.annotationSearchService = annotationSearchService;
        this.validationHelper = validationHelper;

        this.queryTemplate = new BasicSearchQueryTemplate(annotationRetrievalConfig.getSearchReturnedFields(),
                filterConverterFactory);
    }

    /**
     * Search for an Annotations based on their attributes
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<Annotation>> annotationLookup(@Valid AnnotationRequest annotationRequest,
            BindingResult bindingResult) {

        checkArgument(!bindingResult.hasErrors(), "The binding of the request parameters to " +
                "AnnotationRequest %s has errors, see binding result %s", annotationRequest, bindingResult);

        validationHelper.validateRequestedResults(annotationRequest.getLimit());

        Set<RequestFilter> filters = annotationRequest
                .convertToFilters()
                .collect(Collectors.toSet());

        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .setFilters(filters)
                .setPage(annotationRequest.getPage())
                .setPageSize(annotationRequest.getLimit())
                .build();

        return search(queryRequest, annotationSearchService);
    }
}