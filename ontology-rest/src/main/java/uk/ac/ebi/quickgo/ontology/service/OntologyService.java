package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorGraph;
import uk.ac.ebi.quickgo.ontology.model.graph.AncestorVertex;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationType;
import uk.ac.ebi.quickgo.ontology.model.OntologyRelationship;
import uk.ac.ebi.quickgo.ontology.model.SlimTerm;
import uk.ac.ebi.quickgo.rest.search.query.RegularPage;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.List;
import java.util.Set;

/**
 * Service layer for retrieving results from an underlying searchable data store.
 *
 * See also {@link OntologyRepository}
 *
 * Created 11/11/15
 * @author Edd
 */
public interface OntologyService<T extends OBOTerm> {
    /**
     * Search over everything and return a list of results,
     * which fulfil the specification of the {@code pageable} instance.
     * @param page the requested page of results
     * @return the page of results
     */
    QueryResult<T> findAllByOntologyType(OntologyType type, RegularPage page);

    /**
     * Find the complete data set stored for a specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findCompleteInfoByOntologyId(List<String> ids);

    /**
     * Find the core data set stored for a specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findCoreInfoByOntologyId(List<String> ids);

    /**
     * Find historical changes related to specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findHistoryInfoByOntologyId(List<String> ids);

    /**
     * Find the cross-references stored for a specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findXRefsInfoByOntologyId(List<String> ids);

    /**
     * Find the taxonomy constraints stored for a specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findTaxonConstraintsInfoByOntologyId(List<String> ids);

    /**
     * Find information about cross-ontology relations, for a specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findXORelationsInfoByOntologyId(List<String> ids);

    /**
     * Find the annotation guidelines for a specified list of ontology IDs.
     * @param ids the ontology IDs
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findAnnotationGuideLinesInfoByOntologyId(List<String> ids);

    /**
     * Find the list of paths between two sets of vertices in a graph, navigable via
     * a specified set of relations.
     *
     * @param startingIds the starting ids from which returned paths must start
     * @param endingIds the ending ids from which returned paths end
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return a list of paths from {@code startingIds} to {@code endingIds} via {@code relations}
     */
    List<List<OntologyRelationship>> paths(
            Set<String> startingIds,
            Set<String> endingIds,
            OntologyRelationType... relations);

    /**
     * Find the set of ancestor vertices reachable from a list of ids, {@code ids}, navigable via a specified
     * set of relations.
     *
     * @param ids a {@link List} of ids whose ancestors one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findAncestorsInfoByOntologyId(List<String> ids, OntologyRelationType... relations);

    /**
     * Find the set of descendant ids reachable from a specified list of ids, {@code ids}, navigable via a specified
     * set of relations.
     *
     * @param ids a {@link List} ids whose descendants one is interested in
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return a {@link List} of {@link OBOTerm} instances corresponding to the ontology term ids containing the
     * chosen information
     */
    List<T> findDescendantsInfoByOntologyId(List<String> ids, OntologyRelationType... relations);

    /**
     * Maps ids to their equivalent, slimmed ids. The results are presented as a list of {@link SlimTerm} instances,
     * each of which contains a term id, and which shows the ids to which this term slims to.
     *
     * @param slimTerms the terms to which we want to find term ids that map "up" to.
     * @param relationTypes the relationships over which the slimming calculation will traverse
     * @return a list of {@link SlimTerm} objects that provide the slimming information
     */
    List<SlimTerm> findSlimmedInfoForSlimmedTerms(List<String> slimTerms, OntologyRelationType... relationTypes);

    /**
     * Find the set of ancestor vertices reachable from a list of ids, {@code ids}, navigable via a specified
     * set of relations.
     *
     * @param startIds a {@link Set} of ids whose ancestors one is interested in
     * @param stopIds a {@link Set} of ids whose ancestors one is not interested in. An empty set means all
     * ancestors are of interest.
     * @param relations a varargs value containing the relationships over which paths can only travel.
     *                  By omitting a {@code relation} value, all paths will be returned.
     * @return a {@link AncestorGraph} corresponding to the sub-graph of ontology constrained by the selected ids.
     */
    AncestorGraph<AncestorVertex> findOntologySubGraphById(Set<String> startIds, Set<String> stopIds,
            OntologyRelationType... relations);
}
