package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermRepository;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermSource;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTerm;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.service.converter.OntologyDocConverter;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraphTraversal;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.query.Page;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Collections.singleton;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Service to query information from the {@link OntologyRepository}
 * and return {@link OBOTerm} subclasses.
 *
 * Created 11/11/15
 * @author Edd
 */
public class OntologyServiceImpl<T extends OBOTerm> implements OntologyService<T> {
    private static final Logger LOGGER = getLogger(OntologyServiceImpl.class);
    private final AncestorFetcher ancestorFetcher = new AncestorFetcher();
    private final DescendantFetcher descendantFetcher = new DescendantFetcher();

    private OntologyGraphTraversal ontologyTraversal;
    private QueryStringSanitizer queryStringSanitizer;
    private OntologyRepository ontologyRepository;
    private OntologyDocConverter<T> converter;
    private String ontologyType;
    private CoTermRepository coTermRepository;

    // necessary for Spring to create a proxy class
    private OntologyServiceImpl() {}

    OntologyServiceImpl(
            OntologyRepository repository,
            OntologyDocConverter<T> converter,
            OntologyType type,
            QueryStringSanitizer queryStringSanitizer,
            OntologyGraphTraversal ontologyTraversal,
            CoTermRepository coTermRepository) {

        Preconditions.checkArgument(repository != null, "Ontology repository cannot be null");
        Preconditions.checkArgument(type != null, "Ontology type cannot be null");
        Preconditions.checkArgument(converter != null, "Ontology converter cannot be null");
        Preconditions.checkArgument(queryStringSanitizer != null, "Ontology query string sanitizer cannot be null");
        Preconditions.checkArgument(ontologyTraversal != null, "OntologyGraphTraversal cannot be null");
        Preconditions.checkArgument(coTermRepository != null, "CoTermRepository cannot be null");

        this.ontologyType = type.name();
        this.ontologyRepository = repository;
        this.converter = converter;
        this.queryStringSanitizer = queryStringSanitizer;
        this.ontologyTraversal = ontologyTraversal;
        this.coTermRepository = coTermRepository;
    }

    @Override public QueryResult<T> findAllByOntologyType(OntologyType type, Page page) {
        Pageable pageable = new PageRequest(calculatePageNumber(page.getPageNumber()), page.getPageSize());

        org.springframework.data.domain.Page<OntologyDocument> pagedResult =
                ontologyRepository.findAllByOntologyType(type.name(), pageable);

        return buildQueryResult(pagedResult, page);
    }

    @Override public List<T> findCompleteInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCompleteByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<T> findCoreInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<T> findHistoryInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findHistoryByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<T> findXRefsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXRefsByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<T> findTaxonConstraintsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findTaxonConstraintsByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<T> findXORelationsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXOntologyRelationsByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<T> findAnnotationGuideLinesInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findAnnotationGuidelinesByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override public List<List<OntologyRelationship>> paths(Set<String> startingIds, Set<String> endingIds,
            OntologyRelationType... relations) {
        return ontologyTraversal.paths(startingIds, endingIds, relations);
    }

