package uk.ac.ebi.quickgo.repo.ontology;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Ontology repository interface exposing methods for performing searches over its contents.
 *
 * Created 11/11/15
 * @author Edd
 */
public interface OntologyRepository extends SolrCrudRepository<OntologyDocument, String> {

    /**
     * Search by:
     *      docType:?0 AND ontologyType:?1 AND id:?2
     * @param docType
     * @param idType
     * @param id
     * @param pageable
     * @return
     */
    @Query("ontologyType:?0 AND id:?1")
    Optional<OntologyDocument> findByTermId(String idType, String id);

    @Query("text:?0 AND ontologyType:?1 AND id:?2 AND subsets:?3")
    List<OntologyDocument> findByTextOrScopeOrAspect(String searchableText, String scope, String aspect,
            Pageable pageable);

    /**
     * Search by:
     *      docType:?0 AND ontologyType:?1 AND id:?2 AND subsets:?3
     * @return
     */
    @Query("docType:?0 AND ontologyType:?1 AND id:?2 AND subsets:?3")
    List<OntologyDocument> findByTermIdAndSubsets();

    /**
     * Useful methods would be:
     *
     * --> streamed results
     * Stream<DocTypeOfX> findXAndStream(); // probably using paged results will be fine,
     * however if someone wants an unpaged, but mega massive result set, this would be suitable.
     * !!Just remember to close the stream after streaming!!.
     * See http://stackoverflow.com/questions/15283347/stream-directly-to-response-output-stream-in-handler-method-of-spring-mvc-3-1-co
     *
     * --> named queries with positional parameters
     * @Query("title:*?0* OR description:*?0* OR sausage:?1*")
     * public List<MyDocument> findByQueryAnnotation(String zerothSearchTerm, String firstSearchTerm);
     *
     * --> paged results:
     * public List<Stuff> findByName(String name, Pageable pageable);
     *
     * see {@link PagingAndSortingRepository} and examples in:
     * http://docs.spring.io/spring-data/solr/docs/current/reference/html/.
     *
     * --> checkout other flavours of @Query annotations:
     * @Query(fields = { SearchableProductDefinition.ID_FIELD_NAME, SearchableProductDefinition.NAME_FIELD_NAME,
     *                       SearchableProductDefinition.PRICE_FIELD_NAME, SearchableProductDefinition.FEATURES_FIELD_NAME,
     *                       SearchableProductDefinition.AVAILABLE_FIELD_NAME }, defaultOperator = Operator.AND)
     *  HighlightPage<Product> findByNameIn(Collection<String> names, Pageable page);
     */
}
