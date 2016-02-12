package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.Optional;
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
     * Find the complete data set stored about a specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findCompleteInfoByOntologyId(String id);

    /**
     * Find the core data set stored about a specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findCoreInfoByOntologyId(String id);

    /**
     * Find historical changes related to this specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findHistoryInfoByOntologyId(String id);

    /**
     * Find the cross-references stored for a specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findXRefsInfoByOntologyId(String id);

    /**
     * Find the taxonomy constraints stored for a specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findTaxonConstraintsInfoByOntologyId(String id);

    /**
     * Find information about cross-ontology relations, for a specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findXORelationsInfoByOntologyId(String id);

    /**
     * Find the annotation guidelines for a specified ontology ID.
     * @param id the ontology ID
     * @return an {@link Optional} {@link OBOTerm} instance for this ontology term
     */
    Optional<T> findAnnotationGuideLinesInfoByOntologyId(String id);
}
