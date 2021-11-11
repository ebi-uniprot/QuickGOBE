package uk.ac.ebi.quickgo.ontology.service;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.*;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorVertex;
import uk.ac.ebi.quickgo.ontology.service.converter.OntologyDocConverter;
import uk.ac.ebi.quickgo.ontology.traversal.OntologyGraphTraversal;
import uk.ac.ebi.quickgo.ontology.traversal.TermSlimmer;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.results.PageInfo;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Service to query information from the {@link OntologyRepository}
 * and return {@link OBOTerm} subclasses.
 * <p>
 * Created 11/11/15
 *
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

    // necessary for Spring to create a proxy class
    private OntologyServiceImpl() {
    }

    OntologyServiceImpl(
            OntologyRepository repository,
            OntologyDocConverter<T> converter,
            OntologyType type,
            QueryStringSanitizer queryStringSanitizer,
            OntologyGraphTraversal ontologyTraversal) {

        Preconditions.checkArgument(repository != null, "Ontology repository cannot be null");
        Preconditions.checkArgument(type != null, "Ontology type cannot be null");
        Preconditions.checkArgument(converter != null, "Ontology converter cannot be null");
        Preconditions.checkArgument(queryStringSanitizer != null, "Ontology query string sanitizer cannot be null");
        Preconditions.checkArgument(ontologyTraversal != null, "OntologyGraphTraversal cannot be null");

        this.ontologyType = type.name();
        this.ontologyRepository = repository;
        this.converter = converter;
        this.queryStringSanitizer = queryStringSanitizer;
        this.ontologyTraversal = ontologyTraversal;
    }

    @Override
    public QueryResult<T> findAllByOntologyType(OntologyType type, RegularPage page) {
        Pageable pageable = PageRequest.of(calculatePageNumber(page.getPageNumber()), page.getPageSize());

        org.springframework.data.domain.Page<OntologyDocument> pagedResult =
                ontologyRepository.findAllByOntologyType(type.name(), pageable);

        return buildQueryResult(pagedResult, page);
    }

    @Override
    public List<T> findCompleteInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCompleteByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findCoreInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findHistoryInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findHistoryByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findXRefsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXRefsByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findTaxonConstraintsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findTaxonConstraintsByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findXORelationsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXOntologyRelationsByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findAnnotationGuideLinesInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findAnnotationGuidelinesByTermId(ontologyType, buildIdList(ids)))
                .collect(Collectors.toList());
    }

    @Override
    public List<List<OntologyRelationship>> paths(Set<String> startingIds, Set<String> endingIds,
            OntologyRelationType... relations) {
        return ontologyTraversal.paths(startingIds, endingIds, relations);
    }

    @Override
    public List<T> findAncestorsInfoByOntologyId(List<String> ids, OntologyRelationType... relations) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
                .map(term -> this.insertAncestors(term, relations))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findDescendantsInfoByOntologyId(List<String> ids, OntologyRelationType... relations) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
                .map(term -> this.insertDescendants(term, relations))
                .collect(Collectors.toList());
    }

    @Override
    public List<OBOMinimum> findChildrenInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(ids)))
          .map(this::convertMinimum)
          .collect(Collectors.toList());
    }

    private OBOMinimum convertMinimum(OBOTerm term){
        OBOMinimum retObj = new OBOMinimum();
        retObj.name = term.name;
        retObj.id = term.id;
        retObj.children = convertMinimumChildren(term.children);
        return retObj;
    }

    private List<OBOMinimum.Children> convertMinimumChildren(List<OBOTerm.Relation> children) {
        if (children.isEmpty())
            return Collections.emptyList();

        List<String> idsOfChildren = children.stream().map(c -> c.id).collect(Collectors.toList());
        return convertDocs(ontologyRepository.findCoreAttrByTermId(ontologyType, buildIdList(idsOfChildren)))
          .map(term -> convertMinimumChild(children, term))
          .collect(Collectors.toList());
    }

    private OBOMinimum.Children convertMinimumChild(List<OBOTerm.Relation> directChildren, OBOTerm directChildTerm){
        Predicate<OBOTerm.Relation> matchById = childTerm -> childTerm.id.equals(directChildTerm.id);
        OBOTerm.Relation directChild = directChildren.stream().filter(matchById).findFirst().get();

        OBOMinimum.Children retObj = new OBOMinimum.Children();
        retObj.id = directChild.id;
        retObj.relation= directChild.relation;
        retObj.name = directChildTerm.name;
        retObj.hasChildren = !directChildTerm.children.isEmpty();
        return retObj;
    }

    @Override
    public List<SlimTerm> findSlimmedInfoForSlimmedTerms(Set<String> slimsFromTerms, List<String> slimsToTerms,
            OntologyRelationType... relationTypes) {
        TermSlimmer slimmer = TermSlimmer
                .createSlims(OntologyType.valueOf(ontologyType), ontologyTraversal, slimsToTerms, relationTypes);

        return slimmer.getSlimmedTermsMap().entrySet().stream()
                .map(Map.Entry::getKey)
                .filter(term -> slimsFromTerms.isEmpty() || slimsFromTerms.contains(term))
                .map(id -> new SlimTerm(id, slimmer.findSlimmedToTerms(id)))
                .collect(Collectors.toList());
    }

    @Override
    public AncestorGraph<AncestorVertex> findOntologySubGraphById(Set<String> startIds, Set<String> stopIds,
            OntologyRelationType... relations) {
        AncestorGraph<String> ancestorGraph = ontologyTraversal.subGraph(startIds, stopIds, relations);
        Set<AncestorVertex> coreVertices = new HashSet<>();
        if (!ancestorGraph.vertices.isEmpty()) {
            List<T> coreList = this.findCoreInfoByOntologyId(new ArrayList<>(ancestorGraph.vertices));
            coreList.stream()
                    .map(coreInfo -> new AncestorVertex(coreInfo.id, coreInfo.name))
                    .forEach(coreVertices::add);
        }
        return new AncestorGraph<>(ancestorGraph.edges, coreVertices);
    }

    @Override
    public List<T> findSecondaryIdsByOntologyId(List<String> ids) {
        return ontologyRepository.findSecondaryIdsByTermId(ontologyType, buildIdList(ids))
          .stream()
          .map(converter::convert)
          .collect(Collectors.toList());
    }

    List<String> buildIdList(Collection<String> ids) {
        Preconditions.checkArgument(ids != null, "List of IDs cannot be null");

        return ids.stream().map(queryStringSanitizer::sanitize).collect(Collectors.toList());
    }

    /**
     * <p>Converts a specified list of {@link OntologyDocument}s into a {@link Stream}
     * of {@link T} instances.
     * <p>
     * <p>No ontology graph data is added to the {@link OntologyDocument}s.
     *
     * @param docs the list of {@link OntologyDocument}s to convert
     * @return a {@link Stream} of {@link T} instances
     */
    private Stream<T> convertDocs(Collection<OntologyDocument> docs) {
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
     * @param term              the starting {@code term}
     * @param relations         the relationship types that can be navigated
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
            RegularPage page) {
        long totalNumberOfHits = pagedResult.getTotalElements();

        PageInfo pageInfo = new PageInfo.Builder()
                .withTotalPages(pagedResult.getTotalPages())
                .withCurrentPage(page.getPageNumber())
                .withResultsPerPage(page.getPageSize()).build();

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
        @Override
        public List<String> apply(T term, OntologyRelationType[] relations) {
            return ontologyTraversal.ancestors(singleton(term.id), relations);
        }
    }

    private class DescendantFetcher implements BiFunction<T, OntologyRelationType[], List<String>> {
        @Override
        public List<String> apply(T term, OntologyRelationType[] relations) {
            return ontologyTraversal.descendants(singleton(term.id), relations);
        }
    }
}
