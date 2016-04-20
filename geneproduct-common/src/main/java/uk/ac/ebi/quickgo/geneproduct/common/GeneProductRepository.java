package uk.ac.ebi.quickgo.geneproduct.common;

import org.springframework.data.solr.repository.Query;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

import java.util.List;
import java.util.Optional;
import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductFields;

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
                    GeneProductFields.SYNONYM, GeneProductFields.TYPE, GeneProductFields.TAXON_ID,
                    GeneProductFields.TAXON_NAME, GeneProductFields.DATABASE_SUBSET,
                    GeneProductFields.COMPLETE_PROTEOME, GeneProductFields.REFERENCE_POTEOME,
                    GeneProductFields.IS_ISOFORM, GeneProductFields.IS_ANNOTATED, GeneProductFields.PARENT_ID })
    List<GeneProductDocument> findById(List<String> ids);


}
