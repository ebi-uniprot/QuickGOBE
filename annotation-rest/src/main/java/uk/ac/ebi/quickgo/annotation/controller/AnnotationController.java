package uk.ac.ebi.quickgo.annotation.controller;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import uk.ac.ebi.quickgo.annotation.download.header.HeaderContent;
import uk.ac.ebi.quickgo.annotation.download.header.HeaderCreator;
import uk.ac.ebi.quickgo.annotation.download.header.HeaderCreatorFactory;
import uk.ac.ebi.quickgo.annotation.download.header.HeaderUri;
import uk.ac.ebi.quickgo.annotation.download.model.DownloadContent;
import uk.ac.ebi.quickgo.annotation.model.*;
import uk.ac.ebi.quickgo.annotation.service.search.NameService;
import uk.ac.ebi.quickgo.annotation.service.search.SearchServiceConfig;
import uk.ac.ebi.quickgo.annotation.service.statistics.StatisticsService;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.rest.ParameterBindingException;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;
import uk.ac.ebi.quickgo.rest.metadata.MetaData;
import uk.ac.ebi.quickgo.rest.metadata.MetaDataProvider;
import uk.ac.ebi.quickgo.rest.search.DefaultSearchQueryTemplate;
import uk.ac.ebi.quickgo.rest.search.SearchService;
import uk.ac.ebi.quickgo.rest.search.query.QueryRequest;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.converter.FilterConverterFactory;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformationRequests;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformerChain;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.VARY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory.*;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue.EvidenceNameInjector.EVIDENCE_CODE;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue.OntologyNameInjector.GO_ID;
import static uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.transformer.completablevalue.TaxonomyNameInjector.TAXON_ID;
import static uk.ac.ebi.quickgo.common.array.ArrayPopulation.ensureArrayContains;
import static uk.ac.ebi.quickgo.common.array.ArrayPopulation.ensureArrayContainsCommonValue;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.searchAndTransform;
import static uk.ac.ebi.quickgo.rest.search.SearchDispatcher.streamSearchResults;
import static uk.ac.ebi.quickgo.rest.search.query.CursorPage.createFirstCursorPage;

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
 * gpType=protein,miRNA,complex
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
@Api(tags = {"annotations"})
@RequestMapping(value = "/annotation")
public class AnnotationController {
    private static final Logger LOGGER = getLogger(AnnotationController.class);
    private static final DateTimeFormatter DOWNLOAD_FILE_NAME_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("-N-yyyyMMdd");
    private static final String DOWNLOAD_FILE_NAME_PREFIX = "QuickGO-annotations";
    private static final String GO_USAGE_SLIM = "goUsage=slim";
    private static final String DOWNLOAD_STATISTICS_FILE_NAME = "annotation_statistics";
    private static final Function<MediaType, String> TO_DOWNLOAD_STATISTICS_FILENAME = mt -> String.format("%s.%s",
            DOWNLOAD_STATISTICS_FILE_NAME,
            fileExtension(mt));
    private static final Function<MediaType, String> TO_DOWNLOAD_FILENAME = mt -> String.format("%s%s.%s",
            DOWNLOAD_FILE_NAME_PREFIX,
            formattedDateStringForNow(),
            fileExtension(mt));
    private static final String GO_NAME = "goName";
    private static final String TAXON_NAME = "taxonName";
    private static final String EVIDENCE_NAME = "evidenceName";
    private static final String COLLECTION = SolrCollectionName.ANNOTATION;
    private final MetaDataProvider metaDataProvider;
    private final SearchService<Annotation> annotationSearchService;
    private final SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig;
    private final DefaultSearchQueryTemplate queryTemplate;
    private final DefaultSearchQueryTemplate downloadQueryTemplate;
    private final FilterConverterFactory converterFactory;
    private final ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain;
    private final StatisticsService statsService;
    private final TaskExecutor taskExecutor;
    private final HeaderCreatorFactory headerCreatorFactory;
    private final NameService nameService;

