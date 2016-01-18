package uk.ac.ebi.quickgo.repo.solr.io.geneproduct;

import org.springframework.data.solr.repository.SolrCrudRepository;
import uk.ac.ebi.quickgo.repo.solr.document.geneproduct.GeneProductDocument;

/**
 * Gene product repository interface exposing methods for performing searches over its contents.
 * <p>

 * Created 16/1/16
 * @author Edd
 */
//@NoRepositoryBean
public interface GeneProductRepository extends SolrCrudRepository<GeneProductDocument, String> {


}
