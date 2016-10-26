package uk.ac.ebi.quickgo.geneproduct.common;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductFields;

import java.util.List;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Gene product repository interface exposing methods for performing searches over its contents.
 * <p>
 * See here how to change:
 * https://github.com/spring-projects/spring-data-solr/blob/master/README.md
 *
 * @author Ricardo Antunes; Tony Wardell
 */
public interface GeneProductRepository extends SolrCrudRepository<GeneProductDocument, String> {

    String QUERY_GENEPRODUCT_BY_ID = GeneProductFields.ID + ":?0";

    @Query(value = QUERY_GENEPRODUCT_BY_ID,
            fields = {GeneProductFields.ID, GeneProductFields.DATABASE, GeneProductFields.NAME, GeneProductFields.SYMBOL,
                    GeneProductFields.SYNONYM, GeneProductFields.TYPE, GeneProductFields.TAXON_ID, GeneProductFields.DATABASE_SUBSET,
                    GeneProductFields.COMPLETE_PROTEOME, GeneProductFields.REFERENCE_PROTEOME,
                    GeneProductFields.IS_ISOFORM, GeneProductFields.IS_ANNOTATED, GeneProductFields.PARENT_ID})
    List<GeneProductDocument> findById(List<String> ids);

    List<GeneProductDocument> findByTargetSet(String name);
}
