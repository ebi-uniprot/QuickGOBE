package uk.ac.ebi.quickgo.ontology.common;

import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyFields;

import java.util.Optional;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

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
    String QUERY_ONTOLOGY_TYPE_AND_ID = OntologyFields.ONTOLOGY_TYPE + ":?0 AND " + OntologyFields.ID + ":?1";

    // complete
    @Query(QUERY_ONTOLOGY_TYPE_AND_ID) Optional<OntologyDocument> findCompleteByTermId(String idType, String id);

    // core
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.ASPECT, OntologyFields.ANCESTOR,
                    OntologyFields.USAGE, OntologyFields.SYNONYM, OntologyFields.DEFINITION}) Optional<OntologyDocument> findCoreByTermId(String idType, String id);

    // history
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT, OntologyFields.DEFINITION, OntologyFields.HISTORY})
    Optional<OntologyDocument> findHistoryByTermId(String idType, String id);

    // cross-references
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT, OntologyFields.DEFINITION, OntologyFields.XREF}) Optional<OntologyDocument> findXRefsByTermId(
            String idType, String id);

    // taxonomy constraints and blacklist
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT, OntologyFields.DEFINITION, OntologyFields.TAXON_CONSTRAINT, OntologyFields.BLACKLIST})
    Optional<OntologyDocument> findTaxonConstraintsByTermId(String idType, String id);

    // cross-ontology relations
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT, OntologyFields.DEFINITION, OntologyFields.XRELATION})
    Optional<OntologyDocument> findXOntologyRelationsByTermId(String idType, String id);

    // annotation guidelines
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT, OntologyFields.DEFINITION, OntologyFields.ANNOTATION_GUIDELINE})
    Optional<OntologyDocument> findAnnotationGuidelinesByTermId(String idType, String id);
}