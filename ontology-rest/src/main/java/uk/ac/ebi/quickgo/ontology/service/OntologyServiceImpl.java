package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.OntologyDocConverter;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Service to query information from the {@link OntologyRepository}
 * and return {@link OBOTerm} subclasses.
 *
 * Created 11/11/15
 * @author Edd
 */
public class OntologyServiceImpl<T extends OBOTerm> implements OntologyService<T> {
    private QueryStringSanitizer queryStringSanitizer;
    private OntologyRepository ontologyRepository;
    private OntologyDocConverter<T> converter;
    private String ontologyType;

    // necessary for Spring to create a proxy class
    private OntologyServiceImpl() {}

    public OntologyServiceImpl(
            OntologyRepository repository,
            OntologyDocConverter<T> converter,
            OntologyType type,
            QueryStringSanitizer queryStringSanitizer) {

        Preconditions.checkArgument(repository != null, "Ontology repository cannot be null");
        Preconditions.checkArgument(type != null, "Ontology type cannot be null");
        Preconditions.checkArgument(converter != null, "Ontology converter cannot be null");
        Preconditions.checkArgument(queryStringSanitizer != null, "Ontology query string sanitizer cannot be null");

        this.ontologyType = type.name();
        this.ontologyRepository = repository;
        this.converter = converter;
        this.queryStringSanitizer = queryStringSanitizer;
    }

    @Override public List<T> findCompleteInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCompleteByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findCoreInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findHistoryInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findHistoryByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findXRefsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXRefsByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findTaxonConstraintsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findTaxonConstraintsByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findXORelationsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXOntologyRelationsByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findAnnotationGuideLinesInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findAnnotationGuidelinesByTermId(ontologyType, buildIdList(ids)));
    }

    protected List<T> convertDocs(List<OntologyDocument> docs) {
        return docs.stream().map(converter::convert).collect(Collectors.toList());
    }

    @Override public QueryResult<T> findAll(Page page) {
        Pageable pageable = new PageRequest(page.getPageNumber(), page.getPageSize());

        org.springframework.data.domain.Page<OntologyDocument> pagedResult = ontologyRepository.findAll(pageable);

        return buildQueryResult(pagedResult, page);
    }

    private QueryResult<T> buildQueryResult(org.springframework.data.domain.Page<OntologyDocument> pagedResult,
            Page page) {
        long totalNumberOfHits = pagedResult.getTotalElements();

        PageInfo pageInfo = new PageInfo(pagedResult.getTotalPages(), page.getPageNumber(), page.getPageSize());

        List<T> entryHits = pagedResult.getContent().stream()
                .map(converter::convert)
                .collect(Collectors.toList());

        return new QueryResult<>(totalNumberOfHits, entryHits, pageInfo, null, null);
    }

    protected List<String> buildIdList(List<String> ids) {
        Preconditions.checkArgument(ids != null, "List of IDs cannot be null");

        return ids.stream().map(queryStringSanitizer::sanitize).collect(Collectors.toList());
    }
}
