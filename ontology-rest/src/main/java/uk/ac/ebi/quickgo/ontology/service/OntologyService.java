package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import org.springframework.data.domain.Pageable;

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
     * @param pageable the requested page of results
     * @return the page of results
     */
    List<T> findAll(Pageable pageable);

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
}