    @Override public List<T> findAncestorsInfoByOntologyId(List<String> ids, OntologyRelationType... relations) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
                .map(term -> this.insertAncestors(term, relations))
                .collect(Collectors.toList());
    }

    @Override public List<T> findDescendantsInfoByOntologyId(List<String> ids, OntologyRelationType... relations) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
                .map(term -> this.insertDescendants(term, relations))
                .collect(Collectors.toList());
    }

    @Override public List<CoTerm> findCoTermsByGoTermId(String id, CoTermSource type, int limit, float
            similarityThreshold) {
        Predicate<CoTerm> filter = ct -> ct.getSignificance() >= similarityThreshold;
        return coTermRepository.findCoTerms(id, type, limit, filter);
    }

    List<String> buildIdList(List<String> ids) {
        Preconditions.checkArgument(ids != null, "List of IDs cannot be null");

        return ids.stream().map(queryStringSanitizer::sanitize).collect(Collectors.toList());
    }

    /**
     * <p>Converts a specified list of {@link OntologyDocument}s into a {@link Stream}
     * of {@link T} instances.
     *
     * <p>No ontology graph data is added to the {@link OntologyDocument}s.
     *
     * @param docs the list od {@link OntologyDocument}s to convertRelation
     * @return a {@link Stream} of {@link T} instances
     */
    private Stream<T> convertDocs(List<OntologyDocument> docs) {
        return docs.stream()
                .map(converter::convert)
                .map(this::insertChildren);
    }

    /**
     * Looks up the children of the {@code term} and adds them to the term.
     *
     * @param term
     * @return
     */
    private T insertChildren(T term) {
        try {
            Set<OntologyRelationship> childrenEdges = ontologyTraversal.children(term.id);

            term.children = childrenEdges.stream()
                    .map(this::convertRelation)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            LOGGER.info("Could not fetch children for: [" + term.id + "]. Exception message was: " + e.getMessage());
        }

        return term;
    }

    /**
     * Converts an edge {@link OntologyRelationType} into an {@link uk.ac.ebi.quickgo.ontology.model.OBOTerm.Relation}.
     *
     * @param oRel the ontology relationship to convertRelation
     * @return an ontology relation understandable by the client
     */
    private OBOTerm.Relation convertRelation(OntologyRelationship oRel) {
        OBOTerm.Relation relation = new OBOTerm.Relation();
        relation.id = oRel.child;
        relation.relation = oRel.relationship;

        return relation;
    }

    private T insertAncestors(T term, OntologyRelationType... relations) {
        term.ancestors = getRelatives(ancestorFetcher, term, relations);
        return term;
    }

    private T insertDescendants(T term, OntologyRelationType... relations) {
        term.descendants = getRelatives(descendantFetcher, term, relations);
        return term;
    }

    /**
     * Fetch the relatives of a specified {@code term}, via the specified {@code relations}.
     * The specification of the traversal function is provided as parameter, {@code traversalFunction}.
     * Specifically, this can either be ancestors, or descendants.
     *
     * @param traversalFunction the function used to traverse the ontology graph
     * @param term the starting {@code term}
     * @param relations the relationship types that can be navigated
     * @return a list of term ids that are the relatives of {@code term}
     */
    private List<String> getRelatives(
            BiFunction<T, OntologyRelationType[], List<String>> traversalFunction,
            T term,
            OntologyRelationType[] relations) {
        try {
            return traversalFunction.apply(term, relations);
        } catch (Exception e) {
            LOGGER.info("Could not fetch relatives for: [" + term.id + "] with relationships: ["
                    + Stream.of(relations).map(OntologyRelationType::getLongName).collect(Collectors.joining(","))
                    + "]. Exception message was: " + e.getMessage());
            return Collections.unmodifiableList(Collections.emptyList());
        }
    }

    private QueryResult<T> buildQueryResult(org.springframework.data.domain.Page<OntologyDocument> pagedResult,
            Page page) {
        long totalNumberOfHits = pagedResult.getTotalElements();

        PageInfo pageInfo = new PageInfo(pagedResult.getTotalPages(), page.getPageNumber(), page.getPageSize());

        List<T> entryHits = pagedResult.getContent().stream()
                .map(converter::convert)
                .map(this::insertChildren)
                .collect(Collectors.toList());

        return new QueryResult.Builder<>(totalNumberOfHits, entryHits).withPageInfo(pageInfo).build();
    }

    private int calculatePageNumber(int oneBasedPageNum) {
        return oneBasedPageNum - 1;
    }

    private class AncestorFetcher implements BiFunction<T, OntologyRelationType[], List<String>> {
        @Override public List<String> apply(T term, OntologyRelationType[] relations) {
            return ontologyTraversal.ancestors(singleton(term.id), relations);
        }
    }

    private class DescendantFetcher implements BiFunction<T, OntologyRelationType[], List<String>> {
        @Override public List<String> apply(T term, OntologyRelationType[] relations) {
            return ontologyTraversal.descendants(singleton(term.id), relations);
        }
    }
}
