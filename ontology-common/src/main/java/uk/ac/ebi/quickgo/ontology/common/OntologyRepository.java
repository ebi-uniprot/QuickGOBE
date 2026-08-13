package uk.ac.ebi.quickgo.ontology.common;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.quickgo.common.repository.SolrCrudRepository;

/**
 * Ontology repository interface exposing methods for performing searches over its contents.
 * <p>
 * Created 11/11/15
 * @author Edd
 */
public interface OntologyRepository extends SolrCrudRepository<OntologyDocument, String> {
    // complete
    List<OntologyDocument> findCompleteByTermId(String idType, List<String> ids);

    // core
    List<OntologyDocument> findCoreAttrByTermId(String idType, List<String> ids);

    // history
    List<OntologyDocument> findHistoryByTermId(String idType, List<String> ids);

    // cross-references
    List<OntologyDocument> findXRefsByTermId(String idType, List<String> ids);

    // taxonomy constraints and blacklist
    List<OntologyDocument> findTaxonConstraintsByTermId(String idType, List<String> ids);

    // cross-ontology relations
    List<OntologyDocument> findXOntologyRelationsByTermId(String idType, List<String> ids);

    // annotation guidelines
    List<OntologyDocument> findAnnotationGuidelinesByTermId(String idType, List<String> ids);

    Page<OntologyDocument> findAllByOntologyType(String type, Pageable pageable);

    List<OntologyDocument> findSecondaryIdsByTermId(String idType, List<String> ids);
}