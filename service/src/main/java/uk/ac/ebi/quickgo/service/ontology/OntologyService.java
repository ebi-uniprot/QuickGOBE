package uk.ac.ebi.quickgo.service.ontology;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.GOTerm;

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
public interface OntologyService<T> {
    // default page size if required by a query
    int DEFAULT_PAGE_SIZE = 2;

    /**
     * Search over everything and return a list, which fulfils
     * the requested page of results.
     * @param pageable
     * @return
     */
    List<OntologyDocument> findAll(Pageable pageable);

    /**
     * Search by a given GO id and return a list, which fulfils
     * the requested page of results.
     * @param id
     * @param pageable
     * @return
     */
    Optional<T> findByOntologyId(String id);

    /**
     * Search by a given ECO id and return a list, which fulfils
     * the requested page of results.
     * @param ecoId
     * @param pageable
     * @return
     */
//    List<OntologyDocument> findByEcoId(String ecoId, Pageable pageable);
}