    @Autowired
    public AnnotationController(SearchService<Annotation> annotationSearchService,
            SearchServiceConfig.AnnotationCompositeRetrievalConfig annotationRetrievalConfig,
            FilterConverterFactory converterFactory,
            ResultTransformerChain<QueryResult<Annotation>> resultTransformerChain,
            StatisticsService statsService,
            TaskExecutor taskExecutor,
            HeaderCreatorFactory headerCreatorFactory,
            MetaDataProvider metaDataProvider,
            NameService nameService) {
        checkArgument(annotationSearchService != null, "The SearchService<Annotation> instance passed " +
                "to the constructor of AnnotationController should not be null.");
        checkArgument(annotationRetrievalConfig != null, "The SearchServiceConfig" +
                ".AnnotationCompositeRetrievalConfig instance passed to the constructor of AnnotationController " +
                "should not be null.");
        checkArgument(converterFactory != null, "The FilterConverterFactory cannot be null.");
        checkArgument(resultTransformerChain != null,
                "The ResultTransformerChain<QueryResult<Annotation>> cannot be null.");
        checkArgument(statsService != null, "Annotation stats service cannot be null.");
        checkArgument(taskExecutor != null, "TaskExecutor cannot be null.");
        checkArgument(headerCreatorFactory != null, "HeaderCreatorFactory cannot be null.");
        checkArgument(metaDataProvider != null, "Metadata provider cannot be null.");

        this.annotationSearchService = annotationSearchService;
        this.converterFactory = converterFactory;

        this.statsService = statsService;
        this.resultTransformerChain = resultTransformerChain;

        this.annotationRetrievalConfig = annotationRetrievalConfig;
        this.queryTemplate = createSearchQueryTemplate(annotationRetrievalConfig);
        this.downloadQueryTemplate = createDownloadSearchQueryTemplate(annotationRetrievalConfig);

        this.taskExecutor = taskExecutor;
        this.headerCreatorFactory = headerCreatorFactory;

        this.metaDataProvider = metaDataProvider;

        this.nameService = nameService;
    }

