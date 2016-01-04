package uk.ac.ebi.quickgo.repo.ontology;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;

import java.util.Optional;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Ontology repository interface exposing methods for performing searches over its contents.
 *
 * see here how to change
 * https://github.com/spring-projects/spring-data-solr/blob/master/README.md
 *
 * Created 11/11/15
 * @author Edd
 */
public interface OntologyRepository extends SolrCrudRepository<OntologyDocument, String> {
    String ID = "id";
    String ONTOLOGY_TYPE = "ontologyType";
    String NAME = "name";
    String SECONDARY_ID = "secondaryId";
    String IS_OBSOLETE = "obsolete";
    String CONSIDER = "consider";
    String REPLACED_BY = "replacedBy";
    String DEFINITION = "definition";
    String COMMENT = "comment";
    String ASPECT = "aspect";
    String USAGE = "usage";
    String SUBSET = "subset";
    String CHILDREN = "children";
    String ANCESTOR = "ancestor";
    String SYNONYMS = "synonyms";
    String HISTORY = "history";
    String XREF = "xref";
    String ANNOTATION_GUIDELINE = "annotationGuideline";
    String TAXON_CONSTRAINT = "taxonConstraint";
    String XRELATIONS = "xRelation";
    String BLACKLIST = "blacklist";

    @Query("ontologyType:?0 AND id:?1")
    Optional<OntologyDocument> findCompleteByTermId(String idType, String id);

    @Query(value = "ontologyType:?0 AND id:?1",
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, ASPECT, ANCESTOR,
                    USAGE, SYNONYMS, DEFINITION})
    Optional<OntologyDocument> findCoreByTermId(String idType, String id);

    @Query(value = "ontologyType:?0 AND id:?1",
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, HISTORY})
    Optional<OntologyDocument> findHistoryByTermId(String idType, String id);

    @Query(value = "ontologyType:?0 AND id:?1",
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, XREF})
    Optional<OntologyDocument> findXRefsByTermId(String idType, String id);

    @Query(value = "ontologyType:?0 AND id:?1",
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, TAXON_CONSTRAINT, BLACKLIST})
    Optional<OntologyDocument> findTaxonConstraintsByTermId(String idType, String id);

    @Query(value = "ontologyType:?0 AND id:?1",
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, XRELATIONS})
    Optional<OntologyDocument> findXOntologyRelationsByTermId(String idType, String id);

    @Query(value = "ontologyType:?0 AND id:?1",
            fields = {ID, NAME, IS_OBSOLETE, COMMENT, DEFINITION, ANNOTATION_GUIDELINE})
    Optional<OntologyDocument> findAnnotationGuidelinesByTermId(String idType, String id);




    /**
     * Useful methods would be:
     *
     * --> streamed results
     * Stream<DocTypeOfX> findXAndStream(); // probably using paged results will be fine,
     * however if someone wants an unpaged, but mega massive result set, this would be suitable.
     * !!Just remember to close the stream after streaming!!.
     * See http://stackoverflow.com/questions/15283347/stream-directly-to-response-output-stream-in-handler-method-of
     * -spring-mvc-3-1-co
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
     *                       SearchableProductDefinition.PRICE_FIELD_NAME, SearchableProductDefinition
     *                       .FEATURES_FIELD_NAME,
     *                       SearchableProductDefinition.AVAILABLE_FIELD_NAME }, defaultOperator = Operator.AND)
     *  HighlightPage<Product> findByNameIn(Collection<String> names, Pageable page);
     */
}