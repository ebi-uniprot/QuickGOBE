package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.rest.ParameterBindingException;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.search.BasicSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Preconditions.checkArgument;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.searchAndTransform;

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
    private final ControllerValidationHelper validationHelper;

    private final SearchService<Annotation> annotationSearchService;

    private final BasicSearchQueryTemplate queryTemplate;
    private final FilterConverterFactory converterFactory;
    private final ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain;

    @Autowired
    public AnnotationController(SearchService<Annotation> annotationSearchService,
            SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig,
            ControllerValidationHelper validationHelper,
            FilterConverterFactory converterFactory,
            ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain) {
        checkArgument(annotationSearchService != null, "The SearchService<Annotation> instance passed " +
                "to the constructor of AnnotationController should not be null.");
        checkArgument(annotationRetrievalConfig != null, "The SearchServiceConfig" +
                ".AnnotationCompositeRetrievalConfig instance passed to the constructor of AnnotationController " +
                "should not be null.");
        checkArgument(converterFactory != null, "The FilterConverterFactory cannot be null.");
        checkArgument(resultTransformerChain != null,
                "The ResultTransformerChain<QueryResult<Annotation>> cannot be null.");

        this.annotationSearchService = annotationSearchService;
        this.validationHelper = validationHelper;

        this.converterFactory = converterFactory;
        this.queryTemplate = new BasicSearchQueryTemplate(annotationRetrievalConfig.getSearchReturnedFields());
        this.resultTransformerChain = resultTransformerChain;
    }

    /**
     * Search for an Annotations based on their attributes
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @RequestMapping(value = "/search", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<Annotation>> annotationLookup(@Valid AnnotationRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }

        validationHelper.validateRequestedResults(request.getLimit());

        Set<QuickGOQuery> filterQueries = new HashSet<>();
        Set<FilterContext> filterContexts = new HashSet<>();
        extractFilterQueryInfo(request, filterQueries, filterContexts);

        FilterContext filterContext = filterContexts.stream().reduce
                (new FilterContext(), FilterContext::merge);

        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .setFilters(filterQueries)
                .setPage(request.getPage())
                .setPageSize(request.getLimit())
                .build();

        return searchAndTransform(queryRequest, annotationSearchService, resultTransformerChain, filterContext);
    }

    private void extractFilterQueryInfo(
            AnnotationRequest request,
            Set<QuickGOQuery> filterQueries,
            Set<FilterContext> filterContexts) {
        request.createFilterRequests().stream()
                .map(converterFactory::convert)
                .forEach(convertedResponse -> {
                    filterQueries.add(convertedResponse.getConvertedValue());
                    convertedResponse.getFilterContext().ifPresent(filterContexts::add);
                });
    }
}