    /**
     * Search for an Annotations based on their attributes
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Annotation result set has been filtered according to " +
                    "the provided attribute values"),
            @ApiResponse(code = 500, message = "Internal server error occurred whilst searching for " +
                    "matching annotations", response = ResponseExceptionHandler.ErrorInfo.class),
            @ApiResponse(code = 400, message = "Bad request due to a validation issue encountered in one of the " +
                    "filters", response = ResponseExceptionHandler.ErrorInfo.class)})
    @ApiOperation(value = "Search for all annotations that match the supplied filter criteria.")
    @RequestMapping(value = "/search", method = {GET, POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<Annotation>> annotationLookup(
        @ApiParam("Optional body for advance filtering. For example show me all annotations to proteins that are annotated to GO:xxx AND GO:yyy " +
          "or show me all annotations to proteins that are annotated to GO:xxx and NOT GO:yyy. Request Body is in beta and subject to change in future")
        @Valid @RequestBody(required = false) AnnotationRequestBody body,
        @Valid @ModelAttribute AnnotationRequest request, BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        request.addRequestBody(body);
        FilterQueryInfo filterQueryInfo = extractFilterQueryInfo(request);

        QueryRequest queryRequest = queryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .setCollection(COLLECTION)
                .addFilters(filterQueryInfo.getFilterQueries())
                .setPage(new RegularPage(request.getPage(), request.getLimit()))
                .build();

        return searchAndTransform(queryRequest, annotationSearchService, resultTransformerChain,
                filterQueryInfo.getFilterContext());
    }

    /**
     * Return statistics based on the search result.
     *
     * The statistics are subdivided into two areas, each with
     * @return a {@link QueryResult} instance containing the results of the search
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Statistics have been calculated for the annotation result set " +
                    "obtained from the application of the filter parameters"),
            @ApiResponse(code = 500, message = "Internal server error occurred whilst producing statistics",
                    response = ResponseExceptionHandler.ErrorInfo.class),
            @ApiResponse(code = 400, message = "Bad request due to a validation issue encountered in one of the " +
                    "filters", response = ResponseExceptionHandler.ErrorInfo.class)})
    @ApiOperation(value = "Generate statistics for the annotation result set obtained from applying the filters.")
    @RequestMapping(value = "/stats", method = {GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<StatisticsGroup>> annotationStats(
            @Valid @ModelAttribute AnnotationRequest request, BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        QueryResult<StatisticsGroup> stats = statsService.calculateForStandardUsage(request);
        addAllNamesToStatisticsValues(stats);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @ApiOperation(value = "Download all annotations that match the supplied filter criteria.",
            response = File.class)
    @RequestMapping(value = "/downloadSearch", method = {GET, POST},
            produces = {GPAD_MEDIA_TYPE_STRING, GAF_MEDIA_TYPE_STRING, TSV_MEDIA_TYPE_STRING})
    public ResponseEntity<ResponseBodyEmitter> downloadLookup(
            @ApiParam("Optional body for advance filtering. For example show me all annotations to proteins that are annotated to GO:xxx AND GO:yyy " +
              "or show me all annotations to proteins that are annotated to GO:xxx and NOT GO:yyy. Request Body is in beta and subject to change in future")
            @Valid @RequestBody(required = false) AnnotationRequestBody body,
            @Valid @ModelAttribute AnnotationRequest request,
            BindingResult bindingResult,
            @RequestHeader(ACCEPT) MediaType mediaTypeAcceptHeader, HttpServletRequest servletRequest) {
        LOGGER.info("Download Request:: " + request + ", " + mediaTypeAcceptHeader);
        checkBindingErrors(bindingResult);

        if (mediaTypeAcceptHeader.getSubtype().equals("gaf")) {
            //For gaf, gene product name and synonyms must be present, so make sure it appears in the list of  include
            // fields.
            request.setIncludeFields(ensureArrayContains(request.getIncludeFields(), "name"));
            request.setIncludeFields(ensureArrayContains(request.getIncludeFields(), "synonyms"));
        } else if (mediaTypeAcceptHeader.getSubtype().equals("tsv")) {
            //If synonyms are requested, ensure synonyms is in the list of include fields.
            request.setIncludeFields(
                    ensureArrayContainsCommonValue(request.getSelectedFields(), request.getIncludeFields(),
                            "synonyms"));
            //If gene product name is requested, ensure name is in the list of include fields.
            request.setIncludeFields(
                    ensureArrayContainsCommonValue(request.getSelectedFields(), request.getIncludeFields(), "name"));
        }

        request.addRequestBody(body);
        FilterQueryInfo filterQueryInfo = extractFilterQueryInfo(request);
        final int pageLimit = getPageLimit(request);

        QueryRequest queryRequest = createQueryRequest(filterQueryInfo, pageLimit);
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        final List<String> selectedFields = selectedFieldList(request);
        writeHeader(mediaTypeAcceptHeader, servletRequest, emitter, selectedFields);
        writeBody(request, mediaTypeAcceptHeader, filterQueryInfo, queryRequest, emitter, selectedFields);

        return ResponseEntity.ok().headers(createHttpDownloadHeader(mediaTypeAcceptHeader, TO_DOWNLOAD_FILENAME)).body(emitter);
    }

    private void writeBody(@Valid @ModelAttribute AnnotationRequest request,
                           @RequestHeader(ACCEPT) MediaType mediaTypeAcceptHeader,
                           FilterQueryInfo filterQueryInfo,
                           QueryRequest queryRequest,
                           ResponseBodyEmitter emitter,
                           List<String> selectedFields) {
        taskExecutor.execute(() -> {
            final Stream<QueryResult<Annotation>> annotationResultStream =
                    getQueryResultStream(request, filterQueryInfo, queryRequest);
            DownloadContent downloadContent = new DownloadContent(annotationResultStream, selectedFields);
            emitDownloadWithMediaType(emitter, downloadContent, mediaTypeAcceptHeader);
        });
    }

    private QueryRequest createQueryRequest(FilterQueryInfo filterQueryInfo, int pageLimit) {
        return downloadQueryTemplate.newBuilder()
                .setQuery(QuickGOQuery.createAllQuery())
                .setCollection(COLLECTION)
                .addFilters(filterQueryInfo.getFilterQueries())
                .setPage(createFirstCursorPage(pageLimit))
                .build();
    }

    private void writeHeader(@RequestHeader(ACCEPT) MediaType mediaTypeAcceptHeader,
                             HttpServletRequest servletRequest,
                             ResponseBodyEmitter emitter,
                             List<String> selectedFields) {
        HeaderCreator headerCreator = headerCreatorFactory.provide(mediaTypeAcceptHeader.getSubtype());
        HeaderContent headerContent = buildHeaderContent(servletRequest, selectedFields);
        headerCreator.write(emitter, headerContent);
    }

    private int getPageLimit(@Valid @ModelAttribute AnnotationRequest request) {
        return request.getDownloadLimit() < this.annotationRetrievalConfig.getDownloadPageSize() ?
                request.getDownloadLimit() : this.annotationRetrievalConfig.getDownloadPageSize();
    }

    /**
     * Get meta data information about the Annotation service
     *
     * @return response with metadata information.
     */
    @ApiOperation(value = "Get meta-data information about the annotation service",
            response = About.class,
            notes = "Provides the date the annotation information was created.")
    @RequestMapping(value = "/about", method = GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MetaData> provideMetaData() {
        return new ResponseEntity<>(metaDataProvider.lookupMetaData(), HttpStatus.OK);
    }

    @ApiOperation(value = "Download statistics for all annotations that match the supplied filter criteria.",
            response = File.class)
    @RequestMapping(value = "/downloadStats", method = {GET},
            produces = {EXCEL_MEDIA_TYPE_STRING, JSON_MEDIA_TYPE_STRING})
    public ResponseEntity<ResponseBodyEmitter> downloadStats(@Valid @ModelAttribute AnnotationRequest request,
            BindingResult bindingResult, @RequestHeader(ACCEPT) MediaType mediaTypeAcceptHeader) {
        checkBindingErrors(bindingResult);
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        taskExecutor.execute(() -> {
            QueryResult<StatisticsGroup> stats = statsService.calculateForDownloadUsage(request);
            addAllNamesToStatisticsValues(stats);
            emitDownloadWithMediaType(emitter, stats, mediaTypeAcceptHeader);
        });

        return ResponseEntity.ok()
                .headers(createHttpDownloadHeader(mediaTypeAcceptHeader,
                        TO_DOWNLOAD_STATISTICS_FILENAME))
                .body(emitter);
    }

    private static String formattedDateStringForNow() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DOWNLOAD_FILE_NAME_DATE_FORMATTER);
    }

    private void addAllNamesToStatisticsValues(QueryResult<StatisticsGroup> stats) {
        addNamesToStatisticsValues(stats, GO_NAME, GO_ID);
        addNamesToStatisticsValues(stats, TAXON_NAME, TAXON_ID);
        addNamesToStatisticsValues(stats, EVIDENCE_NAME, EVIDENCE_CODE);
    }

    private void checkBindingErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ParameterBindingException(bindingResult);
        }
    }

    private FilterQueryInfo extractFilterQueryInfo(AnnotationRequest request) {
        Set<QuickGOQuery> filterQueries = new HashSet<>();
        Set<FilterContext> filterContexts = new HashSet<>();

        convertFilterRequests(request, filterQueries, filterContexts);
        convertResultTransformationRequests(request, filterContexts);

        return new FilterQueryInfo() {
            @Override public Set<QuickGOQuery> getFilterQueries() {
                return filterQueries;
            }

            @Override public FilterContext getFilterContext() {
                return filterContexts.stream().reduce(new FilterContext(), FilterContext::merge);
            }
        };
    }

    /**
     * Processes the list of {@link FilterRequest}s from the {@link AnnotationRequest} and
     * adds corresponding {@link QuickGOQuery}s to the {@code filterQueries}, and {@link FilterContext}s
     * to the {@code filterContext}s.
     * @param request the annotation request
     * @param filterQueries the {@link QuickGOQuery} list to append to
     * @param filterContexts the {@link FilterContext} list to append to
     */
    private void convertFilterRequests(AnnotationRequest request, Set<QuickGOQuery> filterQueries,
            Set<FilterContext> filterContexts) {
        request.createFilterRequests().stream()
                .map(converterFactory::convert)
                .forEach(convertedFilter -> {
                    filterQueries.add(convertedFilter.getConvertedValue());
                    convertedFilter.getFilterContext().ifPresent(filterContexts::add);
                });
    }

    /**
     * Processes the {@link ResultTransformationRequests} instance from the {@link AnnotationRequest} and
     * adds corresponding {@link FilterContext}s to the {@code filterContext}s.
     * @param request the annotation request
     * @param filterContexts the {@link FilterContext} list to append to
     */
    private void convertResultTransformationRequests(AnnotationRequest request, Set<FilterContext> filterContexts) {
        ResultTransformationRequests transformationRequests = request.createResultTransformationRequests();
        if (!transformationRequests.getRequests().isEmpty()) {
            FilterContext transformationContext = new FilterContext();
            transformationContext.save(ResultTransformationRequests.class, transformationRequests);
            filterContexts.add(transformationContext);
        }
    }

    private List<String> selectedFieldList(AnnotationRequest annotationRequest) {
        if (annotationRequest.getSelectedFields() != null) {
            return Arrays.stream(annotationRequest.getSelectedFields())
                    .map(String::toLowerCase)
                    .collect(toList());
        }
        return Collections.emptyList();
    }

    private HeaderContent buildHeaderContent(HttpServletRequest servletRequest, List<String> selectedFields) {
        HeaderContent.Builder contentBuilder = new HeaderContent.Builder();
        return contentBuilder.setIsSlimmed(isSlimmed(servletRequest))
                .setUri(HeaderUri.uri(servletRequest))
                .setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE))
                .setSelectedFields(selectedFields)
                .build();
    }

    private Stream<QueryResult<Annotation>> getQueryResultStream(@Valid @ModelAttribute AnnotationRequest request,
            FilterQueryInfo filterQueryInfo, QueryRequest queryRequest) {
        LOGGER.info("Creating stream of search results. With limit " + request.getDownloadLimit());
        Stream<QueryResult<Annotation>> resultStream = streamSearchResults(queryRequest,
                queryTemplate,
                annotationSearchService,
                resultTransformerChain,
                filterQueryInfo.getFilterContext(),
                request.getDownloadLimit());
        LOGGER.info("Finished creating stream of search results.");
        return resultStream;
    }

    private <C> void emitDownloadWithMediaType(
            ResponseBodyEmitter emitter,
            C content,
            MediaType mediaType) {
        try {
            emitter.send(content, mediaType);
        } catch (IOException e) {
            LOGGER.error("Failed to stream annotation results", e);
            emitter.completeWithError(e);
        }
        emitter.complete();
        LOGGER.info("Emitted response stream -- which will be written by the HTTP message converter for: " + mediaType);
    }

    private HttpHeaders createHttpDownloadHeader(MediaType mediaType, Function<MediaType, String> toFileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDispositionFormData("attachment", toFileName.apply(mediaType));
        httpHeaders.setContentType(mediaType);
        httpHeaders.add(VARY, ACCEPT);
        return httpHeaders;
    }

    private boolean isSlimmed(HttpServletRequest servletRequest) {
        return Objects.nonNull(servletRequest.getQueryString()) &&
                servletRequest.getQueryString().contains(GO_USAGE_SLIM);
    }

    private DefaultSearchQueryTemplate createSearchQueryTemplate(
            SearchServiceConfig.AnnotationCompositeRetrievalConfig retrievalConfig) {
        DefaultSearchQueryTemplate template = new DefaultSearchQueryTemplate();
        template.setReturnedFields(retrievalConfig.getSearchReturnedFields());
        return template;
    }

    private DefaultSearchQueryTemplate createDownloadSearchQueryTemplate(
            SearchServiceConfig.AnnotationCompositeRetrievalConfig retrievalConfig) {
        DefaultSearchQueryTemplate template = new DefaultSearchQueryTemplate();
        template.setReturnedFields(retrievalConfig.getSearchReturnedFields());
        template.setPage(createFirstCursorPage(retrievalConfig.getDownloadPageSize()));
        retrievalConfig.getDownloadSortCriteria()
                .forEach(criterion ->
                        template.addSortCriterion(criterion.getSortField().getField(), criterion.getSortOrder()));
        return template;
    }

    private void addNamesToStatisticsValues(QueryResult<StatisticsGroup> stats, String nameField, String typeName) {
        try {
            stats.getResults()
                    .stream()
                    .flatMap(statisticsGroup -> statisticsGroup.getTypes().stream())
                    .filter(statisticsByType -> statisticsByType.getType().equals(typeName))
                    .flatMap(statisticsByType -> statisticsByType.getValues().stream())
                    .forEach(statisticsValue -> statisticsValue.setName(nameService.findName(nameField, statisticsValue
                            .getKey())));
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve name information processing statistics request", e);
        }
    }

    private interface FilterQueryInfo {
        Set<QuickGOQuery> getFilterQueries();

        FilterContext getFilterContext();
    }
}
