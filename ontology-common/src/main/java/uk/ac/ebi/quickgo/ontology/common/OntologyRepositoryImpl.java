package uk.ac.ebi.quickgo.ontology.common;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.common.SolrCrudRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.quickgo.ontology.common.OntologyFields.*;

public class OntologyRepositoryImpl extends SolrCrudRepositoryImpl<OntologyDocument> implements OntologyRepository {

    public OntologyRepositoryImpl(SolrClient solrClient) {
        super(solrClient, SolrCollectionName.ONTOLOGY, OntologyDocument.class);
    }

    public List<OntologyDocument> findCompleteByTermId(String idType, List<String> ids) {
        return findByIdQuery(idType, ids);
    }

    public List<OntologyDocument> findCoreAttrByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, ASPECT, ANCESTOR, USAGE, SYNONYM, DEFINITION, DEFINITION_XREFS, ID_LOWERCASE};
        return findByIdQuery(idType, ids, fields);
    }

    public List<OntologyDocument> findHistoryByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, HISTORY};
        return findByIdQuery(idType, ids, fields);
    }

    public List<OntologyDocument> findXRefsByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, XREF};
        return findByIdQuery(idType, ids, fields);
    }

    public List<OntologyDocument> findTaxonConstraintsByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, TAXON_CONSTRAINT, BLACKLIST};
        return findByIdQuery(idType, ids, fields);
    }

    public List<OntologyDocument> findXOntologyRelationsByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, XRELATION};
        return findByIdQuery(idType, ids, fields);
    }

    public List<OntologyDocument> findAnnotationGuidelinesByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, ANNOTATION_GUIDELINE};
        return findByIdQuery(idType, ids, fields);
    }

    public Page<OntologyDocument> findAllByOntologyType(String type, Pageable pageable) {
        SolrQuery query = new SolrQuery(solrConcat(ONTOLOGY_TYPE_LOWERCASE, type));
        return super.queryForPage(query, pageable);
    }

    public List<OntologyDocument> findSecondaryIdsByTermId(String idType, List<String> ids) {
        String[] fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, SECONDARY_ID};
        return findByIdQuery(idType, ids, fields);
    }

    private List<OntologyDocument> findByIdQuery(String idType, List<String> ids, String... fields) {
        if (ids.isEmpty()) {
            return List.of();
        }

        String subIdQueryORSeparated = ids.stream()
          .map(id -> solrConcat(ID_LOWERCASE, id) + " OR " + solrConcat(SECONDARY_ID_LOWERCASE, id))
          .collect(Collectors.joining(" OR "));

        String query = solrConcat(ONTOLOGY_TYPE_LOWERCASE, idType) + " AND (" + subIdQueryORSeparated + ")";
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setFields(fields);
        solrQuery.setRows(ids.size());
        //solrQuery.setSort("id", SolrQuery.ORDER.asc);
        return super.query(solrQuery);
    }

    private String solrConcat(String field, String value) {
        return field + ": " + value;
    }
}
