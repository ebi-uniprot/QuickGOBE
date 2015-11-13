package uk.ac.ebi.quickgo.repo.ontology;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;

import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Ontology repository interface exposing methods for performing searches over its contents.
 *
 * Created 11/11/15
 * @author Edd
 */
public interface OntologyRepository extends SolrCrudRepository<OntologyDocument, String> {

    HighlightPage<OntologyDocument> findByNameIn(Collection<String> name, Pageable pageable);

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
