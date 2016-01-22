package uk.ac.ebi.quickgo.repo.solr.io.ontology;

import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;

import java.util.Optional;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import static uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyFields.*;

/**
 * Ontology repository interface exposing methods for performing searches over its contents.
 * <p>
 * See here how to change:
 * https://github.com/spring-projects/spring-data-solr/blob/master/README.md
 *
 * Created 11/11/15
 * @author Edd
 */
public interface OntologyRepository extends SolrCrudRepository<OntologyDocument, String> {

    String QUERY_ONTOLOGY_TYPE_AND_ID = ONTOLOGY_TYPE + ":?0 AND " + ID + ":?1";

    // complete
    @Query(QUERY_ONTOLOGY_TYPE_AND_ID) Optional<OntologyDocument> findCompleteByTermId(String idType, String id);

    // core
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, ASPECT, ANCESTOR,
                    USAGE, SYNONYM, DEFINITION}) Optional<OntologyDocument> findCoreByTermId(String idType, String id);

    // history
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, HISTORY})
    Optional<OntologyDocument> findHistoryByTermId(String idType, String id);

    // cross-references
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, XREF}) Optional<OntologyDocument> findXRefsByTermId(
            String idType, String id);

    // taxonomy constraints and blacklist
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, TAXON_CONSTRAINT, BLACKLIST})
    Optional<OntologyDocument> findTaxonConstraintsByTermId(String idType, String id);

    // cross-ontology relations
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, XRELATION})
    Optional<OntologyDocument> findXOntologyRelationsByTermId(String idType, String id);

    // annotation guidelines
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, ANNOTATION_GUIDELINE})
    Optional<OntologyDocument> findAnnotationGuidelinesByTermId(String idType, String id);

}
