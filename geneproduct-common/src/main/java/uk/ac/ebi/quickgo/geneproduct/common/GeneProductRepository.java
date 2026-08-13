package uk.ac.ebi.quickgo.geneproduct.common;

import java.util.List;
import uk.ac.ebi.quickgo.common.repository.SolrCrudRepository;

import static uk.ac.ebi.quickgo.geneproduct.common.GeneProductFields.*;

/**
 * Gene product repository interface exposing methods for performing searches over its contents.
 *
 * @author Ricardo Antunes; Tony Wardell
 */
public interface GeneProductRepository extends SolrCrudRepository<GeneProductDocument, String> {

    List<GeneProductDocument> findById(List<String> ids);

    List<GeneProductDocument> findByTargetSet(String name);
}
