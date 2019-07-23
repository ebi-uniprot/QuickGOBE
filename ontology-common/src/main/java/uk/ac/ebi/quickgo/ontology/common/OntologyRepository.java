package uk.ac.ebi.quickgo.ontology.common;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Pivot;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import static org.springframework.data.solr.core.query.Query.Operator.OR;

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
    String QUERY_ONTOLOGY_TYPE_AND_ID = OntologyFields.ONTOLOGY_TYPE_LOWERCASE + ":?0 " +
            "AND (" + OntologyFields.ID_LOWERCASE + ":(?1) OR " + OntologyFields.SECONDARY_ID_LOWERCASE + ":(?1))";

    // complete
    @Query(QUERY_ONTOLOGY_TYPE_AND_ID) List<OntologyDocument> findCompleteByTermId(String idType, List<String> ids);

    // core
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.ASPECT, OntologyFields.ANCESTOR,
                    OntologyFields.USAGE, OntologyFields.SYNONYM, OntologyFields.DEFINITION,
                    OntologyFields.DEFINITION_XREFS, OntologyFields.ID_LOWERCASE})
    List<OntologyDocument> findCoreAttrByTermId(String idType, List<String> ids);

    // history
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.DEFINITION, OntologyFields.HISTORY}) List<OntologyDocument> findHistoryByTermId(
            String idType, List<String> ids);

    // cross-references
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.DEFINITION, OntologyFields.XREF}) List<OntologyDocument> findXRefsByTermId(
            String idType, List<String> ids);

    // taxonomy constraints and blacklist
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.DEFINITION, OntologyFields.TAXON_CONSTRAINT, OntologyFields.BLACKLIST})
    List<OntologyDocument> findTaxonConstraintsByTermId(String idType, List<String> ids);

    // cross-ontology relations
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.DEFINITION, OntologyFields.XRELATION})
    List<OntologyDocument> findXOntologyRelationsByTermId(String idType, List<String> ids);

    // annotation guidelines
    @Query(value = QUERY_ONTOLOGY_TYPE_AND_ID,
            fields = {
                    OntologyFields.ID, OntologyFields.NAME, OntologyFields.IS_OBSOLETE, OntologyFields.COMMENT,
                    OntologyFields.DEFINITION, OntologyFields.ANNOTATION_GUIDELINE})
    List<OntologyDocument> findAnnotationGuidelinesByTermId(String idType, List<String> ids);

    Page<OntologyDocument> findAllByOntologyType(String type, Pageable pageable);
}