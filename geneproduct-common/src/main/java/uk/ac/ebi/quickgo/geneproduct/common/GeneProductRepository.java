package uk.ac.ebi.quickgo.geneproduct.common;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;

import java.util.List;
import java.util.Optional;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Gene product repository interface exposing methods for performing searches over its contents.
 * <p>
 * See here how to change:
 * https://github.com/spring-projects/spring-data-solr/blob/master/README.md
 *
 * @author Ricardo Antunes
 */
public interface GeneProductRepository extends SolrCrudRepository<GeneProductDocument, String> {

    Optional<GeneProductDocument> findById(String id);

    List<GeneProductDocument> findCoreAttrByGeneProductId(List<String> ids);
}
